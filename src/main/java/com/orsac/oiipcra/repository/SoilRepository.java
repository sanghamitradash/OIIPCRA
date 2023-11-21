package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.SoilDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoilRepository extends JpaRepository<SoilDetailsEntity,Integer> {
}
