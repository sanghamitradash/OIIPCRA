package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PhysicalProgressExecutedLog;
import com.orsac.oiipcra.entities.PhysicalProgressPlanned;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicalProgressPlannedRepository extends JpaRepository<PhysicalProgressPlanned,Integer> {
}
