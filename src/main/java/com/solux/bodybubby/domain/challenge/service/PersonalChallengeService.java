package com.solux.bodybubby.domain.challenge.service;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.entity.*;
import com.solux.bodybubby.domain.challenge.repository.ChallengeLogRepository;
import com.solux.bodybubby.domain.challenge.repository.ChallengeRepository;
import com.solux.bodybubby.domain.challenge.repository.UserChallengeRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeLogRepository challengeLogRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 인증 시 매번 지급할 고정 포인트 (예: 10 XP)
    private static final int DAILY_CHECK_IN_REWARD = 10;

    /**
     * 개인 챌린지 목록 조회
     */
    public PersonalListResponse getPersonalDashboard(Long userId) {
        // 1. 유저의 모든 개인 챌린지 참여 정보 조회
        List<UserChallenge> allMyPersonal = userChallengeRepository.findAllByUserId(userId).stream()
                .filter(uc -> uc.getChallenge().getGroupCode() == null)
                .collect(Collectors.toList());

        // 2. 진행 중인 챌린지만 필터링하여 리스트 구성
        List<UserChallenge> ongoing = allMyPersonal.stream()
                .filter(uc -> "IN_PROGRESS".equals(uc.getStatus()))
                .collect(Collectors.toList());

        List<PersonalListResponse.OngoingPersonalChallenge> ongoingList = ongoing.stream()
                .map(uc -> {
                    Challenge c = uc.getChallenge();
                    // 실시간 D-day 계산 (종료일 - 오늘)
                    long dday = ChronoUnit.DAYS.between(LocalDate.now(), c.getEndDate());

                    return PersonalListResponse.OngoingPersonalChallenge.builder()
                            .challengeId(c.getId())
                            .title(c.getTitle())
                            .imageUrl(c.getImageUrl())
                            .category(c.getChallengeType())
                            .progressValue(uc.getCurrentProgress().intValue())
                            .totalValue(c.getPeriod())
                            .expectedReward(c.getBaseRewardPoints() != null ? c.getBaseRewardPoints() : 0) // 엔티티 값 사용
                            .dday((int) dday)
                            .colorCode(getColorByCategory(c.getChallengeType())) // 카테고리별 색상 로직 적용
                            .build();
                }).collect(Collectors.toList());
        User user = userRepository.findById(userId).orElseThrow();

        return PersonalListResponse.builder()
                .summary(PersonalListResponse.Summary.builder()
                        .acquiredPoints(user.getCurrentPoints()) // 유저의 실제 포인트 노출
                        .myAchievementRate(ongoing.isEmpty() ? 0 : (int) ongoing.stream().mapToDouble(uc -> uc.getAchievementRate().doubleValue()).average().orElse(0.0)).build())
                .ongoingChallenges(ongoingList).build();
    }

    /**
     * 개인 챌린지 상세 조회
     * 하드코딩 제거: 목표 일수, 포인트, 보상 배율 등을 엔티티 데이터로 교체
     */
    public PersonalDetailResponse getPersonalDetail(Long challengeId, Long userId) {
        UserChallenge uc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지 정보를 찾을 수 없습니다."));
        Challenge c = uc.getChallenge();

        // 시작일과 종료일 사이의 전체 기간 계산
        long totalTargetDays = ChronoUnit.DAYS.between(c.getStartDate(), c.getEndDate());

        return PersonalDetailResponse.builder()
                .challengeId(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .imageUrl(c.getImageUrl())
                .category(c.getChallengeType())
                .targetDays(c.getPeriod())
                .dailyGoal(c.getTargetValue().intValue())
                .unit(c.getTargetUnit())
                .expectedReward(c.getBaseRewardPoints())
                .rewardRate(c.getBaseRewardPoints() != null && totalTargetDays > 0 ? c.getBaseRewardPoints() / (int) totalTargetDays : 0) // 일일 기여 포인트(예시)
                .myAchievementRate(uc.getAchievementRate().intValue())
                .build();
    }

    /**
     * 개인 챌린지 생성
     */
    @Transactional
    public Long createPersonalChallenge(Long userId, PersonalCreateRequest request, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String uploadedImageUrl = s3Service.uploadFile(image);

        Challenge challenge = Challenge.builder()
                .creator(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(uploadedImageUrl)
                .challengeType(request.getCategory())
                .targetValue(request.getDailyGoal())
                .targetUnit(request.getUnit())
                .period(request.getTargetDays())
                .baseRewardPoints(request.getExpectedReward())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(request.getTargetDays()))
                .visibility(request.getVisibility() != null ? request.getVisibility() : Visibility.PUBLIC)
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
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 1. 권한 확인 (본인의 개인 챌린지만 수정 가능)
        if (!challenge.getCreator().getId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 2. 이미지 삭제 처리 로직
        if (request.isImageDeleted()) {
            if (challenge.getImageUrl() != null) s3Service.deleteFile(challenge.getImageUrl());
            challenge.updateImageUrl("https://default-image-url.com/challenge.png");
        }

        // 3. 엔티티 메서드를 통한 필드 업데이트 (Dirty Checking 활용)
        challenge.updatePersonal(
                request.getTitle(), request.getDescription(), request.getTargetDays(),
                request.getDailyGoal(), request.getUnit(), request.getVisibility()
        );
    }

    /**
     * 개인 챌린지 삭제
     */
    @Transactional
    public void deletePersonalChallenge(Long userId, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow();
        if (!challenge.getCreator().getId().equals(userId)) throw new IllegalStateException("삭제 권한이 없습니다.");
        challengeRepository.delete(challenge);
    }

    /**
     * 카테고리별 색상 지정 로직 (헬퍼 메서드)
     */
    private String getColorByCategory(String category) {
        if (category == null) return "#808080"; // 기본 회색
        return switch (category) {
            case "DAILY" -> "#00FF00"; // 초록
            case "WEEKLY" -> "#0000FF"; // 파랑
            default -> "#FFA500"; // 주황
        };
    }

    /**
     * 추천 챌린지 조회
     */
    public List<PersonalRecommendResponse> getRecommendedChallenges() {
        return challengeRepository.findAll().stream()
                .filter(c -> c.getGroupCode() == null) // 개인용 템플릿만 필터링
                .limit(5)
                .map(c -> PersonalRecommendResponse.builder()
                        .challengeId(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .imageUrl(c.getImageUrl())
                        .goalType("PERIOD")
                        .targetDays((int) ChronoUnit.DAYS.between(c.getStartDate(), c.getEndDate()))
                        .dailyGoal(c.getTargetValue())
                        .unit(c.getTargetUnit())
                        .category(c.getChallengeType())
                        .estimatedReward(c.getBaseRewardPoints())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 개인 챌린지 인증 (Check-in)
     */
    @Transactional
    public void checkIn(Long challengeId, Long userId) {
        UserChallenge uc = userChallengeRepository.findByUserIdAndChallengeId(userId, challengeId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        // 1. 하루 1회 중복 인증 방지
        if (challengeLogRepository.existsByUserChallengeAndLogDate(uc, LocalDate.now())) {
            throw new IllegalStateException("오늘은 이미 인증을 완료했습니다.");
        }

        User user = uc.getUser();
        Challenge challenge = uc.getChallenge();

        // 2. [매일 보상] 인증할 때마다 고정 포인트(10 XP) 즉시 지급
        user.addPoints(DAILY_CHECK_IN_REWARD);

        // 3. 인증 로그 기록
        challengeLogRepository.save(ChallengeLog.builder()
                .userChallenge(uc).logDate(LocalDate.now()).valueAchieved(BigDecimal.ONE).build());

        // 4. 진행률 및 상태 업데이트
        uc.updateCheckInProgress();

        // 5. [최종 보상] 챌린지가 방금 막 완료(COMPLETED)되었다면 전체 보상 지급
        if ("COMPLETED".equals(uc.getStatus())) {
            user.addPoints(challenge.getBaseRewardPoints());
        }
    }

    /**
     * 완료된 개인 챌린지 조회
     */
    public List<PersonalCompletedResponse> getCompletedPersonalList(Long userId) {
        // 1. 해당 유저의 모든 참여 정보 조회
        List<UserChallenge> myChallenges = userChallengeRepository.findAllByUserId(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return myChallenges.stream()
                .filter(uc -> uc.getChallenge().getGroupCode() == null && "COMPLETED".equals(uc.getStatus()))
                .map(uc -> {
                    Challenge challenge = uc.getChallenge();
                    return PersonalCompletedResponse.builder()
                            .challengeId(challenge.getId())
                            .challengeType("PERSONAL")
                            .title(challenge.getTitle())
                            .description(challenge.getDescription())
                            .imageUrl(challenge.getImageUrl()) // 이미지 반영
                            .completedAt(uc.getCompletedAt() != null ? uc.getCompletedAt().format(formatter) : "")
                            .finalSuccessRate(uc.getAchievementRate().intValue())
                            .acquiredPoints(challenge.getBaseRewardPoints() != null ? challenge.getBaseRewardPoints() : 500)
                            .status("SUCCESS")
                            .build();
                })
                .collect(Collectors.toList());
    }
}