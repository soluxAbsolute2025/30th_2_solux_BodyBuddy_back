package com.solux.bodybubby.domain.post.dto.response;

import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.entity.Visibility;
import com.solux.bodybubby.domain.user.entity.User;
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
//    private String title;
    private String content;
    private String writerNickname;
    private String writerProfileImageUrl;
    private Integer writerLevel;
    private String imageUrl;
    private String place;
    private Integer likeCount;
    private boolean isLiked;
    private Visibility visibility;
    private boolean isEdited; // 수정 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> hashtags;
    private List<CommentResponseDto> comments;
    private Integer commentCount;

    public static PostResponseDto fromEntity(Post post, boolean isLiked) {
        User writer = post.getUser();

        List<String> hashtags = post.getPostHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getTagName())
                .collect(Collectors.toList());

        return PostResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .writerNickname(post.getUser().getNickname())
                .writerProfileImageUrl(writer.getProfileImageUrl())
                .writerLevel(writer.getLevel())
                .place(post.getPlace())
                .imageUrl(post.getImageUrl())
                .likeCount(post.getLikeCount())
                .isLiked(isLiked)
                .visibility(post.getVisibility())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                // 생성 시간과 수정 시간이 다르면 (수정됨)으로 판단 (1초 미만 오차 허용)
                .isEdited(post.getUpdatedAt().isAfter(post.getCreatedAt().plusSeconds(1)))
                .hashtags(hashtags)
                .comments(post.getComments().stream()
                        .map(CommentResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .commentCount(post.getComments().size())
                .build();
    }

}
