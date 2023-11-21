package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ContractMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<ContractMaster,Integer> {

    ContractMaster findContractById(Integer id);
}
