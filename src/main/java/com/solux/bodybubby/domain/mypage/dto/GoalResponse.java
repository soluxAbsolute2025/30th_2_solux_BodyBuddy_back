package com.solux.bodybubby.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 조회용
public class GoalResponse {
    private Integer waterGoal;    // 수분 섭취 목표 (ml)
    private Integer mealGoal;     // 식단 관리 목표 (회)
    private Integer medicineGoal; // 약/영양제 복용 목표 (회)
}
