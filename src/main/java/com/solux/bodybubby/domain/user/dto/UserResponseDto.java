package com.solux.bodybubby.domain.user.dto; // 패키지 경로를 꼭 확인하세요!

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Integer currentLevel;
        private String levelName;
        private String levelImageUrl;
    }
}