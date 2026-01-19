package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class MealLogRequest {
    private String mealType;
    private String intakeDate;
    private String intakeTime;
    private List<String> foods;
    private String memo;
}