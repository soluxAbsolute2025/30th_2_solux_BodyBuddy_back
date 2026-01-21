package com.solux.bodybubby.domain.buddy.dto.response;

public record BuddyDetailResponse(
        Long userId,
        String loginId,       // 아이디로 검색했을 때 확인용
        String nickname,
        Integer level,
        String profileImageUrl,
        String introduction,   // 상세 정보 포함
        Integer completedChallenges,
        String status         // "FRIEND", "PENDING", "NONE"
) {}