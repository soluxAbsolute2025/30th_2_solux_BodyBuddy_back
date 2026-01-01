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

    // 소셜 로그인 필수 정보
    @Column(length = 100, unique = true, nullable = false)
    private String email; // 구글 계정 이메일

    @Column(length = 20)
    private String provider;    // "google"

    @Column(name = "provider_id", length = 100)
    private String providerId;  // 구글 측 고유 ID (sub)

    // 온보딩 정보: 프로필 및 기본 정보
    @Column(length = 50, unique = true) // 중복 확인 명세 반영
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl; // 프로필 이미지 경로

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

    @Column(name = "daily_sleep_goal")
    private Integer dailySleepGoal;

    @Column(length = 255)
    private String interests; // 관심사 키워드

    // 설정 정보
    @Column(name = "privacy_scope", length = 50)
    private String privacyScope; // 공개 범위

    @Column(name = "is_notification_enabled")
    private boolean isNotificationEnabled; // 알림 수신 동의

    @Column(name = "referrer_id", length = 50)
    private String referrerId; //추천인 아이디

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