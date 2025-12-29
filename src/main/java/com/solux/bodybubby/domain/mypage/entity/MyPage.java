package com.solux.bodybubby.domain.mypage.entity;

import com.solux.bodybubby.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "my_page")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MyPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 사용자 (1:1) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "status_message", length = 100)
    private String statusMessage; // 예)"건강한 라이프스타일 실천 중"

    @Column(length = 1)
    private String gender;

    private java.time.LocalDate birthdate;

    private BigDecimal height;
    private BigDecimal weight;

    private Integer level;

    @Column(name = "cumulative_points")
    private Integer cumulativePoints; // 누적 활동 지표(exp_points)

    @Column(name = "points_balance")
    private Integer pointsBalance;
}