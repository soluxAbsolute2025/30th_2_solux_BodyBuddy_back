package com.solux.bodybubby.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth"; // 보안 스키마 이름

        // API 요청 시 헤더에 토큰을 포함시키기 위한 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // SecurityScheme 정의
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")            // Bearer 토큰 방식
                        .bearerFormat("JWT"));         // 포맷은 JWT

        return new OpenAPI()
                .info(new Info()
                        .title("Body Buddy API")
                        .description("버디존 및 피드 API 명세서")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}