package com.solux.bodybubby.domain.challenge.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_challenges",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "challenges_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 참여 유저 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 참여 챌린지 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenges_id", nullable = false)
    private Challenge challenge;

    private BigDecimal currentProgress;
    private BigDecimal achievementRate;

    private String status;      // IN_PROGRESS, COMPLETED, FAILED

    private LocalDateTime joinedAt;
    private LocalDateTime completedAt;
}