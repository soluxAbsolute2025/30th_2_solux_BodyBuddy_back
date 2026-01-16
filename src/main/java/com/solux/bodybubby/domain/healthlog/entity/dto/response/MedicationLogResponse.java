package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class MedicationLogResponse {
    private Long id;
    private Long medicationId; // 약 ID (Preset ID)
    private boolean isTaken;
    private LocalDate date;
    private IntakeSlot slot;

    // 엔티티를 받아서 DTO로 변환하는 생성자
    public MedicationLogResponse(MedicationLog log) {
        this.id = log.getId();
        this.medicationId = log.getPreset().getId(); // 여기서 Proxy가 벗겨짐!
        this.isTaken = log.isTaken(); 
        this.date = log.getIntakeDate();
        this.slot = log.getIntakeSlot();
    }
}