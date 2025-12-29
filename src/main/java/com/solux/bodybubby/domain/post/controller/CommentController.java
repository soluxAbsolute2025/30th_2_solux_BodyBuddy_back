package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.dto.request.CommentRequestDto;
import com.solux.bodybubby.domain.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Long> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto dto,
            Long userId) {

        // 작성자를 userId가 아닌 토큰에서 userId 추출하는 것으로 수정 필요
        Long commentId = commentService.createComment(postId, dto.getContent(), userId);
        return ResponseEntity.ok(commentId);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto dto,
            Long userId) {

        // 작성자를 userId가 아닌 토큰에서 userId 추출하는 것으로 수정 필요
        commentService.updateComment(commentId, dto.getContent(), userId);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Long userId) {

        // 작성자를 userId가 아닌 토큰에서 userId 추출하는 것으로 수정 필요
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();

    }
}
