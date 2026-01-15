package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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
    
    private String intakeTiming;
    
    private boolean takeMorning;
    private boolean takeLunch;
    private boolean takeDinner;

    // 수정 편의 메서드 (서비스에서 호출)
    public void update(String name, String intakeTiming, boolean takeMorning, boolean takeLunch, boolean takeDinner) {
        this.name = name;
        this.intakeTiming = intakeTiming;
        this.takeMorning = takeMorning;
        this.takeLunch = takeLunch;
        this.takeDinner = takeDinner;
    }
}