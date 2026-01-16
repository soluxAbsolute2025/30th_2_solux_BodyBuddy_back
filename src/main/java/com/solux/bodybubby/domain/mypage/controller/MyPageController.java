package com.solux.bodybubby.domain.mypage.controller;

import com.solux.bodybubby.domain.mypage.dto.MyPageResponseDto;
import com.solux.bodybubby.domain.mypage.dto.PrivacySettingsDto;
import com.solux.bodybubby.domain.mypage.service.MyPageService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 마이페이지 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * [마이페이지 메인 조회] GET /api/mypage
     * * @param userDetails 현재 인증된 사용자의 정보 (JWT 토큰을 통해 자동 주입)
     *
     * @return 마이페이지 프로필, 레벨, 활동 요약 정보를 포함한 응답
     */
    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 인증 객체에서 유저 ID를 추출하여 서비스를 호출합니다.
        MyPageResponseDto response = myPageService.getMyPageInfo(userDetails.getId());
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

        // 명세서 Response 구조 반영
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "공개 범위 설정이 변경되었습니다.");

        return ResponseEntity.ok(response);
    }
}