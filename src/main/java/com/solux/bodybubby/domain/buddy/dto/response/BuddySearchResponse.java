package com.solux.bodybubby.domain.buddy.dto.response;

public record BuddySearchResponse(
        Long userId,
        String nickname,
        Integer level,
        String profileImageUrl,
        String status // "NONE", "PENDING", "FRIEND"
) {}
