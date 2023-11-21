package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.CdsEntity;
import com.orsac.oiipcra.entities.DesignationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CdsRepository extends JpaRepository<CdsEntity,Integer> {
}
