package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter // 이게 있어야 getPresetId(), getIntakeDate() 등이 자동 생성됨
@NoArgsConstructor
public class MedicationLogRequest {
    
    // 1. 서비스에서 getPresetId()로 부르려면 변수명도 presetId여야 함
    private Long presetId;   
    
    // 2. 서비스에서 getIntakeDate()로 부르려면 이 변수가 있어야 함
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate intakeDate;
    
    // 3. 서비스에서 getIntakeTime()로 부르려면 이 변수가 있어야 함
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime intakeTime;

    // 4. getIntakeSlot()
    private IntakeSlot intakeSlot; 
}