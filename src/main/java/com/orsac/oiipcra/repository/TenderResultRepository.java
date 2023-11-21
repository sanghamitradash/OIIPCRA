package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.Role;
import com.orsac.oiipcra.entities.TenderResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenderResultRepository extends JpaRepository<TenderResult,Integer> {
    TenderResult findTenderResultById(Integer id);

}
