package com.solux.bodybubby.domain.mypage.repository;

import com.solux.bodybubby.domain.mypage.entity.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPageRepository extends JpaRepository<MyPage, Long> {
    // 유저 ID를 통해 마이페이지 정보를 조회합니다.
    Optional<MyPage> findByUserId(Long userId);
}
