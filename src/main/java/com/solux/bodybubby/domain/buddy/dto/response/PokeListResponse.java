package com.solux.bodybubby.domain.buddy.dto.response;

import java.util.List;

public record PokeListResponse(
        List<PokeInfo> pokes
) {
    public record PokeInfo(
            Long pokeId,
            Long senderId,
            String senderNickname,
            String pokedAt // LocalDateTime을 String으로 변환하거나 직접 사용
    ) {}
}