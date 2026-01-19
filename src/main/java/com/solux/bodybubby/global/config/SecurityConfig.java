package com.solux.bodybubby.global.config;

import com.solux.bodybubby.global.security.CustomAuthenticationEntryPoint;
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
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(options -> options.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. [우선 순위] 마이페이지는 반드시 로그인이 필요함 (가장 좁은 범위)
                        .requestMatchers("/api/mypage/**").authenticated()

                        // 2. 그 외의 모든 /api/** 경로는 일단 모두 허용함 (넓은 범위)
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/api/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()

                        // 3. 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // [추가] 인증 예외 발생 시 CustomAuthenticationEntryPoint를 사용하도록 설정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                // JWT 필터를 시큐리티 체인 앞에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}