package com.solux.bodybubby.domain.challenge.dto;

import com.solux.bodybubby.domain.challenge.entity.Visibility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PersonalCreateRequest {
    private String title;          // "30일 걷기 챌린지"
    private String description;    // "매일 10,000보 걷기"
    private String goalType;       // "PERIOD"(기간형), "COUNT"(횟수형)
    private Integer targetDays;    // 전체 목표 일수
    private BigDecimal dailyGoal;  // 일일 목표 수치
    private String unit;           // "steps", "count", "ml", "minutes"
    private String category;       // "DAILY", "WEEKLY"
    private Visibility visibility;
    private Integer expectedReward; // 예상 보상 포인트
}