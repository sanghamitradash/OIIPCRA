package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.Tender;
import com.orsac.oiipcra.entities.TenderNotice;
import com.orsac.oiipcra.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderRepository extends JpaRepository<Tender,Integer> {

    Boolean existsByBidId(String BidId);
    Tender findById(int id);



}
