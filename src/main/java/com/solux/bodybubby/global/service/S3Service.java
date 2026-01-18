package com.solux.bodybubby.global.service;

import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client; // AmazonS3 대신 S3Client 사용

    @Value("${spring.cloud.aws.s3.bucket}") // prefix 주의: spring. 추가
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 파일명 중복 방지를 위해 UUID 사용
        String fileName = createFileName(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            // 2. 메타데이터 포함한 업로드 요청 생성 (SDK v2 방식)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            // 3. S3에 파일 업로드 (RequestBody 사용)
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(inputStream, file.getSize()));

            // 4. 업로드된 파일의 URL 반환 (직접 조합)
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    // 파일명 난독화 (UUID_파일명.확장자)
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat("_").concat(fileName);
    }

    // 기존 파일 삭제 로직 (수정 시 사용)
    public void deleteFile(String fileUrl) {
        if (fileUrl == null) return;

        // URL에서 파일명(Key)만 추출
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}