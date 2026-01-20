package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {

    // "intakeDate" 필드를 기준으로 검색하도록 이름 변경
    List<MealLog> findByUserIdAndIntakeDate(Long userId, LocalDate intakeDate);
    
    // 개수 세기도 마찬가지
    int countByUserIdAndIntakeDate(Long userId, LocalDate intakeDate);

    List<MealLog> findAllByUserIdAndIntakeDateOrderByIntakeTimeAsc(Long userId, LocalDate intakeDate);
}