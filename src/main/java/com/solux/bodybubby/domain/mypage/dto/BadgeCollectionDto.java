package com.solux.bodybubby.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BadgeCollectionDto {
    private Integer totalBadgeCount;      // 전체 뱃지 개수
    private Integer acquiredBadgeCount;   // 내가 획득한 뱃지 개수
    private List<BadgeItemDto> badges;    // 뱃지 리스트

    @Getter
    @Builder
    public static class BadgeItemDto {
        private Long badgeId;
        private String name;
        private String description;
        private String imageUrl;
        private boolean isAcquired;       // 획득 여부 (중요!)
        private String acquiredDate;      // 획득일 (미획득 시 null)
    }
}