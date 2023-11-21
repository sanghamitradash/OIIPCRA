package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.AgencyMaster;
import com.orsac.oiipcra.entities.LicenseMaster;
import com.orsac.oiipcra.entities.UnitMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<LicenseMaster,Integer>  {
    LicenseMaster findLicenseById(Integer id);
}
