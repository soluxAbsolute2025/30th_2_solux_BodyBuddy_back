package com.solux.bodybubby.domain.buddy.controller;

import com.solux.bodybubby.domain.buddy.dto.request.BuddyRequestDto;
import com.solux.bodybubby.domain.buddy.dto.response.BuddyDetailResponse;
import com.solux.bodybubby.domain.buddy.dto.response.BuddyListResponse;
import com.solux.bodybubby.domain.buddy.service.BuddyService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buddy")
@RequiredArgsConstructor
public class BuddyController {

    private final BuddyService buddyService;

    // 버디 리스트 & 요청 목록 조회
    @GetMapping
    public ResponseEntity<BuddyListResponse> getBuddyList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(buddyService.getBuddyList(userDetails.getId()));
    }

    // 버디 상세 프로필 조회 (userId 기준)
    @GetMapping("/{targetId}")
    public ResponseEntity<BuddyDetailResponse> getBuddyDetail(
            @PathVariable Long targetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(buddyService.getBuddyDetail(userDetails.getId(), targetId));
    }

    // 버디 아이디로 검색
    @GetMapping("/search/{targetId}")
    public ResponseEntity<BuddyDetailResponse> searchBuddy(
            @PathVariable String loginId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(buddyService.searchByLoginId(userDetails.getId(), loginId));
    }

    // 버디 추가 요청 보내기
    @PostMapping
    public ResponseEntity<Void> requestBuddy(
            @RequestBody BuddyRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        buddyService.sendBuddyRequest(userDetails.getId(), dto.targetUserId());
        return ResponseEntity.ok().build();
    }

    // 버디 요청 수락
    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptBuddy(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        buddyService.acceptBuddyRequest(userDetails.getId(), requestId);
        return ResponseEntity.ok().build();
    }

    // 버디 요청 거절
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectBuddy(
            @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        buddyService.rejectBuddyRequest(userDetails.getId(), requestId);
        return ResponseEntity.ok().build();
    }

    // 친구 삭제 (서로 버디인 상태에서만)
    @DeleteMapping("/friends/{targetId}")
    public ResponseEntity<Void> deleteBuddy(
            @PathVariable Long targetId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        buddyService.deleteBuddy(userDetails.getId(), targetId);
        return ResponseEntity.ok().build();
    }

}
