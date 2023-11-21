package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.FardFinancialAchievementEntity;
import com.orsac.oiipcra.entities.FardPhysicalAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FardPhysicalAchievementRepository extends JpaRepository<FardPhysicalAchievementEntity, Integer> {
}
