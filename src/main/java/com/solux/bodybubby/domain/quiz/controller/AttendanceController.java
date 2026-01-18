package com.solux.bodybubby.domain.quiz.controller;

import com.solux.bodybubby.domain.quiz.dto.request.QuizSolveRequest;
import com.solux.bodybubby.domain.quiz.dto.response.QuizResponseDto;
import com.solux.bodybubby.domain.quiz.dto.response.QuizSolveResponseDto;
import com.solux.bodybubby.domain.quiz.dto.response.WeeklyAttendanceDto;
import com.solux.bodybubby.domain.quiz.service.AttendanceService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 1. 오늘의 퀴즈 정보 조회
     */
    @GetMapping("/question")
    public ResponseEntity<QuizResponseDto> getTodayQuiz(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 서비스에서 QuizResponseDto(질문, 옵션 포함)를 반환합니다.
        return ResponseEntity.ok(attendanceService.getTodayQuiz(userDetails.getId()));
    }

    /**
     * 2. 주간 출석 현황 조회
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyAttendanceDto> getWeeklyAttendance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 반환 타입이 List<WeeklyStatusDto>에서 WeeklyAttendanceDto(객체)로 변경되었습니다.
        return ResponseEntity.ok(attendanceService.getWeeklyAttendance(userDetails.getId()));
    }

    /**
     * 3. 퀴즈 정답 제출
     */
    @PostMapping("/answer")
    public ResponseEntity<QuizSolveResponseDto> solveQuiz(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody QuizSolveRequest request) {

        // 서비스에서 QuizSolveResponseDto(correct, earnedPoint 포함)를 반환합니다.
        QuizSolveResponseDto response = attendanceService.solveQuiz(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }
}