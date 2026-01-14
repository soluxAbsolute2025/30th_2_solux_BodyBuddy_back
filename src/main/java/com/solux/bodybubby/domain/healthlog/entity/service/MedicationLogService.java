package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;
import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MedicationLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.repository.MedicationLogRepository;
import com.solux.bodybubby.domain.healthlog.entity.repository.MedicationPresetRepository;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.MedicationLogResponse;
import java.util.stream.Collectors;
import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Builder
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationLogService {

    private final MedicationLogRepository logRepository;
    private final MedicationPresetRepository presetRepository;

    // ✅ 1. 복용 체크 (저장)
    @Transactional
    public Long saveLog(Long userId, MedicationLogRequest request) {
        // 1-1. 무슨 약인지 찾기
        MedicationPreset preset = presetRepository.findById(request.getMedicationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약입니다."));

        // 1-2. 이미 먹었는지 확인 (중복 체크 방지)
        boolean alreadyTaken = logRepository.existsByPresetIdAndIntakeDateAndIntakeSlot(
                preset.getId(), 
                LocalDate.now(), 
                request.getIntakeSlot()
        );

        if (alreadyTaken) {
            throw new IllegalArgumentException("이미 복용 체크를 완료했습니다.");
        }

        // 1-3. 기록 저장
        MedicationLog log = MedicationLog.builder()
                .preset(preset)
                .intakeDate(LocalDate.now())          // 오늘 날짜
                .intakeSlot(request.getIntakeSlot())  // "MORNING", "LUNCH" 등
                .isTaken(true)
                .build();

        return logRepository.save(log).getId();
    }
    
    // ✅ 2. 기록 취소 (체크 해제 시 사용)
    @Transactional
    public void deleteLog(Long logId) {
        logRepository.deleteById(logId);
    }

    public void updateLog(Long userId, Long logId, MedicationLogRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateLog'");
    }

  @Transactional(readOnly = true)
public List<MedicationLogResponse> getDailyLogs(Long userId, LocalDate date) {
    // 1. 엔티티 리스트 조회
    List<MedicationLog> logs = logRepository.findAllByUserIdAndDate(userId, date);
    
    // 2. 엔티티 -> DTO 변환 (여기서 에러가 해결됨!)
    return logs.stream()
            .map(MedicationLogResponse::new) // 위에서 만든 생성자 사용
            .collect(Collectors.toList());
}

  

@Transactional 
public void cancelIntake(Long userId, Long medicationId, LocalDate date, IntakeSlot slot) {
    
    // 1. 아까 만든 @Query 메서드로 찾기 (이름이 findLogByCondition 으로 짧아졌죠?)
    MedicationLog log = logRepository.findLogByCondition(userId, medicationId, date, slot)
            .orElseThrow(() -> new IllegalArgumentException("복용 기록이 없습니다."));

    // 2. 아까 만든 엔티티 메서드로 취소 처리
    log.cancelIntake(); // (내부에서 this.isTaken = false 실행됨)
}
}
