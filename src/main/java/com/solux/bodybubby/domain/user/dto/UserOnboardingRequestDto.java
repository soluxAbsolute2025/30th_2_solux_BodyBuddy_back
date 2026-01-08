package com.solux.bodybubby.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class UserOnboardingRequestDto {
    private String nickname;            // 닉네임
    private Integer age;                // 나이
    private String gender;              // 성별 (MALE, FEMALE 등)
    private Double height;              // 키 (cm)
    private Double weight;              // 몸무게 (kg)

    // 일일 목표 설정
    private Integer dailyStepGoal;      // 일일 목표 걸음 수
    private Integer dailyWorkoutGoal;   // 일일 목표 운동 시간 (분 단위)
    private Integer dailySleepHoursGoal;     // 일일 목표 수면 시간 (시간 단위)
    private Integer dailySleepMinutesGoal; // 일일 목표 수면 시간 (분 단위)

    private List<String> interests;      // 관심 분야 (관심사 키워드), 리스트로 받아 서비스에서 변환
    private String referrerId;          // 추천인 아이디
}