package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TenderNoticeLevelMapping;
import com.orsac.oiipcra.entities.TenderNoticePublishedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderNoticePublishRepository extends JpaRepository<TenderNoticePublishedEntity, Integer> {
}
