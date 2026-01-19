package com.solux.bodybubby.global.security;

import com.solux.bodybubby.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 1. request에서 필터가 심어놓은 에러 정보를 가져옵니다.
        String exception = (String) request.getAttribute("exception");

        // 2. 기본값은 UNAUTHORIZED로 설정
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        // 3. 만약 "INVALID_TOKEN"이 심어져 있다면 해당 에러코드로 교체
        if ("INVALID_TOKEN".equals(exception)) {
            errorCode = ErrorCode.INVALID_TOKEN;
        }

        // 4. 응답 설정
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());

        String jsonResponse = String.format(
                "{\"status\": %d, \"code\": \"%s\", \"message\": \"%s\"}",
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        response.getWriter().write(jsonResponse);
    }
}