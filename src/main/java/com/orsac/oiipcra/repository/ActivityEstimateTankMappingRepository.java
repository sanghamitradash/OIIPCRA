package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ActivityEstimateTankMappingEntity;
import com.orsac.oiipcra.entities.UserAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityEstimateTankMappingRepository extends JpaRepository<ActivityEstimateTankMappingEntity, Integer> {
    ActivityEstimateTankMappingEntity findEstimateMappingById(Integer id);
}
