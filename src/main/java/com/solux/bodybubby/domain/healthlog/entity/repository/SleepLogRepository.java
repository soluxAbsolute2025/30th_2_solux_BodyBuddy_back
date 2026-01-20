package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {

    // ✅ 1. 특정 날짜의 수면 기록 조회 (Service의 getSleepLog에서 사용)
    // loggedAt이 00:00:00 ~ 23:59:59 사이인 기록을 찾음
    Optional<SleepLog> findByUserIdAndLoggedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // ✅ 2. 주간 수면 기록 조회 (Service의 analyzeWeeklySleep에서 사용)
    @Query("SELECT s FROM SleepLog s WHERE s.user.id = :userId AND s.loggedAt BETWEEN :startDate AND :endDate ORDER BY s.loggedAt ASC")
    List<SleepLog> findWeeklyLogs(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ✅ 3. 중복 등록 방지용 (선택 사항)
    boolean existsByUserIdAndLoggedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(s.totalHours) FROM SleepLog s WHERE s.user.id = :userId AND s.date = :date")
    Integer sumTotalHoursByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

}