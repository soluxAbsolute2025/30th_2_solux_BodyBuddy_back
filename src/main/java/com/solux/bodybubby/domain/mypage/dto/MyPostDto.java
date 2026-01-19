package com.solux.bodybubby.domain.mypage.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 내가 쓴 글 관련 모든 DTO를 관리하는 클래스
 */
public class MyPostDto {

    /**
     * [내가 쓴 글 조회] GET /api/mypage/posts
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long postId;
        private String nickname;
        private Integer userLevel; // LevelTier 기반 계산
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private String place;
        private String content;
        private List<String> hashtags;
        private String postImageUrl;
        private Integer likeCount;
        private Integer commentCount;
    }

    /**
     * [내가 쓴 글 수정] PATCH /api/mypage/posts/{postId}
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;
        private String visibility; // PUBLIC, SECRET 등
        private String place;
        private List<String> hashtags;
        private Boolean isImageDeleted;
    }
}