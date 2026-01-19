package com.solux.bodybubby.global.util;

import com.solux.bodybubby.global.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            String token = (String) authentication.getCredentials();
            return jwtTokenProvider.getUserId(token);
        }
        throw new RuntimeException("인증되지 않은 사용자입니다.");
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            return (String) authentication.getCredentials();
        }
        throw new RuntimeException("토큰이 없습니다.");
    }
}