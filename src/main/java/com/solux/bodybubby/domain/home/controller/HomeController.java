package com.solux.bodybubby.domain.home.controller;

import com.solux.bodybubby.domain.home.dto.response.HomeResponseDTO;
import com.solux.bodybubby.domain.home.dto.response.HomeTodoListDTO;
import com.solux.bodybubby.domain.home.service.HomeService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<HomeResponseDTO> getHomeDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
             throw new IllegalArgumentException("로그인이 필요합니다."); 
        }
        
        return ResponseEntity.ok(homeService.getHomeData(userDetails.getId()));
    }

    @GetMapping("/todos")
    public ResponseEntity<HomeTodoListDTO> getHomeTodos(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        
        // Service에 새로 만든 메서드 호출!
        return ResponseEntity.ok(homeService.getTodayTodoList(userDetails.getId()));
    }

    // POST /api/home/todos/{ruleId}/check
    @PostMapping("/todos/{ruleId}/check")
    public ResponseEntity<Boolean> checkTodo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ruleId
    ) {
        // 체크 후 변경된 상태(true/false)를 반환
        boolean result = homeService.checkTodo(userDetails.getId(), ruleId);
        return ResponseEntity.ok(result);
    }
}