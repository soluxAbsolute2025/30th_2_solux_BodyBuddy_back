package com.solux.bodybubby.domain.post.repository;

import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.entity.PostLike;
import com.solux.bodybubby.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
}
