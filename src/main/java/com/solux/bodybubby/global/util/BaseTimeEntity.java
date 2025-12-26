package com.solux.bodybubby.global.util;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 공통 요소인 생성일, 수정일을 관리하는 추상 클래스입니다.
 * 이 클래스를 상속받으면 해당 엔티티의 테이블에 created_at, updated_at 컬럼이 자동 생성됩니다.
 */
@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {

    @Column(name = "created_at", updatable = false)// 생성일은 한 번 저장되면 수정되지 않도록 설정
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 엔티티가 처음 저장(Persist)되기 직전에 실행됩니다.
     * 생성일과 수정일을 현재 시간으로 초기화합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티의 내용이 변경(Update)되기 직전에 실행됩니다.
     * 수정일을 현재 시간으로 갱신합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
