package com.solux.bodybubby.domain.user.entity;

import com.solux.bodybubby.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 계정 정보 (ID/PW 방식)
    @Column(name = "login_id", length = 20, unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    // 온보딩 정보: 프로필 및 기본 정보
    @Column(length = 50, unique = true)
    private String nickname;

    private Integer age;

    @Column(length = 10)
    private String gender;
    private Double height;
    private Double weight;

    // 일일 목표 정보
    @Column(name = "daily_step_goal")
    private Integer dailyStepGoal;

    @Column(name = "daily_workout_goal")
    private Integer dailyWorkoutGoal;

    @Column(name = "daily_sleep_hours_goal")
    private Integer dailySleepHoursGoal; // 수면 목표 (시간)

    @Column(name = "daily_sleep_minutes_goal")
    private Integer dailySleepMinutesGoal; // 수면 목표 (분)

    @Column(columnDefinition = "TEXT")
    private String interests; // 관심사 키워드를 문자열로 변환해 저장

    @Column(name = "referrer_id", length = 50)
    private String referrerId; //추천인 아이디

    // 설정 정보
    @Column(length = 100)
    private String introduction;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // 마이페이지 활동 요약 데이터 추가
    @Column(name = "consecutive_attendance")
    @Builder.Default
    private Integer consecutiveAttendance = 0; // 연속 출석 일수

    @Column(name = "completed_challenges_count")
    @Builder.Default
    private Integer completedChallengesCount = 0; // 완료한 챌린지 수

    // 레벨 및 포인트 시스템
    @Builder.Default
    private Integer level = 1;

    @Column(name = "current_exp")
    @Builder.Default
    private Integer currentExp = 0;

    // 공개 범위
    @Builder.Default
    private boolean isWaterPublic = true;
    @Builder.Default
    private boolean isWorkoutPublic = true;
    @Builder.Default
    private boolean isDietPublic = true;
    @Builder.Default
    private boolean isSleepPublic = true;

    @Column(name = "is_onboarded")
    @Builder.Default
    private boolean isOnboarded = false; //온보딩 완료 여부 플래그

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * [온보딩 완료 비즈니스 로직]
     */
    public void completeOnboarding(String nickname, Integer age, String gender, Double height, Double weight,
                                   Integer dailyStepGoal, Integer dailyWorkoutGoal,
                                   Integer dailySleepHoursGoal, Integer dailySleepMinutesGoal,
                                   String interests, String referrerId) {
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.dailyStepGoal = dailyStepGoal;
        this.dailyWorkoutGoal = dailyWorkoutGoal;
        this.dailySleepHoursGoal = dailySleepHoursGoal;
        this.dailySleepMinutesGoal = dailySleepMinutesGoal;
        this.interests = interests;
        this.referrerId = referrerId;
        this.isOnboarded = true;
    }

    /**
     * [프로필 수정 비즈니스 로직]
     */
    public void updateProfile(String nickname, String introduction, String profileImageUrl, String email) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
    }

    /**
     * [비밀번호 변경 비즈니스 로직]
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * [경험치 획득 및 연속 출석 업데이트 비즈니스 로직]
     */
    public void addExp(Integer exp) {
        this.currentExp += exp;
    }

    public void updateAttendance(Integer days) {
        this.consecutiveAttendance = days;
    }

    /**
     * [공개 범위 설정 업데이트 비즈니스 로직]
     */
    public void updatePrivacySettings(boolean water, boolean workout, boolean diet, boolean sleep) {
        this.isWaterPublic = water;
        this.isWorkoutPublic = workout;
        this.isDietPublic = diet;
        this.isSleepPublic = sleep;
    }

}