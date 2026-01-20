package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "sleep_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SleepLog extends HealthLog {

    // 1. 필요한 칼럼들 선언
    @Column(name = "sleep_time")
    private LocalDateTime sleepTime; // 잠든 시간

    @Column(name = "wake_time")
    private LocalDateTime wakeTime;  // 일어난 시간

    @Column(name = "total_minutes")
    private Integer totalMinutes;    // 총 수면 시간(분)

    @Column(name = "sleep_quality")
    private String sleepQuality;     // 수면 질

    @Column(name = "date")
    private LocalDate date;          // 조회용 날짜

    // ▼▼▼ [해결 1] Service의 new SleepLog(...) 에러 해결 ▼▼▼
    // 순서: User, loggedAt, sleepTime, wakeTime, totalMinutes, sleepQuality
    @Builder
    public SleepLog(User user, LocalDateTime loggedAt, 
                    LocalDateTime sleepTime, LocalDateTime wakeTime, 
                    Integer totalMinutes, String sleepQuality) {
        super(user, loggedAt); // 부모(HealthLog)에 User와 기록시간 전달
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.totalMinutes = totalMinutes;
        this.sleepQuality = sleepQuality;
        
        // loggedAt을 기준으로 날짜(date) 자동 설정
        this.date = loggedAt.toLocalDate(); 
    }

    // ▼▼▼ [해결 2] Service의 log.update(...) 에러 해결 ▼▼▼
    // 순서: sleepTime, wakeTime, totalMinutes, sleepQuality
    public void update(LocalDateTime sleepTime, LocalDateTime wakeTime, 
                       Integer totalMinutes, String sleepQuality) {
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.totalMinutes = totalMinutes;
        this.sleepQuality = sleepQuality;
        // (필요하다면 여기서 this.date = sleepTime.toLocalDate(); 로 날짜도 갱신 가능)
    }
}