package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.dto.UserSignupRequestDto;
import com.solux.bodybubby.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * [GET] 내 정보 조회 API
     * 구글 로그인 완성 후 실제 유저 정보 반환 예정
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile() {
        return ResponseEntity.ok("내 정보 조회 API (작업 전)");
    }

    /**
     * [POST] 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody UserSignupRequestDto requestDto) {
        return ResponseEntity.ok(userService.signUp(requestDto));
    }

    /**
     * [POST] 온보딩 정보 등록 API
     */
    @PostMapping("/onboarding")
    public ResponseEntity<String> registerOnboarding(
            @RequestParam Long userId,
            @RequestBody UserOnboardingRequestDto requestDto) {
        userService.registerOnboarding(userId, requestDto);
        return ResponseEntity.ok("온보딩 정보가 등록되었습니다.");
    }

    /**
     * [DELETE] 회원 탈퇴 API
     */
    @DeleteMapping("/signout")
    public ResponseEntity<String> signOut(@RequestParam Long userId) {
        userService.signOut(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}