package com.solux.bodybubby.domain.challenge.controller;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.service.GroupChallengeService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges/group")
public class GroupChallengeController {

    private final GroupChallengeService challengeService;

    /**
     * 참여 중 그룹 챌린지 목록 조회
     */
    @GetMapping("/ongoing")
    public ResponseEntity<ChallengeResponseDto<List<GroupListResponse>>> getOngoingList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupListResponse> response = challengeService.getOngoingList(userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "조회 성공", response));
    }

    /**
     * 참여 중 그룹 챌린지 상세 조회
     */
    @GetMapping("/ongoing/{id}")
    public ResponseEntity<ChallengeResponseDto<GroupDetailResponse>> getDetail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        GroupDetailResponse response = challengeService.getDetail(id, userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "상세 조회 성공", response));
    }

    /**
     * 새로운 그룹 조회
     */
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

    /**
     * 그룹 챌린지 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> updateChallenge(@PathVariable Long id, Long userId, @RequestBody GroupCreateRequest request) {
        challengeService.updateChallenge(id, request, userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "수정 완료", null));
    }

    /**
     * 그룹 챌린지 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> deleteChallenge(@PathVariable Long id, Long userId) {
        challengeService.deleteChallenge(id, userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "삭제 완료", null));
    }


    /**
     * 그룹 챌린지 인증 (Check-in)
     * 수정사항: 기획에 맞춰 RequestBody(사진 파일)를 완전히 제거합니다.
     */
    @Operation(summary = "그룹 챌린지 인증", description = "별도의 데이터 없이 호출 시 자동으로 오늘 인증이 처리됩니다.")
    @PostMapping("/{id}/check-in") // [수정] consumes 설정을 삭제하여 일반 POST 요청으로 변경
    public ResponseEntity<ChallengeResponseDto<GroupCheckInResponse>> checkIn(
            @PathVariable Long id,
            @Parameter(hidden = true) Long userId // [보완] 토큰에서 주입되는 userId는 스웨거에서 숨김
    ) {
        // [수정] 서비스 호출 시에도 더 이상 파일(image)을 전달하지 않습니다.
        GroupCheckInResponse response = challengeService.checkIn(id, userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "인증 성공", response));
    }

    /**
     * 완료된 그룹 챌린지 목록 조회
     */
    @GetMapping("/completed")
    public ResponseEntity<ChallengeResponseDto<List<GroupCompletedResponse>>> getCompletedList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupCompletedResponse> response = challengeService.getCompletedList(userDetails.getId());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "완료한 챌린지 목록 조회 성공", response));
    }
}