package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.ContractInfo;
import com.orsac.oiipcra.bindings.IssueInfoListing;
import com.orsac.oiipcra.bindings.IssueSearchRequest;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.IssueDocument;
import com.orsac.oiipcra.entities.IssueTrackImages;
import com.orsac.oiipcra.entities.IssueTracker;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IssueService {
    Page<IssueInfoListing> getIssueList(IssueSearchRequest issueSearchRequest);
   ContractInfo getIssueById(Integer issueId);
   List<IssueDocumentDto> getIssueDocument(Integer issueId);

    IssueTracker saveIssue(IssueTrackerDto issueTracker);


    IssueDocument saveIssueDocument(IssueDocumentDto issueDocumentDto, Integer id, MultipartFile files);

    List<IssueTrackImages> saveIssueImage(List<IssueTrackImagesDto> issueTrackImagesDto, Integer id, MultipartFile[] issueImages);

    IssueTracker updateIssueById(int id, IssueTrackerDto issueTracker);

    IssueDocument updateIssueDocument(IssueDocumentDto issueDocumentDto, Integer issueId, MultipartFile files);

    IssuePermissionDto getIssueByIssueId(Integer issueId);


    List<IssueTypeDto> getIssueType();

    List<ContractDto> getAllContract();

    List<IssueStatusDto> getIssueStatus();

    List<IssueResolutionLevelDto> getResolutionLevel();
    List<UserDto> getUserByDesignation(Integer designationId);
    List<DesignationDto> getDesignationByUserLevelId(Integer userLevel,Integer deptId);

    List<IssueTrackImagesDto> getIssueImage(Integer issueId);
}
