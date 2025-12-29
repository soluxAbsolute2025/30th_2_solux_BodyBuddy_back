package com.solux.bodybubby.domain.badge.repository;

import com.solux.bodybubby.domain.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    // 기본 제공되는 findAll() 메서드를 사용하여 전체 뱃지를 조회합니다.
}