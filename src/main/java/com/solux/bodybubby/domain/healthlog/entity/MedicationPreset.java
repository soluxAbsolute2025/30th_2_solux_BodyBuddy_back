package com.solux.bodybubby.domain.healthlog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "medication_preset")
public class MedicationPreset {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    private String intakeTiming; // 복용 타이밍 (식후 30분 등)

    private boolean takeMorning;
    private boolean takeLunch;
    private boolean takeDinner;

    // 수정 편의 메서드
    public void update(String name, String intakeTiming, boolean takeMorning, boolean takeLunch, boolean takeDinner) {
        this.name = name;
        this.intakeTiming = intakeTiming;
        this.takeMorning = takeMorning;
        this.takeLunch = takeLunch;
        this.takeDinner = takeDinner;
    }
}