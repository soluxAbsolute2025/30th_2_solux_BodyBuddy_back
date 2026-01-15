package com.solux.bodybubby.global.config;

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


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API 서버 위주이므로 CSRF 보안 비활성화
            .headers(headers -> headers.frameOptions(options -> options.disable())) // H2 콘솔 사용 시 필요
           .authorizeHttpRequests(auth -> auth
                // 기존에 있던 개별 경로 대신 /api/** 로 모든 API를 허용합니다.
            .requestMatchers("/api/**").permitAll()
            .anyRequest().authenticated()
            )
                                
            .logout(logout -> logout
                .logoutSuccessUrl("/") // 로그아웃 성공 시 메인으로 이동
            );

        return http.build();
    }
}