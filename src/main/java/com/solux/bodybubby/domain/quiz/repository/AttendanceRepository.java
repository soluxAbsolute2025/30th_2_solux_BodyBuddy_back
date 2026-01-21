package com.solux.bodybubby.domain.quiz.repository;

import com.solux.bodybubby.domain.quiz.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserIdAndAttendanceDate(Long userId, LocalDate date);
    // 이번 주 기록 조회를 위해 (월요일~일요일)
    List<Attendance> findAllByUserIdAndAttendanceDateBetween(Long userId, LocalDate start, LocalDate end);
}