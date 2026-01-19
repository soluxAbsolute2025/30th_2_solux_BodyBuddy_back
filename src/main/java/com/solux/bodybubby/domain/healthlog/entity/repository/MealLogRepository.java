package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    @Query("SELECT SUM(m.calories) FROM MealLog m WHERE m.user.id = :userId AND m.date = :date")
    Integer sumCaloriesByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // 만약 MealType이 String이면 아래 그대로, Enum이면 타입을 Enum으로 바꾸세요
    boolean existsByUserIdAndDateAndMealType(Long userId, LocalDate date, String mealType);
    
    List<MealLog> findAllByUserIdAndIntakeDateOrderByIntakeTimeAsc(Long userId, LocalDate intakeDate);
}