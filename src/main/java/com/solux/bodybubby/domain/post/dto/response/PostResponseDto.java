package com.solux.bodybubby.domain.post.dto.response;

import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String writerNickname;
    private Integer viewCount;
    private Integer likeCount;
    private Visibility visibility;
    private boolean isEdited; // 수정 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> hashtags;
    // 이미지 관련 추가 필요

    public static PostResponseDto fromEntity(Post post) {
        List<String> hashtags = post.getPostHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getTagName())
                .collect(Collectors.toList());

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerNickname(post.getUser().getName())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .visibility(post.getVisibility())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                // 생성 시간과 수정 시간이 다르면 (수정됨)으로 판단 (1초 미만 오차 허용)
                .isEdited(post.getUpdatedAt().isAfter(post.getCreatedAt().plusSeconds(1)))
                .hashtags(hashtags)
                .build();
    }

}
