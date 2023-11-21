package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PaniPanchayatDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaniPanchayatRepository extends JpaRepository<PaniPanchayatDetailsEntity,Integer> {
}
