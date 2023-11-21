package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PipMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface PipMappingRepository extends JpaRepository<PipMappingEntity, Serializable> {

    PipMappingEntity findById(Integer id);
}
