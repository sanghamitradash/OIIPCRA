package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.dto.GrievanceDto;
import com.orsac.oiipcra.entities.GrievanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrievanceRepository extends JpaRepository<GrievanceEntity, Integer> {
}
