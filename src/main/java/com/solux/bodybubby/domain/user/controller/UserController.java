package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserRequestDto;
import com.solux.bodybubby.domain.user.dto.UserResponseDto;
import com.solux.bodybubby.domain.user.service.UserService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto.Signup dto) {
        userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserRequestDto.LoginResponse> login(@Valid @RequestBody UserRequestDto.Login dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            userService.logout(accessToken);
        }
        return ResponseEntity.ok().build();
    }

    // 간단 정보 조회 (마이페이지 헤더용)
    @GetMapping
    public ResponseEntity<UserResponseDto.SimpleInfo> getSimpleInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getSimpleInfo(userDetails.getId()));
    }

    // ID 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkId(@RequestParam String loginId) {
        return ResponseEntity.ok(userService.isLoginIdAvailable(loginId));
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(userService.isNicknameAvailable(nickname));
    }

    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.withdrawUser(userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // 온보딩 등록
    @PostMapping("/onboarding")
    public ResponseEntity<String> onboarding(@AuthenticationPrincipal CustomUserDetails userDetails, 
                                           @Valid @RequestBody UserRequestDto.Onboarding dto) {
        userService.completeOnboarding(userDetails.getId(), dto);
        return ResponseEntity.ok("온보딩 정보 등록 완료");
    }

    // 프로필 정보 수정 (텍스트 + 이미지)
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "request", required = false) @Valid UserRequestDto.ProfileUpdate dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        userService.updateProfile(userDetails.getId(), dto, image);
        return ResponseEntity.ok("프로필 수정 완료");
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto.PasswordUpdate dto) {
        userService.updatePassword(userDetails.getId(), dto);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
}