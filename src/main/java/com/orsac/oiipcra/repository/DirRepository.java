package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DesignationMaster;
import com.orsac.oiipcra.entities.DirEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirRepository extends JpaRepository<DirEntity,Integer> {
}
