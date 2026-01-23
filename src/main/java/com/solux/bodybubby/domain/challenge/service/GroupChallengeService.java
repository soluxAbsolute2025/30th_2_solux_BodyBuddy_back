package com.solux.bodybubby.domain.challenge.service;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.entity.Challenge;
import com.solux.bodybubby.domain.challenge.entity.ChallengeLog;
import com.solux.bodybubby.domain.challenge.entity.ChallengeStatus;
import com.solux.bodybubby.domain.challenge.entity.UserChallenge;
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
    
    // [중요] 여기가 핵심입니다. S3Test가 아니라 global.s3.S3Provider 여야 합니다.
    private final S3Provider s3Provider; 

    // ... (getOngoingList, getDetail, searchNewGroups 로직은 기존과 동일) ...
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
    
    public GroupDetailResponse getDetail(Long challengeId, Long userId) {
        // 기존 코드 유지 (생략)
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
                        .isPublic(!"PRIVATE".equals(challenge.getPrivacyScope()))
                        .build())
                .myStatus(GroupDetailResponse.MyStatus.builder()
                        .myAchievementRate(myUc.getAchievementRate())
                        .myRank(myUc.getCurrentRank()).build())
                .groupAverageRate(BigDecimal.valueOf(avgRate != null ? avgRate : 0.0))
                .participants(participantDetails).build();
    }
    
    public List<GroupSearchResponse> searchNewGroups() {
        return challengeRepository.findAll().stream()
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
                .privacyScope(request.getPrivacyScope())
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

    // ... (joinByGroupCode 등 기존 로직 동일) ...
    @Transactional
    public void joinByGroupCode(String groupCode, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Challenge challenge = challengeRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹 코드입니다."));
        userChallengeRepository.findByUserIdAndChallengeId(userId, challenge.getId())
                .ifPresent(uc -> { throw new IllegalStateException("이미 참여 중인 챌린지입니다."); });
        if (userChallengeRepository.countByChallengeId(challenge.getId()) >= challenge.getMaxParticipants()) {
            throw new IllegalStateException("참여 인원이 가득 찼습니다.");
        }
        userChallengeRepository.save(UserChallenge.builder()
                .user(user).challenge(challenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());
        updateRanks(challenge.getId());
    }
    
    private void updateRanks(Long challengeId) {
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDescJoinedAtAsc(challengeId);
        for (int i = 0; i < userChallenges.size(); i++) {
            userChallenges.get(i).updateRank(i + 1);
        }
    }
    
    @Transactional
    public void updateChallenge(Long id, GroupCreateRequest request, Long userId) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow();
        if (!challenge.getCreator().getId().equals(userId)) throw new IllegalStateException("권한이 없습니다.");
        challenge.update(request.getTitle(), request.getDescription(), request.getPeriod(), request.getMaxParticipants(), request.getPrivacyScope());
    }

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

        if (challengeLogRepository.existsByUserChallengeAndLogDate(uc, LocalDate.now())) {
            throw new IllegalStateException("오늘은 이미 인증을 완료했습니다.");
        }

        // [수정] 인증 사진 S3 업로드
        String authImageUrl = null;
        if (file != null && !file.isEmpty()) {
            authImageUrl = s3Provider.uploadFile(file, "challenge-auth");
        }

        // 인증 로그 기록 (이미지 URL 포함)
        // 주의: ChallengeLog 엔티티에 imageUrl 필드가 없으면 에러가 납니다. 꼭 추가해주세요!
        challengeLogRepository.save(ChallengeLog.builder()
                .userChallenge(uc)
                .logDate(LocalDate.now())
                .valueAchieved(BigDecimal.ONE)
                .imageUrl(authImageUrl) // 이 필드가 ChallengeLog에 있어야 함
                .build());

        uc.updateGroupProgress();
        updateRanks(challengeId);

        return GroupCheckInResponse.builder()
                .challengeId(challengeId)
                .title(uc.getChallenge().getTitle())
                .earnedPoints(10)
                .myStatus(GroupCheckInResponse.MyStatusUpdate.builder()
                        .updatedAchievementRate(uc.getAchievementRate())
                        .currentRank(uc.getCurrentRank())
                        .build())
                .groupAverageRate(BigDecimal.valueOf(userChallengeRepository.getGroupAverageRate(challengeId)))
                .build();
    }

     // ... (완료 목록 조회 로직 동일) ...
     public List<GroupCompletedResponse> getCompletedList(Long userId) {
        List<UserChallenge> myAllChallenges = userChallengeRepository.findAllByUserId(userId);
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
                            .completedAt(uc.getCompletedAt() != null ? uc.getCompletedAt().toLocalDate().toString() : "")
                            .finalSuccessRate(uc.getAchievementRate().intValue())
                            .acquiredPoints(challenge.getBaseRewardPoints() != null ? challenge.getBaseRewardPoints() : 500)
                            .status("SUCCESS")
                            .build();
                })
                .collect(Collectors.toList());
    }
}