package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DesignationMaster;
import com.orsac.oiipcra.entities.QcbsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QcbsRepository extends JpaRepository<QcbsEntity,Integer> {
}
