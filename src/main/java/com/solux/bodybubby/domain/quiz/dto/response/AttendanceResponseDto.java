package com.solux.bodybubby.domain.quiz.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AttendanceResponseDto(
        QuizDto quiz,
        List<WeeklyStatusDto> weeklyStatus
) {
    public record QuizDto(Long quizId, String question, List<String> options, int rewardExp, boolean isCompleted) {}
    public record WeeklyStatusDto(String day, String status, LocalDate date) {}
}
