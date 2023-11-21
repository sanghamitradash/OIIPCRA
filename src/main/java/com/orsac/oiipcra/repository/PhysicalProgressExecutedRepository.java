package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PhysicalProgressExecuted;
import com.orsac.oiipcra.entities.PhysicalProgressExecutedLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicalProgressExecutedRepository extends JpaRepository<PhysicalProgressExecuted,Integer> {
}
