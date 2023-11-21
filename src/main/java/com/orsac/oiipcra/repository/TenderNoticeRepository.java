package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.TenderNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderNoticeRepository extends JpaRepository<TenderNotice, Integer> {
    TenderNotice findById(int id);
    Boolean existsByWorkIdentificationCode(String workIdentificationCode);

    Boolean existsByTenderId(Integer tenderId);


}
