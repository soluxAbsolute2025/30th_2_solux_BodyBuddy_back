package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class HealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    protected HealthLog() {
    }

    protected HealthLog(User user, LocalDateTime loggedAt) {
        this.user = user;
        this.loggedAt = loggedAt;
    }
}