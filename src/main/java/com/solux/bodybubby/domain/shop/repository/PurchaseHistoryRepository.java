package com.solux.bodybubby.domain.shop.repository;

import com.solux.bodybubby.domain.shop.entity.PurchaseHistory;
import com.solux.bodybubby.domain.shop.entity.ShopItem;
import com.solux.bodybubby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    List<PurchaseHistory> findAllByUser(User user);
    boolean existsByUserAndItem(User user, ShopItem item);

    // 이번 달 획득/구매한 총 개수
    long countByUserAndPurchasedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    // 이번 달 사용한 포인트 합계
    @Query("SELECT SUM(ph.pointsSpent) FROM PurchaseHistory ph WHERE ph.user = :user AND ph.purchasedAt BETWEEN :start AND :end")
    Integer sumPointsSpentByUserInMonth(@Param("user") User user, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    
}