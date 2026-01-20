package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    
    List<MealLog> findAllByUserIdAndIntakeDateOrderByIntakeTimeAsc(Long userId, LocalDate intakeDate);

    @Query("SELECT SUM(m.calories) FROM MealLog m WHERE m.user.id = :userId AND m.date = :date")
    Integer sumCaloriesByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    // (이것도 빨간 줄 뜨면 추가)
    boolean existsByUserIdAndDateAndMealType(Long userId, LocalDate date, String mealType);
}