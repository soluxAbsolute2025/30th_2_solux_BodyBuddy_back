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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 참여 중 그룹 챌린지 목록 조회
     */
    public List<GroupListResponse> getOngoingList(Long userId) {
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUserIdAndStatus(userId, "IN_PROGRESS");

        return userChallenges.stream().map(uc -> {
            Challenge challenge = uc.getChallenge();
            List<GroupListResponse.ParticipantProfile> topProfiles =
                    userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDesc(challenge.getId())
                            .stream().limit(3)
                            .map(p -> GroupListResponse.ParticipantProfile.builder()
                                    .profileImageUrl(p.getUser().getProfileImageUrl()).build())
                            .collect(Collectors.toList());

            long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), challenge.getEndDate());

            return GroupListResponse.builder()
                    .challengeId(challenge.getId()).title(challenge.getTitle())
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
        List<UserChallenge> rankings = userChallengeRepository.findAllByChallengeIdOrderByAchievementRateDesc(challengeId);

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
                        .title(challenge.getTitle()).description(challenge.getDescription())
                        .startDate(challenge.getStartDate().toString()).endDate(challenge.getEndDate().toString())
                        .groupCode(challenge.getGroupCode()).currentParticipantCount(rankings.size())
                        .maxParticipantCount(challenge.getMaxParticipants()).isPublic(true).build())
                .myStatus(GroupDetailResponse.MyStatus.builder()
                        .myAchievementRate(myUc.getAchievementRate()).myRank(myUc.getCurrentRank()).build())
                .groupAverageRate(BigDecimal.valueOf(userChallengeRepository.getGroupAverageRate(challengeId)))
                .participants(participantDetails).build();
    }

    /**
     * 새로운 그룹 챌린지 조회 (모집 중인 것 전체)
     */
    public List<GroupSearchResponse> searchNewGroups() {
        return challengeRepository.findAll().stream()
                .filter(c -> c.getStatus() == ChallengeStatus.RECRUITING)
                .map(c -> GroupSearchResponse.builder()
                        .challengeId(c.getId()).title(c.getTitle()).description(c.getDescription())
                        .currentParticipants((int) userChallengeRepository.countByChallengeId(c.getId()))
                        .maxParticipants(c.getMaxParticipants()).challengeType(c.getChallengeType()).build())
                .collect(Collectors.toList());
    }

    /**
     * 그룹 챌린지 생성
     */
    @Transactional
    public GroupCreateResponse createGroupChallenge(GroupCreateRequest request, Long userId) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(request.getPeriod());

        Challenge challenge = Challenge.builder()
                .creator(creator).title(request.getTitle()).description(request.getDescription())
                .challengeType(request.getChallengeType()).targetType(request.getTargetType())
                .targetValue(request.getTargetValue()).targetUnit(request.getTargetUnit())
                .startDate(startDate).endDate(endDate).maxParticipants(request.getMaxParticipants())
                .status(ChallengeStatus.RECRUITING).build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        userChallengeRepository.save(UserChallenge.builder()
                .user(creator).challenge(savedChallenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());

        return GroupCreateResponse.builder()
                .status(201).groupId(savedChallenge.getId()).groupCode(savedChallenge.getGroupCode())
                .message("그룹 챌린지가 생성되었습니다.").build();
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

        userChallengeRepository.save(UserChallenge.builder()
                .user(user).challenge(challenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());
    }

    /**
     * 그룹 챌린지 수정 (방장 권한 확인)
     */
    @Transactional
    public void updateChallenge(Long id, GroupCreateRequest request, Long userId) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow();
        if (!challenge.getCreator().getId().equals(userId)) throw new IllegalStateException("권한이 없습니다.");
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
     * 그룹 챌린지 인증 (Check-in)
     */
    @Transactional
    public GroupCheckInResponse checkIn(Long challengeId, Long userId, BigDecimal value) {
        UserChallenge uc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        challengeLogRepository.save(ChallengeLog.builder()
                .userChallenge(uc).logDate(LocalDate.now()).valueAchieved(value).build());

        uc.updateProgress(value);

        // 에러 해결: MyStatusUpdate 내부 클래스 빌더 사용
        return GroupCheckInResponse.builder()
                .challengeId(challengeId).title(uc.getChallenge().getTitle()).earnedPoints(10)
                .myStatus(GroupCheckInResponse.MyStatusUpdate.builder()
                        .updatedAchievementRate(uc.getAchievementRate())
                        .currentRank(uc.getCurrentRank()).build())
                .groupAverageRate(BigDecimal.valueOf(userChallengeRepository.getGroupAverageRate(challengeId)))
                .build();
    }
}