package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCheckInResponse {
    private Long challengeId;
    private String nickname;
    private String title;                 // "매일 10,000보 걷기"
    private Integer earnedPoints;         // "10 포인트를 획득하였습니다"
    private MyStatusUpdate myStatus;
    private BigDecimal groupAverageRate;  // 갱신된 그룹 평균
    private String checkInTime;         // 인증 완료 시각

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyStatusUpdate {
        private BigDecimal updatedAchievementRate; // 갱신된 내 달성률
        private Integer currentRank;               // 갱신된 내 순위
    }
}