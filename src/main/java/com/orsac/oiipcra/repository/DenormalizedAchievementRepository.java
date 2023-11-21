package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DemographicDetails;
import com.orsac.oiipcra.entities.DenormalizedAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DenormalizedAchievementRepository extends JpaRepository<DenormalizedAchievement, Integer> {
}
