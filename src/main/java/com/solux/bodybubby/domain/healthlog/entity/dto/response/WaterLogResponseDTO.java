package com.solux.bodybubby.domain.healthlog.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WaterLogResponseDTO {
    private Long waterLogId;
    private Integer amountMl;
    private LocalDateTime loggedAt;
}