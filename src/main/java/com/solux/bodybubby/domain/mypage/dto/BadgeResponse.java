package com.solux.bodybubby.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BadgeResponse {
    private Long badgeId;
    private String name;
    private String description;
    private String iconUrl;
    private boolean isAcquired;      // 획득 여부
    private LocalDateTime acquiredAt; // 획득일
}