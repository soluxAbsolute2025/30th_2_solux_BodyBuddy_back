package com.solux.bodybubby.domain.mypage.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LevelResponseDto {

    // 1. 상단: 나의 현재 등급 상세 정보 (명세서의 myLevelInfo)
    private MyLevelInfo myLevelInfo;

    // 2. 하단: 전체 등급 리스트 (명세서의 allLevels)
    private List<AllLevelInfo> allLevels;

    @Getter
    @Builder
    public static class MyLevelInfo {
        private Integer currentLevel;
        private String levelName;
        private String levelImageUrl;
        private Integer currentExp;
        private Integer nextLevelExp;
        private Integer remainingExp;
    }

    @Getter
    @Builder
    public static class AllLevelInfo {
        private Integer level;
        private String rankName;
        private String levelImageUrl;
        private Integer minPoint;
        private Integer maxPoint;
        private boolean isMyLevel;
    }
}