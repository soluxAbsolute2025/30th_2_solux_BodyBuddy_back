package com.solux.bodybubby.domain.notification.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "notification_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 알림 규칙(계획)에 대해서
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_rule_id", nullable = false)
    private NotificationRule notificationRule;

    // 언제 수행했는지
    @Column(nullable = false)
    private LocalDate date;

    // 완료 여부 (체크: true, 체크해제: false)
    @Column(nullable = false)
    private boolean isCompleted;

    // 상태 변경 메서드 (토글용)
    public void toggleStatus() {
        this.isCompleted = !this.isCompleted;
    }
}