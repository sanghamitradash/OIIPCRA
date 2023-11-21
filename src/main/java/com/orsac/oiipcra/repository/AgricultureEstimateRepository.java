package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ActivityAddEntity;
import com.orsac.oiipcra.entities.AgricultureEstimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgricultureEstimateRepository extends JpaRepository<AgricultureEstimate, Integer> {
}
