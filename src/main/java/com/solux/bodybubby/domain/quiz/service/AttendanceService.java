package com.solux.bodybubby.domain.quiz.service;

import com.solux.bodybubby.domain.quiz.dto.request.QuizSolveRequest;
import com.solux.bodybubby.domain.quiz.dto.response.QuizResponseDto;
import com.solux.bodybubby.domain.quiz.dto.response.QuizSolveResponseDto;
import com.solux.bodybubby.domain.quiz.dto.response.WeeklyAttendanceDto;
import com.solux.bodybubby.domain.quiz.entity.Attendance;
import com.solux.bodybubby.domain.quiz.entity.Quiz;
import com.solux.bodybubby.domain.quiz.repository.AttendanceRepository;
import com.solux.bodybubby.domain.quiz.repository.QuizRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final QuizService quizService;
    private final AttendanceRepository attendanceRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public QuizResponseDto getTodayQuiz(Long userId) {
        // 1. 중복 출석 확인
        if (attendanceRepository.findByUserIdAndAttendanceDate(userId, LocalDate.now()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_ATTENDED);
        }

        // 2. 랜덤 퀴즈 추출 (QuizRepository의 @Query 사용)
        Quiz quiz = quizRepository.findRandomQuiz()
                .orElseThrow(() -> new BusinessException(ErrorCode.QUIZ_NOT_FOUND));

        // 3. 빌더를 이용한 DTO 변환
        List<QuizResponseDto.OptionDto> options = IntStream.range(0, quiz.getOptionsList().size())
                .mapToObj(i -> new QuizResponseDto.OptionDto(i + 1, quiz.getOptionsList().get(i)))
                .collect(Collectors.toList());

        // QuizResponseDto가 @Builder를 가지고 있어야 합니다.
        return QuizResponseDto.builder()
                .questionId(quiz.getId())
                .question(quiz.getQuestion())
                .rewardPoint(quiz.getRewardExp())
                .options(options)
                .build();
    }

    @Transactional(readOnly = true)
    public WeeklyAttendanceDto getWeeklyAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<Attendance> weeklyAttends = attendanceRepository.findAllByUserIdAndAttendanceDateBetween(
                userId, monday, monday.plusDays(6));

        List<WeeklyAttendanceDto.AttendanceDto> attendanceList = IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate date = monday.plusDays(i);
                    boolean checked = weeklyAttends.stream()
                            .anyMatch(a -> a.getAttendanceDate().equals(date));
                    return new WeeklyAttendanceDto.AttendanceDto(date, checked);
                })
                .collect(Collectors.toList());

        return new WeeklyAttendanceDto(attendanceList);
    }

    public QuizSolveResponseDto solveQuiz(Long userId, QuizSolveRequest request) {
        if (attendanceRepository.findByUserIdAndAttendanceDate(userId, LocalDate.now()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_ATTENDED);
        }

        // 해결: QuizSolveRequest가 record이므로 .questionId()로 호출
        Quiz quiz = quizService.findQuizById(request.questionId());

        List<String> options = quiz.getOptionsList();
        // 해결: .optionId()로 호출
        String selectedAnswer = options.get(request.optionId() - 1);
        boolean isCorrect = quiz.getAnswer().equals(selectedAnswer);

        if (isCorrect) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            // 해결: Quiz 엔티티에 @Builder가 있어야 작동함
            attendanceRepository.save(Attendance.builder()
                    .user(user)
                    .attendanceDate(LocalDate.now())
                    .isQuizSolved(true)
                    .build());

            user.addExp(quiz.getRewardExp());
            return new QuizSolveResponseDto(true, quiz.getRewardExp());
        }

        return new QuizSolveResponseDto(false, 0);
    }
}