package com.solux.bodybubby.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    @Size(max = 500, message = "댓글은 최대 500자까지 작성 가능합니다.")
    private String content;
}
