package com.solux.bodybubby.domain.challenge.repository;

import com.solux.bodybubby.domain.challenge.entity.ChallengeLog;
import com.solux.bodybubby.domain.challenge.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ChallengeLogRepository extends JpaRepository<ChallengeLog, Long> {
    // 기본적으로 save() 메서드는 JpaRepository가 제공합니다.
    // Spring Data JPA가 메서드 이름을 분석해 "해당 유저챌린지와 날짜에 로그가 있는지" 확인하는 쿼리를 생성합니다.
    boolean existsByUserChallengeAndLogDate(UserChallenge userChallenge, LocalDate logDate);
}