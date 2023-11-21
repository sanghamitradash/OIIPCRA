package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TenderStipulation;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository

public interface TenderStipulationRepository extends JpaRepository<TenderStipulation, Integer> {


    TenderStipulation findByTenderId(Integer tenderId);

    Boolean existsByTenderId(Integer tenderId);





}
