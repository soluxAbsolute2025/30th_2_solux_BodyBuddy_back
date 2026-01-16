package com.solux.bodybubby.domain.mypage.dto;

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
    private boolean isWaterPublic;   // 수분 섭취량 공개 여부
    private boolean isWorkoutPublic; // 운동 기록 공개 여부
    private boolean isDietPublic;    // 식단 기록 공개 여부
    private boolean isSleepPublic;   // 수면 기록 공개 여부
}