package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SleepLogUpdateRequest {
    private Long sleepRecordId;  // 수정할 ID
    private String bedTime;      // "23:00"
    private String wakeTime;     // "06:30"
    private String sleepQuality; // "VERY_GOOD"
}