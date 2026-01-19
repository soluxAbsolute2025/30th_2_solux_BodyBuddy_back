package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationPresetRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationPresetResponse;
import com.solux.bodybubby.domain.healthlog.entity.repository.MedicationPresetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationPresetService {

    private final MedicationPresetRepository presetRepository;

    // 1. 등록
    @Transactional
    public Long createPreset(Long userId, MedicationPresetRequest request) {
        MedicationPreset preset = MedicationPreset.builder()
                .userId(userId)
                .name(request.getName())
                .intakeTiming(request.getTiming())
                .takeMorning(request.isTakeMorning())
                .takeLunch(request.isTakeLunch())
                .takeDinner(request.isTakeDinner())
                .build();
        return presetRepository.save(preset).getId();
    }

    // 2. 조회
    public List<MedicationPresetResponse> getMyPresets(Long userId) {
        return presetRepository.findByUserId(userId).stream()
                .map(MedicationPresetResponse::from)
                .collect(Collectors.toList());
    }

    // 3. 수정
    @Transactional
    public void updatePreset(Long userId, Long presetId, MedicationPresetRequest request) {
        MedicationPreset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약입니다."));

        if (!preset.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 약 정보만 수정할 수 있습니다.");
        }

        preset.update(
                request.getName(),
                request.getTiming(),
                request.isTakeMorning(),
                request.isTakeLunch(),
                request.isTakeDinner()
        );
    }

    // 4. 삭제
    @Transactional
    public void deletePreset(Long userId, Long presetId) {
        MedicationPreset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약입니다."));

        if (!preset.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 약 정보만 삭제할 수 있습니다.");
        }
        
        presetRepository.delete(preset);
    }
}