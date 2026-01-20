package com.solux.bodybubby.domain.notification.repository;

import com.solux.bodybubby.domain.notification.entity.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationCategoryRepository extends JpaRepository<NotificationCategory, Long> {
    // 이름으로 카테고리 찾기 (예: "MEAL")
    Optional<NotificationCategory> findByName(String name);
}