package com.solux.bodybubby.global.config;

import com.solux.bodybubby.global.security.CustomUserDetailsService;
import com.solux.bodybubby.global.security.JwtAuthenticationFilter;
import com.solux.bodybubby.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API 서버이므로 CSRF 비활성화 (PATCH 등 요청 허용)
                .headers(headers -> headers.frameOptions(options -> options.disable())) // H2 콘솔 사용 시 필요
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 시 필수
                .authorizeHttpRequests(auth -> auth
                        // 1. 공통 리소스 및 H2 콘솔 허용
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                        
                        // 2. 로그인/회원가입 API 허용 (다른 사람 작업 내용)
                        .requestMatchers("/api/users/**").permitAll() 
                        
                        // 3. 테스트 편의를 위해 /api/** 의 일부 경로를 허용하거나, 
                        // 보안이 필요한 모든 API(식단/수분 등)는 인증된 사용자만 허용
                        .anyRequest().authenticated() 
                )
                // JWT 필터를 시큐리티 체인 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}