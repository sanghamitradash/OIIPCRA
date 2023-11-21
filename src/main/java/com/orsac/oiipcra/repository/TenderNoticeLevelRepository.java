package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TenderLevelMapping;
import com.orsac.oiipcra.entities.TenderNoticeLevelMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderNoticeLevelRepository extends JpaRepository<TenderNoticeLevelMapping, Integer> {
    TenderNoticeLevelMapping findById(int id);
}
