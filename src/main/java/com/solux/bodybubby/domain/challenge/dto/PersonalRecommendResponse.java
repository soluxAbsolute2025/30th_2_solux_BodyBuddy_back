package com.solux.bodybubby.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PersonalRecommendResponse {
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl;

    // 생성을 위해 추가된 상세 정보들
    private String goalType;       // PERIOD 또는 COUNT
    private Integer targetDays;    // 추천 목표 일수
    private BigDecimal dailyGoal;  // 추천 일일 목표 수치
    private String unit;           // 단위 (minutes, steps 등)
    private String category;       // DAILY 또는 WEEKLY
    private Integer estimatedReward; // 추천 보상 포인트
}