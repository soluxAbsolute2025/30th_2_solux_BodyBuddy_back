package com.solux.bodybubby.domain.quiz.dto.response;

import java.time.LocalDate;
import java.util.List;

public record WeeklyAttendanceDto(
        List<AttendanceDto> attendance
) {
    public record AttendanceDto(LocalDate date, boolean checked) {}
}