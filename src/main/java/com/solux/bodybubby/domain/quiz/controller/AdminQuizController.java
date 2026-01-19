package com.solux.bodybubby.domain.quiz.controller;

import com.solux.bodybubby.domain.quiz.dto.request.QuizRequestDto;
import com.solux.bodybubby.domain.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/quiz")
@RequiredArgsConstructor
public class AdminQuizController {

    private final QuizService quizService; // QuizService 직접 호출

    @PostMapping
    public ResponseEntity<Long> addQuiz(@RequestBody QuizRequestDto dto) {
        return ResponseEntity.ok(quizService.createQuiz(dto));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> removeQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}