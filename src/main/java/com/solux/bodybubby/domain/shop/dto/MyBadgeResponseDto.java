package com.solux.bodybubby.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyBadgeResponseDto {
    private int totalBadgeCount;      // 내가 가진 뱃지 총 개수
    private int acquiredBadgeCount;   // (혹시 몰라 2개로 나눔, 보통 위와 같음)
    private List<BadgeDto> badges;    // 뱃지 목록 리스트
}