package com.solux.bodybubby.domain.mypage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

//수정용
public class GoalRequest {
    private Integer waterGoal;
    private Integer mealGoal;
    private Integer medicineGoal;
}
