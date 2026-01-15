package com.solux.bodybubby.domain.user.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.solux.bodybubby.domain.user.dto.GoogleInfResponse;
import com.solux.bodybubby.domain.user.dto.GoogleResponse;
import com.solux.bodybubby.domain.user.dto.GoogleRequest;

@Service
public class GoogleOAuthService {
    
    // 1. static 제거 (UserController에서 주입받아 쓰기 위해)
    public ResponseEntity<GoogleInfResponse> getGoogleUserInfo(String code, String id, String secret, String redirectUri) {
        
        RestTemplate restTemplate = new RestTemplate();
        
        // 2. 파라미터를 명확하게 전달
        GoogleRequest googleOAuthRequestParam = GoogleRequest.builder()
                .code(code)
                .clientId(id)
                .clientSecret(secret)
                .redirectUri(redirectUri) // 전달받은 주소를 그대로 사용
                .grantType("authorization_code")
                .build();
        
        // 구글에 토큰 요청
        ResponseEntity<GoogleResponse> tokenEntity = 
                        restTemplate.postForEntity("https://oauth2.googleapis.com/token", googleOAuthRequestParam, GoogleResponse.class);
        
        String idToken = tokenEntity.getBody().getId_token();
        
        Map<String, String> map = new HashMap<>();
        map.put("id_token", idToken);
        
        // 유저 정보 가져오기
        return restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo", map, GoogleInfResponse.class);
    }
}
