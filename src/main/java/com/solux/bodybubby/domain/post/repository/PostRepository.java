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
            "WHERE h.tagName = :tagName " +
            "AND p.deletedAt IS NULL " +
            "AND (p.visibility = 'PUBLIC' OR p.user.id = :currentUserId)")
    Page<Post> findAllByHashtagName(
            @Param("tagName") String tagName,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE (p.visibility = 'PUBLIC' OR p.user.id = :currentUserId) " +
            "AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findAllByKeyword(
            @Param("keyword") String keyword,
            @Param("currentUserId") Long currentUserId,
            Pageable pageable);
}
