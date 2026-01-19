package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sleep_log")
public class SleepLog extends HealthLog {

    @Column(name = "sleep_time", nullable = false)
    private LocalDateTime sleepTime;

    @Column(name = "wake_time", nullable = false)
    private LocalDateTime wakeTime;

    @Column(name = "total_minutes")
    private Integer totalMinutes;

    @Column(name = "quality")
    private String quality;

    // 생성자
    public SleepLog(User user, LocalDateTime loggedAt, LocalDateTime sleepTime, LocalDateTime wakeTime, Integer totalMinutes, String quality) {
        super(user, loggedAt);
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.totalMinutes = totalMinutes;
        this.quality = quality;
    }

    // ✅ 이 메서드가 있어야 Service에서 update 에러가 안 납니다!
    public void update(LocalDateTime sleepTime, LocalDateTime wakeTime, Integer totalMinutes, String quality) {
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.totalMinutes = totalMinutes;
        this.quality = quality;
    }
}