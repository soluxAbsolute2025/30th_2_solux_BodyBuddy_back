package com.solux.bodybubby.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PersonalDetailResponse {
    private Long challengeId;
    private String title;
    private String imageUrl;
    private String description;
    private String category; // DAILY or WEEKLY

    // 목표 및 단위 관련
    private Integer targetDays;
    private Integer dailyGoal;
    private String unit;

    // 달성률 및 보상
    private Integer estimatedReward;
    private Integer rewardRate;
    private Integer myAchievementRate; // 달성률
}