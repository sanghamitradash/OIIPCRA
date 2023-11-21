package com.orsac.oiipcra.repository;


import com.orsac.oiipcra.entities.ExpenditureMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureMappingRepository extends JpaRepository<ExpenditureMapping, Integer> {
    Boolean existsByInvoiceId(Integer invoiceId);
}
