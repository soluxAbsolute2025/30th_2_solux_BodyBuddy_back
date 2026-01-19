package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;
import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.repository.MedicationLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.repository.MedicationPresetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationLogService {

    private final MedicationLogRepository logRepository;
    private final MedicationPresetRepository presetRepository;

    // ✅ 1. 복용 체크 (날짜/시간 직접 입력 방식)
    @Transactional
    public Long saveLog(Long userId, MedicationLogRequest request) {
        // medicationId -> presetId 로 변경된 것 반영
        MedicationPreset preset = presetRepository.findById(request.getPresetId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약입니다."));

        if (!preset.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 약만 복용 체크할 수 있습니다.");
        }

        // 사용자가 보낸 날짜로 중복 체크 (오늘 날짜 강제 아님!)
        boolean alreadyTaken = logRepository.existsByPresetIdAndIntakeDateAndIntakeSlot(
                preset.getId(),
                request.getIntakeDate(), // ✅ request 날짜 사용
                request.getIntakeSlot()
        );

        if (alreadyTaken) {
            throw new IllegalArgumentException("이미 해당 날짜/시간대에 복용 체크를 완료했습니다.");
        }

        MedicationLog log = MedicationLog.builder()
                .preset(preset)
                .intakeDate(request.getIntakeDate()) // ✅ 날짜 저장
                .intakeTime(request.getIntakeTime()) // ✅ 시간 저장
                .intakeSlot(request.getIntakeSlot())
                .isTaken(true)
                .build();

        return logRepository.save(log).getId();
    }
    
    // ... 나머지 취소, 조회 메서드는 그대로 유지 ...
    @Transactional
    public void cancelIntake(Long userId, Long medicationId, LocalDate date, IntakeSlot slot) {
        MedicationLog log = logRepository.findLogByCondition(userId, medicationId, date, slot)
                .orElseThrow(() -> new IllegalArgumentException("해당 복용 기록이 없습니다."));
        logRepository.delete(log);
    }

    public List<MedicationLogResponse> getDailyLogs(Long userId, LocalDate date) {
        List<MedicationLog> logs = logRepository.findAllByUserIdAndDate(userId, date);
        return logs.stream()
                .map(MedicationLogResponse::new)
                .collect(Collectors.toList());
    }
}