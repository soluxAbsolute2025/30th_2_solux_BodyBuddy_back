package com.solux.bodybubby.domain.challenge.controller;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.service.GroupChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.solux.bodybubby.global.security.CustomUserDetails; // UserDetails 경로 확인

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges/group")
public class GroupChallengeController {

    private final GroupChallengeService challengeService;

    // ... (목록 조회 API들은 기존과 동일) ...
    @GetMapping("/ongoing")
    public ResponseEntity<ChallengeResponseDto<List<GroupListResponse>>> getOngoingList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupListResponse> response = challengeService.getOngoingList(userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "조회 성공", response));
    }

    @GetMapping("/ongoing/{id}")
    public ResponseEntity<ChallengeResponseDto<GroupDetailResponse>> getDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        GroupDetailResponse response = challengeService.getDetail(id, userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "상세 조회 성공", response));
    }

    @GetMapping
    public ResponseEntity<ChallengeResponseDto<List<GroupSearchResponse>>> searchGroups() {
        List<GroupSearchResponse> response = challengeService.searchNewGroups();
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "새로운 그룹 조회 성공", response));
    }

    /**
     * 그룹 챌린지 생성 (수정됨)
     */
    @Operation(summary = "그룹 챌린지 생성", description = "챌린지 정보(JSON)와 대표 이미지(File)를 함께 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupCreateResponse> createChallenge(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "챌린지 기본 정보 (JSON)", required = true)
            @RequestPart("request") GroupCreateRequest request,
            @Parameter(description = "챌린지 대표 이미지", required = true)
            @RequestPart(value = "image", required = false) MultipartFile image 
    ) {
        GroupCreateResponse response = challengeService.createGroupChallenge(request, userDetails.getId(), image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 그룹 코드로 챌린지 참여
     */
    @PostMapping("/join")
    public ResponseEntity<ChallengeResponseDto<Void>> joinByCode(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody GroupJoinRequest request) {
        challengeService.joinByGroupCode(request.getGroupCode(), userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "그룹 참여 완료", null));
    }

    // ... (수정, 삭제 API 동일) ...

    /**
     * [중요] 그룹 챌린지 인증 (Check-in) - 사진 업로드 추가
     */
    @PostMapping(value = "/{id}/check-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChallengeResponseDto<GroupCheckInResponse>> checkIn(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "file", required = false) MultipartFile file // 인증샷 받기 추가
    ) {
        GroupCheckInResponse response = challengeService.checkIn(id, userDetails.getId(), file);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "인증 성공", response));
    }
    
    // ... (완료 목록 조회 동일) ...
     @GetMapping("/completed")
    public ResponseEntity<ChallengeResponseDto<List<GroupCompletedResponse>>> getCompletedList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupCompletedResponse> response = challengeService.getCompletedList(userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "완료한 챌린지 목록 조회 성공", response));
    }
}