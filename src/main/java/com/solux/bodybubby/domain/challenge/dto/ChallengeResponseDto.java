package com.solux.bodybubby.domain.challenge.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeResponseDto<T> {
    private int status;      // 200, 400 등 상태 코드
    private String message;  // "성공", "인증 완료" 등 메시지
    private T data;          // 실제 반환할 데이터 객체
}