package com.solux.bodybubby.domain.post.controller;

import com.solux.bodybubby.domain.post.dto.request.PostRequestDto;
import com.solux.bodybubby.domain.post.dto.response.PostResponseDto;
import com.solux.bodybubby.domain.post.service.PostService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class PostController {

    private  final PostService postService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Long> createPost(
            @RequestPart("request") PostRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(dto, image, userDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDto> responses = postService.getAllPosts(userDetails.getId(), pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getOnePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(postService.getPost(postId, userDetails.getId()));
    }

    @PatchMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @RequestPart("request") PostRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.updatePost(postId, dto, image, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(postId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hashtag")
    public ResponseEntity<Page<PostResponseDto>> getPostsByHashtag(
            @RequestParam String tagName,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDto> responses = postService.getPostsByHashtag(tagName, userDetails.getId(), pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDto>> searchPostsByKeyword(
            @RequestParam String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostResponseDto> responses = postService.getPostsByKeyword(keyword, userDetails.getId(), pageable);
        return ResponseEntity.ok(responses);
    }
}
