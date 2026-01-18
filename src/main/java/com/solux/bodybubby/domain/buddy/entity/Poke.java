package com.solux.bodybubby.domain.buddy.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Poke {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poker_id") // 찌른 사람
    private User poker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poked_id") // 찔린 사람
    private User poked;

    private LocalDateTime pokedAt; // 찌른 시간
}