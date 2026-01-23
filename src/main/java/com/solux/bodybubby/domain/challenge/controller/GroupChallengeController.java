package com.solux.bodybubby.domain.challenge.controller;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.service.GroupChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ChallengeResponseDto<List<GroupListResponse>>> getOngoingList(Long userId) {
        // userId는 LoginUserArgumentResolver에서 주입받습니다.
        List<GroupListResponse> response = challengeService.getOngoingList(userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "조회 성공", response));
    }

    /**
     * 참여 중 그룹 챌린지 상세 조회
     */
    @GetMapping("/ongoing/{id}")
    public ResponseEntity<ChallengeResponseDto<GroupDetailResponse>> getDetail(@PathVariable Long id, Long userId) {
        GroupDetailResponse response = challengeService.getDetail(id, userId);
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
     * 그룹 챌린지 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupCreateResponse> createChallenge(
            Long userId,
            @RequestPart("request") GroupCreateRequest request, // JSON 데이터 파트
            @RequestPart(value = "image", required = false) MultipartFile image // 이미지 파일 파트
    ) {
        // 이미지를 처리하는 로직 필요시 image 넘기기
        GroupCreateResponse response = challengeService.createGroupChallenge(request, userId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 그룹 코드로 챌린지 참여
     */
    @PostMapping("/join")
    public ResponseEntity<ChallengeResponseDto<Void>> joinByCode(Long userId, @RequestBody GroupJoinRequest request) {
        challengeService.joinByGroupCode(request.getGroupCode(), userId);
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
     */
    @PostMapping("/{id}/check-in")
    public ResponseEntity<ChallengeResponseDto<GroupCheckInResponse>> checkIn(
            @PathVariable Long id,
            Long userId
    ) {
        // 서비스 메서드 호출 시 인자값(value) 제거
        GroupCheckInResponse response = challengeService.checkIn(id, userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "인증 성공", response));
    }
}