package com.solux.bodybubby.global.util;

import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Provider {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 1. 단일 파일 업로드 (가장 많이 씀)
    public String uploadFile(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            log.info("파일이 비어있어 업로드를 건너뜁니다.");
            return null;
        }

        String fileName = folderName + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        log.info("S3 업로드 시도: bucket={}, fileName={}", bucket, fileName); // 로그 추가

        try (InputStream inputStream = file.getInputStream()) {
            var resource = s3Template.upload(bucket, fileName, inputStream,
                    ObjectMetadata.builder().contentType(file.getContentType()).build());

            String url = resource.getURL().toString();
            log.info("S3 업로드 완료! URL: {}", url); // 로그 추가
            return url;
        } catch (IOException e) {
            log.error("S3 파일 업로드 중 IO 에러: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        } catch (Exception e) {
            log.error("S3 관련 기타 에러: {}", e.getClass().getSimpleName() + " : " + e.getMessage());
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    // 2. 파일 삭제 (기존 이미지 교체 시 필요)
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            // URL에서 파일 키(파일명)만 추출
            String fileKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            s3Template.deleteObject(bucket, fileKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 중 에러 발생: {}", e.getMessage());
        }
    }

    // 3. 파일 수정 (기존 것 삭제 후 새것 업로드)
    public String updateFile(String oldFileUrl, MultipartFile newFile, String folderName) {
        if (oldFileUrl != null) {
            deleteFile(oldFileUrl);
        }
        return uploadFile(newFile, folderName);
    }
}