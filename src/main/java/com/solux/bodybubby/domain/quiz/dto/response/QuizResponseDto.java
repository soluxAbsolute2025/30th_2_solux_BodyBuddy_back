package com.solux.bodybubby.domain.quiz.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record QuizResponseDto(
        Long questionId,
        String question,
        int rewardPoint,
        List<OptionDto> options
) {
    public record OptionDto(int id, String text) {}
}