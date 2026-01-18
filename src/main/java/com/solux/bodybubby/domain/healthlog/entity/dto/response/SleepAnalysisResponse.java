package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SleepAnalysisResponse {
    private double averageSleepHours;        // 평균 수면 시간 (예: 6.8)
    private String sleepQuality;             // 수면 품질 (좋음/보통/나쁨)
    
    // ✅ 추가됨: 그래프를 그리기 위한 하루하루의 기록 리스트
    private List<SleepLogResponse> dailyLogs; 
}