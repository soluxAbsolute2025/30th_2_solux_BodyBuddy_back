package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
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

    protected SleepLog() {
    }

    public SleepLog(
            User user,
            LocalDateTime loggedAt,
            LocalDateTime sleepTime,
            LocalDateTime wakeTime,
            Integer totalMinutes,
            String quality
    ) {
        super(user, loggedAt);
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.totalMinutes = totalMinutes;
        this.quality = quality;
    }
}