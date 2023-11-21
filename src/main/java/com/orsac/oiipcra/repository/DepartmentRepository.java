package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.DepartmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentMaster,Integer> {

    DepartmentMaster findDepartmentById(Integer id);
}
