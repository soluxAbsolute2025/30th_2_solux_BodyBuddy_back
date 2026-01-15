package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealLog extends HealthLog {

    @Column(name = "meal_type")
    private String mealType;

    @Column(name = "intake_date")
    private LocalDate intakeDate; // 필드 추가

    @Column(name = "intake_time")
    private String intakeTime;   // 필드 추가

    @Column(name = "memo")
    private String memo;

    @Column(name = "photo_url")
    private String photoUrl;

    // 생성자에 @Builder를 붙이면 부모 필드(user, loggedAt)까지 한 번에 처리 가능합니다!
    @Builder
    public MealLog(User user, LocalDateTime loggedAt, String mealType, 
                   LocalDate intakeDate, String intakeTime, String memo, String photoUrl) {
        super(user, loggedAt); // 부모 생성자 호출
        this.mealType = mealType;
        this.intakeDate = intakeDate;
        this.intakeTime = intakeTime;
        this.memo = memo;
        this.photoUrl = photoUrl;
    }

    // 우리가 아까 약속한 수정 메서드 (Setter 대신 사용)
    public void update(String mealType, LocalDate intakeDate, String intakeTime, String memo, String photoUrl) {
        this.mealType = mealType;
        this.intakeDate = intakeDate;
        this.intakeTime = intakeTime;
        this.memo = memo;
        this.photoUrl = photoUrl;
    }
}