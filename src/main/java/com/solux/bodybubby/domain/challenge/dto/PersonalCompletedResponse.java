package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalCompletedResponse {
    private Long challengeId;
    private String challengeType;     // "PERSONAL"
    private String title;
    private String description;
    private String imageUrl;          // 이미지 URL
    private String completedAt;       // "2025.12.30" 형식
    private Integer finalSuccessRate; // 개인 최종 달성률
    private Integer acquiredPoints;   // 성공 보상 포인트
    private String status;            // "SUCCESS"
}