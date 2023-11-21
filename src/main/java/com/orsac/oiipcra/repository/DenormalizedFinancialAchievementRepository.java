package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DenormalizedAchievement;
import com.orsac.oiipcra.entities.DenormalizedFinancialAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DenormalizedFinancialAchievementRepository extends JpaRepository<DenormalizedFinancialAchievement, Integer> {
}
