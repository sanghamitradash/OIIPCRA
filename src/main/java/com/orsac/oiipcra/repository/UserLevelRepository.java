package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLevelRepository extends JpaRepository<UserLevel,Integer> {
    UserLevel findUserLevelById(Integer id);
}
