package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {

    // 1. 홈 화면용 (이미 추가하셨던 것)
    @Query("SELECT SUM(s.totalMinutes) FROM SleepLog s WHERE s.user.id = :userId AND s.date = :date")
    Integer sumTotalMinutesByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // ▼▼▼ [추가] 2. 하루 조회용 (getSleepLog에서 사용) ▼▼▼
    // 해당 날짜(00:00 ~ 23:59) 사이에 기록된 로그 찾기
    Optional<SleepLog> findByUserIdAndLoggedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // ▼▼▼ [추가] 3. 주간 분석용 (analyzeWeeklySleep에서 사용) ▼▼▼
    // 기간 내의 기록을 날짜 순서대로 가져오기
    @Query("SELECT s FROM SleepLog s WHERE s.user.id = :userId AND s.loggedAt BETWEEN :start AND :end ORDER BY s.loggedAt ASC")
    List<SleepLog> findWeeklyLogs(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}