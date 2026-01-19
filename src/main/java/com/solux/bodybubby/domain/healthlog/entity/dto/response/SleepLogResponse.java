package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import com.solux.bodybubby.domain.healthlog.entity.SleepLog;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class SleepLogResponse {

    private Long sleepRecordId;  // 수정/삭제할 때 필요한 ID (예: 8001)
    private String sleepDate;    // "2025-12-23"
    private String bedTime;      // "23:30"
    private String wakeTime;     // "07:00"
    private String sleepQuality; // "GOOD"
    private int totalMinutes;    // 총 수면 시간 (분 단위, 예: 450)

    public SleepLogResponse(SleepLog log) {
        this.sleepRecordId = log.getId();
        
        // 날짜 포맷팅 (LocalDateTime -> "yyyy-MM-dd")
        this.sleepDate = log.getLoggedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 시간 포맷팅 (LocalDateTime -> "HH:mm")
        this.bedTime = log.getSleepTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.wakeTime = log.getWakeTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        this.sleepQuality = log.getQuality();
        this.totalMinutes = log.getTotalMinutes() != null ? log.getTotalMinutes() : 0;
    }
}