package com.solux.bodybubby.domain.post.service;

import com.solux.bodybubby.domain.post.entity.Hashtag;
import com.solux.bodybubby.domain.post.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    // 태그 이름 리스트를 받아 Hashtag 엔티티 리스트로 변환 (없으면 생성)
    public List<Hashtag> findOrCreateHashtags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptyList();
        }

        return tagNames.stream()
                .map(this::findOrCreateHashtags)
                .collect(Collectors.toList());
    }

    private Hashtag findOrCreateHashtags(String tagName) {
        return hashtagRepository.findByTagName(tagName)
                .orElseGet(() -> hashtagRepository.save(
                        Hashtag.builder().tagName(tagName).build()
                ));
    }
}
