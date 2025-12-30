package com.solux.bodybubby.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageMainResponse {
    private String nickname;
    private String statusMessage;
    private String profileImageUrl;

    // 레벨 섹션
    private String rankName;     // 예: "챌린저 버디"
    private int currentLevel;    // Lv.15
    private int cumulativePoints;
    private int nextLevelPoints; // 다음 등급까지의 기준점 (예: 3000)

    // 통계 섹션
    private int completedChallenges;
    private int consecutiveAttendance;

    // 최근 뱃지 (최대 4개)
    private List<BadgeDto> recentBadges;
}
