package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MealLogResponse {
    private Long id;
    private String mealType;
    private String memo; // 우리가 음식과 합쳐놓은 메모
    private String photoUrl;
    private String loggedAt; // 시간 표시용

    public static MealLogResponse from(MealLog mealLog) {
        return MealLogResponse.builder()
                .id(mealLog.getId())
                .mealType(mealLog.getMealType())
                .memo(mealLog.getMemo())
                .photoUrl(mealLog.getPhotoUrl())
                .loggedAt(mealLog.getIntakeDate().toString())
                .build();
    }
}