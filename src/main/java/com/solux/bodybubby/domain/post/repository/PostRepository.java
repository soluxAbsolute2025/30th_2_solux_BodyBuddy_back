package com.solux.bodybubby.domain.post.repository;

import com.solux.bodybubby.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "JOIN p.postHashtags ph " +
            "JOIN ph.hashtag h " +
            "WHERE h.tagName = :tagName AND p.deletedAt IS NULL")
    Page<Post> findAllByHashtagName(@Param("tagName") String tagName, Pageable pageable);
}
