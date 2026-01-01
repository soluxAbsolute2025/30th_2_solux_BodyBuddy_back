package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.dto.UserSignupRequestDto;
import com.solux.bodybubby.domain.user.service.UserService;
import com.solux.bodybubby.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // 명세서 기준 /api/users로 통일
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * [POST] 회원가입 API - /api/users/signup
     */
    @PostMapping("/api/users/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody UserSignupRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "회원가입이 완료되었습니다."));
    }

    /**
     * [GET] 닉네임 중복 확인 API - /api/users/check-id
     */
    @GetMapping("/api/users/check-id")
    public ResponseEntity<ApiResponse<Void>> checkNickname(@RequestParam String nickname) {
        if (userService.checkNicknameDuplicate(nickname)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "이미 사용 중인 닉네임입니다."));
        }
        return ResponseEntity.ok(ApiResponse.success(200, "사용 가능한 닉네임입니다."));
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
     * [POST] 온보딩 정보 등록 API - /api/users/onboarding
     */
    @PostMapping("/api/users/onboarding")
    public ResponseEntity<ApiResponse<Void>> registerOnboarding(
            @RequestHeader("userId") Long userId,
            @RequestBody UserOnboardingRequestDto requestDto) {
        userService.registerOnboarding(userId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(200, "온보딩 정보가 등록되었습니다."));
    }

    /**
     * [DELETE] 회원 탈퇴 API - /api/user/signout
     */
    @DeleteMapping("/api/user/signout")
    public ResponseEntity<ApiResponse<Void>> signOut(@RequestHeader("userId") Long userId) {
        userService.withdrawUser(userId);
        return ResponseEntity.ok(ApiResponse.success(200, "회원 탈퇴가 완료되었습니다."));
    }

    /**
     * [POST] 로그아웃 API - /api/google/logout
     */
    @PostMapping("/api/google/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(200, "로그아웃 되었습니다."));
    }
}