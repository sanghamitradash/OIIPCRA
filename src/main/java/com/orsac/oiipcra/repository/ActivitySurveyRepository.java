package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ActivitySurvey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivitySurveyRepository extends JpaRepository<ActivitySurvey,Integer> {
    ActivitySurvey findById(int id);
}
