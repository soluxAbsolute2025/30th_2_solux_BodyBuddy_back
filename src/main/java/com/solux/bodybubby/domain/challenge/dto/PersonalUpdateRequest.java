package com.solux.bodybubby.domain.challenge.dto;

import com.solux.bodybubby.domain.challenge.entity.Visibility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PersonalUpdateRequest {
    private String title;
    private String description;
    private String goalType;
    private Integer targetDays;
    private BigDecimal dailyGoal;
    private String unit;
    private String category;
    private Visibility visibility;
    private boolean isImageDeleted; // 이미지 삭제 여부
}