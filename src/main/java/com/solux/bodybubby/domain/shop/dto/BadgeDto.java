package com.solux.bodybubby.domain.shop.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BadgeDto {
    private Long id;            // 뱃지 ID
    private String name;        // 뱃지 이름 (예: 앱솔루트)
    private String type;        // 타입 (PREMIUM / STANDARD)
    private String iconUrl;     // 이미지 주소
    private LocalDateTime acquiredAt; // 언제 얻었는지 (구매일)
}