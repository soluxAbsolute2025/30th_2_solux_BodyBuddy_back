package com.solux.bodybubby.domain.buddy.dto.response;

import java.util.List;

public record BuddyListResponse(
        List<BuddyInfo> myBuddies,
        List<BuddyRequestInfo> requests
) {
    public record BuddyInfo(
            Long userId,
            String nickname,
            Integer level,
            String profileImageUrl,
            String lastActiveTime,
            boolean isPokedToday
    ) {}

    public record BuddyRequestInfo(
            Long requestId,        // 요청 승인/거절 시 필요한 ID
            Long userId,           // 요청한 사람의 유저 ID
            String nickname,
            String profileImageUrl,
            Integer level,
            String lastActiveTime
    ) {}
}