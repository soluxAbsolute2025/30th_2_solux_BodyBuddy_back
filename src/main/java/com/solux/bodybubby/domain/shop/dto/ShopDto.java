package com.solux.bodybubby.domain.shop.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class ShopDto {

    @Getter @Builder
    public static class ShopResponse {
        private List<StandardBadgeDto> standardBadges;
        private List<PremiumBadgeDto> premiumBadges;
    }

    @Getter @Builder
    public static class StandardBadgeDto {
        private Long id;
        private String name;
        private String unlockCondition;
        private boolean unlocked;
        private String iconUrl;
    }

    @Getter @Builder
    public static class PremiumBadgeDto {
        private Long id;
        private String name;
        private int price;
        private boolean purchased;
        private String iconUrl;
    }

    @Getter @Builder
    public static class MyBadgeResponse {
        private Long badgeId;
        private String name;
        private String description;
        private String iconUrl;
        private boolean isAcquired;
        private LocalDateTime acquiredAt;
    }

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class PurchaseRequest {
        private Long itemId;
    }

    @Getter @Builder
    public static class RewardStatsResponse {
        private int currentPoints;
        private int earnedPoints;
        private int usedPoints;
        private int rewardCount;
    }
}