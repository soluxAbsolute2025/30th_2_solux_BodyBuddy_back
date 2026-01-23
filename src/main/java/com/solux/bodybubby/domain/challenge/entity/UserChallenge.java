package com.solux.bodybubby.domain.challenge.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /**
     * 참여 유저
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 참여 챌린지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenges_id", nullable = false)
    private Challenge challenge;

    private BigDecimal currentProgress; // 현재 진행량
    private BigDecimal achievementRate; // 달성률 (%)

    private String status;      // IN_PROGRESS, COMPLETED, FAILED
    private Integer currentRank; // 실시간 순위 정보

    private LocalDateTime joinedAt;
    private LocalDateTime completedAt;

    /**
     * 개인챌린지 달성률 업데이트 로직
     * 달성률 = (현재량 / 목표량) * 100
     */
    public void updatePersonalProgress(BigDecimal achievedValue) {
        this.currentProgress = this.currentProgress.add(achievedValue);

        BigDecimal target = this.challenge.getTargetValue();
        if (target != null && target.compareTo(BigDecimal.ZERO) > 0) {
            // setScale(0, RoundingMode.DOWN)을 사용하여 소수점 이하 버림
            this.achievementRate = this.currentProgress
                    .divide(target, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(0, RoundingMode.DOWN); // 소수점 0자리, 즉 정수로 절사
        }

        // 100% 도달 시 상태 변경
        if (this.achievementRate.compareTo(new BigDecimal("100")) >= 0) {
            this.status = "COMPLETED";
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * 그룹챌린지 달성률 업데이트 로직
     * 달성률 = (현재량 / 목표량) * 100
     */
    public void updateGroupProgress() {
        // 1. 인증한 일수(currentProgress)를 1 증가시킵니다.
        this.currentProgress = this.currentProgress.add(BigDecimal.ONE);

        // 2. 달성률 = (인증 일수 / 전체 기간) * 100
        Integer period = this.challenge.getPeriod();
        if (period != null && period > 0) {
            this.achievementRate = this.currentProgress
                    .divide(new BigDecimal(period), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(0, RoundingMode.DOWN); // 소수점 버림 (정수 표기)
        }

        // 100% 도달 시 완료 처리
        if (this.achievementRate.compareTo(new BigDecimal("100")) >= 0) {
            this.status = "COMPLETED";
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * 그룹 챌린지 실시간 순위 정보 갱신
     */
    public void updateRank(Integer rank) {
        this.currentRank = rank;
    }
}