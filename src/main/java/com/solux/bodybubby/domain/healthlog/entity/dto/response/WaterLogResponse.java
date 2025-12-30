package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class WaterLogResponse {
    private List<WaterLogResponseDTO> records; // 기록 리스트
    private boolean hasNext;                   // 다음 페이지 존재 여부
    private int totalMl;                       // 오늘 총 섭취량
}