package com.solux.bodybubby.domain.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupCreateRequest {
    @Schema(description = "챌린지 제목", example = "함께 만보 걷기")
    private String title;          // "함께 만보 걷기"
    @Schema(description = "챌린지 상세 설명", example = "30일간 매일 10,000보 걷기")
    private String description;    // "30일간 매일 10,000보 걷기"
    @Schema(description = "목표 기간 (최소 7일)", example = "30")
    private Integer period;        // 기간(일 단위): 30
    @Schema(description = "최대 참여 인원", example = "8")
    private Integer maxParticipants; // 최대 인원: 8
    @Schema(description = "공개 범위 (PUBLIC, SECRET)", example = "SECRET")
    private String visibility;   // 공개 범위: "SECRET"
}