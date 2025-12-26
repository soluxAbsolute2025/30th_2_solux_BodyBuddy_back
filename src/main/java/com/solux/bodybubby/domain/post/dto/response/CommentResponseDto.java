package com.solux.bodybubby.domain.post.dto.response;

import com.solux.bodybubby.domain.post.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String writerNickname;
    private boolean isEdited; // 수정 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponseDto fromEntity(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writerNickname(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                // 생성 시간과 수정 시간이 다르면 (수정됨)으로 판단 (1초 미만 오차 허용)
                .isEdited(comment.getUpdatedAt().isAfter(comment.getCreatedAt().plusSeconds(1)))
                .build();
    }
}
