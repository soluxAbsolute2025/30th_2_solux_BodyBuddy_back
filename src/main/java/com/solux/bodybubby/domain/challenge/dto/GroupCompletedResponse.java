package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCompletedResponse {
    private Long challengeId;
    private String challengeType;     // "GROUP"
    private String title;
    private String description;
    private String imageUrl;
    private String completedAt;       // "2025.12.30"
    private Integer finalSuccessRate; // 내 최종 달성률
    private Integer acquiredPoints;   // 획득 포인트 (예: 500)
    private String status;            // "SUCCESS"
}