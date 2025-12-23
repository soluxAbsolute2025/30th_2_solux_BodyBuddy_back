package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.dto.request.PostRequestDto;
import com.solux.bodybubby.domain.post.dto.response.PostResponseDto;
import com.solux.bodybubby.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class PostController {

    // 유저 인증(토큰) 추가 필요
    private  final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody PostRequestDto dto) {
        return ResponseEntity.ok(postService.createPost(dto));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDto> responses = postService.getAllPosts(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getOnePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto dto) {
        postService.updatePost(postId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hashtag")
    public ResponseEntity<Page<PostResponseDto>> getPostsByHashtag(
            @RequestParam String tagName,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDto> responses = postService.getPostsByHashtag(tagName, pageable);
        return ResponseEntity.ok(responses);
    }
}
