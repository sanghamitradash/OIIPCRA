package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DesignationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<DesignationMaster,Integer> {
    DesignationMaster findDesignationById(Integer id);
}
