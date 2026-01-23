package com.solux.bodybubby.domain.challenge.entity;

import lombok.Getter;

@Getter
public enum Visibility {
    PUBLIC("전체 공개"),
    SECRET("비공개");

    private final String description;

    Visibility(String description) {
        this.description = description;
    }
}