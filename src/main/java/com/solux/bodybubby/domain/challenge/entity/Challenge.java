package com.solux.bodybubby.domain.challenge.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /** 챌린지 생성자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private User creator;

    private String challengeType;     // 예: WATER, WALK, SLEEP
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String targetType;         // 예: DAILY, TOTAL
    private BigDecimal targetValue;
    private String targetUnit;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer baseRewardPoints;
    private String status;             // OPEN, CLOSED 등

    private String groupCode;
    private Integer maxParticipants;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}