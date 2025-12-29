package com.solux.bodybubby.domain.badge.repository;

import com.solux.bodybubby.domain.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    /**
     * [마이페이지 메인용]
     * 사용자가 최근에 획득한 뱃지 4개를 조회합니다.
     *
     * @param userId 사용자 식별자
     * @return 획득일(acquiredAt) 내림차순 기준 상위 4개 뱃지 리스트
     */
    List<UserBadge> findTop4ByUser_IdOrderByAcquiredAtDesc(Long userId);

    /**
     * [뱃지 컬렉션 전체 조회용]
     * 사용자가 보유한 모든 뱃지 내역을 조회합니다.
     *
     * @param userId 사용자 식별자
     * @return 사용자가 획득한 전체 뱃지 리스트
     */
    List<UserBadge> findAllByUser_Id(Long userId);
}
