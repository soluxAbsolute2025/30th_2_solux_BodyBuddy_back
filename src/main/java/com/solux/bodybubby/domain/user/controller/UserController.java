package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.dto.UserSignupRequestDto;
import com.solux.bodybubby.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // 명세서 기준 /api/users로 통일
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * [POST] 회원가입 API
     * 명세서에 정의된 /api/users/signup 경로입니다.
     * 소셜 로그인 성공 후 최초 1회 호출하거나 테스트용으로 사용됩니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody UserSignupRequestDto requestDto) {
        // 기존 UserService의 signUp 로직을 호출합니다.
        // 소셜 로그인 도입 시 서비스 로직에서 email, provider 정보를 처리하도록 수정된 상태여야 합니다.
        return ResponseEntity.ok(userService.signUp(requestDto.getEmail(), "google", requestDto.getReferrerId()));
    }

    /**
     * [GET] 내 정보 조회 API
     * 명세서의 '마이페이지 조회'(/api/mypage) 기능과 연동되는 부분
     * 현재는 기본 경로(/me)를 유지하되, 추후 마이페이지 서비스와 연결합니다.
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile(@RequestHeader("userId") Long userId) {
        return ResponseEntity.ok("유저 " + userId + "님의 정보를 조회합니다. (작업 예정)");
    }

    /**
     * [GET] 닉네임 중복 확인 API
     * 명세서: /api/users/check-id
     */
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    /**
     * [POST] 온보딩 정보 등록 API
     * 명세서: /api/users/onboarding
     */
    @PostMapping("/onboarding")
    public ResponseEntity<String> registerOnboarding(
            @RequestHeader("userId") Long userId,
            @RequestBody UserOnboardingRequestDto requestDto) {
        userService.registerOnboarding(userId, requestDto);
        return ResponseEntity.ok("온보딩 정보가 등록되었습니다.");
    }

    /**
     * [DELETE] 회원 탈퇴 API
     * 명세서: /api/user/signout
     */
    @DeleteMapping("/signout")
    public ResponseEntity<String> signOut(@RequestHeader("userId") Long userId) {
        userService.signOut(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}