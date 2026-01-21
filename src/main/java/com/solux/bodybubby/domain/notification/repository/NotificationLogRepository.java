package com.solux.bodybubby.domain.notification.repository;

import com.solux.bodybubby.domain.notification.entity.NotificationLog;
import com.solux.bodybubby.domain.notification.entity.NotificationRule;
import com.solux.bodybubby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    // 특정 날짜에, 특정 규칙에 대한 기록이 있는지 찾기
    Optional<NotificationLog> findByUserAndNotificationRuleAndDate(User user, NotificationRule rule, LocalDate date);
}