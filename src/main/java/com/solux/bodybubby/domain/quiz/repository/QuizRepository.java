package com.solux.bodybubby.domain.quiz.repository;

import com.solux.bodybubby.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // 특정 날짜에 게시될 퀴즈를 조회합니다.
    Optional<Quiz> findByDisplayDate(LocalDate displayDate);

    // (선택) 특정 날짜에 이미 등록된 퀴즈가 있는지 중복 체크할 때 사용합니다.
    boolean existsByDisplayDate(LocalDate displayDate);
}
