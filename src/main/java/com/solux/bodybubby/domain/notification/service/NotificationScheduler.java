package com.solux.bodybubby.domain.notification.service;

import com.solux.bodybubby.domain.notification.entity.NotificationRule;
import com.solux.bodybubby.domain.notification.repository.NotificationRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationRuleRepository notificationRuleRepository;

    // 1분마다 실행 (초 분 시 일 월 요일)
    // 0초 0분 * * * * -> 매 시 0분 0초에 실행 (x) -> 매 분 0초에 실행하고 싶으면 "0 * * * * *"
    @Scheduled(cron = "0 * * * * *") 
    @Transactional(readOnly = true)
    public void checkAndSendNotifications() {
        // 1. 현재 시간 구하기 (예: "08:00")
        String nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        // 2. 현재 요일 구하기 (예: "MON", "TUE") - 영어 약어, 대문자
        String currentDay = LocalDate.now().getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                .toUpperCase();

        log.info("⏰ 스케줄러 실행 중... 현재 시간: {}, 요일: {}", nowTime, currentDay);

        // 3. DB에서 '시간'이 같고 '켜져있는(ON)' 알림들 다 가져오기
        List<NotificationRule> rules = notificationRuleRepository.findByTimeOfDayAndIsEnabledTrue(nowTime);

        for (NotificationRule rule : rules) {
            // 4. 요일 체크 (DB에는 "MON,WED,FRI" 문자열로 저장되어 있음)
            if (rule.getRepeatDays().contains(currentDay)) {
                
                // 5. 조건 만족! 알림 발송
                sendPushNotification(rule);
            }
        }
    }

    // 실제 알림 발송 로직 (FCM 등)
    private void sendPushNotification(NotificationRule rule) {
        // 여기에 실제 FCM 전송 코드가 들어갑니다.
        // 지금은 로그로 확인
        System.out.println("========================================");
        System.out.println("🚀 [알림 발송] 유저: " + rule.getUser().getNickname());
        System.out.println("📩 내용: " + rule.getLabel() + " (" + rule.getCategory().getName() + ")");
        System.out.println("========================================");
    }
}