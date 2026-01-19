package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MedicationLogResponse {

    private Long id;              // 로그 ID (예: 6)
    private Long medicationId;    // 약 ID (예: 4)
    private String medicationName; // ✅ 추가됨! (예: "종합비타민")
    private LocalDate date;       // 날짜
    private IntakeSlot slot;      // 시간대 (MORNING)
    private boolean taken;        // 복용 여부

    public MedicationLogResponse(MedicationLog log) {
        this.id = log.getId();
        this.medicationId = log.getPreset().getId();
        this.medicationName = log.getPreset().getName(); // ✅ 여기서 이름을 꺼내옵니다!
        this.date = log.getIntakeDate();
        this.slot = log.getIntakeSlot();
        this.taken = log.isTaken();
    }
}