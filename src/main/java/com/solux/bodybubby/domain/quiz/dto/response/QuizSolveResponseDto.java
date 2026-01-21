package com.solux.bodybubby.domain.quiz.dto.response;

public record QuizSolveResponseDto(
        boolean correct,
        int earnedPoint,
        String correctAnswer
) {}