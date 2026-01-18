package com.solux.bodybubby.domain.healthlog.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solux.bodybubby.domain.healthlog.entity.MedicationPreset;
import com.solux.bodybubby.domain.user.entity.User;

import java.util.List;

public interface MedicationPresetRepository extends JpaRepository<MedicationPreset, Long> {
    List<MedicationPreset> findByUserId(Long userId);
    
}