package com.solux.bodybubby.domain.healthlog.entity.controller;

import com.solux.bodybubby.domain.healthlog.entity.dto.request.SleepLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.SleepLogUpdateRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.SleepAnalysisResponse;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.SleepLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.SleepLogService; // ✅ 서비스 임포트 확인
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/sleep-log")
@RequiredArgsConstructor
public class SleepLogController {

    private final SleepLogService sleepLogService; // 👈 이 친구를 써야 합니다!

    // 1. 수면 기록 추가
    @PostMapping
    public ResponseEntity<String> createLog(@AuthenticationPrincipal CustomUserDetails user, @RequestBody SleepLogRequest req) {
        // 여긴 잘 작성하셨습니다!
        sleepLogService.createSleepLog(user.getId(), req);
        return ResponseEntity.ok("수면 기록 저장 완료");
    }

    // 2. 수면 기록 조회 (?date=...)
    @GetMapping
    public ResponseEntity<SleepLogResponse> getLog(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        // ❌ SleepLog.getSleepLog (X)
        // ⭕ sleepLogService.getSleepLog (O)
        return ResponseEntity.ok(sleepLogService.getSleepLog(user.getId(), date));
    }

    // 3. 수면 기록 수정 (Body에 ID 포함)
    @PatchMapping
    public ResponseEntity<String> updateLog(@AuthenticationPrincipal CustomUserDetails user, @RequestBody SleepLogUpdateRequest req) {
        
        // ❌ SleepLog.updateSleepLog (X)
        // ⭕ sleepLogService.updateSleepLog (O)
        sleepLogService.updateSleepLog(user.getId(), req);
        return ResponseEntity.ok("수면 기록 수정 완료");
    }

    // 4. 수면 기록 삭제 (Body로 ID 받음)
    @DeleteMapping
    public ResponseEntity<String> deleteLog(@AuthenticationPrincipal CustomUserDetails user, @RequestBody Map<String, Long> req) {
        
        // ❌ SleepLog.deleteSleepLog (X)
        // ⭕ sleepLogService.deleteSleepLog (O)
        sleepLogService.deleteSleepLog(user.getId(), req.get("sleepRecordId"));
        return ResponseEntity.ok("수면 기록 삭제 완료");
    }

    // 5. 주간 분석
    @GetMapping("/weekly")
    public ResponseEntity<SleepAnalysisResponse> getAnalysis(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // ❌ SleepLog.analyzeWeeklySleep (X)
        // ⭕ sleepLogService.analyzeWeeklySleep (O)
        return ResponseEntity.ok(sleepLogService.analyzeWeeklySleep(user.getId(), startDate, endDate));
    }
}