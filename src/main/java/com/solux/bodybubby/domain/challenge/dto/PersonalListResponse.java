package com.solux.bodybubby.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PersonalListResponse {
    private Summary summary;
    private List<OngoingPersonalChallenge> ongoingChallenges;

    @Getter
    @Builder
    public static class Summary {
        private Integer acquiredPoints; // 획득한 포인트
        private Integer myAchievementRate;    // 성공률 (%)
    }

    @Getter
    @Builder
    public static class OngoingPersonalChallenge {
        private Long challengeId;
        private String title;
        private String description;
        private String imageUrl;
        private String category;      // DAILY(초록), WEEKLY(파랑)
        private Integer progressValue;
        private Integer totalValue;
        private Integer estimatedReward;
        private Integer dday;
        private String colorCode;
    }
}