package com.solux.bodybubby.domain.post.repository;

import com.solux.bodybubby.domain.post.entity.Hashtag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByTagName(String tagName);
    List<Hashtag> findAllByTagNameIn(Collection<String> tagNames);

    @Query("SELECT h.tagName FROM PostHashtag ph " +
            "JOIN ph.hashtag h " +
            "GROUP BY h.id " +
            "ORDER BY COUNT(ph.id) DESC")
    List<String> findTop5PopularTags(Pageable pageable);
}
