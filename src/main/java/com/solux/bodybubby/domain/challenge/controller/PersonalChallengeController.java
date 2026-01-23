package com.solux.bodybubby.domain.challenge.controller;

import com.solux.bodybubby.domain.challenge.dto.*;
import com.solux.bodybubby.domain.challenge.service.PersonalChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges/personal")
public class PersonalChallengeController {

    private final PersonalChallengeService personalChallengeService;

    /**
     * 개인 챌린지 목록 조회
     * GET /api/challenges/personal/ongoing
     */
    @GetMapping("/ongoing")
    public ResponseEntity<ChallengeResponseDto<PersonalListResponse>> getOngoingList(Long userId) {
        // LoginUserArgumentResolver가 토큰에서 userId를 추출하여 주입.
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "조회 성공", personalChallengeService.getPersonalDashboard(userId)));
    }

    /**
     * 개인 챌린지 상세 조회
     * GET /api/challenges/personal/ongoing/{id}
     */
    @GetMapping("/ongoing/{id}")
    public ResponseEntity<ChallengeResponseDto<PersonalDetailResponse>> getDetail(@PathVariable Long id, Long userId) {
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "상세 조회 성공", personalChallengeService.getPersonalDetail(id, userId)));
    }

    /**
     * 개인 챌린지 생성
     * POST /api/challenges/personal
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Multipart 설정
    public ResponseEntity<ChallengeResponseDto<Long>> createPersonal(
            Long userId,
            @RequestPart("request") PersonalCreateRequest request, // JSON 파트
            @RequestPart(value = "image", required = false) MultipartFile image // [추가] 이미지 파일 파트
    ) {
        // 서비스 호출 시 image 객체 전달
        Long id = personalChallengeService.createPersonalChallenge(userId, request, image);
        return ResponseEntity.status(201).body(new ChallengeResponseDto<>(201, "생성 성공", id));
    }

    /**
     * 개인 챌린지 수정
     * PATCH /api/challenges/personal/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> updatePersonal(@PathVariable Long id, Long userId, @RequestBody PersonalUpdateRequest request) {
        personalChallengeService.updatePersonalChallenge(userId, id, request);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "수정 성공", null));
    }

    /**
     * 개인 챌린지 삭제
     * DELETE /api/challenges/personal/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ChallengeResponseDto<Void>> deletePersonal(@PathVariable Long id, Long userId) {
        personalChallengeService.deletePersonalChallenge(userId, id);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "삭제 성공", null));
    }

    /**
     * 추천 챌린지 조회
     * GET /api/challenges/personal/recommend
     */
    @GetMapping("/recommend")
    public ResponseEntity<ChallengeResponseDto<List<PersonalRecommendResponse>>> getRecommend() {
        List<PersonalRecommendResponse> response = personalChallengeService.getRecommendedChallenges();
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "추천 챌린지 조회 성공", response));
    }

    /**
     * 완료된 개인 챌린지 목록 조회
     * GET /api/challenges/personal/completed
     */
    @Operation(summary = "완료한 개인 챌린지 조회", description = "달성률 100%를 기록하여 완료된 개인 챌린지 목록을 가져옵니다.")
    @GetMapping("/completed")
    public ResponseEntity<ChallengeResponseDto<List<PersonalCompletedResponse>>> getCompletedList(Long userId) {
        List<PersonalCompletedResponse> response = personalChallengeService.getCompletedPersonalList(userId);
        return ResponseEntity.ok(new ChallengeResponseDto<>(200, "완료된 개인 챌린지 조회 성공", response));
    }
}