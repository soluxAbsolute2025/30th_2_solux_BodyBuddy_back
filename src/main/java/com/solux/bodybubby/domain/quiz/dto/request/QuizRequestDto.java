package com.solux.bodybubby.domain.quiz.dto.request;

import java.util.List;

public record QuizRequestDto(
        String question,
        List<String> options,
        String answer,
        int rewardPoint
) {}