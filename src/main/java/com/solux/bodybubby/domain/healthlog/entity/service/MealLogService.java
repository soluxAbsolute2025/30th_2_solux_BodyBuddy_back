package com.solux.bodybubby.domain.healthlog.entity.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;
import com.solux.bodybubby.domain.healthlog.entity.repository.MealLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MealLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MealLogResponse;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Builder
@Service
@RequiredArgsConstructor
public class MealLogService {

    private final MealLogRepository mealLogRepository; 
    private final UserRepository userRepository;

    @Transactional
    public void saveMealLog(Long userId, MealLogRequest request) {
        
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));

        // 2. 음식 리스트 문자열 합치기
        String foodListString = (request.getFoods() != null && !request.getFoods().isEmpty()) 
                                ? String.join(", ", request.getFoods()) 
                                : "입력 없음";

        // 3. 메모 구성
        String combinedMemo = String.format(
            "[음식: %s]\n메모: %s", 
            foodListString, 
            request.getMemo() != null ? request.getMemo() : ""
        );

        // 4. 엔티티 생성
        MealLog mealLog = MealLog.builder() // 빌더 패턴을 사용하면 순서 상관없이 안전합니다!
        .user(user)
        .loggedAt(LocalDateTime.now())
        .mealType(request.getMealType())
        .intakeDate(LocalDate.parse(request.getIntakeDate()))
        .intakeTime(request.getIntakeTime())
        .memo(combinedMemo)
        .photoUrl(request.getImageUrl())
        .build();
        // 5. 저장
        mealLogRepository.save(mealLog);
    }

   @Transactional(readOnly = true)
    public List<MealLogResponse> getMealLogsByDate(Long userId, String date) {
    // 조회 시: 이제 intakeDate 컬럼에서 찾습니다.
    return mealLogRepository.findAllByUserIdAndIntakeDateOrderByIntakeTimeAsc(userId, LocalDate.parse(date))
            .stream()
            .map(MealLogResponse::from)
            .collect(Collectors.toList());
}

@Transactional
public void updateMealLog(Long userId, Long dietRecordId, MealLogRequest request) {
    
    // 1. 기존 객체를 DB에서 꺼내옵니다 (이 객체는 영속성 컨텍스트가 관리함)
    MealLog mealLog = mealLogRepository.findById(dietRecordId)
            .orElseThrow(() -> new IllegalArgumentException("기록 없음 id=" + dietRecordId));

    // [참고] 여기서 MealLog.builder()를 쓰면 절대 안 됩니다!

    // 2. 꺼내온 객체의 내부 값만 바꿔줍니다 (우리가 만든 update 메서드 활용)
    // 메모 합치는 로직은 아까처럼 서비스나 엔티티 내부에서 처리
    String combinedMemo = String.format("[음식: %s]\n메모: %s", 
            String.join(", ", request.getFoods()), 
            request.getMemo());

    mealLog.update(
        request.getMealType(),
        LocalDate.parse(request.getIntakeDate()),
        request.getIntakeTime(),
        combinedMemo,
        request.getImageUrl()
    );
    
}

    @Transactional
    public void deleteMealLog(Long userId, Long dietRecordId) {
        // 1. 존재 여부 확인
        MealLog mealLog = mealLogRepository.findById(dietRecordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 존재하지 않습니다. id=" + dietRecordId));

        // 2. 권한 확인 (중요!)
        if (!mealLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 기록만 삭제할 수 있습니다.");
        }

        // 3. 삭제 처리
        mealLogRepository.delete(mealLog);
    }
}