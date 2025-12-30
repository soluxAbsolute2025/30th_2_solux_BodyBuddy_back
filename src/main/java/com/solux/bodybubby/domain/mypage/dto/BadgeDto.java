package com.solux.bodybubby.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BadgeDto {
    private String badgeName;
    private String badgeImageUrl;
    private String acquiredDate; // "2025.01.20" 형식
}