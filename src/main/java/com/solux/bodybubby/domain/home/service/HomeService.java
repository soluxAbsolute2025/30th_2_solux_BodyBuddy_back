package com.solux.bodybubby.domain.home.service;

import com.solux.bodybubby.domain.home.dto.HomeResponseDTO;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.domain.healthlog.entity.repository.WaterLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.repository.MealLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.repository.SleepLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserRepository userRepository;
    private final WaterLogRepository waterLogRepository;
    private final MealLogRepository mealLogRepository;
    private final SleepLogRepository sleepLogRepository;

    @Transactional(readOnly = true)
    public HomeResponseDTO getHomeDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        // 1. 💧 수분 계산
        Integer waterSum = waterLogRepository.sumAmountByUserIdAndDate(userId, today);
        int currentWater = (waterSum != null) ? waterSum : 0;
        int goalWater = user.getDailyWaterGoal() != null ? user.getDailyWaterGoal() : 2000;

        // 2. 🥗 식단 계산
        Integer calSum = mealLogRepository.sumCaloriesByUserIdAndDate(userId, today);
        int currentCal = (calSum != null) ? calSum : 0;
        int goalCal = user.getDailyDietGoal() != null ? user.getDailyDietGoal() : 1500;
        
        boolean ateBreakfast = mealLogRepository.existsByUserIdAndDateAndMealType(userId, today, "BREAKFAST");
        boolean ateLunch = mealLogRepository.existsByUserIdAndDateAndMealType(userId, today, "LUNCH");
        boolean ateDinner = mealLogRepository.existsByUserIdAndDateAndMealType(userId, today, "DINNER");

        // 3. 🌙 수면 계산
        Integer sleepSum = sleepLogRepository.sumTotalHoursByUserIdAndDate(userId, today);
        int currentSleep = (sleepSum != null) ? sleepSum : 0;
        int goalSleep = user.getDailySleepHoursGoal() != null ? user.getDailySleepHoursGoal() : 8;

        // 4. 📦 구조에 맞게 포장 (여기가 바뀜!)
        return HomeResponseDTO.builder()
                .date(today.toString()) // 날짜 추가
                .water(HomeResponseDTO.WaterInfo.builder()
                        .current(currentWater)
                        .goal(goalWater)
                        .percent(calcPercent(currentWater, goalWater))
                        .build())
                .meal(HomeResponseDTO.MealInfo.builder()
                        .current(currentCal)
                        .goal(goalCal)
                        .percent(calcPercent(currentCal, goalCal))
                        .isBreakfastEaten(ateBreakfast)
                        .isLunchEaten(ateLunch)
                        .isDinnerEaten(ateDinner)
                        .build())
                .sleep(HomeResponseDTO.SleepInfo.builder()
                        .current(currentSleep)
                        .goal(goalSleep)
                        .build())
                .build();
    }

    private int calcPercent(int current, int goal) {
        if (goal == 0) return 0;
        return (int) ((double) current / goal * 100);
    }
}