package com.solux.bodybubby.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSignupRequestDto {
    private String loginId;
    private String password;
    private String email;
    private String nickname; // 닉네임 여부 미정
    private List<String> mainGoals;    // 주요 목표 (수분, 식단, 약 등)
    private String privacyScope;       // 허용 범위 (수분만 공유 등)
    private String referrerId;         // 추천인 아이디 (논의 중)
}