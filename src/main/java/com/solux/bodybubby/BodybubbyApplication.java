package com.solux.bodybubby;

import com.solux.bodybubby.domain.user.repository.RefreshTokenRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

// @SpringBootApplication 어노테이션에 DB 관련 자동 설정을 제외하는 옵션을 추가합니다.
@EnableScheduling
@SpringBootApplication
// 1. JPA 레포지토리 설정: 모든 레포지토리를 스캔하되, Redis 전용인 RefreshTokenRepository는 제외합니다.
@EnableJpaRepositories(
        basePackages = "com.solux.bodybubby",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RefreshTokenRepository.class
        )
)
// 2. Redis 레포지토리 설정: RefreshTokenRepository만 Redis용으로 인식하도록 명시합니다.
@EnableRedisRepositories(
        basePackages = "com.solux.bodybubby",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RefreshTokenRepository.class
        )
)

public class BodybubbyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BodybubbyApplication.class, args);
    }

}
