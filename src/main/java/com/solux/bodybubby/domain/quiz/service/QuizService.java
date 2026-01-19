package com.solux.bodybubby.domain.quiz.service;

import com.solux.bodybubby.domain.quiz.dto.request.QuizRequestDto;
import com.solux.bodybubby.domain.quiz.entity.Quiz;
import com.solux.bodybubby.domain.quiz.repository.QuizRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;

    // 1. 관리자: 퀴즈 생성
    public Long createQuiz(QuizRequestDto dto) {
        String optionsString = String.join(",", dto.options());

        Quiz quiz = Quiz.builder()
                .question(dto.question())
                .options(optionsString)
                .answer(dto.answer())
                .rewardExp(dto.rewardPoint())
                .build();

        return quizRepository.save(quiz).getId();
    }

    @Transactional(readOnly = true)
    public Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUIZ_NOT_FOUND));
    }

    public void deleteQuiz(Long quizId) {
        // 1. 삭제할 퀴즈가 존재하는지 먼저 확인
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUIZ_NOT_FOUND));

        // 2. DB에서 삭제
        quizRepository.delete(quiz);
    }

}