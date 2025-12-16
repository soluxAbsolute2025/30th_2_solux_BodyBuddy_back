package com.solux.bodybubby.domain.badge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    // 예: WATER, SLEEP, MEAL, POST
    @Column(name = "condition_type", length = 50)
    private String conditionType;

    // 예: 7일, 100회 등
    @Column(name = "condition_value")
    private Integer conditionValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}