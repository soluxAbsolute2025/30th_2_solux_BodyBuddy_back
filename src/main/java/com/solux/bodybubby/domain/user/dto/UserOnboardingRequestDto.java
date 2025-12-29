package com.solux.bodybubby.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserOnboardingRequestDto {
    private String nickname;            // 닉네임
    private String profileImageUrl;     // 프로필 이미지 URL
    private Integer age;                // 나이
    private String gender;              // 성별 (MALE, FEMALE 등)
    private Double height;              // 키 (cm)
    private Double weight;              // 몸무게 (kg)

    // 일일 목표 설정
    private Integer dailyStepGoal;      // 일일 목표 걸음 수
    private Integer dailyWorkoutGoal;   // 일일 목표 운동 시간 (분 단위)
    private Integer dailySleepGoal;     // 일일 목표 수면 시간 (분 단위)

    private String interests;           // 관심 분야 (관심사 키워드)
    private String privacyScope;        // 공개 범위 설정 (전체 공개, 수분만 공유 등)
    private boolean isNotificationEnabled; // 알림 수신 동의 여부
    private String referrerId;          // 추천인 아이디 (논의 중)
}