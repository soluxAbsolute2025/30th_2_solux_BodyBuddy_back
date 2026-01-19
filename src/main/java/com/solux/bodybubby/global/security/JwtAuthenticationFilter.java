package com.solux.bodybubby.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) request);

        if (token != null) {
            // 1. 먼저 토큰 자체의 유효성(만료, 변조 등)을 검사
            if (jwtTokenProvider.validateToken(token)) {
                // 2. 유효하다면 블랙리스트(로그아웃 여부) 확인
                String isLogout = redisTemplate.opsForValue().get(token);
                if (isLogout == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUsername(token));
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()));
                } else {
                    // 로그아웃된 토큰인 경우
                    request.setAttribute("exception", "INVALID_TOKEN");
                }
            } else {
                // 3. 토큰 자체가 유효하지 않은 경우 (이 부분이 누락되어 있었습니다)
                request.setAttribute("exception", "INVALID_TOKEN");
            }
        }
        // 토큰이 null이면 아무것도 세팅 안 함 -> SecurityConfig에 의해 401(AUTH001) 발생
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return null;
    }
}