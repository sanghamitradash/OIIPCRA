package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TankSurveyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TankSurveyImageRepository extends JpaRepository<TankSurveyImage,Integer> {

    List<TankSurveyImage> findBySurveyId(int surveyId);

}
