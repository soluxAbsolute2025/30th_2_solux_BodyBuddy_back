package com.solux.bodybubby.domain.challenge.controller;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.service.PersonalChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges/personal")
public class PersonalChallengeController {

    private final PersonalChallengeService personalChallengeService;

    /**
     * 개인 챌린지 목록 조회
     */
    @GetMapping("/ongoing")
    public ResponseEntity<ChallengeResponseDto<PersonalListResponse>> getOngoingList(Long userId) {
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "조회 성공", personalChallengeService.getPersonalDashboard(userId)));
    }

    /**
     * 개인 챌린지 상세 조회
     */
    @GetMapping("/ongoing/{id}")
    public ResponseEntity<ChallengeResponseDto<PersonalDetailResponse>> getDetail(@PathVariable Long id, Long userId) {
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "상세 조회 성공", personalChallengeService.getPersonalDetail(id, userId)));
    }

    /**
     * 개인 챌린지 생성
     */
    public ResponseEntity<ChallengeResponseDto<Long>> createPersonal(Long userId, @RequestBody PersonalCreateRequest request) {
        Long id = personalChallengeService.createPersonalChallenge(userId, request);
        return ResponseEntity.status(201).body(new ChallengeResponseDto<>(201, "생성 성공", id));
    }

    /**
     * 개인 챌린지 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> updatePersonal(@PathVariable Long id, Long userId, @RequestBody PersonalUpdateRequest request) {
        personalChallengeService.updatePersonalChallenge(userId, id, request);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "수정 성공", null));
    }

    /**
     * 개인 챌린지 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> deletePersonal(@PathVariable Long id, Long userId) {
        personalChallengeService.deletePersonalChallenge(userId, id);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "삭제 성공", null));
    }

    /**
     * 추천 챌린지 조회
     */
    @GetMapping("/recommend")
    public ResponseEntity<ChallengeResponseDto<List<PersonalRecommendResponse>>> getRecommend() {
        List<PersonalRecommendResponse> response = personalChallengeService.getRecommendedChallenges();
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "추천 챌린지 조회 성공", response));
    }
}