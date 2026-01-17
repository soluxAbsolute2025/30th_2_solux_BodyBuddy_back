package com.solux.bodybubby.domain.chatbot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.solux.bodybubby.domain.chatbot.DTO.GeminiRequest;
import com.solux.bodybubby.domain.chatbot.DTO.GeminiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String getContents(String prompt) {
        String requestUrl = apiUrl + "?key=" + apiKey;

        //4-5줄 안넘어가게

        String modifiedPrompt = prompt + " (답변은 한국어로, 4~5줄 이내로 핵심만 요약해서 간결하게 작성해줘.)";

        GeminiRequest request = new GeminiRequest(
            List.of(new GeminiRequest.Content(
                    List.of(new GeminiRequest.Part(modifiedPrompt)) // 
            ))
    );

      try {
            GeminiResponse response = restTemplate.postForObject(requestUrl, request, GeminiResponse.class);
            
            if (response != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            }
        } catch (Exception e) {
            e.printStackTrace(); // 터미널에 로그 찍기
            // 👇 여기를 수정하세요! (에러 내용을 그대로 리턴)
            return "에러 발생 원인: " + e.getMessage(); 
        }

        return "답변을 생성하지 못했습니다.";
    }
}