package com.solux.bodybubby.domain.notification.repository;

import com.solux.bodybubby.domain.notification.entity.NotificationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRuleRepository extends JpaRepository<NotificationRule, Long> {
    // [수정] ByUserId -> ByUser_Id (언더바 _ 필수!)
    // 의미: NotificationRule 안의 user 필드의 id 값을 기준으로 찾겠다.
    List<NotificationRule> findAllByUser_Id(Long userId);
    List<NotificationRule> findByTimeOfDayAndIsEnabledTrue(String timeOfDay);
}