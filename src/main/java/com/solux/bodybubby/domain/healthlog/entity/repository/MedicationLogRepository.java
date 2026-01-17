package com.solux.bodybubby.domain.healthlog.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.solux.bodybubby.domain.healthlog.entity.IntakeSlot;
import com.solux.bodybubby.domain.healthlog.entity.MedicationLog;
import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {
@Query("SELECT m FROM MedicationLog m " +
           "WHERE m.preset.userId = :userId " +   // 👈 [수정됨] user.id -> userId
           "AND m.preset.id = :medicationId " +
           "AND m.intakeDate = :date " +
           "AND m.intakeSlot = :slot")
    Optional<MedicationLog> findLogByCondition(
            @Param("userId") Long userId,
            @Param("medicationId") Long medicationId,
            @Param("date") LocalDate date,
            @Param("slot") IntakeSlot slot
    );
    @Query("SELECT m FROM MedicationLog m " +
           "WHERE m.preset.userId = :userId " +
           "AND m.intakeDate = :date")
    List<MedicationLog> findAllByUserIdAndDate(
            @Param("userId") Long userId, 
            @Param("date") LocalDate date
    );
   boolean existsByPresetIdAndIntakeDateAndIntakeSlot(Long presetId, LocalDate intakeDate, IntakeSlot intakeSlot);
    
    // [변경 후] "Preset 안에 있는 UserId로 찾아줘"
    List<MedicationLog> findByPresetUserId(Long userId);
}
