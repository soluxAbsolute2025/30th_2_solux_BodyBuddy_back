package com.solux.bodybubby.domain.healthlog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "medication_log")
public class MedicationLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preset_id")
    private MedicationPreset preset;

    private LocalDate intakeDate;    // 날짜 (2025-12-23)
    
    private LocalTime intakeTime;    // ✅ 추가됨: 시간 (08:30)

    @Enumerated(EnumType.STRING)
    private IntakeSlot intakeSlot;   // 아침/점심/저녁
    
    private boolean isTaken;

    public void cancelIntake() {
        this.isTaken = false;
    }
}