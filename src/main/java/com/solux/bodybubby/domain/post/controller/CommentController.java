package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.dto.request.CommentRequestDto;
import com.solux.bodybubby.domain.post.service.CommentService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long commentId = commentService.createComment(postId, dto.getContent(), userDetails.getId());
        return ResponseEntity.ok(commentId);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(commentId, dto.getContent(), userDetails.getId());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.ok().build();

    }
}
