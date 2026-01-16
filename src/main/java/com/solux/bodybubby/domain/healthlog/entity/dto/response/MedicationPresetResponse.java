package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;

public record MedicationPresetResponse(
        Long presetId,        // PK
        String medicineName,  // 약 이름
        String timing,        // 복용 시점 (식후, 식전 등)
        boolean takeMorning,  // 아침 복용 여부
        boolean takeLunch,    // 점심 복용 여부
        boolean takeDinner    // 저녁 복용 여부
) {
    public static MedicationPresetResponse from(MedicationPreset entity) {
        return new MedicationPresetResponse(
                entity.getId(),
                entity.getName(),
                entity.getIntakeTiming(), // 기존 getDose() 등은 삭제됨 -> getIntakeTiming() 사용
                entity.isTakeMorning(),   // boolean 값 (Lombok의 getter는 is... 로 시작)
                entity.isTakeLunch(),
                entity.isTakeDinner()
        );
    }
}