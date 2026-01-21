package com.solux.bodybubby.domain.challenge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateGroupRequest {
    private String title;          // "함께 만보 걷기"
    private String description;    // "30일간 매일 10,000보 걷기"
    private Integer period;        // 기간(일 단위): 30
    private Integer maxParticipants; // 최대 인원: 8
    private String privacyScope;   // 공개 범위: "FRIENDS"

    // 추가 정보 (엔티티 구성을 위해 필요한 값들)
    private String challengeType;  // WALK, WATER 등
    private String targetType;     // DAILY, TOTAL
    private BigDecimal targetValue; // 10000
    private String targetUnit;     // "보"
}