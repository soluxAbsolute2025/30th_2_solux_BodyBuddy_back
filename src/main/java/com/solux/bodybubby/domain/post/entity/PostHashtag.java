package com.solux.bodybubby.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "post_hashtags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "hashtag_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;
}