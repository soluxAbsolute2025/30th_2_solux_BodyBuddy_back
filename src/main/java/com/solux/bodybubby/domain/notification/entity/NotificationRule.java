package com.solux.bodybubby.domain.notification.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 대상 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 알림 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", nullable = false)
    private NotificationCategory category;

    // 반복 타입 (ONCE / DAILY / WEEKLY / INTERVAL)
    @Column(length = 20)
    private String repeatType;

    // HH:mm
    @Column(length = 20)
    private String timeOfDay;

    // 1~7 (월~일)
    private Integer dayOfWeek;

    // 분 단위
    private Integer intervalMinutes;

    private String intervalStart;
    private String intervalEnd;
}