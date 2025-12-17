package com.solux.bodybubby.domain.badge.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_badge",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "badge_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 배지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;

    @Column(name = "is_equipped")
    @Builder.Default
    private Boolean isEquipped = false;

    @PrePersist
    void onAcquire() {
        this.acquiredAt = LocalDateTime.now();
    }
}