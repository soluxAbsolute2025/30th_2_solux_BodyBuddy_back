package com.solux.bodybubby.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 로그인 필수 정보
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 20)
    private String provider;    // "google"

    @Column(name = "provider_id", length = 100)
    private String providerId;  // 구글 고유 ID (sub)

    // 온보딩 정보: 프로필 및 기본 정보
    @Column(length = 50, unique = true) // 중복 확인 명세 반영
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    private Integer age;

    @Column(length = 10)
    private String gender;

    private Double height;
    private Double weight;

    // 일일 목표 정보 (DB 컬럼명 명시적 지정)
    @Column(name = "daily_step_goal")
    private Integer dailyStepGoal;

    @Column(name = "daily_workout_goal")
    private Integer dailyWorkoutGoal;

    @Column(name = "daily_sleep_goal")
    private Integer dailySleepGoal;

    @Column(length = 255)
    private String interests;

    // 설정 정보
    @Column(name = "privacy_scope", length = 50)
    private String privacyScope;

    @Column(name = "is_notification_enabled")
    private boolean isNotificationEnabled;

    @Column(name = "referrer_id", length = 50)
    private String referrerId;

    @Column(name = "is_onboarded")
    private boolean isOnboarded = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * [온보딩 업데이트 비즈니스 로직]
     */
    public void updateOnboarding(String nickname, String profileImageUrl, Integer age, String gender,
                                 Double height, Double weight, Integer dailyStepGoal,
                                 Integer dailyWorkoutGoal, Integer dailySleepGoal,
                                 String interests, String privacyScope,
                                 boolean isNotificationEnabled, String referrerId) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.dailyStepGoal = dailyStepGoal;
        this.dailyWorkoutGoal = dailyWorkoutGoal;
        this.dailySleepGoal = dailySleepGoal;
        this.interests = interests;
        this.privacyScope = privacyScope;
        this.isNotificationEnabled = isNotificationEnabled;
        this.referrerId = referrerId;
        this.isOnboarded = true;
    }
}