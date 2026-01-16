package com.solux.bodybubby.global.config;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터 이름이 "userId"이고 타입이 Long인 경우 동작
        return parameter.getParameterName().equals("userId") && 
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 정보가 없거나, 익명 사용자라면 null 반환 (또는 에러 처리)
        if (authentication == null || authentication.getName().equals("anonymousUser")) {
             // throw new RuntimeException("로그인이 필요합니다."); // 필요시 에러 발생
             return 1L; // 테스트용 임시 ID (나중엔 지우세요)
        }

        // JWT 필터에서 저장한 user ID (보통 authentication.getName()에 ID를 저장해둡니다)
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            return 1L; // 파싱 실패 시 기본값
        }
    }
}