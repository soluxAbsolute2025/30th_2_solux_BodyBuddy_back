package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.*;
import com.solux.bodybubby.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
    }

    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkId(@RequestParam String loginId) {
        return ResponseEntity.ok(userService.isLoginIdAvailable(loginId));
    }

    @PostMapping("/onboarding")
    public ResponseEntity<String> onboarding(@RequestBody UserOnboardingRequestDto dto) {
        // 실제로는 토큰에서 userId를 추출해야 합니다. 임시로 1L 사용.
        userService.completeOnboarding(1L, dto);
        return ResponseEntity.ok("온보딩 정보 등록 완료");
    }
}