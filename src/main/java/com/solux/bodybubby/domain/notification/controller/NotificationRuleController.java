package com.solux.bodybubby.domain.notification.controller;

import com.solux.bodybubby.domain.notification.dto.NotificationRuleResponseDTO;
import com.solux.bodybubby.domain.notification.dto.request.NotificationRuleRequestDTO;
import com.solux.bodybubby.domain.notification.service.NotificationRuleService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-rule") // 요청하신 URL로 고정
@RequiredArgsConstructor
public class NotificationRuleController {

    private final NotificationRuleService notificationRuleService;

    // 1. 알림 등록
    @PostMapping
    public ResponseEntity<String> createNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NotificationRuleRequestDTO request
    ) {
        notificationRuleService.createNotification(userDetails.getId(), request);
        return ResponseEntity.ok("알림이 등록되었습니다.");
    }

    // 2. 알림 조회
    @GetMapping
    public ResponseEntity<List<NotificationRuleResponseDTO>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(notificationRuleService.getNotifications(userDetails.getId()));
    }

    // 3. 알림 수정
    @PutMapping("/{alarmId}")
    public ResponseEntity<String> updateNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long alarmId,
            @RequestBody NotificationRuleRequestDTO request
    ) {
        notificationRuleService.updateNotification(userDetails.getId(), alarmId, request);
        return ResponseEntity.ok("알림이 수정되었습니다.");
    }

    // 4. 알림 삭제
    @DeleteMapping("/{alarmId}")
    public ResponseEntity<String> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long alarmId
    ) {
        notificationRuleService.deleteNotification(userDetails.getId(), alarmId);
        return ResponseEntity.ok("알림이 삭제되었습니다.");
    }
}