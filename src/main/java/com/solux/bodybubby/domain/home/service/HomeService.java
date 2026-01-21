package com.solux.bodybubby.domain.home.service;

import com.solux.bodybubby.domain.home.dto.response.HomeResponseDTO; // DTO 임포트 확인
import com.solux.bodybubby.domain.home.dto.response.HomeTodoListDTO;
import com.solux.bodybubby.domain.home.dto.response.TodoItemDTO;
import com.solux.bodybubby.domain.notification.entity.NotificationLog;
import com.solux.bodybubby.domain.notification.entity.NotificationRule;
import com.solux.bodybubby.domain.notification.repository.NotificationLogRepository;
import com.solux.bodybubby.domain.notification.repository.NotificationRuleRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeService {

    private final UserRepository userRepository;
    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationLogRepository notificationLogRepository;

    // [추가된 메서드] 홈 화면 메인 데이터 조회 (getHomeData)
    // 컨트롤러에서 호출하는 이름과 똑같이 만들어야 합니다.
    @Transactional(readOnly = true)
    public HomeResponseDTO getHomeData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 1. 투두 리스트 가져오기 (기존 로직 활용)
        HomeTodoListDTO todoList = getTodayTodoList(userId);

        // 2. 홈 응답 DTO 생성
        // (HomeResponseDTO의 필드 구성에 따라 .builder() 내용은 달라질 수 있습니다)
        // 예시: 닉네임과 투두리스트를 반환한다고 가정
        return HomeResponseDTO.builder()
                // .nickname(user.getNickname()) // 만약 닉네임 필드가 있다면 주석 해제
                // .progress(80) // 만약 달성률 필드가 있다면
                // .todoList(todoList) // 만약 투두리스트를 포함한다면
                .build();
    }

    // [기존 메서드] 투두 리스트 조회 (getTodayTodoList)
    @Transactional(readOnly = true)
    public HomeTodoListDTO getTodayTodoList(Long userId) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        String currentDayOfWeek = today.getDayOfWeek().toString().substring(0, 3); // MON, TUE...

        List<NotificationRule> rules = notificationRuleRepository.findAllByUser_Id(userId);

        List<TodoItemDTO> medicineList = new ArrayList<>();
        List<TodoItemDTO> waterList = new ArrayList<>();
        List<TodoItemDTO> exerciseList = new ArrayList<>();
        List<TodoItemDTO> mealList = new ArrayList<>();

        for (NotificationRule rule : rules) {
            if (Boolean.FALSE.equals(rule.getIsEnabled())) continue;

            if (rule.getRepeatDays() == null || !rule.getRepeatDays().contains(currentDayOfWeek)) {
                continue;
            }

            Optional<NotificationLog> log = notificationLogRepository.findByUserAndNotificationRuleAndDate(user, rule, today);
            boolean isChecked = log.map(NotificationLog::isCompleted).orElse(false);

            TodoItemDTO item = TodoItemDTO.builder()
                    .notificationId(rule.getId())
                    .title(rule.getLabel())
                    .time(LocalTime.parse(rule.getTimeOfDay()))
                    .isChecked(isChecked)
                    .build();

            String categoryName = rule.getCategory().getName();

            if ("MEDICINE".equals(categoryName)) medicineList.add(item);
            else if ("WATER".equals(categoryName)) waterList.add(item);
            else if ("EXERCISE".equals(categoryName)) exerciseList.add(item);
            else if ("MEAL".equals(categoryName)) mealList.add(item);
        }

        return HomeTodoListDTO.builder()
                .medicine(medicineList)
                .water(waterList)
                .exercise(exerciseList)
                .meal(mealList)
                .build();
    }

    // [기존 메서드] 투두 체크/해제
    public boolean checkTodo(Long userId, Long ruleId) {
        LocalDate today = LocalDate.now();
        User user = userRepository.findById(userId).orElseThrow();
        NotificationRule rule = notificationRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        Optional<NotificationLog> existingLog = notificationLogRepository.findByUserAndNotificationRuleAndDate(user, rule, today);

        if (existingLog.isPresent()) {
            NotificationLog log = existingLog.get();
            log.toggleStatus();
            return log.isCompleted();
        } else {
            NotificationLog newLog = NotificationLog.builder()
                    .user(user)
                    .notificationRule(rule)
                    .date(today)
                    .isCompleted(true)
                    .build();
            notificationLogRepository.save(newLog);
            return true;
        }
    }
}