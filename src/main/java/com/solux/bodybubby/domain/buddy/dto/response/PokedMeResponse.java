package com.solux.bodybubby.domain.buddy.dto.response;

import java.time.LocalDateTime;

public record PokedMeResponse(
        Long userId,
        String nickname,
        String profileImageUrl,
        Integer level,
        LocalDateTime pokedAt,
        boolean isAlreadyPoked // 내가 상대방을 맞찔렀는지 여부
) {}