package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.SubDepartmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubDepartmentRepository extends JpaRepository<SubDepartmentMaster,Integer> {
    SubDepartmentMaster findSubDepartmentById(Integer id);
}
