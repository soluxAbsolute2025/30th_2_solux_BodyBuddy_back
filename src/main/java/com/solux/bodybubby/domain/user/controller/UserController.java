package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.dto.UserSignupRequestDto;
import com.solux.bodybubby.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** [POST] 회원가입 API - /api/users/signup */
    @PostMapping("/api/users/signup")
    public ResponseEntity<Long> signUp(@RequestBody UserSignupRequestDto requestDto) {
        return ResponseEntity.ok(userService.signUp(requestDto.getEmail(), "google", requestDto.getReferrerId()));
    }

    /** [GET] 닉네임 중복 확인 API - /api/users/check-id */
    @GetMapping("/api/users/check-id")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    /** [POST] 온보딩 정보 등록 API - /api/users/onboarding */
    @PostMapping("/api/users/onboarding")
    public ResponseEntity<String> registerOnboarding(
            @RequestHeader("userId") Long userId,
            @RequestBody UserOnboardingRequestDto requestDto) {
        userService.registerOnboarding(userId, requestDto);
        return ResponseEntity.ok("온보딩 정보가 등록되었습니다.");
    }

    /** * [DELETE] 회원 탈퇴 API - /api/user/signout
     * 명세서상 단수 'user' 경로임을 주의하세요.
     */
    @DeleteMapping("/api/user/signout")
    public ResponseEntity<Void> signOut(@RequestHeader("userId") Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.noContent().build(); // 탈퇴 성공 시 204 No Content 반환
    }
}