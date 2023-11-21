package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TankSurveyData;
import com.orsac.oiipcra.entities.TankWorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TankSurveyDataRepository extends JpaRepository<TankSurveyData,Integer> {
    TankSurveyData findById(int id);
    TankSurveyData findByTankId(int id);


   /* @Query(value = "", nativeQuery = true)
    TankSurveyData surveyTankGeoJson(int id);
*/


}
