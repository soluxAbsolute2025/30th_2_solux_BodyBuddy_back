package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feeds/hashtag")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularTags() {
        return ResponseEntity.ok(hashtagService.getPopularTags());
    }
}
