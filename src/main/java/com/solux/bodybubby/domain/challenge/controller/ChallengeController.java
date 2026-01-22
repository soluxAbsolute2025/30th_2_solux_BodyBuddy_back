package com.solux.bodybubby.domain.challenge.controller;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges/group")
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * 참여 중 그룹 챌린지 목록 조회
     */
    @GetMapping("/ongoing")
    public ResponseEntity<ChallengeResponseDto<List<OngoingGroupListResponse>>> getOngoingList(Long userId) {
        // userId는 LoginUserArgumentResolver에서 주입받습니다.
        List<OngoingGroupListResponse> response = challengeService.getOngoingList(userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "조회 성공", response));
    }

    /**
     * 참여 중 그룹 챌린지 상세 조회
     */
    @GetMapping("/ongoing/{id}")
    public ResponseEntity<ChallengeResponseDto<OngoingGroupDetailResponse>> getDetail(@PathVariable Long id, Long userId) {
        OngoingGroupDetailResponse response = challengeService.getDetail(id, userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "상세 조회 성공", response));
    }

    /**
     * 새로운 그룹 조회
     */
    @GetMapping
    public ResponseEntity<ChallengeResponseDto<List<SearchGroupResponse>>> searchGroups() {
        List<SearchGroupResponse> response = challengeService.searchNewGroups();
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "새로운 그룹 조회 성공", response));
    }

    /**
     * 그룹 챌린지 생성
     */
    @PostMapping
    public ResponseEntity<CreateGroupResponse> createChallenge(Long userId, @RequestBody CreateGroupRequest request) {
        CreateGroupResponse response = challengeService.createGroupChallenge(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 그룹 코드로 챌린지 참여
     */
    @PostMapping("/join")
    public ResponseEntity<ChallengeResponseDto<Void>> joinByCode(Long userId, @RequestBody JoinGroupRequest request) {
        challengeService.joinByGroupCode(request.getGroupCode(), userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "그룹 참여 완료", null));
    }

    /**
     * 그룹 챌린지 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> updateChallenge(@PathVariable Long id, Long userId, @RequestBody CreateGroupRequest request) {
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
    public ResponseEntity<ChallengeResponseDto<CheckInResponse>> checkIn(@PathVariable Long id, Long userId, @RequestBody CheckInRequest request) {
        CheckInResponse response = challengeService.checkIn(id, userId, request.getCurrentValue());
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "인증 성공", response));
    }
}