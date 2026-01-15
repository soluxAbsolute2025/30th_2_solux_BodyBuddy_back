package com.solux.bodybubby.domain.user.repository;

import com.solux.bodybubby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    // 소셜 로그인 시 이메일로 기존 회원을 찾기 위한 메서드
    static Optional<User> findByEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByEmail'");
    }

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}