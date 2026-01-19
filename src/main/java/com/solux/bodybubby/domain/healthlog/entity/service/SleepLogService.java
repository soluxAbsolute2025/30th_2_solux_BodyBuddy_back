package com.solux.bodybubby.domain.healthlog.entity.service;

import com.solux.bodybubby.domain.healthlog.entity.SleepLog;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.SleepLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.SleepLogUpdateRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.SleepAnalysisResponse;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.SleepLogResponse;
import com.solux.bodybubby.domain.healthlog.entity.repository.SleepLogRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SleepLogService {

    private final SleepLogRepository sleepLogRepository;
    private final UserRepository userRepository;

    /**
     * 1. 수면 기록 생성 (Create)
     */
    @Transactional
    public Long createSleepLog(Long userId, SleepLogRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. String -> 날짜/시간 변환
        LocalDate date = LocalDate.parse(request.getSleepDate());
        LocalTime bedTime = LocalTime.parse(request.getBedTime());
        LocalTime wakeTime = LocalTime.parse(request.getWakeTime());

        // 2. 날짜+시간 합치기 및 자정 처리
        LocalDateTime sleepDateTime = LocalDateTime.of(date, bedTime);
        LocalDateTime wakeDateTime = LocalDateTime.of(date, wakeTime);

        // 기상 시간이 취침 시간보다 빠르면(예: 23시 취침 -> 07시 기상), 기상 날짜는 다음날
        if (wakeTime.isBefore(bedTime)) {
            wakeDateTime = wakeDateTime.plusDays(1);
        }

        // 3. 수면 시간(분) 계산
        int totalMinutes = (int) Duration.between(sleepDateTime, wakeDateTime).toMinutes();

        // 4. 엔티티 생성 및 저장
        SleepLog sleepLog = new SleepLog(
                user,
                date.atStartOfDay(), // loggedAt (조회 기준 날짜)
                sleepDateTime,
                wakeDateTime,
                totalMinutes,
                request.getSleepQuality()
        );

        return sleepLogRepository.save(sleepLog).getId();
    }

    /**
     * 2. 수면 기록 조회 (Read - 하루)
     */
    public SleepLogResponse getSleepLog(Long userId, LocalDate date) {
        // 해당 날짜의 00:00:00 ~ 23:59:59 사이 기록 조회
        SleepLog log = sleepLogRepository.findByUserIdAndLoggedAtBetween(
                userId, 
                date.atStartOfDay(), 
                date.atTime(23, 59, 59)
        ).orElseThrow(() -> new IllegalArgumentException("해당 날짜의 수면 기록이 없습니다."));

        return new SleepLogResponse(log);
    }

    /**
     * 3. 수면 기록 수정 (Update)
     */
    @Transactional
    public void updateSleepLog(Long userId, SleepLogUpdateRequest request) {
        SleepLog log = sleepLogRepository.findById(request.getSleepRecordId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수면 기록입니다."));

        if (!log.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 수면 기록만 수정할 수 있습니다.");
        }

        // 시간 재계산 로직
        LocalDate date = log.getLoggedAt().toLocalDate(); // 날짜는 기존 날짜 유지
        LocalTime bedTime = LocalTime.parse(request.getBedTime());
        LocalTime wakeTime = LocalTime.parse(request.getWakeTime());

        LocalDateTime sleepDateTime = LocalDateTime.of(date, bedTime);
        LocalDateTime wakeDateTime = LocalDateTime.of(date, wakeTime);

        if (wakeTime.isBefore(bedTime)) {
            wakeDateTime = wakeDateTime.plusDays(1);
        }

        int totalMinutes = (int) Duration.between(sleepDateTime, wakeDateTime).toMinutes();

        // 엔티티 업데이트
        log.update(sleepDateTime, wakeDateTime, totalMinutes, request.getSleepQuality());
    }

    /**
     * 4. 수면 기록 삭제 (Delete)
     */
    @Transactional
    public void deleteSleepLog(Long userId, Long logId) {
        SleepLog log = sleepLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수면 기록입니다."));

        if (!log.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 수면 기록만 삭제할 수 있습니다.");
        }

        sleepLogRepository.delete(log);
    }

    /**
     * 5. 주간 수면 분석 (Analyze + Graph Data)
     */
    public SleepAnalysisResponse analyzeWeeklySleep(Long userId, LocalDate startDate, LocalDate endDate) {
        // 1. 기간 내 기록 조회
        List<SleepLog> logs = sleepLogRepository.findWeeklyLogs(
                userId, 
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59)
        );

        if (logs.isEmpty()) {
            return new SleepAnalysisResponse(0.0, "데이터 없음", List.of());
        }

        // 2. 평균 수면 시간 계산
        double totalMinutesSum = logs.stream()
                .mapToInt(log -> log.getTotalMinutes() != null ? log.getTotalMinutes() : 0)
                .sum();

        double averageHours = (totalMinutesSum / logs.size()) / 60.0;
        averageHours = Math.round(averageHours * 10.0) / 10.0; // 소수점 한자리 반올림

        // 3. 품질 판정
        String quality;
        if (averageHours >= 7.0) quality = "좋음";
        else if (averageHours >= 5.0) quality = "보통";
        else quality = "나쁨";

        // 4. [중요] 그래프를 그리기 위한 하루하루 데이터 리스트 변환
        List<SleepLogResponse> dailyLogs = logs.stream()
                .map(SleepLogResponse::new)
                .collect(Collectors.toList());

        // 5. 결과 반환
        return new SleepAnalysisResponse(averageHours, quality, dailyLogs);
    }
}