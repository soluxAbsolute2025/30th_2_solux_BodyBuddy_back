package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // 이거 하나면 getPresetId(), getIntakeSlot() 자동 생성됨!
@NoArgsConstructor // JSON 데이터를 받기 위해 필수
public class MedicationLogRequest {
    
    private Long medicationId;   // 어떤 약인지 (예: 1번 약)
    
    // ✅ "MORNING" 같은 글자를 자동으로 Enum으로 변환해서 받습니다.
    private IntakeSlot intakeSlot; 
}