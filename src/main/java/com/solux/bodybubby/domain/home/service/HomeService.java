package com.solux.bodybubby.domain.home.service;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;
import com.solux.bodybubby.domain.healthlog.entity.repository.MealLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.repository.SleepLogRepository; // import 확인!
import com.solux.bodybubby.domain.healthlog.entity.repository.WaterLogRepository;
import com.solux.bodybubby.domain.home.dto.response.HomeResponseDTO;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final UserRepository userRepository;
    private final WaterLogRepository waterLogRepository;
    private final MealLogRepository mealLogRepository;
    
    // ▼▼▼ [필수] 이 줄이 꼭 있어야 합니다! ▼▼▼
    private final SleepLogRepository sleepLogRepository; 

    public HomeResponseDTO getHomeData(Long userId) {
        // 1. 오늘 날짜 및 유저 조회
        LocalDate today = LocalDate.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // ==========================================
        // 2. 수분 (WaterInfo)
        // ==========================================
      int waterGoal = user.getDailyWaterGoal() != null ? user.getDailyWaterGoal() : 2000;
        
        // ▼▼▼ [수정] 날짜만 넣던 것을 -> 시작시간/끝시간으로 변경 ▼▼▼
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        Integer currentWater = waterLogRepository.sumAmountByUserIdAndDate(userId, startOfDay, endOfDay);
        // ▲▲▲ 여기까지 수정 ▲▲▲
        
        if (currentWater == null) currentWater = 0;

        int waterPercent = (int) ((double) currentWater / waterGoal * 100);
        if (waterPercent > 100) waterPercent = 100;

        HomeResponseDTO.WaterInfo waterInfo = HomeResponseDTO.WaterInfo.builder()
                .current(currentWater)
                .goal(waterGoal)
                .percent(waterPercent)
                .build();

        // ==========================================
        // 3. 식단 (MealInfo)
        // ==========================================
        int mealGoal = 3;
        List<MealLog> todayMeals = mealLogRepository.findByUserIdAndIntakeDate(userId, today);
        int currentMealCount = todayMeals.size();

        int mealPercent = (int) ((double) currentMealCount / mealGoal * 100);
        if (mealPercent > 100) mealPercent = 100;

        boolean isBreakfast = todayMeals.stream()
                .anyMatch(m -> "BREAKFAST".equalsIgnoreCase(m.getMealType()) || "아침".equals(m.getMealType()));
        boolean isLunch = todayMeals.stream()
                .anyMatch(m -> "LUNCH".equalsIgnoreCase(m.getMealType()) || "점심".equals(m.getMealType()));
        boolean isDinner = todayMeals.stream()
                .anyMatch(m -> "DINNER".equalsIgnoreCase(m.getMealType()) || "저녁".equals(m.getMealType()));

        HomeResponseDTO.MealInfo mealInfo = HomeResponseDTO.MealInfo.builder()
                .current(currentMealCount)
                .goal(mealGoal)
                .percent(mealPercent)
                .isBreakfastEaten(isBreakfast)
                .isLunchEaten(isLunch)
                .isDinnerEaten(isDinner)
                .build();

        // ==========================================
        // 4. 수면 (SleepInfo) - [수정 완료]
        // ==========================================
        
       // A. 유저의 목표 가져오기 (시간 & 분)
        Integer goalHour = user.getDailySleepHoursGoal();
        Integer goalMin = user.getDailySleepMinutesGoal();

        // (값이 없을 경우 기본값 설정: 8시간 0분)
        if (goalHour == null) goalHour = 8;
        if (goalMin == null) goalMin = 0;

        // B. 목표를 '분'으로 통합 (시간*60 + 분)
        // 예: 7시간 30분 -> (7*60) + 30 = 450분
        int totalGoalMinutes = (goalHour * 60) + goalMin;

        // C. 오늘 실제 잔 시간 가져오기 (DB에서 이미 분 단위 합계)
        Integer currentMinutes = sleepLogRepository.sumTotalMinutesByUserIdAndDate(userId, today);
        if (currentMinutes == null) currentMinutes = 0;

        // D. 퍼센트 계산
        int sleepPercent = 0;
        if (totalGoalMinutes > 0) { // 목표가 0이면 나눗셈 에러 나니까 체크
            sleepPercent = (int) ((double) currentMinutes / totalGoalMinutes * 100);
            if (sleepPercent > 100) sleepPercent = 100;
        }

        // E. 결과 담기
        HomeResponseDTO.SleepInfo sleepInfo = HomeResponseDTO.SleepInfo.builder()
                .current(currentMinutes)     // 현재 기록 (분)
                .goal(totalGoalMinutes)      // 변환된 목표 (분)
                // .percent(sleepPercent)    // DTO에 퍼센트 필드 있으면 주석 해제
                .build();

                return HomeResponseDTO.builder()
                .date(today.toString())
                .water(waterInfo)
                .meal(mealInfo)
                .sleep(sleepInfo)
                .build();
    }
}