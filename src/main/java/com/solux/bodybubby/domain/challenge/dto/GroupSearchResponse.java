package com.solux.bodybubby.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 새로운 그룹 조회 응답
 */

@Getter
@Builder
@AllArgsConstructor
public class GroupSearchResponse {
    private Long challengeId;
    private String title;
    private String description;
    private Integer currentParticipants;
    private Integer maxParticipants;
    private String challengeType;
}