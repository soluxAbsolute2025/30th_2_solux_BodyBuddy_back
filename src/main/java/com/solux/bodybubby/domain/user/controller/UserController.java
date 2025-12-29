package com.solux.bodybubby.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    /**
     * [GET] 내 정보 조회 API
     * 구글 로그인 완성 후 실제 유저 정보 반환 예정
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyProfile() {
        // 성공했다는 의미의 200 OK와 함께 메시지를 보낼 예정 
        return ResponseEntity.ok("내 정보 조회 API (작업 전)");
    }
}