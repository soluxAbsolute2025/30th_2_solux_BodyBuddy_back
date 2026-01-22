package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserRequestDto;
import com.solux.bodybubby.domain.user.dto.UserResponseDto;
import com.solux.bodybubby.domain.user.service.UserService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * [회원가입] POST /api/users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto.Signup dto) {
        userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
    }

    /**
     * [로그인] POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<UserRequestDto.LoginResponse> login(@Valid @RequestBody UserRequestDto.Login dto) {
        // 서비스에서 조립된 응답 객체를 바로 받음
        UserRequestDto.LoginResponse response = userService.login(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * [로그아웃] POST /api/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            userService.logout(accessToken);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * [회원정보 간단 조회] GET /api/users
     */
    @GetMapping //
    public ResponseEntity<UserResponseDto.SimpleInfo> getSimpleInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 토큰에서 추출된 ID를 서비스에 전달합니다.
        UserResponseDto.SimpleInfo response = userService.getSimpleInfo(userDetails.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * [아이디 중복 확인] GET /api/users/check-id
     */
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkId(@RequestParam String loginId) {
        return ResponseEntity.ok(userService.isLoginIdAvailable(loginId));
    }

    /**
     * [닉네임 중복 확인] GET /api/users/check-nickname
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.isNicknameAvailable(nickname));
    }

    /**
     * [회원 탈퇴] DELETE /api/users
     */
    @DeleteMapping
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.withdrawUser(userDetails.getId()); // 실제 토큰 정보
        return ResponseEntity.noContent().build();
    }

    /**
     * [온보딩 정보 등록] POST /api/users/onboarding
     */
    @PostMapping("/onboarding")
    public ResponseEntity<String> onboarding(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserRequestDto.Onboarding dto) {
        userService.completeOnboarding(userDetails.getId(), dto); // 실제 토큰 정보
        return ResponseEntity.ok("온보딩 정보 등록 완료");
    }

    /**
     * [프로필 수정] PATCH /api/users/profile
     */
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 실제 토큰 정보
            @Valid @RequestBody UserRequestDto.ProfileUpdate dto) {
        userService.updateProfile(userDetails.getId(), dto);
        return ResponseEntity.ok("프로필 수정 완료");
    }

    /**
     * [비밀번호 변경] PATCH /api/users/password
     */
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 실제 토큰 정보
            @Valid @RequestBody UserRequestDto.PasswordUpdate dto) {
        userService.updatePassword(userDetails.getId(), dto);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
}