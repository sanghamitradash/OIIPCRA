package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.FinancialBidInfo;
import com.orsac.oiipcra.entities.FinancialBidDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialBidRepository extends JpaRepository<FinancialBidDetails, Integer> {
    Boolean existsByBidderId(Integer bidderId);


}
