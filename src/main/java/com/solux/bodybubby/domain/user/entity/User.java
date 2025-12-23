package com.solux.bodybubby.domain.user.entity;

import com.solux.bodybubby.domain.mypage.entity.MyPage;
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

    /**
     * 기본 계정 정보
     */
    @Column(name = "login_id", length = 50, unique = true)
    private String loginId;

    @Column(length = 255)
    private String password;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String name;

    /**
     * 온보딩 수집 정보
     */
    @Column(length = 50)
    private String nickname; // 닉네임

    @Column(name = "privacy_scope")
    private String privacyScope; // 허용 범위 (수분만 공유 등)

    @Column(name = "referrer_id")
    private String referrerId; // 추천인 아이디

    @Column(name = "is_onboarded")
    private boolean isOnboarded = false; // 온보딩 완료 여부

    /**
     * 시간 정보
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 마이페이지 (1:1 관계)
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MyPage myPage;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 온보딩 정보 업데이트 메서드
     * 요구사항에 따라 신체 정보(키, 몸무게 등)는 제외하고 핵심 정보만 업데이트합니다.
     */
    public void updateOnboarding(String nickname, String privacyScope, String referrerId) {
        this.nickname = nickname;
        this.privacyScope = privacyScope;
        this.referrerId = referrerId;
        this.isOnboarded = true;
    }
}