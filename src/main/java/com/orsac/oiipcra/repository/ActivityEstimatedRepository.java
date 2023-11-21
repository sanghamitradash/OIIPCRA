package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ActivityAddEntity;
import com.orsac.oiipcra.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface ActivityEstimatedRepository extends JpaRepository<ActivityAddEntity, Integer> {
    ActivityAddEntity getEstimateById(Integer id);
}
