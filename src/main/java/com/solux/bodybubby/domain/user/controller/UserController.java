package com.solux.bodybubby.domain.user.controller;

import com.solux.bodybubby.domain.user.dto.UserRequestDto;
import com.solux.bodybubby.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserRequestDto.Login dto) {
        String token = userService.login(dto);

        // 응답 데이터 구성
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("tokenType", "Bearer");

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
    public ResponseEntity<Void> withdraw() {
        // TODO: 실제로는 토큰에서 userId를 추출해야 합니다. 현재 임시 1L 사용.
        userService.withdrawUser(1L);
        return ResponseEntity.noContent().build();
    }

    /**
     * [온보딩 정보 등록] POST /api/users/onboarding
     */
    @PostMapping("/onboarding")
    public ResponseEntity<String> onboarding(@Valid @RequestBody UserRequestDto.Onboarding dto) {
        // TODO: 실제로는 토큰에서 userId를 추출해야 합니다. 현재 임시 1L 사용.
        userService.completeOnboarding(1L, dto);
        return ResponseEntity.ok("온보딩 정보 등록 완료");
    }

    /**
     * [프로필 수정] PATCH /api/users/profile
     */
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UserRequestDto.ProfileUpdate dto) {
        // TODO: 실제로는 토큰에서 userId를 추출해야 합니다. 현재 임시 1L 사용.
        userService.updateProfile(1L, dto);
        return ResponseEntity.ok("프로필 수정 완료");
    }

    /**
     * [비밀번호 변경] PATCH /api/users/password
     */
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UserRequestDto.PasswordUpdate dto) {
        // TODO: 실제로는 토큰에서 userId를 추출해야 합니다. 현재 임시 1L 사용.
        userService.updatePassword(1L, dto);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
}