package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalCheckInResponse {
    private Long logId;               // 생성된 로그 ID
    private Integer currentProgress;   // 인증 후 진행도 (회차)
    private Integer achievementRate;   // 업데이트된 달성률 (%)
    private Integer earnedPoints;      // 이번 인증으로 획득한 포인트 (10 XP)
    private boolean isCompleted;       // 이번 인증으로 완료되었는지 여부
}