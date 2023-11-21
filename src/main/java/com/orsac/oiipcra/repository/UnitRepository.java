package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.UnitMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<UnitMaster,Integer> {

    UnitMaster findUnitById(Integer id);
}
