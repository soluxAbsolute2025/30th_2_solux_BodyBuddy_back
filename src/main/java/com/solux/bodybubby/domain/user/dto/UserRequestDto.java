package com.solux.bodybubby.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserRequestDto {

    /**
     * [회원가입 요청]
     */
    @Getter
    @NoArgsConstructor
    public static class Signup {
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Pattern(regexp = "^[a-z0-9]{4,12}$", message = "아이디는 4~12자의 영문 소문자 및 숫자 조합이어야 합니다.")
        private String loginId;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d|.*\\W).{6,20}$",
                message = "비밀번호는 6~20자의 영문 대소문자, 숫자, 특수문자 중 2가지 이상 조합이어야 합니다.")
        private String password;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }

    /**
     * [로그인 요청]
     */
    @Getter
    @NoArgsConstructor
    public static class Login {
        @NotBlank(message = "아이디를 입력해주세요.")
        private String loginId;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    /**
     * [온보딩 정보 등록 요청]
     */
    @Getter
    @NoArgsConstructor
    public static class Onboarding {
        private String nickname;            // 닉네임
        private Integer age;                // 나이
        private String gender;              // 성별 (MALE, FEMALE 등)
        private Double height;              // 키 (cm)
        private Double weight;              // 몸무게 (kg)

        private Integer dailyStepGoal;      // 일일 목표 걸음 수
        private Integer dailyWorkoutGoal;   // 일일 목표 운동 시간 (분 단위)
        private Integer dailySleepHoursGoal;     // 일일 목표 수면 시간 (시간 단위)
        private Integer dailySleepMinutesGoal;   // 일일 목표 수면 시간 (분 단위)

        private List<String> interests;      // 관심 분야 키워드 리스트
        private String referrerId;          // 추천인 아이디
    }

    /**
     * [프로필 수정 요청]
     */
    @Getter
    @NoArgsConstructor
    public static class ProfileUpdate {
        private String nickname;
        private String introduction;
        private String profileImageUrl;

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }

    /**
     * [비밀번호 변경 요청]
     */
    @Getter
    @NoArgsConstructor
    public static class PasswordUpdate {
        @NotBlank(message = "새로운 비밀번호는 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d|.*\\W).{6,20}$",
                message = "비밀번호는 6~20자의 영문 대소문자, 숫자, 특수문자 중 2가지 이상 조합이어야 합니다.")
        private String newPassword;
    }

}
