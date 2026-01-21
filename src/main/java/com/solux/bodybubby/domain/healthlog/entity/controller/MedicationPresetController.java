package com.solux.bodybubby.domain.healthlog.entity.controller;

import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationPresetRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationPresetResponse;
import com.solux.bodybubby.domain.healthlog.entity.service.MedicationPresetService; // 👈 이 부분이 추가되었습니다!
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication-preset")
public class MedicationPresetController {

    private final MedicationPresetService medicationPresetService;

    // 1. 등록
    @PostMapping
    public ResponseEntity<Long> createPreset(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MedicationPresetRequest request) {
        Long presetId = medicationPresetService.createPreset(userDetails.getId(), request);
        return ResponseEntity.ok(presetId);
    }

    // 2. 조회 (DTO 반환으로 변경 ✅)
    @GetMapping
    public ResponseEntity<List<MedicationPresetResponse>> getMyPresets(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(medicationPresetService.getMyPresets(userDetails.getId()));
    }

    // 3. 수정
    @PatchMapping("/{presetId}")
    public ResponseEntity<String> updatePreset(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long presetId,
            @RequestBody MedicationPresetRequest request) {
        medicationPresetService.updatePreset(userDetails.getId(), presetId, request);
        return ResponseEntity.ok("약 정보가 수정되었습니다.");
    }

    // 4. 삭제
    @DeleteMapping("/{presetId}")
    public ResponseEntity<String> deletePreset(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long presetId) {
        medicationPresetService.deletePreset(userDetails.getId(), presetId);
        return ResponseEntity.ok("약 정보가 삭제되었습니다.");
    }
}