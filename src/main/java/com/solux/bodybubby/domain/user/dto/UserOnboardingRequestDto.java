package com.solux.bodybubby.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserOnboardingRequestDto {
    private String nickname;            // 닉네임
    private List<GoalRequestDto> goals; // 주요 목표 (수분 2L, 식사 2끼 등 상세 설정)
    private String privacyScope;        // 허용 범위 (수분만 공유 등)
    private String referrerId;          // 추천인 아이디 (논의 중)

    @Getter
    @NoArgsConstructor
    public static class GoalRequestDto {
        private String goalType;    // WATER, MEAL, MEDICATION 등
        private Double targetValue; // 2.0, 2 등
        private String targetUnit;  // L, 끼, 회 등
    }
}