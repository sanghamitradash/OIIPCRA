package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.AgencyMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<AgencyMaster,Integer> {

    AgencyMaster findAgencyById(Integer id);

}
