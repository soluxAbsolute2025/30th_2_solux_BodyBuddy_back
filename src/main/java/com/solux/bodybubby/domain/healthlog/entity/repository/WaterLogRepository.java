package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.WaterLog;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {
    
    // 특정 기간 동안의 기록을 모두 가져옵니다.
    List<WaterLog> findAllByUserIdAndLoggedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
   
    
  @Query("SELECT SUM(w.amountMl) FROM WaterLog w WHERE w.user.id = :userId AND w.loggedAt BETWEEN :start AND :end")
    Integer sumAmountByUserIdAndDate(@Param("userId") Long userId, 
                                     @Param("start") LocalDateTime start, 
                                     @Param("end") LocalDateTime end);
}