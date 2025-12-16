package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "meal_log")
public class MealLog extends HealthLog {

    @Column(name = "meal_type")
    private String mealType; // BREAKFAST, LUNCH, DINNER

    @Column(name = "memo")
    private String memo;

    @Column(name = "photo_url")
    private String photoUrl;

    protected MealLog() {
    }

    public MealLog(
            User user,
            LocalDateTime loggedAt,
            String mealType,
            String memo,
            String photoUrl
    ) {
        super(user, loggedAt);
        this.mealType = mealType;
        this.memo = memo;
        this.photoUrl = photoUrl;
    }
}