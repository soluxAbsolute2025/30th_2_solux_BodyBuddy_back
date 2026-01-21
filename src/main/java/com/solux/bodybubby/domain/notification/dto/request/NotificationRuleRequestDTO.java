package com.solux.bodybubby.domain.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solux.bodybubby.domain.notification.enums.NotificationCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRuleRequestDTO {

    

    @Schema(description = "카테고리 (MEAL, MEDICINE, EXERCISE, WATER)", example = "MEAL")
    private NotificationCategory category;

    @Schema(description = "알림 이름(라벨)", example = "아침 식사 알림")
    private String label;

    @Schema(description = "알림 시간 (HH:mm)", example = "07:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime alarmTime;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isEnabled;

    @Schema(description = "반복 요일", example = "[\"MON\", \"TUE\"]")
    private Set<String> repeatDays;
}