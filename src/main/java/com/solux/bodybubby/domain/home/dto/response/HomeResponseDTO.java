package com.solux.bodybubby.domain.home.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeResponseDTO {

    private String date;      // "2025-09-25"
    private WaterInfo water;  // 수분 정보 묶음
    private MealInfo meal;    // 식단 정보 묶음
    private SleepInfo sleep;  // 수면 정보 묶음

    @Getter
    @Builder
    public static class WaterInfo {
        private int current;
        private int goal;
        private int percent;
    }

    @Getter
    @Builder
    public static class MealInfo {
        private int current;
        private int goal;
        private int percent;
        // 끼니 섭취 여부도 원하시면 여기에 추가
        private boolean isBreakfastEaten;
        private boolean isLunchEaten;
        private boolean isDinnerEaten;
    }

    @Getter
    @Builder
    public static class SleepInfo {
        private int current;
        private int goal;
    }
}