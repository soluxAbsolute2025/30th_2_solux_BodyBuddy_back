package com.solux.bodybubby.domain.challenge.repository;

import com.solux.bodybubby.domain.challenge.entity.ChallengeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeLogRepository extends JpaRepository<ChallengeLog, Long> {
    // 기본적으로 save() 메서드는 JpaRepository가 제공합니다.
}