package com.solux.bodybubby.domain.mypage.controller;

import com.solux.bodybubby.domain.mypage.dto.MyPageMainResponse;
import com.solux.bodybubby.domain.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage") // 기능 명세서의 URL 반영
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping // GET 메서드 사용
    public ResponseEntity<MyPageMainResponse> getMyPageMain() {
        // TODO: 스프링 시큐리티 적용 후 @AuthenticationPrincipal User user 등을 통해 ID를 받아와야 함
        // 현재는 구현 및 테스트를 위해 임시로 유저 ID 1L을 사용합니다.
        MyPageMainResponse response = myPageService.getMyPageMain(1L);

        return ResponseEntity.ok(response);
    }
}
