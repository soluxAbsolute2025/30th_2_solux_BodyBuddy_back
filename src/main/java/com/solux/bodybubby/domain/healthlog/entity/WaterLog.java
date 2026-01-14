package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 1. 외부에서 빈 객체 생성 차단
public class WaterLog extends HealthLog {

    @Column(name = "amount_ml", nullable = false)
    private Integer amountMl;

    // 2. 빌더 패턴 적용 (부모 필드까지 포함하여 생성)
    @Builder
    public WaterLog(User user, LocalDateTime loggedAt, Integer amountMl) {
        super(user, loggedAt); // HealthLog(부모)의 필드 초기화
        this.amountMl = amountMl;
    }

    public void update(Integer amountMl, LocalDate recordDate) {
    if (amountMl == null || amountMl < 0) {
        throw new IllegalArgumentException("섭취량은 0 이상이어야 합니다.");
    }
    this.amountMl = amountMl;

    if (recordDate != null) {
        // loggedAt이 null인 경우 방어
        LocalDateTime baseTime =
                (this.loggedAt != null)
                ? this.loggedAt
                : LocalDateTime.now();

        this.loggedAt = recordDate.atTime(baseTime.toLocalTime());
    }
}



    
}