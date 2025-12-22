package com.solux.bodybubby.domain.post.repository;

import com.solux.bodybubby.domain.post.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByTagName(String tagName);
}
