package com.solux.bodybubby.domain.healthlog.entity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 변환을 위해 기본 생성자 필수!
public class MedicationPresetRequest {

    private String name;        // 약 이름 (예: "종합비타민")
    
    private String timing;      // 복용 시간/방법 (예: "식후", "식전", "공복")
    
    // ✅ 디자인의 '1일 복용 횟수' 체크박스와 1:1 매칭
    private boolean takeMorning; // 아침 체크 여부
    private boolean takeLunch;   // 점심 체크 여부
    private boolean takeDinner;  // 저녁 체크 여부
}