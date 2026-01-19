package com.solux.bodybubby.domain.healthlog.entity.controller;

import com.solux.bodybubby.domain.healthlog.entity.dto.request.WaterLogRequestDTO;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.WaterLogResponseDTO;
import com.solux.bodybubby.domain.healthlog.entity.service.WaterLogService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/water-log")
@RequiredArgsConstructor
public class WaterLogController {

    private final WaterLogService waterLogService;

    /** 1. 수분 기록 생성 (정리 완료) */
    @PostMapping
    public ResponseEntity<String> createWaterLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody WaterLogRequestDTO request
    ) {
        waterLogService.saveWaterLog(userDetails.getId(), request);
        return ResponseEntity.ok("수분 섭취 기록 저장 완료!");
    }

    /** 2. 오늘 기록 조회 (내 기록만 나오도록 수정 완료) */
    @GetMapping("/today")
    public ResponseEntity<List<WaterLogResponseDTO>> getTodayLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(waterLogService.getDailyWaterLogs(userDetails.getId(), targetDate));
    }

    /** 3. 주간 기록 조회 (경로 충돌 방지를 위해 /weekly 추가) */
    @GetMapping("/weekly")
    public ResponseEntity<Map<LocalDate, Integer>> getWeeklyLogs(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(waterLogService.getWeeklyWaterLogs(userDetails.getId()));
    }

    /** 4. 단일 기록 상세 조회 (경로 구분을 위해 detail 추가) */
    @GetMapping("/detail/{waterLogId}")
    public ResponseEntity<WaterLogResponseDTO> getWaterLogDetail(@PathVariable Long waterLogId) {
        return ResponseEntity.ok(waterLogService.getWaterLogDetail(waterLogId));
    }

    /** 5. 기록 삭제 (토큰 유저 정보 사용) */
    @DeleteMapping("/{waterLogId}")
    public ResponseEntity<Void> deleteWaterLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long waterLogId
    ) {
        waterLogService.deleteWaterLog(waterLogId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    /** 6. 기록 수정 (토큰 유저 정보 사용) */
    @PatchMapping("/{waterLogId}")
    public ResponseEntity<String> updateWaterLog(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long waterLogId,
            @RequestBody WaterLogRequestDTO request
    ) {
        waterLogService.updateWaterLog(userDetails.getId(), waterLogId, request);
        return ResponseEntity.ok("수분 섭취 기록 수정 완료!");
    }
}