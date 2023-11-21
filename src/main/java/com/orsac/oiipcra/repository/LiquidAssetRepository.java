package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.LiquidAssetAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquidAssetRepository extends JpaRepository<LiquidAssetAvailability,Integer>{
}
