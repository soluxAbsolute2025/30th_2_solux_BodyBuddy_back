package com.solux.bodybubby.domain.chatbot.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // 추가됨
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solux.bodybubby.domain.chatbot.service.GeminiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final GeminiService geminiService;

    // 1. 채팅 메시지 보내기 (기존 코드)
    @PostMapping
    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = geminiService.getContents(question);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/suggest")
    public ResponseEntity<Map<String, Object>> getSuggest() {
        // 1) 추천 질문 데이터 준비 (하드코딩 or DB조회)
        List<String> suggestions = Arrays.asList(
            "다이어트 식단 추천",
            "운동 후 근육통 완화",
            "수면 개선 방법",
            "스트레스 관리법"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", suggestions);

        return ResponseEntity.ok(response);
    }
}