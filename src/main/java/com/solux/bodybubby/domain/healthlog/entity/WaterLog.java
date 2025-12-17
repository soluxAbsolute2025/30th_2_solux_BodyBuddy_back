package com.solux.bodybubby.domain.healthlog.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "water_log")
public class WaterLog extends HealthLog {

    @Column(name = "amount_ml")
    private Integer amountMl;

    protected WaterLog() {
    }

    public WaterLog(User user, LocalDateTime loggedAt, Integer amountMl) {
        super(user, loggedAt);
        this.amountMl = amountMl;
    }
}