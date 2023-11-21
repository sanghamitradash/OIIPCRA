package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.IssueDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueDocumentRepository extends JpaRepository<IssueDocument,Integer> {
    IssueDocument findIssueByIssueId(Integer id);
}
