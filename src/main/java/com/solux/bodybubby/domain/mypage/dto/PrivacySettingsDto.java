package com.solux.bodybubby.domain.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공개 범위 설정 조회 및 수정 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsDto {
    @JsonProperty("waterPublic")
    private boolean isWaterPublic;   // 수분 섭취량 공개 여부

    @JsonProperty("workoutPublic")
    private boolean isWorkoutPublic; // 운동 기록 공개 여부

    @JsonProperty("dietPublic")
    private boolean isDietPublic;    // 식단 기록 공개 여부

    @JsonProperty("sleepPublic")
    private boolean isSleepPublic;   // 수면 기록 공개 여부
}