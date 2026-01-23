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

    // 계정 정보
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

    @Column(name = "daily_water_goal")
    private Integer dailyWaterGoal;

    @Column(name = "daily_diet_goal")
    private Integer dailyDietGoal;

    @Column(columnDefinition = "TEXT")
    private String interests; // 관심사 키워드

    @Column(name = "referrer_id", length = 50)
    private String referrerId; // 추천인

    // 설정 정보
    @Column(length = 100)
    @Builder.Default
    private String introduction = "건강한 라이프 스타일 실천 중";

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // 마이페이지 활동 요약
    @Column(name = "consecutive_attendance")
    @Builder.Default
    private Integer consecutiveAttendance = 0;

    @Column(name = "completed_challenges_count")
    @Builder.Default
    private Integer completedChallengesCount = 0;

    // 레벨 및 포인트 시스템
    @Builder.Default
    private Integer level = 1;

    @Column(name = "current_exp")
    @Builder.Default
    private Integer currentExp = 0;

    @Builder.Default
    private Integer currentPoints = 0; // 포인트

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
    private boolean isOnboarded = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // === 비즈니스 로직 ===

    // 1. 온보딩 완료
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

    // 2. 프로필 전체 수정 (텍스트 정보)
    public void updateProfile(String nickname, String introduction, String profileImageUrl, String email) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
    }

    // [중요] S3 이미지 업로드 서비스에서 사용하기 위해 추가된 메서드
    public void updateProfileUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 3. 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 4. 경험치 & 포인트 관련
    public void addExp(Integer exp) {
        this.currentExp += exp;
    }

    public void minusPoints(int amount) {
        if (this.currentPoints < amount) throw new IllegalStateException("포인트 부족");
        this.currentPoints -= amount;
    }

    public void updateAttendance(Integer days) {
        this.consecutiveAttendance = days;
    }

    // 5. 공개 범위 설정
    public void updatePrivacySettings(boolean water, boolean workout, boolean diet, boolean sleep) {
        this.isWaterPublic = water;
        this.isWorkoutPublic = workout;
        this.isDietPublic = diet;
        this.isSleepPublic = sleep;
    }

    /**
     * 포인트 획득 로직 추가
     */
    public void addPoints(Integer points) {
        if (points != null && points > 0) {
            this.currentPoints += points;
        }
    }

}