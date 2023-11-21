package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ContractDocumentModel;
import com.orsac.oiipcra.entities.ContractMappingModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractDocumentRepository  extends JpaRepository<ContractDocumentModel,Integer> {
    Boolean existsByContractId(Integer contractId);
}
