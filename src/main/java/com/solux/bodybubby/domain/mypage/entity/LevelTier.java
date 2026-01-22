package com.solux.bodybubby.domain.mypage.entity;

import lombok.Getter;

@Getter
public enum LevelTier {
    // 1. 등급별 데이터 정의 (명칭, 최소포인트, 최대포인트, 아이콘URL)
    STARTER("스타터 버디", 0, 999, "https://bodybuddy-s3.com/levels/starter.png"),
    CHALLENGER("챌린저 버디", 1000, 2999, "https://bodybuddy-s3.com/levels/challenger.png"),
    ACHIEVER("어치버 버디", 3000, 6999, "https://bodybuddy-s3.com/levels/achiever.png"),
    GRINDER("그라인더 버디", 7000, 14999, "https://bodybuddy-s3.com/levels/grinder.png"),
    TRAINER("트레이너 버디", 15000, 29999, "https://bodybuddy-s3.com/levels/trainer.png"),
    MASTER("마스터 버디", 30000, Integer.MAX_VALUE, "https://bodybuddy-s3.com/levels/master.png");

    private final String rankName;
    private final int minPoint;
    private final int maxPoint;
    private final String iconUrl;

    LevelTier(String rankName, int minPoint, int maxPoint, String iconUrl) {
        this.rankName = rankName;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.iconUrl = iconUrl;
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