package com.solux.bodybubby.domain.mypage.entity;

import lombok.Getter;

@Getter
public enum LevelTier {
    STARTER("스타터 버디", 0, 999),
    CHALLENGER("챌린저 버디", 1000, 2999),
    ACHIEVER("어치버 버디", 3000, 6999),
    GRINDER("그라인더 버디", 7000, 14999),
    TRAINER("트레이너 버디", 15000, 29999),
    MASTER("마스터 버디", 30000, Integer.MAX_VALUE);

    private final String rankName;
    private final int minPoint;
    private final int maxPoint;

    LevelTier(String rankName, int minPoint, int maxPoint) {
        this.rankName = rankName;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    // 포인트에 해당하는 등급을 반환하는 메서드
    public static LevelTier getTier(int points) {
        for (LevelTier tier : values()) {
            if (points >= tier.minPoint && points <= tier.maxPoint) {
                return tier;
            }
        }
        return STARTER;
    }
}