package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {
    
    // 특정 기간 동안의 기록을 모두 가져옵니다.
    List<WaterLog> findAllByUserIdAndLoggedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}