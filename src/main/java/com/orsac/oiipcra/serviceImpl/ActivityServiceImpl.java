package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.exception.RecordExistException;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.*;
import com.orsac.oiipcra.service.ActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Value("${accessEstimateDocument}")
    private String accessEstimateDocument;

    @Autowired
    private ActivityQryRepository activityQryRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private ActivityEstimateTankMappingRepository activityEstimateTankMappingRepository;

    @Autowired
    private PipMappingRepository pipMappingRepository;

    @Autowired
    private PipDetailsRepository pipDetailsRepository;

    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private ActivityEstimatedRepository activityEstimatedRepository;

    @Override
    public List<ComponentInfo> getAllComponentsByPipId(int pipId, int parentId) {
        return activityQryRepository.getAllComponentsByPipId(pipId, parentId);
    }

    @Override
    public List<ComponentResponse> getComponentHierarchy() {
        List<ComponentResponse> parentList = activityQryRepository.getChildList(0);
        return getRecursiveList(parentList);
    }

    @Override
    public ActivityOrTerminalInfo getTerminalInfoById(int id) {
        return activityQryRepository.getTerminalInfoById(id);
    }

    @Override
    public List<ActivityTargetInfo> getActivityTargetInfo(int id) {
        return activityQryRepository.getActivityTargetInfo(id);
    }

    /*@Override
    public List<ActivityExpenditureInfo> getActivityExpenditureInfo(int id, int financialYr) {
        return activityQryRepository.getActivityExpenditure(id, financialYr);
    }*/

    @Override
    public ActivityEntity saveActivity(ActivityRequest activityRequest) {
        String code = activityQryRepository.getCode(activityRequest.getParentId());
        Integer checkUniqueCode = checkUniqueCodeByMasterHeadId(activityRequest.getCode(), activityRequest.getMasterHeadId());
        if (checkUniqueCode > 0) {
            throw new RecordExistException("Activity", "Code", activityRequest.getCode());
        }
        Boolean codeStatus = false;
        try {
            int intValue = Integer.parseInt(code);
            codeStatus = true;
            intValue = intValue + 1;
            code = String.valueOf(intValue);
        } catch (NumberFormatException e) {
            codeStatus = false;
            int length = code.length();
            if (length == 1) {
                char charValue = code.charAt(0);
                String next = String.valueOf((char) (charValue + 1));
                code = next;
            } else if (length > 1) {
                char charValue = code.charAt(length - 1);
                String next = String.valueOf((char) (charValue + 1));
                code = code.substring(0, length - 1) + next;
            }
        }
        ActivityEntity activityEntity = new ActivityEntity();
        BeanUtils.copyProperties(activityRequest, activityEntity);
        activityEntity.setActiveFlag(true);
        activityEntity.setCode(code);
        activityEntity.setApprovalStatus(1);
        ActivityEntity savedActivity = activityRepository.save(activityEntity);
        return savedActivity;
    }

    @Override
    public ActivityInformationDto getActivityInformation(int activityId) {
        return activityQryRepository.getActivityInformation(activityId);
    }

    @Override
    public Integer checkUniqueCodeByMasterHeadId(String code, Integer masterHeadId) {
        return activityQryRepository.checkUniqueCodeByMasterHeadId(code, masterHeadId);
    }

    @Override
    public PipMappingEntity savePipMappingDetails(ActivityRequest activityRequest, int masterHeadDetailsId) {
        PipMappingEntity pipMappingEntity = new PipMappingEntity();
        BeanUtils.copyProperties(activityRequest, pipMappingEntity);
        pipMappingEntity.setMasterHeadDetailsId(masterHeadDetailsId);
        pipMappingEntity.setPipId(1);
        PipMappingEntity savedPipMapping = pipMappingRepository.save(pipMappingEntity);
        return savedPipMapping;
    }

    @Override
    public List<PipDetailsEntity> savePipDetails(ActivityRequest activityRequest, int pipMappingId) {
        List<PipDetailsEntity> pipDetailsEntity = new ArrayList<>();
        for (int i = 0; i < activityRequest.getFinyrId().size(); i++) {
            PipDetailsEntity pipDetailEntityObj = new PipDetailsEntity();
            pipDetailEntityObj.setPipMappingId(pipMappingId);
            pipDetailEntityObj.setFinyrId(activityRequest.getFinyrId().get(i));
            pipDetailEntityObj.setUnitId(activityRequest.getUnitId().get(i));
            pipDetailEntityObj.setPhysicalTarget(activityRequest.getPhysicalTarget().get(i));
            pipDetailEntityObj.setFinancialTarget(activityRequest.getFinancialTarget().get(i));
            pipDetailEntityObj.setUnitCostRs(activityRequest.getUnitCostRs().get(i));
            pipDetailEntityObj.setContractAmount(activityRequest.getContractAmount());
            pipDetailEntityObj.setCreatedBy(activityRequest.getCreatedBy());
            pipDetailEntityObj.setActiveFlag(true);
            pipDetailsEntity.add(pipDetailEntityObj);
        }
        return pipDetailsRepository.saveAll(pipDetailsEntity);
    }

    @Override
    public ActivityAddEntity saveActivityEstimate(ActivityEstimateAddDto activityRequest, MultipartFile file) {

        ActivityAddEntity activityAddEntity = new ActivityAddEntity();
        activityAddEntity.setActivityId(activityRequest.getActivityId());
        activityAddEntity.setLevelId(activityRequest.getLevelId());
        if(activityRequest.getEstimateType()==3){
            activityAddEntity.setStatusId(4);
            activityAddEntity.setApprovedStatus(3);
        }
        else{
            activityAddEntity.setStatusId(3);
            activityAddEntity.setApprovedStatus(1);
        }

      /*  activityAddEntity.setDistId(activityRequest.getDistId());
        activityAddEntity.setBlockId(activityRequest.getBlockId());*/
        activityAddEntity.setWorkType(activityRequest.getWorkType());
        activityAddEntity.setApprovalOrder(activityRequest.getApprovalOrder());
        activityAddEntity.setNameOfWork(activityRequest.getNameOfWork());
        activityAddEntity.setTechnicalSanctionNo(activityRequest.getTechnicalSanctionNo());
        activityAddEntity.setProjectId(1);
        if(activityRequest.getProcurementType()!=null && activityRequest.getProcurementType() >0) {
            activityAddEntity.setProcurementType(activityRequest.getProcurementType());
        }
        activityAddEntity.setDistrictZoneIdentification(activityRequest.getDistrictZoneIdentification());
        activityAddEntity.setNolOfTorByWb(activityRequest.getNolOfTorByWb());
        activityAddEntity.setApprovalRef(activityRequest.getApprovalRef());
        activityAddEntity.setCorrespondanceFileNo(activityRequest.getCorrespondanceFileNo());
        activityAddEntity.setPeriodOfCompletion(activityRequest.getPeriodOfCompletion());
        activityAddEntity.setStartDate(activityRequest.getStartDate());
        activityAddEntity.setEndDate(activityRequest.getEndDate());
        activityAddEntity.setEstimatedAmount(activityRequest.getEstimatedAmount());

        activityAddEntity.setEstimateType(activityRequest.getEstimateType());
        activityAddEntity.setReviewType(activityRequest.getReviewType());
        activityAddEntity.setMarketApproach(activityRequest.getMarketApproach());
        activityAddEntity.setLoanCreditNo(activityRequest.getLoanCreditNo());
        activityAddEntity.setProcurementDocumentType(activityRequest.getProcurementDocumentType());
        activityAddEntity.setHighSeaShRisk(activityRequest.getHighSeaShRisk());
        activityAddEntity.setProcurementProcess(activityRequest.getProcurementProcess());
        activityAddEntity.setEvaluationOptions(activityRequest.getEvaluationOptions());
        // activityAddEntity.setApprovedBy(activityRequest.getApprovedBy());
        activityAddEntity.setCreatedBy(activityRequest.getCreatedBy());
        if(file!=null) {
            activityAddEntity.setDocumentName(file.getOriginalFilename());
            activityAddEntity.setDocumentPath(accessEstimateDocument);
        }
        activityAddEntity.setActiveFlag(true);
        return activityEstimatedRepository.save(activityAddEntity);
    }

    @Override
    public List<ActivityEstimateTankMappingEntity> saveActivityEstimateTankMapping(List<ActivityEstimateTankMappingEntity> activityRequest, Integer estimateId) {
        List<ActivityEstimateTankMappingEntity> estimateTankMapping = new ArrayList<>();
        for (ActivityEstimateTankMappingEntity estimateTank : activityRequest) {

            estimateTank.setEstimateId(estimateId);
            estimateTank.setActiveFlag(true);
            estimateTankMapping.add(estimateTank);
        }
        return activityEstimateTankMappingRepository.saveAll(estimateTankMapping);
    }

    @Override
    public Page<ActivityEstimateListingResponseDto> getAllActivityEstimate(ActivityEstimateRequestDto activityRequest) {
        List<Integer> estimateIds=new ArrayList<>();
//        getEstimateByExpdr
        if(activityRequest.getExpenditureId() != null && activityRequest.getExpenditureId() > 0){
            estimateIds = activityQryRepository.getEstimateByExpdr(activityRequest.getExpenditureId());
        }
        return activityQryRepository.getAllActivityEstimate(activityRequest, estimateIds);
    }

    @Override
    public List<ReviewTypeDto> getAllReviewType() {
        return activityQryRepository.getAllReviewType();
    }

    @Override
    public List<MarketApproachDto> getAllMarketApproach() {
        return activityQryRepository.getAllMarketApproach();
    }

    @Override
    public List<ProcurementDocumentTypeDto> getAllProcurementDocumentType() {
        return activityQryRepository.getAllProcurementDocumentType();
    }

    @Override
    public List<EvaluationOptionsDto> getAllEvaluationOptions() {
        return activityQryRepository.getAllEvaluationOptions();
    }

    @Override
    public List<ProcurementProcessDto> getAllProcurementProcess() {
        return activityQryRepository.getAllProcurementProcess();
    }

    @Override
    public List<Activity> getActivityByDeptId(Integer deptId, Integer parentId) {
        List<Activity> activityByParentId = new ArrayList<>();
        List<Activity> activityByParentFinal = new ArrayList<>();
        if (deptId != null) {
            activityByParentId = activityQryRepository.getActivityByParentIdAndDeptId(deptId, parentId);
        }
        for (Activity item : activityByParentId) {
            String depName = "";
            List<String> deptName = activityQryRepository.getActivityDeptId(item.getId());
            for (String departName : deptName) {
                if (depName.isEmpty()) {
                    depName = departName;
                } else {
                    depName = depName + ", " + departName;
                }
            }
            item.setDeptName(depName);
            activityByParentFinal.add(item);
        }

        return activityByParentFinal;
    }


    @Override
    public String getTechnicalSanctionNo() throws ParseException {
        Integer technicalSanctionNo = activityQryRepository.getTechnicalSanctionNo();
        Integer finalTechnicalSanctionNo = technicalSanctionNo + 1;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateTime = dateFormat.format(new Date());
        Date date = dateFormat.parse(currentDateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentYear = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String finalDay = "";
        if (day / 10 == 0) {
            finalDay = "0" + day;
        }
        int currentMonth = month + 1;
        String lastMonth = "";
        if (currentMonth / 10 == 0) {
            lastMonth = "0" + currentMonth;
        }
        return finalTechnicalSanctionNo + "-" + currentYear + lastMonth + finalDay;
    }

    @Override
    public ActivityEstimateResponseDto getActivityEstimateByID(Integer estimateId) {
        ActivityEstimateResponseDto activityEstimate = activityQryRepository.getActivityEstimateByID(estimateId);
        if (activityEstimate.getApprovedBy() != null) {
            UserInfoDto userInfo = userQueryRepository.getUserById(activityEstimate.getApprovedBy());
            activityEstimate.setApprovedUserName(userInfo.getName());
        }
        return activityEstimate;
    }

    @Override
    public List<ActivityEstimateTankMappingDto> getActivityEstimateTankById(Integer estimateId) {
        List<ActivityEstimateTankMappingDto> activityEstimateTankList = activityQryRepository.getActivityEstimateTankById(estimateId);
        return activityEstimateTankList;
    }


    @Override
    public boolean updateEstimate(Integer estimateID, ActivityEstimateAddDto activityRequest, MultipartFile file) {

        return activityQryRepository.updateEstimate(estimateID, activityRequest, file);
    }

    @Override
    public List<ActivityEstimateTankMappingEntity> updateActivityEstimateTank(List<ActivityEstimateTankMappingEntity> activityEstimateTankMapping, Integer estimateId) {
        List<ActivityEstimateTankMappingEntity> estimateTankMapping = new ArrayList<>();
         activityQryRepository.deactivateEstimateMapping(estimateId);
        for (ActivityEstimateTankMappingEntity estimateTank : activityEstimateTankMapping) {
            estimateTank.setEstimateId(estimateId);
            estimateTank.setActiveFlag(true);
            estimateTankMapping.add(estimateTank);
        }
        return activityEstimateTankMappingRepository.saveAll(estimateTankMapping);
    }

    @Override    public boolean updateEstimateApproval(Integer estimateID, ApprovalDto approvalRequest) {
        return activityQryRepository.updateEstimateApproval(estimateID, approvalRequest);
    }

    @Override
    public boolean deactivateEstimate(Integer estimateId) {

        return activityQryRepository.deactivateEstimate(estimateId);
    }

    /*@Override
    public List<ActivityContractInfo> getActivityContractInfo(int id, int financialYr) {
        return activityQryRepository.getActivityContractInfo(id, financialYr);
    }*/

    @Override
    public List<ActivityTreeTargetInfo> getActivityTreeTargetInfo(int parentId, int financialYr) {
        return activityQryRepository.getActivityTreeTargetInfo(parentId, financialYr);
    }

    @Override
    public List<Integer> getTerminalIds(int parentId) {
        return activityQryRepository.getTerminalId(parentId);
    }

    @Override
    public ActivityEntity updateActivity(int id, ActivityRequest activityRequest) {
        ActivityEntity existingEntity = activityRepository.findById(id);
        if (existingEntity == null) {
            throw new RecordNotFoundException("Activity", "id", id);
        }
        existingEntity.setName(activityRequest.getName());
        existingEntity.setUpdatedBy(activityRequest.getUpdatedBy());
        ActivityEntity save = activityRepository.save(existingEntity);
        return save;
    }

    @Override
    public List<PipDetailsEntity> updatePipDetails(ActivityRequest activityRequest, int id) {
        //PipDetailsEntity existingPipDetails = pipDetailsRepository.findById(id);
        List<PipDetailsEntity> existingPipDetails = pipDetailsRepository.findByPipMappingId(id);
        if (existingPipDetails == null) {
            throw new RecordNotFoundException("PipDetails", "id", id);
        }
        for (int i = 0; i < existingPipDetails.size(); i++) {
            existingPipDetails.get(i).setFinyrId(activityRequest.getFinyrId().get(i));
            existingPipDetails.get(i).setPhysicalTarget(activityRequest.getPhysicalTarget().get(i));
            existingPipDetails.get(i).setFinancialTarget(activityRequest.getFinancialTarget().get(i));
            existingPipDetails.get(i).setUpdatedBy(activityRequest.getUpdatedBy());
        }
        return pipDetailsRepository.saveAll(existingPipDetails);
    }

    @Override
    public List<ComponentDetailsDto> getComponentActivity(Integer id) {
        Activity detailsById = masterQryRepository.getDetailsByParentId(id);
        List<ComponentDetailsDto> componentDetails = new ArrayList<>();
        List<ComponentDetailsDto> componentDetails1 = new ArrayList<>();
        if (detailsById.getIsTerminal()) {
            componentDetails = activityQryRepository.getComponentActivity(detailsById.getId());
        } else {
            Double totalEstimatedAmount = 0.0;
            Integer totalEstimateApproved = 0;
            Integer totalEstimatePending = 0;
            Integer totalEstimateRejected = 0;
            Double totalExpenditure = 0.0;
            Integer totalCompleted = 0;
            Integer totalOngoing = 0;
            componentDetails1 = activityQryRepository.getComponentActivityDetails(id);
            CountDto count = activityQryRepository.getCount(id);
            for (ComponentDetailsDto cd : componentDetails1) {
                totalEstimatedAmount = totalEstimatedAmount + cd.getTotalEstimatedAmount();
                totalEstimateApproved = totalEstimateApproved + cd.getTotalEstimateApproved();
                totalEstimatePending = totalEstimatePending + cd.getTotalEstimatePending();
                totalEstimateRejected = totalEstimateRejected + cd.getTotalEstimateRejected();
                totalExpenditure = totalExpenditure + cd.getTotalExpenditure();
                totalCompleted = totalCompleted + cd.getTotalCompleted();
                totalOngoing = totalOngoing + cd.getTotalOngoing();
            }
            //Get all estimates from terminal id for Approved Data
//            ;;

//            int countActivities =
            List<Integer> subActivities= activityQryRepository.getTerminalId(id);
            List<Integer> countActivities = new ArrayList<>();
            List<Integer> countSubComp = new ArrayList<>();
            List<Integer> countSubActivities=new ArrayList<>();
           if(subActivities.size()>0) {
               //Estimate
                countSubActivities = activityQryRepository.getCompletedEstimatesSubactivity(subActivities);
               List<Integer> countSubActivitiesComp1 = activityQryRepository.getCompletedEstimatesSubactivityComp1(subActivities);
               for (int i = 0; i < countSubActivitiesComp1.size(); i++) {
                   if (!countSubActivities.contains(countSubActivitiesComp1.get(i))) {
                       countSubActivities.add(countSubActivitiesComp1.get(i));
                   }
               }

               if (countSubActivities.size() > 0) {
                   countActivities = activityQryRepository.getCompletedEstimatesActivity(countSubActivities);


                   if (countActivities.size() > 0) {
                       List<Activity> activityVal = activityQryRepository.getAllUpperData(countActivities);
                       if (activityVal.size() > 0) {
                           if (activityVal.get(0).getMasterHeadId() != 1) {
                               countSubComp = activityQryRepository.getCompletedEstimatesActivity(countActivities);
                           }
                       }
                   }
               }
           }


            //Contract
            List<Integer> countSubActivitiesCont = activityQryRepository.getCompletedContractSubactivity(subActivities);
            List<Integer> countSubActivitiesComp2 = activityQryRepository.getPhysicalProgressSAComp1(subActivities);


            for(int i =0 ;i<countSubActivitiesComp2.size();i++){
                if(!countSubActivitiesCont.contains(countSubActivitiesComp2.get(i))){
                    countSubActivitiesCont.add(countSubActivitiesComp2.get(i));
                }
            }

            List<Integer> countSubCompCont = new ArrayList<>();
            List<Integer> countActivitiesCont=new ArrayList<>();
           if(countSubActivitiesCont.size()>0) {
               countActivitiesCont = activityQryRepository.getCompletedEstimatesActivity(countSubActivitiesCont);


               if (countActivitiesCont.size() > 0) {
                   List<Activity> activityValCont = activityQryRepository.getAllUpperData(countActivitiesCont);
                   if (activityValCont.size() > 0) {
                       if (activityValCont.get(0).getMasterHeadId() != 1) {
                           countSubCompCont = activityQryRepository.getCompletedEstimatesActivity(countActivitiesCont);
                       }
                   }
               }
           }

            ComponentDetailsDto cd2 = new ComponentDetailsDto();
            cd2.setTotalEstimatedAmount(totalEstimatedAmount);
            cd2.setTotalEstimateApproved(totalEstimateApproved);
            cd2.setTotalEstimatePending(totalEstimatePending);

            cd2.setTotalActivityImplemented(countActivitiesCont.size());
            cd2.setTotalSubActivityImplemented(countSubActivitiesCont.size());
            cd2.setTotalSubComponentImplemented(countSubCompCont.size());

            cd2.setTotalActivityApproved(countActivities.size());
            cd2.setTotalSubActivityApproved(countSubActivities.size());
            cd2.setTotalSubComponentApproved(countSubComp.size());


            cd2.setTotalEstimateRejected(totalEstimateRejected);
            cd2.setTotalExpenditure(totalExpenditure);
            cd2.setTotalCompleted(totalCompleted);
            cd2.setTotalOngoing(totalOngoing);
            cd2.setTotalActivity(count.getTotalActivity());
            cd2.setTotalSubActivity(count.getTotalSubActivity());
            cd2.setTotalSubComponent(count.getTotalSubComponent());
            cd2.setIsTerminal(false);
            cd2.setId(detailsById.getId());
            cd2.setName(detailsById.getName());
            cd2.setParentId(detailsById.getParentId());
            cd2.setDescription(detailsById.getDescription());
            String depName = "";
            List<String> deptName = activityQryRepository.getActivityDeptId(cd2.getId());
            for (String departName : deptName) {
                if (depName.isEmpty()) {
                    depName = departName;
                } else {
                    depName = depName + ", " + departName;
                }
            }
            cd2.setDeptName(depName);

            componentDetails.add(cd2);
        }
        return componentDetails;
    }

    @Override
    public List<Activity> getActivityByParentId(Integer userId, Integer parentId) {
        Integer deptIdByUserId = masterQryRepository.getDeptByUserId(userId);
        Integer roleIdByUserId = masterQryRepository.getRoleByUserId(userId);
        List<Activity> activityByParentId = new ArrayList<>();
        List<Activity> activityByParentFinal = new ArrayList<>();
        if (roleIdByUserId > 4) {
            activityByParentId = activityQryRepository.getActivityByParentIdAndDeptId(deptIdByUserId, parentId);
        } else {
            activityByParentId = activityQryRepository.getActivityByParentId(parentId);
        }
        for (Activity item : activityByParentId) {
            String depName = "";
            List<String> deptName = activityQryRepository.getActivityDeptId(item.getId());
            for (String departName : deptName) {
                if (depName.isEmpty()) {
                    depName = departName;
                } else {
                    depName = depName + ", " + departName;
                }
            }
            item.setDeptName(depName);
            activityByParentFinal.add(item);
        }

        return activityByParentFinal;
    }
    public List<Activity> getAllSubActivity(Integer userId) {
        Integer deptIdByUserId = masterQryRepository.getDeptByUserId(userId);
        Integer roleIdByUserId = masterQryRepository.getRoleByUserId(userId);
        List<Activity> activityByParentId = new ArrayList<>();
        List<Activity> activityByParentFinal = new ArrayList<>();
        if (roleIdByUserId > 4) {
            activityByParentId = activityQryRepository.getActivityByDeptId(deptIdByUserId);
        } else {
            activityByParentId = activityQryRepository.getSubActivity();
        }


        return activityByParentId;
    }

    @Override
    public List<ActivityLevelDto> getAllActivityLevel() {
        return activityQryRepository.getAllActivityLevel();
    }

    @Override
    public List<ActivityStatusDto> getAllMasterHeadDropDown(Integer parentId) {
        return activityQryRepository.getAllMasterHeadDropDown(parentId);
    }

    @Override
    public Boolean deactivatePipDetailsByPipMappingId(Integer pipMappingId) {
        return activityQryRepository.deactivatePipDetailsByPipMappingId(pipMappingId);
    }

    @Override
    public NameCodeTree getParentNameCodeTree(int nodeId) {
        NameCodeTree nameCodeTree = activityQryRepository.getNameCodeTree(nodeId);
        return nameCodeTree;
    }

    @Override
    public List<IdNameCodeTree> getParentNameCodeStruct(int nodeId) {
        return activityQryRepository.getNameCodeStruct(nodeId);
    }

    @Override
    public List<TerminalIdName> getTerminalActivityNameCode(int terminalId) {
        return activityQryRepository.getTermialActivityNameCode(terminalId);
    }

    public List<ComponentResponse> getRecursiveList(List<ComponentResponse> parentList) {
        for (ComponentResponse compRes : parentList) {
            Integer id = compRes.getId();
            if (!compRes.isTerminal()) {
                List<ComponentResponse> childList = activityQryRepository.getChildList(id);
                compRes.setChildren(childList);
                getRecursiveList(childList);
            }
        }
        return parentList;
    }
}
