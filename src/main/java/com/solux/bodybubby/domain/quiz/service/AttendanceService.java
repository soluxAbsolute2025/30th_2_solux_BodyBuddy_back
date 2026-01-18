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

    private final QuizRepository quizRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    /**
     * 1. 오늘의 퀴즈 정보 조회
     * 반환 구조: { questionId, question, rewardPoint, options: [{id, text}, ...] }
     */
    @Transactional(readOnly = true)
    public QuizResponseDto getTodayQuiz(Long userId) {
        LocalDate today = LocalDate.now();

        Quiz quiz = quizRepository.findByDisplayDate(today)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUIZ_NOT_FOUND));

        // DB 콤마 문자열 -> [{id: 1, text: "옵션1"}, ...] 변환
        List<String> rawOptions = quiz.getOptionsList();
        List<QuizResponseDto.OptionDto> options = IntStream.range(0, rawOptions.size())
                .mapToObj(i -> new QuizResponseDto.OptionDto(i + 1, rawOptions.get(i)))
                .collect(Collectors.toList());

        return QuizResponseDto.builder()
                .questionId(quiz.getId())
                .question(quiz.getQuestion())
                .rewardPoint(quiz.getRewardExp())
                .options(options)
                .build();
    }

    /**
     * 2. 주간 출석 현황 조회
     * 반환 구조: { attendance: [{date, checked}, ...] }
     */
    @Transactional(readOnly = true)
    public WeeklyAttendanceDto getWeeklyAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<Attendance> weeklyAttends = attendanceRepository.findAllByUserIdAndAttendanceDateBetween(
                userId, monday, monday.plusDays(6));

        // 가지고 계신 WeeklyAttendanceDto.AttendanceDto 구조에 맞게 매핑
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

    /**
     * 3. 퀴즈 정답 제출 및 결과 반환
     * 반환 구조: { correct, earnedPoint }
     */
    public QuizSolveResponseDto solveQuiz(Long userId, QuizSolveRequest request) {
        // 중복 출석 체크
        if (attendanceRepository.findByUserIdAndAttendanceDate(userId, LocalDate.now()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_ATTENDED);
        }

        Quiz quiz = quizRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUIZ_NOT_FOUND));

        // 정답 검증 (optionId는 1부터 시작하므로 -1 해서 인덱스 접근)
        List<String> options = quiz.getOptionsList();
        String selectedAnswer = options.get(request.getOptionId() - 1);
        boolean isCorrect = quiz.getAnswer().equals(selectedAnswer);

        if (isCorrect) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            // 출석 데이터 저장 및 경험치 지급
            attendanceRepository.save(Attendance.builder()
                    .user(user)
                    .attendanceDate(LocalDate.now())
                    .isQuizSolved(true)
                    .build());

            user.addExp(quiz.getRewardExp());
            return new QuizSolveResponseDto(true, quiz.getRewardExp());
        }

        // 오답일 경우 포인트 0 반환 (다시 시도 가능하도록 에러를 던지지 않음)
        return new QuizSolveResponseDto(false, 0);
    }
}