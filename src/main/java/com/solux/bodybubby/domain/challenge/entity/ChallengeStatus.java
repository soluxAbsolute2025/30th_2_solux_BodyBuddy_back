package com.solux.bodybubby.domain.challenge.entity;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    RECRUITING("모집중"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    CLOSED("종료");

    private final String description;

    ChallengeStatus(String description) {
        this.description = description;
    }
}
