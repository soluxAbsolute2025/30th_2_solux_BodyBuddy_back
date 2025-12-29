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

    private String title;
    private String content;
    private Visibility visibility;
    private List<String> hashtags;

   // 이미지 관련 추가 필요
}
