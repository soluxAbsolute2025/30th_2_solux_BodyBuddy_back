package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationPresetRequest;
import com.solux.bodybubby.domain.healthlog.entity.repository.MedicationPresetRepository;

import lombok.RequiredArgsConstructor; // Builder 제거함
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // 👈 Service는 이것만 있으면 됩니다!
@Transactional(readOnly = true)
public class MedicationPresetService {

    private final MedicationPresetRepository presetRepository;

    // ✅ 1. 약 추가하기
    @Transactional
    public Long createPreset(Long userId, MedicationPresetRequest request) {
        MedicationPreset preset = MedicationPreset.builder() // 이제 오류 안 날 겁니다!
                .userId(userId)
                .name(request.getName())
                .intakeTiming(request.getTiming())
                .takeMorning(request.isTakeMorning())
                .takeLunch(request.isTakeLunch())
                .takeDinner(request.isTakeDinner())
                .build();

        return presetRepository.save(preset).getId();
    }

    // ✅ 2. 내 약 목록 조회
    public List<MedicationPreset> getMyPresets(Long userId) {
        return presetRepository.findByUserId(userId);
    }
    
    // ✅ 3. 삭제
    @Transactional
    public void deletePreset(Long presetId) {
        presetRepository.deleteById(presetId);
    }

    // ✅ 4. 수정 (괄호 안으로 잘 들어왔습니다)
    @Transactional
    public void updatePreset(Long presetId, MedicationPresetRequest request) {
        MedicationPreset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약입니다. id=" + presetId));

        // Entity에 만들어둔 update 메서드 사용
        preset.update(
                request.getName(),
                request.getTiming(),
                request.isTakeMorning(),
                request.isTakeLunch(),
                request.isTakeDinner()
        );
    }
} 