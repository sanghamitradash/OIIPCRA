package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface ActivityRepository extends JpaRepository<ActivityEntity, Serializable> {

    ActivityEntity findById(int id);
}
