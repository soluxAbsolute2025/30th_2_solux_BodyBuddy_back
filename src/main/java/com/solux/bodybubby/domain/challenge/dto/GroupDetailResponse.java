package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class GroupDetailResponse {
    private ChallengeInfo challengeInfo;    // 챌린지 기본 정보
    private MyStatus myStatus;              // 내 현재 상태
    private BigDecimal groupAverageRate;    // 그룹 전체 평균 진행률
    private List<ParticipantDetail> participants; // 상세 순위 리스트

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChallengeInfo {
        private String title;               // "친구들과 다이어트"
        private String description;         // 챌린지 내용
        private String startDate;           // "2025. 11. 27"
        private String endDate;             // "2025. 12. 15"
        private String groupCode;           // "DIET2025"
        private Integer currentParticipantCount; // 4명
        private Integer maxParticipantCount;     // 8명
        private boolean isPublic;           // 친구 공개 여부
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyStatus {
        private BigDecimal myAchievementRate; // 내 달성률 (%)
        private Integer myRank;               // 내 현재 순위
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ParticipantDetail {
        private Integer rank;               // 순위
        private String nickname;
        private String profileImageUrl;
        private BigDecimal achievementRate; // 100%
        private boolean isMe;               // '나'인지 여부
    }
}
