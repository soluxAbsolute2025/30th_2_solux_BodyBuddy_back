package com.solux.bodybubby.domain.healthlog.entity.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationPresetRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationPresetResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.MedicationPresetService;

// 나중에 토큰 기능 완성되면 아래 임포트 주석 다 해제하면됨
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication-preset")
public class MedicationPresetController {

    private final MedicationPresetService medicationPresetService;

    // 1. 등록 (Create)
    // POST http://localhost:8080/api/medication-preset?userId=1
    @PostMapping
    public ResponseEntity<Long> createPreset(
            @RequestParam Long userId, 
            @RequestBody MedicationPresetRequest request) {
        Long presetId = medicationPresetService.createPreset(userId, request);
        return ResponseEntity.ok(presetId);
    }

    // 2. 조회 (Read - 내 약 목록 보기)
    // GET http://localhost:8080/api/medication-preset?userId=1
    @GetMapping
    public ResponseEntity<List<MedicationPreset>> getMyPresets(@RequestParam Long userId) {
        return ResponseEntity.ok(medicationPresetService.getMyPresets(userId));
    }

    // 3. 수정 (Update)
    // PATCH http://localhost:8080/api/medication-preset/{presetId}
    @PatchMapping("/{presetId}")
    public ResponseEntity<String> updatePreset(
            @PathVariable Long presetId,
            @RequestBody MedicationPresetRequest request) {
        medicationPresetService.updatePreset(presetId, request);
        return ResponseEntity.ok("약 정보가 수정되었습니다.");
    }

    // 4. 삭제 (Delete)
    // DELETE http://localhost:8080/api/medication-preset/{presetId}
    @DeleteMapping("/{presetId}")
    public ResponseEntity<String> deletePreset(@PathVariable Long presetId) {
        medicationPresetService.deletePreset(presetId);
        return ResponseEntity.ok("약 정보가 삭제되었습니다.");
    }
}