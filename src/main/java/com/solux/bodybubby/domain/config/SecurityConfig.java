package com.solux.bodybubby.config;

import com.solux.bodybubby.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API 서버 위주이므로 CSRF 보안 비활성화
            .headers(headers -> headers.frameOptions(options -> options.disable())) // H2 콘솔 사용 시 필요
            .authorizeHttpRequests(auth -> auth
                // 로그인 없이도 접근 가능한 경로들 (메인 페이지, 정적 리소스 등)
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                // 그 외 모든 요청은 인증(로그인)이 필요함
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // 로그아웃 성공 시 메인으로 이동
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 구글 로그인 성공 후 처리할 서비스 등록
                )
                .defaultSuccessUrl("/") // 로그인 성공 시 이동할 기본 페이지
            );

        return http.build();
    }
}