package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.PipDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface PipDetailsRepository extends JpaRepository<PipDetailsEntity, Serializable> {

    PipDetailsEntity findById(Integer id);

    List<PipDetailsEntity> findByPipMappingId(Integer pipMappingId);
}
