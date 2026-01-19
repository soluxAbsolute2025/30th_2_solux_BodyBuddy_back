package com.solux.bodybubby.domain.healthlog.entity.repository;

import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {

    // 1. 특정 날짜, 특정 약, 특정 시간대 기록 찾기 (취소할 때 사용)
    @Query("SELECT m FROM MedicationLog m " +
           "WHERE m.preset.userId = :userId " +
           "AND m.preset.id = :medicationId " +
           "AND m.intakeDate = :date " +
           "AND m.intakeSlot = :slot")
    Optional<MedicationLog> findLogByCondition(
            @Param("userId") Long userId,
            @Param("medicationId") Long medicationId,
            @Param("date") LocalDate date,
            @Param("slot") IntakeSlot slot
    );

    // 2. 특정 유저의 해당 날짜 모든 복용 기록 조회 (일별 조회용)
    @Query("SELECT m FROM MedicationLog m " +
           "JOIN FETCH m.preset " + 
           "WHERE m.preset.userId = :userId " +
           "AND m.intakeDate = :date")
    List<MedicationLog> findAllByUserIdAndDate(
            @Param("userId") Long userId, 
            @Param("date") LocalDate date
    );

    // 3. 중복 체크용
    boolean existsByPresetIdAndIntakeDateAndIntakeSlot(Long presetId, LocalDate intakeDate, IntakeSlot intakeSlot);
}