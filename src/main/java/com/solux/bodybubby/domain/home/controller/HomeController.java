package com.solux.bodybubby.domain.home.controller;

import com.solux.bodybubby.domain.home.dto.response.HomeResponseDTO;
import com.solux.bodybubby.domain.home.service.HomeService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
}