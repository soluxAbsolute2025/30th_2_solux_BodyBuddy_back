package com.solux.bodybubby.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {
    private BigDecimal currentValue; // 오늘 수행한 수치 (예: 10000)
}