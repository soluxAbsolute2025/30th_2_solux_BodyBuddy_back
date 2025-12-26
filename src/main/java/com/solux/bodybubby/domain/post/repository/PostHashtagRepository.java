package com.solux.bodybubby.domain.post.repository;

import com.solux.bodybubby.domain.post.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    void deleteByPostId(Long postId);
}
