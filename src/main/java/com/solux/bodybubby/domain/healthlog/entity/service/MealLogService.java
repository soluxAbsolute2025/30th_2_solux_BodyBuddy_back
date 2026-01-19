package com.solux.bodybubby.domain.healthlog.entity.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;
import com.solux.bodybubby.domain.healthlog.entity.repository.MealLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MealLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MealLogResponse;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Builder@Service
@RequiredArgsConstructor
public class MealLogService {

    private final MealLogRepository mealLogRepository; 
    private final UserRepository userRepository;
    // private final S3Service s3Service; // 실제 구현 시 필요

    @Transactional
    public void saveMealLog(Long userId, MealLogRequest request, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 1. 이미지 처리: 파일을 저장하고 URL을 반환받는 로직 (가상)
        String storedImageUrl = null;
        if (image != null && !image.isEmpty()) {
            // storedImageUrl = s3Service.upload(image); 
            storedImageUrl = "uploads/" + image.getOriginalFilename(); // 임시 로직
        }

        String foodListString = (request.getFoods() != null && !request.getFoods().isEmpty()) 
                                ? String.join(", ", request.getFoods()) : "입력 없음";

        String combinedMemo = String.format("[음식: %s]\n메모: %s", 
                foodListString, request.getMemo() != null ? request.getMemo() : "");

        MealLog mealLog = MealLog.builder()
                .user(user)
                .loggedAt(LocalDateTime.now())
                .mealType(request.getMealType())
                .intakeDate(LocalDate.parse(request.getIntakeDate()))
                .intakeTime(request.getIntakeTime())
                .memo(combinedMemo)
                .photoUrl(storedImageUrl) // 저장된 URL 주소 삽입
                .build();

        mealLogRepository.save(mealLog);
    }

    @Transactional
    public void updateMealLog(Long userId, Long dietRecordId, MealLogRequest request, MultipartFile image) {
        
        // 1. 기존 기록 조회
        MealLog mealLog = mealLogRepository.findById(dietRecordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 존재하지 않습니다. id=" + dietRecordId));

        // 2. 권한 확인 (내 기록인지)
        if (!mealLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 3. 이미지 업데이트 처리
        String updatedImageUrl = mealLog.getPhotoUrl(); // 기본은 기존 이미지 유지
        if (image != null && !image.isEmpty()) {
            // 새 이미지가 들어온 경우에만 업로드 로직 실행 (기존 이미지는 삭제하는 로직이 있으면 더 좋음)
            // updatedImageUrl = s3Service.upload(image); 
            updatedImageUrl = "uploads/" + image.getOriginalFilename(); // 임시 로직
        }

        // 4. 메모 재구성
        String foodListString = (request.getFoods() != null && !request.getFoods().isEmpty()) 
                                ? String.join(", ", request.getFoods()) : "입력 없음";
        String combinedMemo = String.format("[음식: %s]\n메모: %s", 
                foodListString, request.getMemo() != null ? request.getMemo() : "");

        // 5. 엔티티 데이터 변경 (더티 체킹 활용)
        mealLog.update(
            request.getMealType(),
            LocalDate.parse(request.getIntakeDate()),
            request.getIntakeTime(),
            combinedMemo,
            updatedImageUrl
        );
    }

    @Transactional(readOnly = true)
    public List<MealLogResponse> getMealLogsByDate(Long userId, String date) {
    // String으로 들어온 날짜를 LocalDate로 변환하여 조회
    LocalDate targetDate = LocalDate.parse(date);
    
    return mealLogRepository.findAllByUserIdAndIntakeDateOrderByIntakeTimeAsc(userId, targetDate)
            .stream()
            .map(MealLogResponse::from) // MealLogResponse에 static from(MealLog) 메서드가 있어야 함
            .collect(Collectors.toList());
}

    @Transactional
    public void deleteMealLog(Long userId, Long dietRecordId) {
        // 1. 삭제할 기록이 존재하는지 확인
        MealLog mealLog = mealLogRepository.findById(dietRecordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 존재하지 않습니다. id=" + dietRecordId));

        // 2. 권한 확인: 기록의 주인 ID와 현재 로그인한 유저 ID가 일치하는지 검사
        if (!mealLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 기록만 삭제할 수 있습니다.");
        }

        // 3. 기록 삭제
        // (참고: S3 등을 사용 중이라면 여기서 실제 이미지 파일도 삭제하는 로직을 추가하면 좋습니다.)
        mealLogRepository.delete(mealLog);
    }
}