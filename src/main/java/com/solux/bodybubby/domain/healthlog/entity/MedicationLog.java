package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // Builder를 쓰려면 이게 필수입니다!
@EntityListeners(AuditingEntityListener.class)
@Table(name = "medication_log")
public class MedicationLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 성능 최적화
    @JoinColumn(name = "preset_id")
    private MedicationPreset preset; // 어떤 약을 먹었는지

    private LocalDate intakeDate;    // 언제? (2026-01-14)
    
    // ✅ 아침/점심/저녁 중 언제 먹은 건지 구분
    @Enumerated(EnumType.STRING)
    private IntakeSlot intakeSlot;   // MORNING, LUNCH, DINNER
    
    private boolean isTaken;         // 복용 여부 (true)

    public void cancelIntake() {
        this.isTaken = false;
    }
}