package com.solux.bodybubby.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChallengeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 유저-챌린지 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_challenges_id", nullable = false)
    private UserChallenge userChallenge;

    private LocalDate logDate;
    private BigDecimal valueAchieved;

    private LocalDateTime createdAt;

    /**
     * 생성 시간 로직 강화
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.logDate == null) {
            this.logDate = LocalDate.now();
        }
    }
}