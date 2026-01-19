package com.solux.bodybubby.s3Test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test/s3")
@RequiredArgsConstructor
public class S3TestController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTest(@RequestParam("file") MultipartFile file) {
        String imageUrl = s3Service.uploadFile(file);
        return ResponseEntity.ok(imageUrl); // 성공 시 이미지 URL 반환
    }
}