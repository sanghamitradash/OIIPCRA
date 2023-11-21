package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r where r.roleName=:name")
    Role findByRoleName(@Param("name") String name);

    Role findRoleById(Integer id);
}
