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
    // 유빈님 추가한 에러 처리기 (
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
                        // 1. 정적 리소스, H2 콘솔, 그리고 Swagger추가
                        .requestMatchers(
                                "/", "/css/**", "/images/**", "/js/**", "/h2-console/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()

                        // 2.유지 
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/users/login",
                                "/api/users/check-id",
                                "/api/users/check-nickname"
                        ).permitAll()

                        // 3. 나머지는 "전부 인증 필요"
                        .anyRequest().authenticated()
                )
                // 애러 처리 설정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}