package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.WorkTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTypeRepository extends JpaRepository<WorkTypeMaster,Integer> {
    WorkTypeMaster findWorkTypeById(Integer id);
}
