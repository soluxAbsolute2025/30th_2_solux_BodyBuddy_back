package com.solux.bodybubby.domain.mypage.repository;

import com.solux.bodybubby.domain.mypage.entity.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 마이페이지 데이터 접근을 위한 레포지토리
 */
public interface MyPageRepository extends JpaRepository<MyPage, Long> {

    /**
     * 유저 고유 ID(userId)를 통해 해당 유저의 마이페이지 정보를 조회합니다.
     *
     * @param userId 유저 ID
     * @return 마이페이지 엔티티
     */
    Optional<MyPage> findByUserId(Long userId);
}