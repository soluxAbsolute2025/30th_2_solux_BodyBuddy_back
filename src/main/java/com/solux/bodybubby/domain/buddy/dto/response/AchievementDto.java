package com.solux.bodybubby.domain.buddy.dto.response;

public record AchievementDto(
        Double workoutRate, // 운동 달성률
        Double waterRate,   // 수분 섭취율
        Double mealRate,    // 식사 기록률
        Double sleepRate,   // 수면 달성률
        Double totalRate    // 전체 평균 달성률
) {}