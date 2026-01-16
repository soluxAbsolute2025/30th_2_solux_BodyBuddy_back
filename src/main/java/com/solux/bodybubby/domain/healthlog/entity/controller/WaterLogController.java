package com.solux.bodybubby.domain.healthlog.entity.controller;

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

    private final WaterLogService waterLogService;

    // 1. 수분 기록 추가
    // POST /api/water-log
    @PostMapping
    public ResponseEntity<String> createWaterLog(@RequestBody WaterLogRequestDTO request) {
        waterLogService.saveWaterLog(request);
        return ResponseEntity.ok("수분 섭취 기록 저장 완료!");
    }

    // 2. 수분 기록 상세 조회
    // GET /api/water-log/{id}
    @GetMapping("/{waterLogId}")
    public ResponseEntity<WaterLogResponseDTO> getWaterLogDetail(@PathVariable Long waterLogId) {
        return ResponseEntity.ok(waterLogService.getWaterLogDetail(waterLogId));
    }

    // 3. 오늘의 수분 기록 조회 (날짜 선택 가능)
    // GET /api/water-log/today?date=2025-12-30
    @GetMapping("/today")
    public ResponseEntity<List<WaterLogResponseDTO>> getTodayLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // date가 없으면 오늘 날짜(LocalDate.now()) 사용
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        
        // userId 1L 고정 (Security 적용 시 수정)
        return ResponseEntity.ok(waterLogService.getDailyWaterLogs(1L, targetDate));
    }

    // 4. 주간 수분 섭취량 조회
    // GET /api/water-log/weekly
    @GetMapping("/weekly")
    public ResponseEntity<Map<LocalDate, Integer>> getWeeklyLogs() {
        return ResponseEntity.ok(waterLogService.getWeeklyWaterLogs(1L));
    }

    // 5. 수분 기록 삭제
    // DELETE /api/water-log/{id}?userId=1
    @DeleteMapping("/{waterLogId}")
    public ResponseEntity<Void> deleteWaterLog(
            @PathVariable Long waterLogId,
            @RequestParam Long userId
    ) {
        waterLogService.deleteWaterLog(waterLogId, userId);
        return ResponseEntity.ok().build();
    }
}