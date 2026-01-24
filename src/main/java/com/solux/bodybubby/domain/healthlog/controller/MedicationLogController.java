package com.solux.bodybubby.domain.healthlog.controller;

import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.MedicationLogService;
import com.solux.bodybubby.global.security.CustomUserDetails; // 👈 필수 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medication-log")
@RequiredArgsConstructor
public class MedicationLogController {

    private final MedicationLogService medicationLogService;

    // 1. 약 복용 체크 (생성) ☑️
    @PostMapping
    public ResponseEntity<Long> createLog(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 👈 토큰 정보 받기
            @RequestBody MedicationLogRequest request) {
        
        // userId = 1L 하드코딩 삭제
        Long logId = medicationLogService.saveLog(userDetails.getId(), request); // 👈 토큰의 ID 사용
        return ResponseEntity.status(HttpStatus.CREATED).body(logId);
    }

    // 2. 복용 취소 (미완료 처리) ↩️
    @PatchMapping("/cancel") 
    public ResponseEntity<String> cancelIntake(
            @AuthenticationPrincipal CustomUserDetails userDetails, 
            @RequestParam Long medicationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam IntakeSlot slot
    ) {
        // userId = 1L 하드코딩 삭제
        medicationLogService.cancelIntake(userDetails.getId(), medicationId, date, slot);
        return ResponseEntity.ok("복용 취소(미완료) 처리되었습니다.");
    }

    // 3. 오늘/특정 날짜 복용 조회 📅
    @GetMapping("/today") // 👈 원하시는대로 "/today"로 설정
    public ResponseEntity<List<MedicationLogResponse>> getDailyLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // userId = 1L 하드코딩 삭제
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        
        List<MedicationLogResponse> response = medicationLogService.getDailyLogs(userDetails.getId(), targetDate);
        
        return ResponseEntity.ok(response);
    }
}