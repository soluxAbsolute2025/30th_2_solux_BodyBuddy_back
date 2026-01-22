package com.solux.bodybubby.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateGroupResponse {
    private int status;
    private Long groupId;
    private String groupCode;  // "ABC123Z" (자동 생성된 그룹 코드)
    private String message;    // "그룹 챌린지가 생성되었습니다."
}