package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PaniPanchayatDetailsEntity;
import com.orsac.oiipcra.entities.PhysicalProgressPlannedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicalProgressPlannedLogRepository extends JpaRepository<PhysicalProgressPlannedLog,Integer> {
}
