package com.solux.bodybubby.domain.notification.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_rule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 카테고리 (Entity로 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private NotificationCategory category;

    // ▼▼▼ [화면 기능을 위해 추가/수정된 필드] ▼▼▼
    
    private String label; // 예: "아침 식사 알림"

    private Boolean isEnabled; // ON/OFF 여부 (화면 토글)

    // "07:00" 같은 문자열로 저장 (기존 timeOfDay 활용)
    @Column(length = 20)
    private String timeOfDay;

    // 요일 반복 (화면에서 '월,화,수' 복수 선택 하므로 문자열로 저장 추천)
    // 예: "MON,TUE,WED"
    private String repeatDays; 

    // (기존 필드 유지 - 필요 없다면 삭제해도 됨)
    @Column(length = 20)
    private String repeatType; 
    private Integer intervalMinutes;
    private String intervalStart;
    private String intervalEnd;

    // [수정 메서드]
    public void update(String timeOfDay, Boolean isEnabled, String repeatDays) {
        if (timeOfDay != null) this.timeOfDay = timeOfDay;
        if (isEnabled != null) this.isEnabled = isEnabled;
        if (repeatDays != null) this.repeatDays = repeatDays;
    }
}