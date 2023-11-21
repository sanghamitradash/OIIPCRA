package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.RecgargeShaftImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecgargeShaftImageRepository extends JpaRepository<RecgargeShaftImage,Integer> {

    List<RecgargeShaftImage> findBySurveyId(int surveyId);
}
