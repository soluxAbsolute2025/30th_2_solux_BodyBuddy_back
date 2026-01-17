package com.solux.bodybubby.domain.healthlog.entity.service; // 경로가 맞는지 꼭 확인하세요!

import com.solux.bodybubby.domain.healthlog.entity.MealLog;
import com.solux.bodybubby.domain.healthlog.entity.WaterLog;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.MealLogRequest;
import com.solux.bodybubby.domain.healthlog.entity.dto.request.WaterLogRequestDTO;
import com.solux.bodybubby.domain.healthlog.entity.dto.response.WaterLogResponseDTO;
import com.solux.bodybubby.domain.healthlog.entity.repository.WaterLogRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Builder
@Service
@RequiredArgsConstructor
@Transactional
public class WaterLogService {

    private final WaterLogRepository waterLogRepository;
    private final UserRepository userRepository;

    /**
     * [저장] 수분 섭취 기록하기
     */
    public void saveWaterLog(WaterLogRequestDTO request) {
        // 실제 운영시는 SecurityContextHolder에서 userId를 가져와야 하지만, 
        // 테스트를 위해 일단 1번 유저를 찾습니다.
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다. DB에 유저가 있는지 확인해주세요."));

        WaterLog waterLog = WaterLog.builder()
                .user(user)
                .amountMl(request.getMlAmount()) 
                // 기록 날짜가 없으면 현재 시간으로 저장하도록 방어 코드 추가
                .loggedAt(request.getRecordDate() != null ? 
                          request.getRecordDate().atTime(LocalTime.now()) : LocalDateTime.now()) 
                .build();

        waterLogRepository.save(waterLog);
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
     * [일별 조회] 특정 날짜의 기록 리스트 (today API에서 호출)
     */
    @Transactional(readOnly = true)
    public List<WaterLogResponseDTO> getDailyWaterLogs(Long userId, LocalDate date) {
        // 해당 날짜의 00:00:00 ~ 23:59:59 범위 설정
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

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
     * [주간 조회] 최근 7일간 일별 합계
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
     * [삭제] 기록 삭제
     */
    public void deleteWaterLog(Long waterLogId, Long userId) {
        WaterLog waterLog = waterLogRepository.findById(waterLogId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기록입니다."));

        if (!waterLog.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        waterLogRepository.delete(waterLog);
    }

    

@Transactional
public WaterLogResponseDTO updateWaterLog(Long userId, Long waterLogId, WaterLogRequestDTO request) {

    // 1. 기존 객체를 디비에서 가져오기
    WaterLog waterLog = waterLogRepository.findById(waterLogId)
            .orElseThrow(() -> new IllegalArgumentException("기록 없음 id=" + waterLogId));

    // 2. 권한 확인
    if (!waterLog.getUser().getId().equals(userId)) {
        throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    // 3. WaterLog 전용 update 메서드 호출
    // 식단(MealLog)과 달리 음식 합치기(String.join) 로직이 필요 없음
    waterLog.update(
        request.getMlAmount(), 
        request.getRecordDate()
    );

    // 4. 수정된 결과 반환 (기존에 있다고 하신 ResponseDTO 활용)
    return new WaterLogResponseDTO(
        waterLog.getId(),
        waterLog.getAmountMl(),
        waterLog.getLoggedAt()
    );
}
}

