package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.IssueTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<IssueTracker, Integer> {
    IssueTracker findIssueById(int id);
}
