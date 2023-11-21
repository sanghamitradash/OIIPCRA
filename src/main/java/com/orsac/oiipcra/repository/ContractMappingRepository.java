package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ContractMappingModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractMappingRepository extends JpaRepository<ContractMappingModel,Integer> {
    ContractMappingModel findContractMappingById(Integer id);
}
