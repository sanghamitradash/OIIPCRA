package com.orsac.oiipcra.repository;


import com.orsac.oiipcra.entities.WorkProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkProjectRepository extends JpaRepository<WorkProjectMapping, Integer> {

    WorkProjectMapping findByTenderNoticeId(int tenderNoticeId);
    WorkProjectMapping findById(int id);

}
