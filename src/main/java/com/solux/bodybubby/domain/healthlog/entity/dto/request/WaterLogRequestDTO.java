package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WaterLogRequestDTO {
    private Long recordId;      // 수정/삭제 시 필요
    private LocalDate recordDate;
    private String unit;        // "GLASS" 등
    private Integer amount;     // 1잔
    private Integer mlAmount;   // 250ml
    private String actionType;  // "CREATE", "UPDATE", "DELETE"

}