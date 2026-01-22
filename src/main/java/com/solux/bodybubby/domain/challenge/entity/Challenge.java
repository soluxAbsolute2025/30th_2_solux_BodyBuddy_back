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

    private String privacyScope;     // 공개 범위 설정 (FRIENDS, PUBLIC, PRIVATE)

    private String targetType;         // DAILY, TOTAL
    private BigDecimal targetValue;
    private String targetUnit;

    private LocalDate startDate;
    private LocalDate endDate;

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

        // 기본 상태 '모집중' 설정
        if (this.status == null) {
            this.status = ChallengeStatus.RECRUITING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}