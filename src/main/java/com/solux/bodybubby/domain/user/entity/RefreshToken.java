package com.solux.bodybubby.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 604800) // 7일 후 자동 삭제
public class RefreshToken {

    @Id
    private Long userId; // 유저 ID를 키로 사용

    @Indexed // 토큰으로 조회할 수 있도록 인덱스 추가
    private String token;
}
