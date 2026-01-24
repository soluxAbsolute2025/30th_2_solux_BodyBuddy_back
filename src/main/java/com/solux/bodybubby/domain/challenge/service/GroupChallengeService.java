package com.solux.bodybubby.domain.challenge.service;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.entity.*;
import com.solux.bodybubby.domain.challenge.repository.ChallengeLogRepository;
import com.solux.bodybubby.domain.challenge.repository.ChallengeRepository;
import com.solux.bodybubby.domain.challenge.repository.UserChallengeRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.util.S3Provider;

import com.solux.bodybubby.s3Test.S3Service;
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
//    private final S3Service s3Service;
    private static final int DAILY_CHECK_IN_REWARD = 10;

    /**
     * 참여 중 그룹 챌린지 목록 조회
     */
    public List<GroupListResponse> getOngoingList(Long userId) {
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

        // [보완] 평균 달성률이 null일 경우(참여자가 없을 때 등) 0.0으로 처리하여 에러 방지
        Double avgRate = userChallengeRepository.getGroupAverageRate(challengeId);
        BigDecimal groupAverage = BigDecimal.valueOf(avgRate != null ? avgRate : 0.0);

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

        // 기간은 최소 7일 이상
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

        // 방장 자동 참여 등록
        userChallengeRepository.save(UserChallenge.builder()
                .user(creator).challenge(savedChallenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());

        // 성공 화면을 위한 그룹 코드 반환
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
        Challenge challenge = challengeRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹 코드입니다."));

        userChallengeRepository.findByUserIdAndChallengeId(userId, challenge.getId())
                .ifPresent(uc -> {
                    throw new IllegalStateException("이미 참여 중인 챌린지입니다.");
                });

        if (userChallengeRepository.countByChallengeId(challenge.getId()) >= challenge.getMaxParticipants()) {
            throw new IllegalStateException("참여 인원이 가득 찼습니다.");
        }

        UserChallenge newParticipant = userChallengeRepository.save(UserChallenge.builder()
                .user(user).challenge(challenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());
        updateRanks(challenge.getId());
    }

    /**
     * 해당 그룹의 모든 참가자 순위 일괄 업데이트
     */
    private void updateRanks(Long challengeId) {
        // 달성률 내림차순 + 참여시간 오름차순으로 전체 참가자 조회
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDescJoinedAtAsc(challengeId);

        // 순서대로 순위(1위부터) 부여
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
     * 그룹 챌린지 인증 (S3 업로드 및 로그 저장)
     */
    @Transactional
    public GroupCheckInResponse checkIn(Long challengeId, Long userId, MultipartFile file) {
        UserChallenge uc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        // 1. 중복 인증 체크
        if (challengeLogRepository.existsByUserChallengeAndLogDate(uc, LocalDate.now())) {
            throw new IllegalStateException("오늘은 이미 인증을 완료했습니다.");
        }

        // 2. 이미지 S3 업로드 (변수 선언 추가)
        String uploadedImageUrl = null;
        if (file != null && !file.isEmpty()) {
            uploadedImageUrl = s3Provider.uploadFile(file, "challenge-auth");
        }

        // 3. 유저 포인트 지급
        User user = uc.getUser();
        user.addPoints(DAILY_CHECK_IN_REWARD);

        // 4. 인증 로그 기록 (imageUrl에 업로드된 경로 저장)
        challengeLogRepository.save(ChallengeLog.builder()
                .userChallenge(uc)
                .logDate(LocalDate.now())
                .valueAchieved(BigDecimal.ONE)
                .imageUrl(uploadedImageUrl) // 👈 여기서 변수 사용
                .build());

        // 5. 진행률 업데이트 (UserChallenge 엔티티 내부 로직 실행)
        uc.updateGroupProgress();

        // 6. 100% 달성 보너스 (Null 방어 추가)
        if ("COMPLETED".equals(uc.getStatus())) {
            Integer bonus = uc.getChallenge().getBaseRewardPoints();
            user.addPoints(bonus != null ? bonus : 500);
        }

        // 7. 실시간 순위 업데이트
        updateRanks(challengeId);

        // 8. 평균 달성률 조회 및 응답 (Null 방어)
        Double avgRate = userChallengeRepository.getGroupAverageRate(challengeId);
        BigDecimal finalAvgRate = BigDecimal.valueOf(avgRate != null ? avgRate : 0.0);

        return GroupCheckInResponse.builder()
                .challengeId(challengeId)
                .title(uc.getChallenge().getTitle())
                .earnedPoints(DAILY_CHECK_IN_REWARD)
                .myStatus(GroupCheckInResponse.MyStatusUpdate.builder()
                        .updatedAchievementRate(uc.getAchievementRate())
                        .currentRank(uc.getCurrentRank())
                        .build())
                .groupAverageRate(finalAvgRate)
                .build();
    }

    /**
     * 완료된 그룹 챌린지 목록 조회
     * 조건: 그룹 전체 평균 달성률(groupAverageRate)이 100%인 경우
     */
    public List<GroupCompletedResponse> getCompletedList(Long userId) {
        // 1. 유저가 참여했던 모든 챌린지 정보 조회 (상태 상관없이 일단 조회)
        List<UserChallenge> myAllChallenges = userChallengeRepository.findAllByUserId(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return myAllChallenges.stream()
                .filter(uc -> {
                    // 2. 해당 챌린지의 그룹 평균 달성률 계산
                    Double avgRate = userChallengeRepository.getGroupAverageRate(uc.getChallenge().getId());
                    // 3. 평균이 100%인 것만 필터링 (정수 변환 후 비교)
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