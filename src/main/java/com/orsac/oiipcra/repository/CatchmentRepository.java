package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.CatchmentDetails;
import com.orsac.oiipcra.entities.DprInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatchmentRepository extends JpaRepository<CatchmentDetails, Integer> {
}
