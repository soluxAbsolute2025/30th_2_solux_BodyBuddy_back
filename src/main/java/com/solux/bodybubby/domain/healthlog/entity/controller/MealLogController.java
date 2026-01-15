package com.solux.bodybubby.domain.healthlog.entity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solux.bodybubby.domain.healthlog.entity.dto.request.MealLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MealLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.MealLogService;


import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/meal-log")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    // 식단 기록 생성
    @PostMapping
    public ResponseEntity<String> addMealLog(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody MealLogRequest request) {

        mealLogService.saveMealLog(userId, request);
        return ResponseEntity.ok("식단 기록 성공!");
    }

    // 특정 날짜 식단 조회
    @GetMapping
    public ResponseEntity<List<MealLogResponse>> getMealLogs(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam("date") String date) {

        return ResponseEntity.ok(
                mealLogService.getMealLogsByDate(userId, date)
        );
    }

    // 식단 기록 수정
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateMealLog(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("id") Long id,
            @RequestBody MealLogRequest request) {

        mealLogService.updateMealLog(userId, id, request);
        return ResponseEntity.ok("식단이 수정되었습니다.");
    }

    // 식단 기록 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMealLog(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("id") Long id) {

        mealLogService.deleteMealLog(userId, id);
        return ResponseEntity.ok("식단 기록이 삭제되었습니다.");
    }
}
