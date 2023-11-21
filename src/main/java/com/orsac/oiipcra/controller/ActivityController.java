package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.repository.PipMappingRepository;
import com.orsac.oiipcra.repositoryImpl.DashboardRepositoryImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.ActivityService;
import com.orsac.oiipcra.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private SurveyService surveyService;
    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private PipMappingRepository pipMappingRepository;

    @Autowired
    private ActivityQryRepository activityQryRepository;
    @Autowired
    private DashboardRepositoryImpl dashboardRepositoryImpl;

    @Autowired
    private AWSS3StorageService awss3StorageService;

    @PostMapping("/getAllComponentsByPipId")
    public OIIPCRAResponse getComponentByPipId(@RequestParam Integer pipId,
                                               @RequestParam Integer parentId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ComponentInfo> componentList = activityService.getAllComponentsByPipId(pipId, parentId);
            if (!componentList.isEmpty() && componentList.size() > 0) {
                result.put("componentList", componentList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/getComponentTree")
    public OIIPCRAResponse getComponentHierarchy(){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ComponentResponse> componentHierarchy = activityService.getComponentHierarchy();
            response.setData(componentHierarchy);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Component Hierarchy.");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTerminalInfoById")
    public OIIPCRAResponse getTerminalInfoById(@RequestParam Integer terminalActivityId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
         //   ActivityOrTerminalInfo activityOrTerminalInfo = activityService.getTerminalInfoById(terminalActivityId);
            List<ActivityTargetInfo> activityTargetInfo = activityService.getActivityTargetInfo(terminalActivityId);
           // ContractListRequestDto contractListRequestDto = new ContractListRequestDto();
           /* contractListRequestDto.setActivityId(id);
            contractListRequestDto.setUserId(2);
            contractListRequestDto.setTypeId(-1);
            contractListRequestDto.setDivisionId(-1);
            contractListRequestDto.setAgencyId(-1);
            contractListRequestDto.setBlockId(-1);
            contractListRequestDto.setContractId(-1);
            contractListRequestDto.setDistId(-1);
            contractListRequestDto.setStatusId(-1);
            contractListRequestDto.setTankId(-1);
            contractListRequestDto.setPage(0);
            contractListRequestDto.setSize(5);
            contractListRequestDto.setSortBy("id");
            contractListRequestDto.setSortOrder("asc");*/
         /*   Page<ContractInfoListing> contractListPage = surveyService.getContractList(contractListRequestDto);
            List<ContractInfoListing> contractList = contractListPage.getContent();*/
           // result.put("terminalOrActivityInfo", activityOrTerminalInfo);
            result.put("activityTargetInfoList", activityTargetInfo);
           // result.put("activityContractList", contractList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Terminal Info");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //Method to save Component, SubComponent, Activity, SubActivity, SubSubActivity
    @PostMapping("/saveActivity")
    public OIIPCRAResponse saveActivity(@RequestBody ActivityRequest activityRequest){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            ActivityEntity savedActivity = activityService.saveActivity(activityRequest);
            PipMappingEntity savedPipMappingEntity = activityService.savePipMappingDetails(activityRequest, savedActivity.getId());
            if(savedActivity.isTerminalFlag()){
                List<PipDetailsEntity> pipDetailsEntity = activityService.savePipDetails(activityRequest, savedPipMappingEntity.getId());
                result.put("pipDetailsEntity", pipDetailsEntity);
            }
           /* if(savedActivity.isTerminalFlag()){
                List<ActivityAddEntity> estimatedEntity = activityService.saveActivityEstimate(activityRequest.getActivityAddDto(), savedActivity.getId());
                result.put("extimateDetails", estimatedEntity);
            }*/
            result.put("savedActivity", savedActivity);
            result.put("pipMappingEntity", savedPipMappingEntity);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Activity Saved");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/saveActivityEstimate")
    public OIIPCRAResponse saveActivityEstimate(@RequestParam String data,
                                                @RequestParam(name = "doc", required = false) MultipartFile file) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ActivityEstimateAddDto activityRequest = objectMapper.readValue(data, ActivityEstimateAddDto.class);

            ActivityAddEntity estimatedEntity = activityService.saveActivityEstimate(activityRequest, file);
            List<ActivityEstimateTankMappingEntity> activityEstimateTankMapping = activityService.saveActivityEstimateTankMapping(activityRequest.getActivityEstimateMappingList(), estimatedEntity.getId());
            if (file != null) {
                boolean saveDocument = awss3StorageService.uploadEstimateDocument(file, String.valueOf(estimatedEntity.getId()), file.getOriginalFilename());
            }
            result.put("estimateDetails", estimatedEntity);
            result.put("activityEstimateTankMapping", activityEstimateTankMapping);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("ActivityEstimate Saved");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTechnicalSanctionNo")
    public OIIPCRAResponse getTechnicalSanctionNo() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            String technicalSanctionNo = activityService.getTechnicalSanctionNo();
            result.put("technicalSanctionNo", technicalSanctionNo);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("TechnicalSanctionNo");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllActivityEstimate")
    public OIIPCRAResponse getAllActivityEstimate(@RequestBody ActivityEstimateRequestDto activityRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            Page<ActivityEstimateListingResponseDto> allEstimate = activityService.getAllActivityEstimate(activityRequest);
            List<ActivityEstimateListingResponseDto> estimateList = allEstimate.getContent();
           // List<ActivityEstimateListingResponseDto> allEstimate = activityService.getAllActivityEstimate(activityRequest);
            int i = 0;
            List<ActivityEstimateListingResponseDto> allEstimateData = new ArrayList<>();
            if(estimateList.size()>0 && allEstimate!=null) {
                for (ActivityEstimateListingResponseDto activity : estimateList) {
                    String districtName = "";
                    String blockName = "";
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(activity.getActivityId());
                    String code = "";
                    for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
                        code += item.getCode();
                        if(!code.isEmpty() && item.getMasterHeadId() < 1){
                            code = code+".";
                        }
                        activity.setCode(code);
                    }
                    List<ActivityEstimateTankMappingDto> activityTankList = activityService.getActivityEstimateTankById(activity.getEstimateId());
                    for (int j = 0; j < activityTankList.size(); j++) {
                        if (activityTankList.get(j).getDistrictName() != null) {
                            if (!districtName.contains(activityTankList.get(j).getDistrictName())) {
                                if (j > 0) {
                                    districtName += ",";
                                }

                                districtName += activityTankList.get(j).getDistrictName();
                            }
                        }
                        if (activityTankList.get(j).getBlockName() != null) {
                            if (!blockName.contains(activityTankList.get(j).getBlockName())) {
                                if (j > 0) {
                                    blockName += ",";
                                }

                                blockName += activityTankList.get(j).getBlockName();
                            }
                        }
                    }

                    activity.setDistrictName(districtName);
                    activity.setBlockName(blockName);
                    allEstimateData.add(activity);
                }
            }
            result.put("activityEstimate", allEstimateData);
            result.put("currentPage", allEstimate.getNumber());
            result.put("totalItems", allEstimate.getTotalElements());
            result.put("totalPages", allEstimate.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All ActivityEstimate");
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getActivityEstimateByID")
    public OIIPCRAResponse getActivityEstimateByID(@RequestParam Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ActivityEstimateResponseDto activityEstimateById = activityService.getActivityEstimateByID(estimateId);
            if(activityEstimateById.getDocPath()!=null && activityEstimateById.getDocName()!=null) {
                activityEstimateById.setFinalDocPath(activityEstimateById.getDocPath() + activityEstimateById.getEstimateId() + "/" + activityEstimateById.getDocName());
            }
            String districtName = "";
            String blockName = "";
            String tankName="";
            String divisionName="";
            List<ActivityEstimateTankMappingDto> activityTankList = activityService.getActivityEstimateTankById(estimateId);
            for (int j = 0; j < activityTankList.size(); j++) {
                if (activityTankList.get(j).getDistrictName() != null) {
                    if (!districtName.contains(activityTankList.get(j).getDistrictName())) {
                        if (j > 0) {
                            districtName += ",";
                        }

                        districtName += activityTankList.get(j).getDistrictName();
                    }
                }
                if (activityTankList.get(j).getBlockName() != null) {
                    if (!blockName.contains(activityTankList.get(j).getBlockName())) {
                        if (j > 0) {
                            blockName += ",";
                        }

                        blockName += activityTankList.get(j).getBlockName();
                    }
                }
                if (activityTankList.get(j).getTankName() != null) {
                    if (!tankName.contains(activityTankList.get(j).getTankName())) {
                        if (j > 0) {
                            tankName += ",";
                        }

                        tankName += activityTankList.get(j).getTankName();
                    }
                }
                if (activityTankList.get(j).getDivisionName() != null) {
                    if (!divisionName.contains(activityTankList.get(j).getDivisionName())) {
                        if (j > 0) {
                            divisionName += ",";
                        }

                        divisionName += activityTankList.get(j).getDivisionName();
                    }
                }
            }

            activityEstimateById.setDistrictName(districtName);
            activityEstimateById.setBlockName(blockName);
            activityEstimateById.setTankName(tankName);
            activityEstimateById.setDivisionName(divisionName);

            Integer activityId=activityQryRepository.getDistinctActivityId(estimateId);
            List<ActivityUpperHierarchyInfo>   upperLevel= activityQryRepository.getUpperHierarchyInfoById(activityId);
            result.put("upperHierarchy",upperLevel);

            result.put("activityEstimate", activityEstimateById);
            result.put("activityEstimateTankDetails", activityTankList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("ActivityEstimate By Id");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/updateActivityEstimate")
    public OIIPCRAResponse updateActivityEstimate(@RequestParam(name = "data") String data, @RequestParam Integer estimateId,
                                                 @RequestParam(required = false) MultipartFile file) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ActivityEstimateAddDto updateEstimate = mapper.readValue(data, ActivityEstimateAddDto.class);
            boolean result = activityService.updateEstimate(estimateId, updateEstimate, file);
            List<ActivityEstimateTankMappingEntity> activityEstimate = activityService.updateActivityEstimateTank(updateEstimate.getActivityEstimateMappingList(), estimateId);
            if (file != null) {
                boolean saveDocument = awss3StorageService.uploadEstimateDocument(file, String.valueOf(estimateId), file.getOriginalFilename());
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Estimate Updated");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    (Map<String, Object>) response);
        }
        return response;
    }


    @PostMapping("/updateActivityEstimateApproval")
    public OIIPCRAResponse updateActivityEstimateApproval(@RequestParam int userId, @RequestParam(name = "data") String data, @RequestParam Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            Integer roleId = masterQryRepository.getRoleByUserId(userId);
            if (roleId > 4) {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("You have no permission to approve!!");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ApprovalDto updateEstimateApproval = mapper.readValue(data, ApprovalDto.class);
                boolean result = activityService.updateEstimateApproval(estimateId, updateEstimateApproval);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("EstimateApproval Updated");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    response);
        }
        return response;
    }

    @PostMapping("/deactivateEstimate")
    public OIIPCRAResponse activateAndDeactivateMaster(@RequestParam Integer estimateId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean res = activityService.deactivateEstimate(estimateId);

            if (res==true) {
                response.setData(result);
                response.setStatus(1);
                response.setMessage("Estimate Deactivated ");
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setStatus(0);
                response.setMessage("Something went wrong");
                response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;
    }

    @PostMapping("/getBasicActivityInfo")
    public OIIPCRAResponse getBasicActivityInfo(@RequestParam Integer terminalId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
        //Activity activity = activityService.getBasicActivityInfo(terminalId);
        }catch (Exception e){
        response = new OIIPCRAResponse(0,
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                e.getMessage(),
                result);
    }
        return response;
    }

    @PostMapping("/updateActivity")
    public OIIPCRAResponse updateActivity(@RequestParam Integer id,
                                          @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            ObjectMapper mapper = new ObjectMapper();
            ActivityRequest activityRequest = mapper.readValue(data, ActivityRequest.class);
            ActivityEntity updateActivity = activityService.updateActivity(id, activityRequest);
            PipMappingEntity existingPipMappingEntity = pipMappingRepository.findById(updateActivity.getId());
            List<PipDetailsEntity> pipDetailsEntity = new ArrayList<>();
            if(updateActivity.isTerminalFlag()){
                Boolean status = activityService.deactivatePipDetailsByPipMappingId(existingPipMappingEntity.getId());
                pipDetailsEntity = activityService.savePipDetails(activityRequest, existingPipMappingEntity.getId());
                //pipDetailsEntity = activityService.updatePipDetails(activityRequest, existingPipMappingEntity.getId());
            }
            result.put("updatedActivity", updateActivity);
            result.put("updatePipDetails", pipDetailsEntity);
            response.setData(updateActivity);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Activity Updated");

        }catch (Exception e){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //This Activity is to get All terminal activity based on parent and financial Yr
    @PostMapping("/getActivityTreeTargets")
    public OIIPCRAResponse getActivityTreeTarget(@RequestParam Integer parentId,
                                                 @RequestParam(required = false, defaultValue = "0") Integer financialYr){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            List<ActivityTreeTargetInfo> activityTreeTargetInfoList = activityService.getActivityTreeTargetInfo(parentId, financialYr);
            result.put("activityTreeTargetInfoList", activityTreeTargetInfoList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Terminal Info");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //This is a dependency API will be used to get the Terminal Ids based on the parentId
    @PostMapping("/getTerminalIds")
    public OIIPCRAResponse getTerminalId(@RequestParam(required = false, defaultValue = "0") Integer parentId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> terminalIds = activityService.getTerminalIds(parentId);
            result.put("terminalIds", terminalIds);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Terminal Ids");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTerminalActivityNameCode")
    public OIIPCRAResponse getTerminalActivityNameCode(@RequestParam(required = false, defaultValue = "0") Integer terminalId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TerminalIdName> terminalActivityNameCode = activityService.getTerminalActivityNameCode(terminalId);
            result.put("terminalInfo", terminalActivityNameCode);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Terminal Info");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    //API to get the details by clicking on eye button
    @PostMapping("/getComponentActivityInfo")
    public OIIPCRAResponse getComponentActivity(@RequestParam Integer id){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ComponentDetailsDto> activityInfo = activityService.getComponentActivity(id);
            for(ComponentDetailsDto item1 : activityInfo){
                item1.setEstimateAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item1.getId(), null,null,null));
                item1.setExpenditureAmount(dashboardRepositoryImpl.getValueOfComponents(activityService.getTerminalIds(item1.getId()),null,null));
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getId());
                String code = "";
                for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
                    code += item.getCode();
                    if(!code.isEmpty() && item.getMasterHeadId() <= 1 && item.getParentId() != 0){
                        code = code+".";
                    }
                }
                item1.setCode(code);
                List<Integer> terminalIds=activityService.getTerminalIds(item1.getId());
                AdaptFinancialDto adaptEstimateData=activityQryRepository.getAdaptFinancialDataByTerminalIds(terminalIds);
//                BigDecimal estimate=new BigDecimal(adaptEstimateData.getFinancialAllocationInApp());
//                BigDecimal expenditure=new BigDecimal(adaptEstimateData.getExpenditure());
                if(adaptEstimateData!=null) {
                    item1.setEstimateAmount(new BigDecimal(item1.getEstimateAmount().doubleValue() + (adaptEstimateData.getFinancialAllocationInApp()/100)));
                    item1.setExpenditureAmount(new BigDecimal(item1.getExpenditureAmount().doubleValue() + (adaptEstimateData.getExpenditure()/100)));
                    item1.setTotalEstimateApproved(item1.getTotalEstimateApproved() + adaptEstimateData.getCount());
                }
/*                item1.setEstimateAmount(new BigDecimal(item1.getEstimateAmount().doubleValue()/Double.valueOf(100)));
                item1.setExpenditureAmount(new BigDecimal(item1.getExpenditureAmount().doubleValue()/Double.valueOf(100)));*/

            }
            result.put("activityInfo", activityInfo);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Activity Info ");
        }catch (Exception ex){
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //Getting the list of component or activity based on parentId
    @PostMapping("/getActivityByParentId")
    public OIIPCRAResponse getActivityByParentId(@RequestParam Integer userId,
                                                 @RequestParam(required = false, defaultValue = "0") Integer parentId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //Main Service which returns the activity List.
            List<Activity> activityList = activityService.getActivityByParentId(userId, parentId);

            //This service to get the Parent Hierarchy Info of a particular Id
            List<ActivityUpperHierarchyInfo> upperHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(parentId);

            String type = "";
            Boolean i = true;
            if(parentId == 0){
                type = "Component";
            }
            else{
                String code = "";
                int masterHeadId = 0;
                for(ActivityUpperHierarchyInfo item : upperHierarchyInfoById){
                    code += item.getCode();
                    if(!code.isEmpty() && item.getMasterHeadId() <= 1 && item.getParentId() != 0){
                        code = code+".";
                    }
                    /*if(!type.isEmpty()){
                        type = type+" : ";
                        //code = code+".";
                    }*/
                    type = "";
                }
                if(upperHierarchyInfoById.size()>0){
                    masterHeadId = upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getMasterHeadId();
                    if(masterHeadId == 0){
                        type = "Component " + code + ": " + upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getDescription();
                    }
                    else if (masterHeadId == 1) {
                        type = "Sub Component " + code + ": " + upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getDescription();
                    }
                    else {
                        type += code + " " + upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getName();
                    }
                }
            }
            for(Activity item1 : activityList){
//                item1.setEstimateAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item1.getId()));

                /*if(i){
                    if(item1.getMasterHeadId() == 2){
                        type += " Sub Component";
                    }
                    else if(item1.getMasterHeadId() == 3){
                        type += " Activity";
                    }
                    else if(item1.getMasterHeadId() > 3){
                        type += " Sub Activity";
                    }
                }*/
                i = false;
                if(parentId != 0){
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getId());
                    String code = "";
                    for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
                        code += item.getCode();
                        if(!code.isEmpty() && item.getMasterHeadId() <= 1){
                            code = code+".";
                        }
                    }
                    item1.setCode(code);
                }
                item1.setType(type);
            }
            result.put("activityList", activityList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Activity List");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllActivityLevel")
    public OIIPCRAResponse getAllActivityLevel() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityLevelDto> level = activityService.getAllActivityLevel();
            ;
            if (!level.isEmpty() && level.size() > 0) {
                result.put("ActivityLevel", level);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ActivityLevel.");
            } else {
                result.put("ActivityLevel", level);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //Api To Get MasterHead DropDown USing Recursive
    @PostMapping("/getAllMasterHeadDropDown")
    public OIIPCRAResponse getAllMasterHeadDropDown(@RequestParam Integer parentId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityStatusDto> level = activityService.getAllMasterHeadDropDown(parentId);
            ;
            if (!level.isEmpty() && level.size() > 0) {
                result.put("ActivityLevel", level);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ActivityLevel.");
            } else {
                result.put("ActivityLevel", level);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //This API to get All the information about the passed activity Id
    @PostMapping("/getActivityInformationById")
    public OIIPCRAResponse getActivityInformationById(@RequestParam Integer activityId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ActivityInformationDto activityInfo = activityService.getActivityInformation(activityId);
            result.put("ActivityInfo", activityInfo);
            if(activityInfo.getIsTerminal() == true){
                List<ActivityTargetInfo> activityTargetInfo = activityService.getActivityTargetInfo(activityId);
                result.put("ActivityTargetInfo", activityTargetInfo);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All ActivityLevel.");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllReviewType")
    public OIIPCRAResponse getAllReviewType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ReviewTypeDto> reviewType = activityService.getAllReviewType();
            if (!reviewType.isEmpty() && reviewType.size() > 0) {
                result.put("ReviewType", reviewType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("AllReviewType.");
            } else {
                result.put("ReviewType", reviewType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAllMarketApproach")
    public OIIPCRAResponse getAllMarketApproach() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<MarketApproachDto> marketApproach = activityService.getAllMarketApproach();
            if (!marketApproach.isEmpty() && marketApproach.size() > 0) {
                result.put("MarketApproach", marketApproach);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("AllMarketApproach.");
            } else {
                result.put("MarketApproach", marketApproach);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllProcurementDocumentType")
    public OIIPCRAResponse getAllProcurementDocumentType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProcurementDocumentTypeDto> procurementDocumentType = activityService.getAllProcurementDocumentType();
            if (!procurementDocumentType.isEmpty() && procurementDocumentType.size() > 0) {
                result.put("ProcurementDocumentType", procurementDocumentType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("AllProcurementDocumentType.");
            } else {
                result.put("ProcurementDocumentType", procurementDocumentType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAllEvaluationOptions")
    public OIIPCRAResponse getAllEvaluationOptions() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<EvaluationOptionsDto> evaluationOption = activityService.getAllEvaluationOptions();
            if (!evaluationOption.isEmpty() && evaluationOption.size() > 0) {
                result.put("EvaluationOption", evaluationOption);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("AllEvaluationOptions.");
            } else {
                result.put("EvaluationOption", evaluationOption);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllProcurementProcess")
    public OIIPCRAResponse getAllProcurementProcess() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProcurementProcessDto> procurementProcess = activityService.getAllProcurementProcess();
            if (!procurementProcess.isEmpty() && procurementProcess.size() > 0) {
                result.put("ProcurementProcess", procurementProcess);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("AllProcurementProcess.");
            } else {
                result.put("ProcurementProcess", procurementProcess);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getActivityByDeptId")
    public OIIPCRAResponse getActivityByDeptId(@RequestParam(required = false, defaultValue = "0") Integer parentId,
                                                 @RequestParam (required = false)Integer deptId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //Main Service which returns the activity List.
            List<Activity> activityList = activityService.getActivityByDeptId(deptId,parentId);

            //This service to get the Parent Hierarchy Info of a particular Id
            List<ActivityUpperHierarchyInfo> upperHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(parentId);

            String type = "";
            Boolean i = true;
            if(parentId == 0){
                type = "Component";
            }
            else{
                String code = "";
                int masterHeadId = 0;
                for(ActivityUpperHierarchyInfo item : upperHierarchyInfoById){
                    code += item.getCode();
                    if(!code.isEmpty() && item.getMasterHeadId() <= 1 && item.getParentId() != 0){
                        code = code+".";
                    }
                    /*if(!type.isEmpty()){
                        type = type+" : ";
                        //code = code+".";
                    }*/
                    type = "";
                }
                if(upperHierarchyInfoById.size()>0){
                    masterHeadId = upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getMasterHeadId();
                    if(masterHeadId == 0){
                        type = "Component " + code + ": " + upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getDescription();
                    }
                    else if (masterHeadId == 1) {
                        type = "Sub Component " + code + ": " + upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getDescription();
                    }
                    else {
                        type += code + " " + upperHierarchyInfoById.get(upperHierarchyInfoById.size()-1).getName();
                    }
                }
            }
            for(Activity item1 : activityList){
//                item1.setEstimateAmount(dashboardRepositoryImpl.TotalApproxEstCostByComponentId(item1.getId()));

                /*if(i){
                    if(item1.getMasterHeadId() == 2){
                        type += " Sub Component";
                    }
                    else if(item1.getMasterHeadId() == 3){
                        type += " Activity";
                    }
                    else if(item1.getMasterHeadId() > 3){
                        type += " Sub Activity";
                    }
                }*/
                i = false;
                if(parentId != 0){
                    List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getId());
                    String code = "";
                    for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
                        code += item.getCode();
                        if(!code.isEmpty() && item.getMasterHeadId() <= 1){
                            code = code+".";
                        }
                    }
                    item1.setCode(code);
                }
                item1.setType(type);
            }
            result.put("activityList", activityList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Activity List");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
}
