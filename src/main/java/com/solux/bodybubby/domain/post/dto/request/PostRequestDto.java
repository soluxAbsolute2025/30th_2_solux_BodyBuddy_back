package com.solux.bodybubby.domain.post.dto.request;

import com.solux.bodybubby.domain.post.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

//    private String title;
    private String content;
    private String place;
    private Visibility visibility;
    private List<String> hashtags;
    private Boolean imageDeleted; // 게시물 수정 시 이미지 삭제했는지 여부
}
