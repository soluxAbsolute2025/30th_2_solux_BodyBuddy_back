package com.solux.bodybubby.domain.badge.repository;

import com.solux.bodybubby.domain.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    // 유저 ID 기준, 최근 획득일(acquiredAt) 내림차순으로 상위 4개 조회
    List<UserBadge> findTop4ByUser_IdOrderByAcquiredAtDesc(Long userId);
}
