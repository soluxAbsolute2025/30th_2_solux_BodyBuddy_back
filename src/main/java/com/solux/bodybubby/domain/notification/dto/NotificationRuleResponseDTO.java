package com.solux.bodybubby.domain.notification.dto;

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
public class NotificationRuleResponseDTO {

    private Long alarmId;

    private NotificationCategory category;

    private String label;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime alarmTime;

    private Boolean isEnabled;

    private Set<String> repeatDays;
}