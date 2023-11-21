package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PhysicalProgressExecutedLog;
import com.orsac.oiipcra.entities.PhysicalProgressPlannedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicalProgressExecutedLogRepository extends JpaRepository<PhysicalProgressExecutedLog,Integer> {
}
