package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.IssueTrackImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueTrackImageRepository extends JpaRepository<IssueTrackImages,Integer> {
    IssueTrackImages findIssueByIssueId(Integer id);
}
