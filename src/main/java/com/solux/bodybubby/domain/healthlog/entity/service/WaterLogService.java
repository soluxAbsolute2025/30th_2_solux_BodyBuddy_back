package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.WaterLog;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.WaterLogRequestDTO;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.WaterLogResponseDTO;
import com.solux.bodybubby.domain.healthlog.entity.repository.WaterLogRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WaterLogService {

    private final WaterLogRepository waterLogRepository;
    private final UserRepository userRepository;

    /**
     * [저장] 수분 섭취 기록하기
     */
    public void saveWaterLog(Long userId, WaterLogRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // DTO의 LocalDate를 LocalDateTime으로 변환 (시간은 현재시간 혹은 00:00)
        LocalDateTime loggedAt = (request.getRecordDate() != null) 
                ? request.getRecordDate().atStartOfDay() 
                : LocalDateTime.now();

        WaterLog log = WaterLog.builder()
                .user(user)
                .amountMl(request.getMlAmount())
                .loggedAt(loggedAt)
                .build();
        waterLogRepository.save(log);
    }

    /**
     * [상세 조회] 단건 조회
     */
    @Transactional(readOnly = true)
    public WaterLogResponseDTO getWaterLogDetail(Long waterLogId) {
        WaterLog waterLog = waterLogRepository.findById(waterLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다."));

        return new WaterLogResponseDTO(
                waterLog.getId(),
                waterLog.getAmountMl(),
                waterLog.getLoggedAt()
        );
    }

    /**
     * [일별 조회]
     */
    @Transactional(readOnly = true)
    public List<WaterLogResponseDTO> getDailyWaterLogs(Long userId, LocalDate targetDate) {
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        List<WaterLog> logs = waterLogRepository.findAllByUserIdAndLoggedAtBetween(userId, start, end);

        return logs.stream()
                .map(log -> new WaterLogResponseDTO(
                        log.getId(),
                        log.getAmountMl(),
                        log.getLoggedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * [주간 조회]
     */
    @Transactional(readOnly = true)
    public Map<LocalDate, Integer> getWeeklyWaterLogs(Long userId) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(6);

        List<WaterLog> logs = waterLogRepository.findAllByUserIdAndLoggedAtBetween(userId, start, end);

        return logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getLoggedAt().toLocalDate(),
                        Collectors.summingInt(WaterLog::getAmountMl)
                ));
    }

    /**
     * [삭제]
     */
    public void deleteWaterLog(Long waterLogId, Long userId) {
        WaterLog waterLog = waterLogRepository.findById(waterLogId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));

        if (!waterLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        waterLogRepository.delete(waterLog);
    }

    /**
     * [수정] 여기가 수정되었습니다!
     */
    public WaterLogResponseDTO updateWaterLog(Long userId, Long waterLogId, WaterLogRequestDTO request) {
        // 1. 조회
        WaterLog waterLog = waterLogRepository.findById(waterLogId)
                .orElseThrow(() -> new IllegalArgumentException("기록 없음 id=" + waterLogId));

        // 2. 권한 확인
        if (!waterLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 3. 날짜 타입 변환 (LocalDate -> LocalDateTime)
        LocalDateTime dateToUpdate = waterLog.getLoggedAt(); // 기본값: 기존 시간 유지
        if (request.getRecordDate() != null) {
            // 날짜가 들어왔으면 그 날짜의 00:00:00으로 변경
            dateToUpdate = request.getRecordDate().atStartOfDay();
        }

        // 4. 업데이트 실행
       waterLog.update(request.getMlAmount(), request.getRecordDate());

    // 4. 반환
    return new WaterLogResponseDTO(
            waterLog.getId(),
            waterLog.getAmountMl(),
            waterLog.getLoggedAt()
    );
}
}