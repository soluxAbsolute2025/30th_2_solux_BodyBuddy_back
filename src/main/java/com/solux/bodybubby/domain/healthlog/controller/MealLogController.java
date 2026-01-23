package com.solux.bodybubby.domain.healthlog.controller;

import com.solux.bodybubby.domain.healthlog.entity.dto.request.MealLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MealLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.MealLogService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/meal-log")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    // 1. 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addMealLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("request") MealLogRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        mealLogService.saveMealLog(userDetails.getId(), request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("식단 기록 성공!");
    }

    // 2. 조회 (쿼리 파라미터 ?date=2024-01-24)
    @GetMapping
    public ResponseEntity<List<MealLogResponse>> getMealLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") String date) {

        return ResponseEntity.ok(mealLogService.getMealLogsByDate(userDetails.getId(), date));
    }

    // 3. 수정
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateMealLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long id,
            @RequestPart("request") MealLogRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        mealLogService.updateMealLog(userDetails.getId(), id, request, image);
        return ResponseEntity.ok("식단이 수정되었습니다.");
    }

    // 4. 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMealLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long id) {

        mealLogService.deleteMealLog(userDetails.getId(), id);
        return ResponseEntity.ok("식단 기록이 삭제되었습니다.");
    }
}