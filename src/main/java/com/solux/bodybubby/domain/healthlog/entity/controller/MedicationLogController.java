package com.solux.bodybubby.domain.healthlog.entity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.MedicationLogService;
import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;

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
            @AuthenticationPrincipal Object principal, 
            @RequestBody MedicationLogRequest request) {
        
        Long userId = 1L; 
        Long logId = medicationLogService.saveLog(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(logId);
    }

    // 2. 복용 취소 (미완료 처리) ↩️
    @PatchMapping("/cancel") 
    public ResponseEntity<String> cancelIntake(
            @AuthenticationPrincipal Object principal, 
            @RequestParam Long medicationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam IntakeSlot slot
    ) {
        Long userId = 1L; 
        medicationLogService.cancelIntake(userId, medicationId, date, slot);
        return ResponseEntity.ok("복용 취소(미완료) 처리되었습니다.");
    }

@GetMapping("/today")
public ResponseEntity<List<MedicationLogResponse>> getDailyLogs(
        @AuthenticationPrincipal Object principal,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
) {
    Long userId = 1L; 
    LocalDate targetDate = (date != null) ? date : LocalDate.now();
    
    // 서비스가 이제 DTO를 줍니다!
    List<MedicationLogResponse> response = medicationLogService.getDailyLogs(userId, targetDate);
    
    return ResponseEntity.ok(response);
}
}