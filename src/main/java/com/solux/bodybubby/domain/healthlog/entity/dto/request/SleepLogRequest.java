package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SleepLogRequest {
    private String sleepDate;    // "2025-12-23"
    private String bedTime;      // "23:30"
    private String wakeTime;     // "07:00"
    private String sleepQuality; // "GOOD" (사용자가 입력한 주관적 품질)
}