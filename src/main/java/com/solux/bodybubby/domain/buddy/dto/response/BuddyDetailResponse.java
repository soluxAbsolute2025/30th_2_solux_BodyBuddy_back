package com.solux.bodybubby.domain.buddy.dto.response;

import com.solux.bodybubby.domain.home.dto.response.HomeResponseDTO;

public record BuddyDetailResponse(
        // 1. 기본 프로필 정보
        Long userId,
        String loginId,
        String nickname,
        Integer level,
        String profileImageUrl,
        String lastActivityTime,

        // 2. 친구 관계 상태
        String status,

        // 3. 목표 달성 정보 (객체로 분리)
        HomeResponseDTO homeData
) {}
