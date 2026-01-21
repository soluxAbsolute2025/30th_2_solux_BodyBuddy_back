package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class OngoingGroupListResponse {
    private Long challengeId;
    private String title;                 // 챌린지명
    private String imageUrl;              // 배경 이미지
    private Integer myRank;               // 카드 우측 상단 '2위' 배지
    private Integer participantCount;      // "4명 참여중" 문구
    private Integer remainingDays;         // "7일 남음" 문구
    private List<ParticipantProfile> topParticipants; // 프로필 이미지 아이콘 리스트

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ParticipantProfile {
        private String profileImageUrl;   // 참여자 프로필 이미지 URL
    }
}
