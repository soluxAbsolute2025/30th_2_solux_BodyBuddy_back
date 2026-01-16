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
@EnableWebSecurity // 스프링 시큐리티 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

    /**
     * [비밀번호 암호화 빈 등록]
     * UserService에서 비밀번호를 암호화할 때 이 빈을 사용합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API 서버 위주이므로 CSRF 보안 비활성화
                .headers(headers -> headers.frameOptions(options -> options.disable())) // H2 콘솔 사용 시 필요
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 시 필수
                .authorizeHttpRequests(auth -> auth
                        // 기존에 있던 개별 경로 대신 /api/** 로 모든 API를 허용합니다
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/api/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 필터를 시큐리티 체인 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService, redisTemplate), UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 메인으로 이동
                );

        return http.build();
    }
}