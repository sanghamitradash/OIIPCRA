package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

public interface ActivityService {
    List<ComponentInfo> getAllComponentsByPipId(int pipId, int parentId);

    List<ComponentResponse> getComponentHierarchy();

    ActivityOrTerminalInfo getTerminalInfoById(int id);

    List<ActivityTargetInfo> getActivityTargetInfo(int id);

    //List<ActivityExpenditureInfo> getActivityExpenditureInfo(int id, int financialYr);

    ActivityEntity saveActivity(ActivityRequest activityRequest);

    ActivityInformationDto getActivityInformation(int activityId);

    Integer checkUniqueCodeByMasterHeadId(String code, Integer masterHeadId);

    PipMappingEntity savePipMappingDetails(ActivityRequest activityRequest, int masterHeadDetailsId);

    List<PipDetailsEntity> savePipDetails(ActivityRequest activityRequest, int pipMappingId);

    ActivityAddEntity saveActivityEstimate(ActivityEstimateAddDto activityRequest, MultipartFile file);

    List<ActivityEstimateTankMappingEntity> saveActivityEstimateTankMapping(List<ActivityEstimateTankMappingEntity> activityRequest,Integer estimateId);

    //List<ActivityEstimateListingResponseDto> getAllActivityEstimate(ActivityEstimateRequestDto activityRequest);

    String getTechnicalSanctionNo() throws ParseException;

    ActivityEstimateResponseDto getActivityEstimateByID(Integer estimateId);

    List<ActivityEstimateTankMappingDto> getActivityEstimateTankById(Integer estimateId);

    boolean updateEstimate(Integer estimateID, ActivityEstimateAddDto activityRequest,MultipartFile file);

    List<ActivityEstimateTankMappingEntity> updateActivityEstimateTank(List<ActivityEstimateTankMappingEntity> activityEstimateTankMapping,Integer estimateId);

    boolean updateEstimateApproval(Integer estimateID, ApprovalDto estimateApprovalRequest);

    boolean deactivateEstimate(Integer estimateId);

    //List<ActivityContractInfo> getActivityContractInfo(int id, int financialYr);

    List<ActivityTreeTargetInfo> getActivityTreeTargetInfo(int parentId, int financialYr);

    NameCodeTree getParentNameCodeTree(int nodeId);

    List<IdNameCodeTree> getParentNameCodeStruct(int nodeId);

    List<TerminalIdName> getTerminalActivityNameCode(int terminalId);

    List<Integer> getTerminalIds(int parentId);

    ActivityEntity updateActivity(int id, ActivityRequest activityRequest);

    List<PipDetailsEntity> updatePipDetails(ActivityRequest activityRequest, int id);

    List<ComponentDetailsDto> getComponentActivity(Integer id);

    List<Activity> getActivityByParentId(Integer userId, Integer parentId);

    List<ActivityLevelDto> getAllActivityLevel();
    List<ActivityStatusDto> getAllMasterHeadDropDown(Integer parentId);

    Boolean deactivatePipDetailsByPipMappingId(Integer pipMappingId);

    Page<ActivityEstimateListingResponseDto> getAllActivityEstimate(ActivityEstimateRequestDto activityRequest);

    List<ReviewTypeDto> getAllReviewType();

    List<MarketApproachDto> getAllMarketApproach();

    List<ProcurementDocumentTypeDto> getAllProcurementDocumentType();

    List<EvaluationOptionsDto> getAllEvaluationOptions();

    List<ProcurementProcessDto> getAllProcurementProcess();

    List<Activity> getActivityByDeptId(Integer deptId, Integer parentId);
}
