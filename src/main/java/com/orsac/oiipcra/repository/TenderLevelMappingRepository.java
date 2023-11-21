package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TenderLevelMapping;
import com.orsac.oiipcra.entities.WorkProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderLevelMappingRepository extends JpaRepository<TenderLevelMapping,Integer> {

}
