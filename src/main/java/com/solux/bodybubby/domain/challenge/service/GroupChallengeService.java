package com.solux.bodybubby.domain.challenge.service;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.entity.*;
import com.solux.bodybubby.domain.challenge.repository.ChallengeLogRepository;
import com.solux.bodybubby.domain.challenge.repository.ChallengeRepository;
import com.solux.bodybubby.domain.challenge.repository.UserChallengeRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.util.S3Provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChallengeService {
    
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeLogRepository challengeLogRepository;
    private final S3Provider s3Provider;
    private static final int DAILY_CHECK_IN_REWARD = 10;

    /**
     * 참여 중 그룹 챌린지 목록 조회
     */
    public List<GroupListResponse> getOngoingList(Long userId) {
        // 기존 코드 유지 (생략)
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUserIdAndStatus(userId, "IN_PROGRESS");
        return userChallenges.stream().map(uc -> {
             Challenge challenge = uc.getChallenge();
             List<GroupListResponse.ParticipantProfile> topProfiles =
                    userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDescJoinedAtAsc(challenge.getId())
                            .stream().limit(3)
                            .map(p -> GroupListResponse.ParticipantProfile.builder()
                                    .profileImageUrl(p.getUser().getProfileImageUrl()).build())
                            .collect(Collectors.toList());
            long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), challenge.getEndDate());
            return GroupListResponse.builder()
                    .challengeId(challenge.getId())
                    .title(challenge.getTitle())
                    .imageUrl(challenge.getImageUrl())
                    .myRank(uc.getCurrentRank())
                    .participantCount((int) userChallengeRepository.countByChallengeId(challenge.getId()))
                    .remainingDays((int) remainingDays)
                    .topParticipants(topProfiles).build();
        }).collect(Collectors.toList());
    }

    /**
     * 참여 중 그룹 챌린지 상세 조회
     */
    public GroupDetailResponse getDetail(Long challengeId, Long userId) {
        UserChallenge myUc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("참여 중인 챌린지가 아닙니다."));
        Challenge challenge = myUc.getChallenge();
        List<UserChallenge> rankings = userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDescJoinedAtAsc(challenge.getId());
        Double avgRate = userChallengeRepository.getGroupAverageRate(challengeId);
        
        List<GroupDetailResponse.ParticipantDetail> participantDetails = IntStream.range(0, rankings.size())
                .mapToObj(i -> {
                    UserChallenge uc = rankings.get(i);
                    return GroupDetailResponse.ParticipantDetail.builder()
                            .rank(i + 1).nickname(uc.getUser().getNickname())
                            .profileImageUrl(uc.getUser().getProfileImageUrl())
                            .achievementRate(uc.getAchievementRate())
                            .isMe(uc.getUser().getId().equals(userId)).build();
                }).collect(Collectors.toList());

        return GroupDetailResponse.builder()
                .challengeInfo(GroupDetailResponse.ChallengeInfo.builder()
                        .challengeId(challenge.getId())
                        .title(challenge.getTitle())
                        .description(challenge.getDescription())
                        .imageUrl(challenge.getImageUrl())
                        .startDate(challenge.getStartDate().toString())
                        .endDate(challenge.getEndDate().toString())
                        .groupCode(challenge.getGroupCode())
                        .currentParticipantCount(rankings.size())
                        .maxParticipantCount(challenge.getMaxParticipants())
                        .isPublic(challenge.getVisibility() != Visibility.SECRET)
                        .build())
                .myStatus(GroupDetailResponse.MyStatus.builder()
                        .myAchievementRate(myUc.getAchievementRate())
                        .myRank(myUc.getCurrentRank()).build())
                .groupAverageRate(BigDecimal.valueOf(avgRate != null ? avgRate : 0.0))
                .participants(participantDetails).build();
    }

    /**
     * 새로운 그룹 챌린지 조회 (모집 중인 것 전체)
     */
    public List<GroupSearchResponse> searchNewGroups() {
        return challengeRepository.findAll().stream()
                .filter(c -> c.getVisibility() == Visibility.PUBLIC)
                .filter(c -> c.getStatus() == ChallengeStatus.RECRUITING)
                .map(c -> GroupSearchResponse.builder()
                        .challengeId(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .imageUrl(c.getImageUrl())
                        .currentParticipants((int) userChallengeRepository.countByChallengeId(c.getId()))
                        .maxParticipants(c.getMaxParticipants()).challengeType(c.getChallengeType()).build())
                .collect(Collectors.toList());
    }

    /**
     * 그룹 챌린지 생성 (S3 업로드 적용)
     */
    @Transactional
    public GroupCreateResponse createGroupChallenge(GroupCreateRequest request, Long userId, MultipartFile image) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (request.getPeriod() == null || request.getPeriod() < 7) {
            throw new IllegalArgumentException("챌린지 기간은 최소 7일 이상으로 설정해야 합니다.");
        }

        // [수정] 이미지가 있을 때만 업로드 (Null Safety)
        String uploadedImageUrl = null;
        if (image != null && !image.isEmpty()) {
            uploadedImageUrl = s3Provider.uploadFile(image, "challenge-cover");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getPeriod());

        Challenge challenge = Challenge.builder()
                .creator(creator)
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(uploadedImageUrl) // S3 URL 저장
                .period(request.getPeriod())
                .visibility(request.getVisibility() != null ? request.getVisibility() : Visibility.PUBLIC)
                .startDate(startDate)
                .endDate(endDate)
                .maxParticipants(request.getMaxParticipants())
                .status(ChallengeStatus.RECRUITING)
                .build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        userChallengeRepository.save(UserChallenge.builder()
                .user(creator).challenge(savedChallenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());

        return GroupCreateResponse.builder()
                .status(201)
                .groupId(savedChallenge.getId())
                .groupCode(savedChallenge.getGroupCode())
                .message("그룹 챌린지가 생성되었습니다.")
                .build();
    }

    /**
     * 그룹 코드로 챌린지 참여
     */
    @Transactional
    public void joinByGroupCode(String groupCode, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // 1. 그룹 코드 존재 확인
        Challenge challenge = challengeRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹 코드입니다."));

        // [추가] 모집 중인 상태인지 확인 (기획에 따라 추가 가능)
        if (challenge.getStatus() != ChallengeStatus.RECRUITING) {
            throw new IllegalStateException("현재 모집 중인 그룹이 아닙니다.");
        }

        // 2. 이미 참여 중인지 확인
        userChallengeRepository.findByUserIdAndChallengeId(userId, challenge.getId())
                .ifPresent(uc -> { throw new IllegalStateException("이미 참여 중인 챌린지입니다."); });

        // 3. 인원수 체크 (NPE 방지)
        long currentCount = userChallengeRepository.countByChallengeId(challenge.getId());
        if (challenge.getMaxParticipants() != null && currentCount >= challenge.getMaxParticipants()) {
            throw new IllegalStateException("참여 인원이 가득 찼습니다.");
        }

        // 4. 참여 정보 저장
        userChallengeRepository.save(UserChallenge.builder()
                .user(user).challenge(challenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());

        updateRanks(challenge.getId());
    }

    /**
     * 해당 그룹의 모든 참가자 순위 일괄 업데이트
     */
    private void updateRanks(Long challengeId) {
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDescJoinedAtAsc(challengeId);
        for (int i = 0; i < userChallenges.size(); i++) {
            userChallenges.get(i).updateRank(i + 1);
        }
    }

    /**
     * 그룹 챌린지 수정 (방장 권한 확인)
     */
    @Transactional
    public void updateChallenge(Long id, GroupCreateRequest request, Long userId) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow();
        if (!challenge.getCreator().getId().equals(userId)) throw new IllegalStateException("권한이 없습니다.");

        // [보완] 실제 데이터 업데이트 로직 추가 (엔티티에 update 메서드 필요)
        challenge.update(request.getTitle(), request.getDescription(), request.getPeriod(), request.getMaxParticipants(), request.getVisibility());
    }

    /**
     * 그룹 챌린지 삭제 (방장 권한 확인)
     */
    @Transactional
    public void deleteChallenge(Long id, Long userId) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow();
        if (!challenge.getCreator().getId().equals(userId)) throw new IllegalStateException("권한이 없습니다.");
        challengeRepository.delete(challenge);
    }

    /**
     * 그룹 챌린지 인증
     */
    @Transactional
    public GroupCheckInResponse checkIn(Long challengeId, Long userId) {
        UserChallenge uc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        // 1. 하루 1회 중복 인증 체크
        if (challengeLogRepository.existsByUserChallengeAndLogDate(uc, LocalDate.now())) {
            throw new IllegalStateException("오늘은 이미 인증을 완료했습니다.");
        }

        // 2. 유저 포인트 지급 (매일 10 XP)
        User user = uc.getUser();
        user.addPoints(DAILY_CHECK_IN_REWARD);

        // 3. 인증 로그 기록 (사진 없이 기록만 남김)
        challengeLogRepository.save(ChallengeLog.builder()
                .userChallenge(uc)
                .logDate(LocalDate.now())
                .valueAchieved(BigDecimal.ONE)
                .build());

        // 4. 진행률 및 달성률 업데이트
        uc.updateCheckInProgress(); // 통합된 인증 로직 사용

        // 5. 최종 완수 보너스 포인트 지급
        if ("COMPLETED".equals(uc.getStatus())) {
            Integer bonus = uc.getChallenge().getBaseRewardPoints();
            user.addPoints(bonus != null ? bonus : 500);
        }

        // 6. 실시간 순위 일괄 업데이트
        updateRanks(challengeId);

        // 7. 그룹 평균 달성률 계산
        Double avgRate = userChallengeRepository.getGroupAverageRate(challengeId);
        BigDecimal finalAvgRate = BigDecimal.valueOf(avgRate != null ? avgRate : 0.0);

        return GroupCheckInResponse.builder()
                .challengeId(challengeId)
                .nickname(user.getNickname())
                .title(uc.getChallenge().getTitle())
                .earnedPoints(DAILY_CHECK_IN_REWARD)
                .myStatus(GroupCheckInResponse.MyStatusUpdate.builder()
                        .updatedAchievementRate(uc.getAchievementRate())
                        .currentRank(uc.getCurrentRank())
                        .build())
                .groupAverageRate(finalAvgRate)
                .checkInTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))) // [보완] 인증 시각 추가
                .build();
    }

    /**
     * 완료된 그룹 챌린지 목록 조회
     * 조건: 그룹 전체 평균 달성률(groupAverageRate)이 100%인 경우
     */
     public List<GroupCompletedResponse> getCompletedList(Long userId) {
        List<UserChallenge> myAllChallenges = userChallengeRepository.findAllByUserId(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return myAllChallenges.stream()
                .filter(uc -> {
                    Double avgRate = userChallengeRepository.getGroupAverageRate(uc.getChallenge().getId());
                    return avgRate != null && avgRate.intValue() == 100;
                })
                .map(uc -> {
                    Challenge challenge = uc.getChallenge();
                    return GroupCompletedResponse.builder()
                            .challengeId(challenge.getId())
                            .challengeType("GROUP")
                            .title(challenge.getTitle())
                            .description(challenge.getDescription())
                            .imageUrl(challenge.getImageUrl())
                            .completedAt(uc.getCompletedAt() != null ? uc.getCompletedAt().format(formatter) : "")
                            .finalSuccessRate(uc.getAchievementRate().intValue())
                            .acquiredPoints(challenge.getBaseRewardPoints() != null ? challenge.getBaseRewardPoints() : 500)
                            .status("SUCCESS")
                            .build();
                })
                .collect(Collectors.toList());
    }
}