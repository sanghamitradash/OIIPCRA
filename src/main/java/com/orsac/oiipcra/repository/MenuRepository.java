package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.MenuMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuMaster,Integer> {
    MenuMaster findMenuById(int id);
}
