package com.solux.bodybubby.domain.mypage.service;

import com.solux.bodybubby.domain.mypage.dto.MyPageResponseDto;
import com.solux.bodybubby.domain.mypage.dto.PrivacySettingsDto;
import com.solux.bodybubby.domain.mypage.entity.LevelTier;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;

    /**
     * 마이페이지 메인 정보 조회 로직
     */
    public MyPageResponseDto getMyPageInfo(Long userId) {
        // 1. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 현재 포인트에 맞는 등급(Tier) 계산
        int currentExp = user.getCurrentExp();
        LevelTier currentTier = LevelTier.getTier(currentExp);

        // 3. DTO 조립 및 반환
        return MyPageResponseDto.builder()
                .userProfile(MyPageResponseDto.UserProfileDto.builder()
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImageUrl())
                        .introduction(user.getIntroduction())
                        .build())
                .levelInfo(buildLevelInfo(currentExp, currentTier)) // 레벨 계산 로직 분리
                .activitySummary(MyPageResponseDto.ActivitySummaryDto.builder()
                        .completedChallenges(user.getCompletedChallengesCount())
                        .consecutiveAttendance(user.getConsecutiveAttendance())
                        .build())
                .recentBadges(new ArrayList<>()) // 뱃지 기능 연동 전까지 빈 리스트 처리
                .build();
    }

    /**
     * 등급 정보를 바탕으로 상세 레벨 데이터 계산
     */
    private MyPageResponseDto.LevelInfoDto buildLevelInfo(int exp, LevelTier tier) {
        int nextLevelExp = tier.getMaxPoint() + 1; // 다음 등급 시작 점수
        int remainingExp = (tier == LevelTier.MASTER) ? 0 : nextLevelExp - exp; // 마스터 등급은 0 처리

        return MyPageResponseDto.LevelInfoDto.builder()
                .currentLevel(tier.ordinal() + 1) // Enum 순서를 레벨 숫자로 활용 (1~6)
                .levelName(tier.getRankName())    // 등급 명칭
                .currentExp(exp)
                .nextLevelExp((tier == LevelTier.MASTER) ? exp : nextLevelExp)
                .remainingExp(remainingExp)
                .build();
    }

    /**
     * [공개 범위 설정 조회] GET /api/mypage/privacy
     */
    public PrivacySettingsDto getPrivacySettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return PrivacySettingsDto.builder()
                .isWaterPublic(user.isWaterPublic())
                .isWorkoutPublic(user.isWorkoutPublic())
                .isDietPublic(user.isDietPublic())
                .isSleepPublic(user.isSleepPublic())
                .build();
    }

    /**
     * [공개 범위 설정 수정] PATCH /api/mypage/privacy
     */
    @Transactional
    public void updatePrivacySettings(Long userId, PrivacySettingsDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티의 비즈니스 메서드 호출 (Dirty Checking으로 자동 업데이트)
        user.updatePrivacySettings(
                dto.isWaterPublic(),
                dto.isWorkoutPublic(),
                dto.isDietPublic(),
                dto.isSleepPublic()
        );
    }
}
