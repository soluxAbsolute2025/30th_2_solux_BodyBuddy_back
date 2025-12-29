package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<String> toggleLike(
            @PathVariable Long postId,
            Long userId) {
        // 유저 id 토큰으로 추출하는 로직 필요

        boolean isLiked = likeService.toggleLike(postId, userId);
        String message = isLiked ? "좋아요 성공" : "좋아요 취소";

        return ResponseEntity.ok(message);
    }
}
