package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.ActivityUpperHierarchyInfo;
import com.orsac.oiipcra.bindings.ContractInfo;
import com.orsac.oiipcra.bindings.IssueInfoListing;
import com.orsac.oiipcra.bindings.IssueSearchRequest;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.IssueDocumentRepository;
import com.orsac.oiipcra.repository.IssueRepository;
import com.orsac.oiipcra.repository.IssueTrackImageRepository;
import com.orsac.oiipcra.repositoryImpl.IssueRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.service.IssueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IssueServiceImpl implements IssueService {
    @Autowired
    private IssueRepositoryImpl issueRepository;

    @Autowired
    private IssueRepository issueRepo;

    @Autowired
    private IssueDocumentRepository issueDocumentRepository;

    @Autowired
    private IssueTrackImageRepository issueTrackImageRepository;

    @Value("${accessIssueDocumentPath}")
    private String accessIssueDocumentPath;

    @Value("${accessIssueImagePath}")
    private String accessIssueImagePath;

    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;

    @Override
    @Transactional
    public Page<IssueInfoListing> getIssueList(IssueSearchRequest issueSearchRequest) {

        List<Integer> tankIds = new ArrayList<>();

        if(issueSearchRequest.getTankId()!=null && issueSearchRequest.getTankId()>0){
            tankIds.add(issueSearchRequest.getTankId());
        }else if(issueSearchRequest.getContractId()!=null && issueSearchRequest.getContractId()>0){
            //Get Issue by Contract
            List<Integer> tankData = surveyRepositoy.getTankIdsByContractId(issueSearchRequest.getContractId());
            tankIds.addAll(tankData);
        }else if(issueSearchRequest.getInvoiceId()!=null && issueSearchRequest.getInvoiceId()>0){
            //Get Issue by Invoice
            List<Integer> tankData = surveyRepositoy.getTankIdsByInvoiceId(issueSearchRequest.getInvoiceId());
            tankIds.addAll(tankData);
        } else if (issueSearchRequest.getDeptId()!=null && issueSearchRequest.getDeptId() > 0) {
            //Get Issue by Dept
            List<Integer> tankData = surveyRepositoy.getTankIdsByDeptId(issueSearchRequest.getDeptId());
            tankIds.addAll(tankData);
        }

        return issueRepository.getAllIssueList(issueSearchRequest, tankIds);
    }

    @Override
    public ContractInfo getIssueById(Integer issueId) {
        return issueRepository.getIssueById(issueId);
    }
    @Override
    public List<IssueDocumentDto> getIssueDocument(Integer issueId) {

        return issueRepository.getIssueDocument(issueId);
    }

    @Override
    public IssueTracker saveIssue(IssueTrackerDto issueTracker) {
        IssueTracker issueTracker1 = new IssueTracker();
        BeanUtils.copyProperties(issueTracker, issueTracker1);
       /* if(issueTracker.getPermissionRequired()==false){
            issueTracker1.setDeptId(1);
        }*/
        issueTracker1.setStatus(4);
        return issueRepo.save(issueTracker1);
    }

    @Override
    public IssueDocument saveIssueDocument(IssueDocumentDto issueDocumentDto, Integer id, MultipartFile files) {



        IssueDocument issueDocument1 = new IssueDocument();
        BeanUtils.copyProperties(issueDocumentDto, issueDocument1);
        issueDocument1.setIssueId(id);
        issueDocument1.setDocName(files.getOriginalFilename());
        issueDocument1.setDocPath(accessIssueDocumentPath + "/" +id+"/"+ files.getOriginalFilename());
        return issueDocumentRepository.save(issueDocument1);
    }

    @Override
    public List<IssueTrackImages> saveIssueImage(List<IssueTrackImagesDto> issueTrackImagesDto, Integer id, MultipartFile[] issueImages) {
        List<IssueTrackImages> image = new ArrayList<>();
        for(MultipartFile multipartFile : issueImages) {

                IssueTrackImages issueTrackImages1 = new IssueTrackImages();
                issueTrackImages1.setImageLocation(accessIssueImagePath + "/" +id+"/"+ multipartFile.getOriginalFilename());
                issueTrackImages1.setIssueId(id);
                issueTrackImages1.setImageName(multipartFile.getOriginalFilename());
                issueTrackImages1.setLongitude(issueTrackImagesDto.get(0).getLongitude());
                issueTrackImages1.setLatitude(issueTrackImagesDto.get(0).getLatitude());
                issueTrackImages1.setAltitude(issueTrackImagesDto.get(0).getAltitude());
                issueTrackImages1.setCreatedBy(issueTrackImagesDto.get(0).getCreatedBy());
                issueTrackImages1.setUpdatedBy(issueTrackImagesDto.get(0).getUpdatedBy());

                issueTrackImageRepository.save(issueTrackImages1);
                image.add(issueTrackImages1);
          }

        return image;
    }

    @Override
    public IssueTracker updateIssueById(int id, IssueTrackerDto issueTracker) {
        IssueTracker existingIssue = issueRepo.findIssueById(id);
        if (existingIssue == null) {
            throw new RecordNotFoundException("Issue", "id", id);
        }
        existingIssue.setIssueDate(issueTracker.getIssueDate());
        existingIssue.setDescription(issueTracker.getDescription());
        existingIssue.setPermissionRequired(issueTracker.getPermissionRequired());
        existingIssue.setDeptId(issueTracker.getDeptId());
        existingIssue.setApprovedBy(issueTracker.getApprovedBy());
        //existingIssue.setStatus(4);
        existingIssue.setStatus(issueTracker.getStatus());
        existingIssue.setUpdatedBy(issueTracker.getUpdatedBy());
        existingIssue.setResolvedBy(issueTracker.getResolvedBy());
        existingIssue.setResolutionRemarks(issueTracker.getResolutionRemarks());
        existingIssue.setResolutionLevel(issueTracker.getResolutionLevel());
        existingIssue.setDesignationId(issueTracker.getDesignationId());
        existingIssue.setResolvedUserId(issueTracker.getResolvedUserId());
       return issueRepo.save(existingIssue);
    }

    @Override
    public IssueDocument updateIssueDocument(IssueDocumentDto issueDocumentDto, Integer id, MultipartFile files) {
        IssueDocument existingIssueDocument = issueDocumentRepository.findIssueByIssueId(id);
        if (existingIssueDocument == null) {
            throw new RecordNotFoundException("Issue", "id", id);
        }
        existingIssueDocument.setIssueId(id);
        existingIssueDocument.setDocName(files.getOriginalFilename());
        existingIssueDocument.setDocPath(accessIssueDocumentPath + "/" +id+"/"+ files.getOriginalFilename());
        return issueDocumentRepository.save(existingIssueDocument);
    }

    @Override
    public IssuePermissionDto getIssueByIssueId(Integer issueId) {
        return issueRepository.getIssueByIssueId(issueId);
    }

    @Override
    public List<IssueTypeDto> getIssueType() {
        return issueRepository.getIssueType();
    }

    @Override
    public List<ContractDto> getAllContract() {
        return issueRepository.getAllContract();
    }

    @Override
    public List<IssueStatusDto> getIssueStatus() {
        return issueRepository.getIssueStatus();
    }

    @Override
    public List<IssueResolutionLevelDto> getResolutionLevel() {
        return issueRepository.getResolutionLevel();
    }

    @Override
    public List<UserDto> getUserByDesignation(Integer designationId) {
        return issueRepository.getUserByDesignation(designationId);
    }

    @Override
    public List<DesignationDto> getDesignationByUserLevelId(Integer userLevel,Integer deptId) {
        return issueRepository.getDesignationByUserLevelId(userLevel,deptId);
    }

    @Override
    public List<IssueTrackImagesDto> getIssueImage(Integer issueId) {
        return issueRepository.getIssueImage(issueId);
    }

   /* @Override
    public IssueTrackImages updateIssueImage(IssueTrackImagesDto issueTrackImagesDto, Integer id, MultipartFile issueImages) {
        IssueTrackImages existingIssueImages = issueTrackImageRepository.findIssueByIssueId(id);
        if (existingIssueImages == null) {
            throw new RecordNotFoundException("Issue", "id", id);
        }
        existingIssueImages.setIssueId(id);
        existingIssueImages.setImageName(issueImages.getOriginalFilename());
        existingIssueImages.setImageLocation("https://ofarisbucket.s3.ap-south-1.amazonaws.com/IssueDocument");
        return issueTrackImageRepository.save(existingIssueImages);
    }*/

}



