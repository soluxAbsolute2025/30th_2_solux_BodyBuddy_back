package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
        // 1. 물 양 수정
        if (amountMl != null) {
            this.amountMl = amountMl;
        }

        // 2. 날짜 수정 (날짜가 들어오면, 시간은 기존 시간을 유지함)
        if (recordDate != null) {
            // 기존 시간이 있으면 그 시간을 쓰고, 없으면 현재 시간을 씀
            LocalTime timePart = (this.loggedAt != null) 
                    ? this.loggedAt.toLocalTime() 
                    : LocalTime.now();
            
            // 입력받은 날짜 + 기존 시간 합치기
            this.loggedAt = recordDate.atTime(timePart);
        }
    }

}