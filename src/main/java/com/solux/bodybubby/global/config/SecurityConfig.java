package com.solux.bodybubby.global.config;

import com.solux.bodybubby.domain.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

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
                .authorizeHttpRequests(auth -> auth
                        // 1. 수분 기록 API와 H2 콘솔 등에 대해 누구나 접근 가능하도록 허용
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/api/water-log/**").permitAll()

                        // .requestMatchers("/api/**").permitAll()

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