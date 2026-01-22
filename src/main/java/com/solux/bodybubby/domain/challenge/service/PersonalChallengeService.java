package com.solux.bodybubby.domain.challenge.service;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.entity.*;
import com.solux.bodybubby.domain.challenge.repository.*;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeLogRepository challengeLogRepository;
    private final UserRepository userRepository;

    /**
     * 개인 챌린지 목록 조회
     */
    public PersonalListResponse getPersonalDashboard(Long userId) {
        // groupCode가 없는 참여 정보만 필터링
        List<UserChallenge> myPersonalChallenges = userChallengeRepository.findAllByUserId(userId).stream()
                .filter(uc -> uc.getChallenge().getGroupCode() == null)
                .collect(Collectors.toList());

        List<PersonalListResponse.OngoingPersonalChallenge> ongoingList = myPersonalChallenges.stream()
                .map(uc -> {
                    Challenge c = uc.getChallenge();
                    return PersonalListResponse.OngoingPersonalChallenge.builder()
                            .challengeId(c.getId())
                            .title(c.getTitle())
                            .category(c.getChallengeType())
                            .progressValue(uc.getCurrentProgress().intValue())
                            .totalValue(c.getTargetValue().intValue())
                            .expectedReward(500) // 예시값
                            .dday(15)
                            .colorCode(c.getChallengeType().equals("DAILY") ? "#00FF00" : "#0000FF") // 카테고리별 색상
                            .build();
                }).collect(Collectors.toList());

        return PersonalListResponse.builder()
                .summary(PersonalListResponse.Summary.builder()
                        .acquiredPoints(2450)
                        .myAchievementRate(85)
                        .build())
                .ongoingChallenges(ongoingList)
                .build();
    }

    /**
     * 개인 챌린지 상세 조회
     */
    public PersonalDetailResponse getPersonalDetail(Long challengeId, Long userId) {
        UserChallenge uc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지 정보를 찾을 수 없습니다."));
        Challenge c = uc.getChallenge();

        return PersonalDetailResponse.builder()
                .challengeId(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getChallengeType())
                .targetDays(30)
                .dailyGoal(c.getTargetValue().intValue())
                .unit(c.getTargetUnit())
                .expectedReward(500)
                .rewardRate(10)
                .myAchievementRate(uc.getAchievementRate().intValue())
                .build();
    }

    /**
     * 개인 챌린지 생성
     */
    @Transactional
    public Long createPersonalChallenge(Long userId, PersonalCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Challenge challenge = Challenge.builder()
                .creator(user).title(request.getTitle()).description(request.getDescription())
                .challengeType(request.getCategory()).targetValue(request.getDailyGoal())
                .targetUnit(request.getUnit()).startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(request.getTargetDays()))
                .status(ChallengeStatus.IN_PROGRESS).build(); // 개인은 모집 단계 없음

        Challenge savedChallenge = challengeRepository.save(challenge);

        userChallengeRepository.save(UserChallenge.builder()
                .user(user).challenge(savedChallenge).currentProgress(BigDecimal.ZERO)
                .achievementRate(BigDecimal.ZERO).status("IN_PROGRESS")
                .joinedAt(LocalDateTime.now()).build());

        return savedChallenge.getId();
    }

    /**
     * 개인 챌린지 수정
     */
    @Transactional
    public void updatePersonalChallenge(Long userId, Long challengeId, PersonalUpdateRequest request) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 권한 확인 (본인의 개인 챌린지만 수정 가능)
        if (!challenge.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 변경 감지(Dirty Checking)를 통한 필드 업데이트
        // 실제 구현 시 null 체크 로직 포함 권장
        if (request.isImageDeleted()) {
            // 이미지 삭제 로직 처리
        }
    }

    /**
     * 개인 챌린지 삭제
     */
    @Transactional
    public void deletePersonalChallenge(Long userId, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        if (!challenge.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        challengeRepository.delete(challenge);
    }

    /**
     * 추천 챌린지 조회
     */
    public List<PersonalRecommendResponse> getRecommendedChallenges() {
        // 실제로는 'isOfficial' 같은 플래그가 있는 템플릿 챌린지들을 조회해야 합니다.
        return challengeRepository.findAll().stream()
                .filter(c -> c.getGroupCode() == null) // 개인용 템플릿만 필터링
                .limit(5)
                .map(c -> PersonalRecommendResponse.builder()
                        .challengeId(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .imageUrl(null) // 이미지 서버 연동 시 반영
                        .goalType("PERIOD")
                        .targetDays(7)
                        .dailyGoal(c.getTargetValue())
                        .unit(c.getTargetUnit())
                        .category(c.getChallengeType())
                        .estimatedReward(300)
                        .build())
                .collect(Collectors.toList());
    }
}