package com.solux.bodybubby.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
}