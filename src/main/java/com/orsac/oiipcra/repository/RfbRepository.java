package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DesignationMaster;
import com.orsac.oiipcra.entities.RfqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RfbRepository extends JpaRepository<RfqEntity,Integer> {
}
