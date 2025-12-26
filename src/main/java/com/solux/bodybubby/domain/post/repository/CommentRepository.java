package com.solux.bodybubby.domain.post.repository;

import com.solux.bodybubby.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}
