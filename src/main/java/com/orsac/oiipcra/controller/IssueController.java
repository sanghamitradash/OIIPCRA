package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.IssueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/issue")
public class IssueController {
    @Autowired
    private IssueService issueService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityQryRepository activityQryRepository;

    @Value("${accessIssueDocumentPath}")
    private String accessIssueDocumentPath;

    @Value("${accessIssueImagePath}")
    private String accessIssueImagePath;

    @Autowired
    private AWSS3StorageService awss3StorageService;

    @PostMapping("/getIssueList")
    public OIIPCRAResponse getIssueList(@RequestBody IssueSearchRequest issueSearchRequest)
    {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            IssueTracker issueTracker = new IssueTracker();
            Page<IssueInfoListing> issueListPage = issueService.getIssueList(issueSearchRequest);
            List<IssueInfoListing> issueList = issueListPage.getContent();
            for(IssueInfoListing item1 : issueList) {
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getActivityId());
                String code = "";
//                for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
//                    code += item.getCode();
//                    if(!code.isEmpty() && item.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
                if (currentHierarchyInfoById.size() == 1) {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                    }
                    item1.setCode(code);

                } else {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                        if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);

                }
            }
           /* for(int i = 0; i<issueList.size(); i++){
                if(issueList.get(i).getTankId() != null && issueList.get(i).getTankId() > 0){
                    issueList.get(i).setIssueTypeName("Project");
                }
                else if(issueList.get(i).getContractId() != null && issueList.get(i).getContractId() > 0){
                    issueList.get(i).setIssueTypeName("Contract");
                }
                else if(issueList.get(i).getWorkId() != null && issueList.get(i).getWorkId() > 0){
                    issueList.get(i).setIssueTypeName("Work/ Tender");
                }
                else if(issueList.get(i).getActivityId() != null && issueList.get(i).getActivityId() >0) {
                    issueList.get(i).setIssueTypeName("Activity");
                }
            }*/
            /*for(IssueInfoListing item2 : issueList) {
                if (issueSearchRequest.getTankId() != null) {
                    item2.setIssueTypeName("Project");
                } else if (issueSearchRequest.getContractId() != null) {
                    item2.setIssueTypeName("Contract");
                } else if (issueSearchRequest.getWorkId() != null) {
                    item2.setIssueTypeName("Work/ Tender");
                } else if (issueSearchRequest.getSubActivityId() != null) {
                    item2.setIssueTypeName("Activity");
                }
            }*/

            result.put("issueList", issueList);
            result.put("currentPage", issueListPage.getNumber());
            result.put("totalItems", issueListPage.getTotalElements());
            result.put("totalPages", issueListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("IssueList");
        }catch (Exception e){
            e.printStackTrace();
            log.info("Issue List Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getIssueByIssueId")
    public OIIPCRAResponse getIssueByIssueId(@RequestParam Integer issueId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            
            IssuePermissionDto issuePermission = issueService.getIssueByIssueId(issueId);
            //ActivityEstimateResponseDto activity1=activityQryRepository.getActivityEstimateByID(issuePermission.getEstimateId());
            List<ActivityUpperHierarchyInfo> upperLevel= new ArrayList<>();

            if(issuePermission.getSubActivityId()!=null){
                upperLevel=activityQryRepository.getUpperHierarchyInfoById(issuePermission.getSubActivityId());

            }
            else
            {
                ActivityEstimateResponseDto activity1=activityQryRepository.getActivityEstimateByID(issuePermission.getEstimateId());
                upperLevel=activityQryRepository.getUpperHierarchyInfoById(activity1.getActivityId());

            }
            /*if(issuePermission.getTankId()!=null){
                issuePermission.setIssueType("Project");
            }else if(issuePermission.getContractId()!=null){
                issuePermission.setIssueType("Contract");
            }else if(issuePermission.getWorkId()!=null){
                issuePermission.setIssueType("Work/ Tender");
            }else if(issuePermission.getSubActivityId()!=null){
                issuePermission.setIssueType("Activity");
            }*/

            String documentPath=accessIssueDocumentPath + "/" + issueId + "/";
            String imagePath = accessIssueImagePath + "/"+ issueId + "/";



           //ContractInfo issue =issueService.getIssueById(issueId);
            List<IssueDocumentDto> issueDocument=issueService.getIssueDocument(issueId);
            for( int i=0;i<issueDocument.size();i++){
                issueDocument.get(i).setDocPath(documentPath+issueDocument.get(i).getDocName());
            }
            List<IssueTrackImagesDto> issueImage=issueService.getIssueImage(issueId);
            for( int i=0;i<issueImage.size();i++){
                issueImage.get(i).setImageLocation(imagePath+issueImage.get(i).getImageName());
            }
         //   ActivityEstimateResponseDto activityEstimateById = activityService.getActivityEstimateByID(issuePermission.getEstimateId());
            if(issuePermission!=null) {
                result.put("issuePermission",issuePermission);
                result.put("upperLevel", upperLevel);
                //result.put("activityEstimateById",activity1);
                result.put("issueDocument",issueDocument);
                result.put("issueImage",issueImage);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All Issue");
            }
            else{
                //result.put("Issue", issue);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/createIssue")
    public OIIPCRAResponse saveIssue(@RequestParam(name = "data") String data,
                                       @RequestParam(name = "doc", required = false) MultipartFile file,
                                       @RequestParam(name = "image",required = false)MultipartFile[] issueImages) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            IssueTrackerDto issueTracker = objectMapper.readValue(data, IssueTrackerDto.class);
            IssueTracker issueMObj = issueService.saveIssue(issueTracker);

            if(file!=null && issueTracker.getIssueDocument() != null) {
                IssueDocument issueDocumentObj = issueService.saveIssueDocument(issueTracker.getIssueDocument(), issueMObj.getId(), file);
                    boolean saveDocument1 = awss3StorageService.uploadDocument1(file, String.valueOf(issueMObj.getId()), file.getOriginalFilename());

            }

            if (issueImages!=null && issueTracker.getIssueTrackImages().size()>0 && issueImages.length > 0) {
                List<IssueTrackImages> issueTrackImageObj = issueService.saveIssueImage(issueTracker.getIssueTrackImages(),issueMObj.getId(),issueImages );
                for (MultipartFile mult : issueImages) {
                    boolean saveDocument = awss3StorageService.uploadIssueTrackerImages(mult, String.valueOf(issueMObj.getId()), mult.getOriginalFilename());
                }
            }
            result.put("IssueTrackerMaster", issueMObj);
           // result.put("IssueDocument", issueDocumentObj);
           // result.put("IssueTrackImages",issueTrackImageObj);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Issue Created");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/updateIssueById")
    public OIIPCRAResponse updateIssueById(@RequestParam int id, @RequestParam(name = "data") String data,
                                           @RequestParam(name = "doc", required = false) MultipartFile files,
                                           @RequestParam(name = "image",required = false)MultipartFile  issueImages){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            IssueTrackerDto issueTracker = mapper.readValue(data, IssueTrackerDto.class);
            IssueTracker issueTrackerObj = issueService.updateIssueById(id, issueTracker);
         /*   IssueDocument issueDocumentObj = issueService.updateIssueDocument(issueTracker.getIssueDocument(), issueTrackerObj.getId(),files);
         //   IssueTrackImages issueTrackImagesObj = issueService.updateIssueImage(issueTracker.getIssueTrackImagesDto(), issueTrackerObj.getId(),issueImages );
            if (files!=null) {

                    boolean saveDocument = awss3StorageService.uploadIssueDocument(files, String.valueOf(issueTrackerObj.getId()), files.getOriginalFilename());
            }*/
            result.put("IssueTracker", issueTrackerObj);
           // result.put("IssueDocument", issueDocumentObj);
          //  result.put("IssueTrackImages", issueTrackImagesObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Issue Updated Successfully");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping ("/getIssueType")
    public OIIPCRAResponse getIssueType(){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<IssueTypeDto> issueType = issueService.getIssueType();
            result.put("issueType", issueType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Issue Type");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllContract")
    public OIIPCRAResponse getAllContract(){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractDto> contract = issueService.getAllContract();
            result.put("contract", contract);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get all Contract");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping ("/getIssueStatus")
    public OIIPCRAResponse getIssueStatus(){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<IssueStatusDto> issueStatus = issueService.getIssueStatus();
            result.put("issueStatus", issueStatus);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get All Issue Status");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping ("/getResolutionLevel")
    public OIIPCRAResponse getResolutionLevel(){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<IssueResolutionLevelDto> level = issueService.getResolutionLevel();
            result.put("level", level);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get All Resolution Level");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping ("/getUserByDesignationId")
    public OIIPCRAResponse getUserByDesignation(@RequestParam int designationId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserDto> user = issueService.getUserByDesignation(designationId);
            result.put("user", user);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("User List By DesignationId");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping ("/getDesignationByUserLevelId")
    public OIIPCRAResponse getDesignationByUserLevelId(@RequestParam int userLevelId,@RequestParam int deptId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DesignationDto> designation = issueService.getDesignationByUserLevelId(userLevelId,deptId);
            result.put("designation", designation);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Designation List By UserLevelId");
        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }




}
