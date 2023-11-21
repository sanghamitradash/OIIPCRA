package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DprInformationEntity;
import com.orsac.oiipcra.entities.UserAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DprRepository extends JpaRepository<DprInformationEntity, Integer> {
}
