package com.solux.bodybubby.domain.quiz.dto.request;

public record QuizSolveRequest(
        Long questionId,
        int optionId // 유저가 선택한 번호 (1, 2, 3)
) {}