package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.dto.request.PostRequestDto;
import com.solux.bodybubby.domain.post.dto.response.PostResponseDto;
import com.solux.bodybubby.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class PostController {

    // 유저 인증(토큰) 추가 필요
    private  final PostService postService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createPost(
            @RequestPart("request") PostRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId) {
        return ResponseEntity.ok(postService.createPost(dto, image, currentUserId));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDto> responses = postService.getAllPosts(currentUserId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getOnePost(
            @PathVariable Long postId,
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId) {
        return ResponseEntity.ok(postService.getPost(postId, currentUserId));
    }

    @PatchMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @RequestPart("request") PostRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId) {
        postService.updatePost(postId, dto, image, currentUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId) {
        postService.deletePost(postId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hashtag")
    public ResponseEntity<Page<PostResponseDto>> getPostsByHashtag(
            @RequestParam String tagName,
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDto> responses = postService.getPostsByHashtag(tagName, currentUserId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDto>> searchPostsByKeyword(
            @RequestParam String keyword,
            @RequestHeader(value = "userId", defaultValue = "1") Long currentUserId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponseDto> responses = postService.getPostsByKeyword(keyword, currentUserId, pageable);
        return ResponseEntity.ok(responses);
    }
}
