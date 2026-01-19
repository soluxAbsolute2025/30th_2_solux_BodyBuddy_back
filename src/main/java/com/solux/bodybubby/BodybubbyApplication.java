package com.solux.bodybubby;

import com.solux.bodybubby.domain.user.repository.RefreshTokenRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

// 1. S3 관련 자동 설정을 제외하도록 통합
@SpringBootApplication(exclude = {
    io.awspring.cloud.autoconfigure.s3.S3AutoConfiguration.class,
    io.awspring.cloud.autoconfigure.core.CredentialsProviderAutoConfiguration.class
})
// 2. JPA 설정: Redis 전용인 RefreshTokenRepository 제외
@EnableJpaRepositories(
        basePackages = "com.solux.bodybubby",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RefreshTokenRepository.class
        )
)
// 3. Redis 설정: RefreshTokenRepository만 포함
@EnableRedisRepositories(
        basePackages = "com.solux.bodybubby",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RefreshTokenRepository.class
        )
)
public class BodybubbyApplication { // 클래스 이름 하나로 통일!

    public static void main(String[] args) {
        SpringApplication.run(BodybubbyApplication.class, args);
    }

}