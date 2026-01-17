package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor // JSON 파싱을 위해 기본 생성자 필수
public class MealLogRequest {

    private String mealType;        // BREAKFAST, LUNCH, DINNER, SNACK
    private String intakeDate;      // "2025-12-23"
    private String intakeTime;      // "08:30"
    private List<String> foods;     // ["김치찌개", "현미밥"]
    private String imageUrl;        // 사진 경로
    private String memo;            // 추가 메모
}