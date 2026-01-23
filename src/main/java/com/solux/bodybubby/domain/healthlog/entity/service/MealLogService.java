package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.MealLog;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MealLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MealLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.repository.MealLogRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.util.S3Provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;
    private final S3Provider s3Provider; // S3 기능 주입

    // 1. 식단 저장
    public void saveMealLog(Long userId, MealLogRequest request, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 이미지 업로드 (파일이 있을 때만 S3에 저장, 폴더명 "meal")
        String storedImageUrl = null;
        if (image != null && !image.isEmpty()) {
            storedImageUrl = s3Provider.uploadFile(image, "meal");
        }

        // 음식 목록과 메모 합치기
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
                .photoUrl(storedImageUrl)
                .build();

        mealLogRepository.save(mealLog);
    }

    // 2. 식단 수정
    public void updateMealLog(Long userId, Long dietRecordId, MealLogRequest request, MultipartFile image) {
        MealLog mealLog = mealLogRepository.findById(dietRecordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 존재하지 않습니다. id=" + dietRecordId));

        if (!mealLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 이미지 처리 로직
        String updatedImageUrl = mealLog.getPhotoUrl(); // 기본값: 기존 URL 유지
        
        // 새 이미지가 들어왔다면?
        if (image != null && !image.isEmpty()) {
            // 기존 이미지가 있으면 지우고 새로 업로드, 없으면 그냥 업로드
            if (updatedImageUrl != null && !updatedImageUrl.isEmpty()) {
                updatedImageUrl = s3Provider.updateFile(updatedImageUrl, image, "meal");
            } else {
                updatedImageUrl = s3Provider.uploadFile(image, "meal");
            }
        }

        String foodListString = (request.getFoods() != null && !request.getFoods().isEmpty())
                                ? String.join(", ", request.getFoods()) : "입력 없음";
        String combinedMemo = String.format("[음식: %s]\n메모: %s",
                foodListString, request.getMemo() != null ? request.getMemo() : "");

        mealLog.update(
            request.getMealType(),
            LocalDate.parse(request.getIntakeDate()),
            request.getIntakeTime(),
            combinedMemo,
            updatedImageUrl
        );
    }

    // 3. 날짜별 조회
    @Transactional(readOnly = true)
    public List<MealLogResponse> getMealLogsByDate(Long userId, String date) {
        LocalDate targetDate = LocalDate.parse(date);
        
        // Repository 메서드 이름이 맞는지 확인 필요 (없으면 Repository에 추가해야 함)
        return mealLogRepository.findAllByUserIdAndIntakeDateOrderByIntakeTimeAsc(userId, targetDate)
                .stream()
                .map(MealLogResponse::from)
                .collect(Collectors.toList());
    }

    // 4. 삭제
    public void deleteMealLog(Long userId, Long dietRecordId) {
        MealLog mealLog = mealLogRepository.findById(dietRecordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 존재하지 않습니다."));

        if (!mealLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 기록만 삭제할 수 있습니다.");
        }

        // S3에서 이미지 삭제
        if (mealLog.getPhotoUrl() != null) {
            s3Provider.deleteFile(mealLog.getPhotoUrl());
        }

        mealLogRepository.delete(mealLog);
    }
}