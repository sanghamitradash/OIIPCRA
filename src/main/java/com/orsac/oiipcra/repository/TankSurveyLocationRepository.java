package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TankSurveyLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TankSurveyLocationRepository extends JpaRepository<TankSurveyLocation,Integer> {
    List<TankSurveyLocation> findBySurveyId(int surveyId);

}
