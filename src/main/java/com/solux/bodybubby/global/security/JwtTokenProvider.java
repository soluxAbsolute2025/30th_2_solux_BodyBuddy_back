package com.solux.bodybubby.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:verysecretkey12345678901234567890}")
    private String secretKeyString;

    private SecretKey secretKey;
    private long tokenValidTime = 3600000L; // 1시간

    @PostConstruct
    protected void init() {
        // 문자열 키를 바이트 배열로 변환하여 SecretKey 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String createToken(Long userId, String loginId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidTime);

        return Jwts.builder()
                .subject(loginId)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey) // 알고리즘을 직접 지정하지 않아도 키 크기에 따라 자동 설정됩니다.
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 사용자 아이디 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰에서 유저 ID 추출
    public Long getUserId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    // 토큰의 남은 유효 시간(밀리초) 계산
    public long getExpiration(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.getTime() - new Date().getTime();
    }
}