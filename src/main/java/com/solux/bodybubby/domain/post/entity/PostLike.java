package com.solux.bodybubby.domain.post.entity;

import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private LocalDateTime createdAt;

    @Builder
    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}