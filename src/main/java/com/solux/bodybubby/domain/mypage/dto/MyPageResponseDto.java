package com.solux.bodybubby.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 마이페이지 메인 조회 (GET /api/mypage) 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {

    private UserProfileDto userProfile;    // 상단 프로필 섹션
    private LevelInfoDto levelInfo;        // 레벨 및 경험치 섹션
    private ActivitySummaryDto activitySummary; // 핵심 성과(활동 요약) 섹션
    private List<RecentBadgeDto> recentBadges; // 최근 획득 뱃지 리스트 (최대 3개)

    /**
     * 상단 유저 프로필 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDto {
        private String nickname;         // 닉네임 (ex: 김바디)
        private String profileImageUrl;  // 프로필 이미지 URL
        private String introduction;     // 한 줄 소개 (ex: 건강한 라이프 스타일 실천 중)
    }

    /**
     * 레벨 및 경험치 상세 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelInfoDto {
        private Integer currentLevel;    // 현재 레벨 숫자 (ex: 15)
        private String levelName;        // 레벨 등급명 (ex: 챌린저 버디)
        private String levelImageUrl;    // 등급 이미지 URL
        private Integer currentExp;      // 현재 보유 경험치 (ex: 2450)
        private Integer nextLevelExp;    // 다음 레벨 달성 기준 경험치 (ex: 3000)
        private Integer remainingExp;    // 다음 레벨까지 남은 경험치 (ex: 550)
    }

    /**
     * 핵심 활동 요약 (성과 카드)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivitySummaryDto {
        private Integer completedChallenges;   // 완료한 챌린지 수 (ex: 12)
        private Integer consecutiveAttendance; // 연속 출석 일수 (ex: 47)
    }

    /**
     * 최근 획득 뱃지 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentBadgeDto {
        private Long badgeId;            // 뱃지 고유 ID
        private String badgeName;        // 뱃지 이름 (ex: 30일 챌린지)
        private String badgeImageUrl;    // 뱃지 이미지 아이콘 URL
        private String acquiredDate;     // 획득 일자
    }
}