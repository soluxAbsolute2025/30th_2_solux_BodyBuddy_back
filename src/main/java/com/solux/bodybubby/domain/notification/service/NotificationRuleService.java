package com.solux.bodybubby.domain.notification.service;

// ▼▼▼ 1. DTO와 엔티티 임포트 (경로 확인 필수) ▼▼▼
import com.solux.bodybubby.domain.notification.dto.NotificationRuleResponseDTO;
import com.solux.bodybubby.domain.notification.dto.request.NotificationRuleRequestDTO;
import com.solux.bodybubby.domain.notification.entity.NotificationRule;
import com.solux.bodybubby.domain.notification.enums.NotificationCategory;
// [주의] NotificationCategory는 Entity와 Enum 이름이 같아서 충돌 방지를 위해 여기서 임포트 하지 않고 코드 안에서 풀네임으로 씁니다.

// ▼▼▼ 2. 레포지토리 임포트 ▼▼▼
import com.solux.bodybubby.domain.notification.repository.NotificationCategoryRepository;
import com.solux.bodybubby.domain.notification.repository.NotificationRuleRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;

// ▼▼▼ 3. 스프링/롬복 필수 임포트 ▼▼▼
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ▼▼▼ 4. [핵심] 빨간줄 원인 해결! 자바 유틸리티 임포트 ▼▼▼
// 이 부분들이 빠져서 사진처럼 오류가 났던 겁니다.
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors; 

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationRuleService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationCategoryRepository notificationCategoryRepository;
    private final UserRepository userRepository;

    // 1. 알림 등록
    public Long createNotification(Long userId, NotificationRuleRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        // 카테고리(Entity) 조회
        // request의 Enum 값을 String으로 바꿔서 DB에서 찾음
        com.solux.bodybubby.domain.notification.entity.NotificationCategory categoryEntity = 
                notificationCategoryRepository.findByName(request.getCategory().name())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        String daysStr = String.join(",", request.getRepeatDays());
        String timeStr = request.getAlarmTime().toString();

        NotificationRule rule = NotificationRule.builder()
                .user(user)
                .category(categoryEntity)
                .label(request.getLabel())
                .timeOfDay(timeStr)
                .isEnabled(request.getIsEnabled())
                .repeatDays(daysStr)
                .repeatType("WEEKLY")
                .build();

        return notificationRuleRepository.save(rule).getId();
    }

    // 2. 알림 조회 (스크린샷 오류 해결 부분)
    @Transactional(readOnly = true)
    public List<NotificationRuleResponseDTO> getNotifications(Long userId) {
        // findAllByUser_Id: User 엔티티 안의 ID로 조회
        return notificationRuleRepository.findAllByUser_Id(userId).stream()
                .map(rule -> {
                    // (1) 요일 변환 Logic
                    Set<String> daysSet = new HashSet<>();
                    // getRepeatDays()가 빨간줄이면 Entity에 @Getter가 없는 것입니다.
                    if (rule.getRepeatDays() != null && !rule.getRepeatDays().isEmpty()) {
                        String[] split = rule.getRepeatDays().split(",");
                        daysSet.addAll(Arrays.asList(split));
                    }

                    // (2) 시간 변환 Logic
                    LocalTime time = null;
                    if (rule.getTimeOfDay() != null) {
                        time = LocalTime.parse(rule.getTimeOfDay());
                    }

                    // (3) 카테고리 변환 (Entity -> Enum)
                    // 충돌 방지를 위해 패키지명 전체 작성
                    NotificationCategory categoryEnum = 
                            NotificationCategory.valueOf(rule.getCategory().getName());

                    // (4) DTO 빌더
                    return NotificationRuleResponseDTO.builder()
                            .alarmId(rule.getId())
                            .category(categoryEnum)
                            .label(rule.getLabel())
                            .alarmTime(time)
                            .isEnabled(rule.getIsEnabled())
                            .repeatDays(daysSet)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 3. 알림 수정
    public void updateNotification(Long userId, Long alarmId, NotificationRuleRequestDTO request) {
        NotificationRule rule = notificationRuleRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 없습니다."));

        if (!rule.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        String daysStr = String.join(",", request.getRepeatDays());
        String timeStr = request.getAlarmTime().toString();

        rule.update(timeStr, request.getIsEnabled(), daysStr);
    }

    // 4. 알림 삭제
    public void deleteNotification(Long userId, Long alarmId) {
        NotificationRule rule = notificationRuleRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 없습니다."));

        if (!rule.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        notificationRuleRepository.delete(rule);
    }
}