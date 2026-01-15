package com.solux.bodybubby.domain.healthlog.entity.controller;

import com.solux.bodybubby.domain.healthlog.entity.WaterLog;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.WaterLogRequestDTO;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.WaterLogResponseDTO;
import com.solux.bodybubby.domain.healthlog.entity.service.WaterLogService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/water-log")
@RequiredArgsConstructor
public class WaterLogController {

    // 1. 타입을 WaterLogService(서비스)로 정확히 지정해야함.
    private final WaterLogService waterLogService; 

    @PostMapping
    public ResponseEntity<String> createWaterLog(@RequestBody WaterLogRequestDTO request) {
        // 2. 서비스 인스턴스를 통해 호출
        waterLogService.saveWaterLog(request);
        return ResponseEntity.ok("수분 섭취 기록 저장 완료!");
    }

    @GetMapping("/{waterLogId}")
    public ResponseEntity<WaterLogResponseDTO> getWaterLogDetail(@PathVariable Long waterLogId) {
        return ResponseEntity.ok(waterLogService.getWaterLogDetail(waterLogId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<WaterLogResponseDTO>> getTodayLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        
        // 3. 클래스 이름(WaterLog)이 아닌 서비스 변수(waterLogService)를 사용
        return ResponseEntity.ok(waterLogService.getDailyWaterLogs(1L, targetDate));
    }

    @GetMapping("/weekly")
    public ResponseEntity<Map<LocalDate, Integer>> getWeeklyLogs() {
        return ResponseEntity.ok(waterLogService.getWeeklyWaterLogs(1L));
    }

    @DeleteMapping("/{waterLogId}")
    public ResponseEntity<Void> deleteWaterLog(
            @PathVariable Long waterLogId,
            @RequestParam Long userId
    ) {
        waterLogService.deleteWaterLog(waterLogId, userId);
        return ResponseEntity.ok().build();
    }

  /** [PATCH] 수분 기록 수정 API */
    @PatchMapping("/{waterLogId}")
    public ResponseEntity<String> updateWaterLog(
            @PathVariable Long waterLogId,
            @RequestParam Long userId,
            @RequestBody WaterLogRequestDTO request
    ) {
        waterLogService.updateWaterLog(userId, waterLogId, request);
        return ResponseEntity.ok("수분 섭취 기록 수정 완료!");
    }


}


