package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.OfficeDataEntity;
import com.orsac.oiipcra.entities.RoleMenuMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeDataSaveRepository extends JpaRepository<OfficeDataEntity,Integer> {
    Boolean existsByDivisionId(Integer divisionId);

    OfficeDataEntity findByDivisionId(Integer divisionId);

}
