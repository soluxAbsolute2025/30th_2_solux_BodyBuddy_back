package com.solux.bodybubby.domain.challenge.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "challenges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private User creator;

    private String challengeType;     // 예: WATER, WALK, SLEEP
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl; // S3 업로드 이미지 URL

    @Enumerated(EnumType.STRING)
    private Visibility visibility;  // 공개 범위 설정 (PUBLIC, SECRET)

    private String targetType;         // DAILY, TOTAL
    private BigDecimal targetValue;
    private String targetUnit;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = true) // 개인 챌린지에서는 비어있을 수 있음
    private Integer period; // 그룹 챌린지에서의 목표기간

    private Integer baseRewardPoints;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;    // RECRUITING, IN_PROGRESS 등

    @Column(unique = true)
    private String groupCode;
    private Integer maxParticipants;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // 그룹 챌린지일 경우 고유 코드 8자리 자동 생성
        if (this.groupCode == null || this.groupCode.isEmpty()) {
            this.groupCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // 이미지 URL 기본값 설정 (이미지가 없을 경우 대비)
        if (this.imageUrl == null) {
            this.imageUrl = "https://default-image-url.com/challenge.png";
        }

        // 디자인상 입력받지 않는 필드들에 대한 '기본값' 자동 할당
        // 값이 null일 때만 세팅되므로 개인 챌린지의 커스텀 값은 건드리지 않습니다.
        if (this.challengeType == null) this.challengeType = "WALK";
        if (this.targetType == null) this.targetType = "DAILY";
        if (this.targetValue == null) this.targetValue = new BigDecimal("10000");
        if (this.targetUnit == null) this.targetUnit = "보";

        // 기본 상태 '모집중' 설정
        if (this.status == null) {
            this.status = ChallengeStatus.RECRUITING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 그룹챌린지 업데이트
    public void update(String title, String description, Integer period, Integer maxParticipants, String privacyScope) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (period != null) this.period = period;
        if (maxParticipants != null) this.maxParticipants = maxParticipants;
        if (visibility != null) this.visibility = visibility;
    }

    /**
     * 개인 챌린지 정보 업데이트 메서드
     */
    public void updatePersonal(String title, String description, Integer targetDays, BigDecimal dailyGoal, String unit, Visibility visibility) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (targetDays != null) {
            this.period = targetDays; // 목표 일수/횟수를 period에 저장
            this.endDate = LocalDate.now().plusDays(targetDays);
        }
        if (dailyGoal != null) this.targetValue = dailyGoal;
        if (unit != null) this.targetUnit = unit;
        if (visibility != null) this.visibility = visibility;
    }

    /**
     * 이미지 URL 업데이트
     */
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}