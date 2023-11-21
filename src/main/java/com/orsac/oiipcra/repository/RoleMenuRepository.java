package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.ContractMaster;
import com.orsac.oiipcra.entities.RoleMenuMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleMenuRepository  extends JpaRepository<RoleMenuMaster,Integer> {

    RoleMenuMaster findRoleMenuById(Integer id);
}
