package com.solux.bodybubby.domain.mypage.controller;

import com.solux.bodybubby.domain.mypage.dto.*;
import com.solux.bodybubby.domain.mypage.service.MyPageService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * [마이페이지 메인 조회] GET /api/mypage
     */
    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MyPageResponseDto response = myPageService.getMyPageInfo(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * [버디 등급 조회] GET /api/mypage/levels
     */
    @GetMapping("/levels") //
    public ResponseEntity<LevelResponseDto> getBuddyLevels(
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 토큰에서 유저 정보 추출

        // 서비스에서 조립된 전체 등급 및 내 정보 DTO를 가져옵니다.
        LevelResponseDto response = myPageService.getBuddyLevels(userDetails.getId());

        return ResponseEntity.ok(response); // 200 OK 응답 반환
    }

    /**
     * [뱃지 컬렉션 전체 조회] GET /api/mypage/badges
     */
    @GetMapping("/badges")
    public ResponseEntity<Map<String, Object>> getBadgeCollection(@AuthenticationPrincipal CustomUserDetails userDetails) {
        BadgeCollectionDto data = myPageService.getBadgeCollection(userDetails.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "뱃지 컬렉션을 성공적으로 조회했습니다.");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * [공개 범위 설정 조회] GET /api/mypage/privacy
     */
    @GetMapping("/privacy")
    public ResponseEntity<PrivacySettingsDto> getPrivacySettings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(myPageService.getPrivacySettings(userDetails.getId()));
    }

    /**
     * [공개 범위 설정 수정] PATCH /api/mypage/privacy
     */
    @PatchMapping("/privacy")
    public ResponseEntity<Map<String, Object>> updatePrivacySettings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PrivacySettingsDto dto) {

        myPageService.updatePrivacySettings(userDetails.getId(), dto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "공개 범위 설정이 변경되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * [내가 쓴 글 목록 조회] GET /api/mypage/posts
     */
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getMyPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MyPostDto.Response> posts = myPageService.getMyPosts(userDetails.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "내가 쓴 글 목록 조회 성공");
        response.put("data", posts);

        return ResponseEntity.ok(response);
    }

    /**
     * [내가 쓴 글 수정] PATCH /api/mypage/posts/{postId}
     */
    @PatchMapping(value = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateMyPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestPart("request") MyPostDto.UpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        myPageService.updateMyPost(userDetails.getId(), postId, request, image);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "게시글이 성공적으로 수정되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * [내가 쓴 글 삭제] DELETE /api/mypage/posts/{postId}
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> deleteMyPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {

        myPageService.deleteMyPost(userDetails.getId(), postId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "내가 쓴 글이 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }
}