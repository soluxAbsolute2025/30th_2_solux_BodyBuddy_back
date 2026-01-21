package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import com.solux.bodybubby.domain.healthlog.entity.SleepLog;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class SleepLogResponse {

    private Long sleepRecordId;
    private String sleepDate;
    private String bedTime;
    private String wakeTime;
    private String sleepQuality;
    private int totalMinutes;

    public SleepLogResponse(SleepLog log) {
        this.sleepRecordId = log.getId();
        
        // 날짜 포맷팅
        this.sleepDate = log.getLoggedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 시간 포맷팅
        this.bedTime = log.getSleepTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        this.wakeTime = log.getWakeTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        // ▼▼▼ [수정] getQuality() -> getSleepQuality() 로 변경! ▼▼▼
        this.sleepQuality = log.getSleepQuality(); 
        
        this.totalMinutes = log.getTotalMinutes() != null ? log.getTotalMinutes() : 0;
    }
}