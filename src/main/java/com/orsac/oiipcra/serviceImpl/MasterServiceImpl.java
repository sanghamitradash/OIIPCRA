package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.entities.AgricultureEstimateMapping;
import com.orsac.oiipcra.repository.*;
import com.orsac.oiipcra.entities.DepartmentMaster;
import com.orsac.oiipcra.entities.Role;
import com.orsac.oiipcra.entities.User;
import com.orsac.oiipcra.entities.UserLevel;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.repository.MasterRepository;
import com.orsac.oiipcra.repository.RoleRepository;
import com.orsac.oiipcra.repository.UserRepository;
import com.orsac.oiipcra.repositoryImpl.DashboardRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.MasterRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.TenderRepositoryImpl;
import com.orsac.oiipcra.service.MasterService;
import com.orsac.oiipcra.utility.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MasterServiceImpl implements MasterService {

    //Here
    @Autowired
    private ConfigurableEnvironment env;
    @Autowired
    private ActivityEstimatedRepository activityEstimatedRepository;
    @Autowired
    private AgricultureEstimateRepository agricultureEstimateRepository;
    @Autowired
    private PhysicalProgressConsultancyRepository physicalProgressConsultancyRepository;
    @Autowired
    private ActivityEstimateTankMappingRepository activityEstimateTankMappingRepository;


    @Autowired
    private AgricultureEstimateMappingRepository agricultureEstimateMappingRepository;
    @Autowired
    private DenormalizedAchievementRepository denormalizedAchievementRepository;
    @Autowired
    private FardPhysicalAchievementRepository fardPhysicalAchievementRepository;


    @Autowired
    private DenormalizedFinancialAchievementRepository denormalizedFinancialAchievementRepository;
    @Autowired
    private FardFinancialAchievementRepository fardFinancialAchievementRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DashboardRepositoryImpl dashboardRepositoryImpl;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private MasterRepositoryImpl masterRepositoryImpl;

    @Autowired
    private DepartmentRepository deptRepository;

    @Autowired
    private UserLevelRepository userLevelRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private ActivityQryRepository activityQryRepository;

    @Autowired
    private WorkTypeRepository workTypeRepository;

    @Autowired
    private SubDepartmentRepository subDepartmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    TenderRepositoryImpl tenderRepositoryImpl;

    @Autowired
    private ContractMappingRepository contractMappingRepository;

    @Autowired
    private ContractDocumentRepository contractDocumentRepository;

    @Autowired
    private ApprovalStatusRepository approvalStatusRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ContractMappingRepositoryImpl contractMappingRepositoryImpl;

    @Autowired
    private PhysicalProgressPlannedRepository physicalProgressPlannedRepository;

    @Autowired
    private PhysicalProgressExecutedRepository physicalProgressExecutedRepository;

    @Autowired
    private AdaptPhysicalBeneficiaryRepository adaptPhysicalBeneficiaryRepository;

    @Autowired
    private RfbRepository rfbRepository;
    @Autowired
    private RfqRepository rfqRepository;
    @Autowired
    private DirRepository dirRepository;
    @Autowired
    private CdsRepository cdsRepository;
    @Autowired
    private QcbsRepository qcbsRepository;



    @Async("asyncExecutor")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<OIIPCRAResponse> masterSync(int userId, String lastUpdateDate) {

        OIIPCRAResponse response = new OIIPCRAResponse();
        HashMap<String, Object> masterMap = new HashMap<>();
        try {
            User user = userRepository.findById(userId);
            if (user != null) {
                // int deptId = user.getDept_id();
                masterMap.put(Constant.DEPARTMENT, masterRepository.getDepartment(lastUpdateDate));
                // masterMap.put(Constant.PROJECT,masterRepository.getProjectList(lastUpdateDate));
                // masterMap.put(Constant.AREA,masterRepository.getAreaList(lastUpdateDate));
                masterMap.put(Constant.WORK_STATUS, masterRepository.getWorkStatus(lastUpdateDate));
                masterMap.put(Constant.ROLE, masterRepository.getRole(lastUpdateDate));
                masterMap.put(Constant.APPROVAL_STATUS, masterRepository.getApprovalStatus(lastUpdateDate));
                masterMap.put(Constant.DESIGNATION, masterRepository.getDesignation(lastUpdateDate));
               /* masterMap.put(Constant.DIVISION_MASTER,masterRepository.getDivision());
                masterMap.put(Constant.SUB_DIVISION_MASTER,masterRepository.getSubDivision());
                masterMap.put(Constant.SECTION_MASTER,masterRepository.getSection());
                masterMap.put(Constant.DISTRICT_MASTER,masterRepository.getDistrict());
                masterMap.put(Constant.BLOCK_MASTER,masterRepository.getBlock());
                masterMap.put(Constant.GP_MASTER,masterRepository.getGp());
                masterMap.put(Constant.VILLAGE_MASTER,masterRepository.getVillage());*/
                masterMap.put(Constant.JURISDICTION, masterRepository.getMasterSynLevelInfo(userId));
                masterMap.put(Constant.DIV_TO_SEC, masterRepository.getMasterSynDivisionToSection(userId));
                masterMap.put(Constant.SUB_DEPT, masterRepository.getSubDept());
                masterMap.put(Constant.TANK_MASTER, masterRepository.getAllTank(userId));
                masterMap.put(Constant.Tank_STATUS, masterRepository.getStatusOfTank(lastUpdateDate));
                masterMap.put(Constant.USAGES, masterRepository.getUsage(lastUpdateDate));
                masterMap.put(Constant.TURBIDITY, masterRepository.getTurbidity(lastUpdateDate));

                response.setStatus(1);
                response.setMessage("");
                response.setData(masterMap);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                log.info("successfully synsc");
            } else {
                response.setMessage(env.getProperty(Constant.USER_REGISTER));
                response.setStatus(0);
                response.setData(Collections.EMPTY_LIST);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            log.info(e.getMessage());
            response.setData(Collections.EMPTY_LIST);
        }
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public List<DepartmentMaster> getAllDepartment() {
        List<DepartmentMaster> departmentList = masterQryRepository.getAllDepartment();
        return departmentList;
    }
    @Override
    public List<DepartmentMaster> getAllAdaptDepartment() {
        List<DepartmentMaster> departmentList = masterQryRepository.getAllAdaptDepartment();
        return departmentList;
    }

    @Override
    public List<SubDepartmentDto> getSubDepartmentByDeptId(int deptId) {
        return masterQryRepository.getSubDepartmentByDeptId(deptId);
    }

    @Override
    public List<SubDepartmentDto> getSubDepartmentById(int id) {
        return masterQryRepository.getSubDepartmentById(id);
    }

    @Override
    public SubDepartmentMaster updateSubDept(int id, SubDepartmentMaster subDepartmentMaster) {
        SubDepartmentMaster existingSubDept = subDepartmentRepository.findSubDepartmentById(id);
        if (existingSubDept == null) {
            throw new RecordNotFoundException("SubDepartment", "id", id);
        }
        existingSubDept.setName(subDepartmentMaster.getName());
        existingSubDept.setDeptId(subDepartmentMaster.getDeptId());
        existingSubDept.setUpdatedBy(subDepartmentMaster.getUpdatedBy());
        return subDepartmentRepository.save(existingSubDept);
    }

    @Override
    public List<UserLevel> getAllUserLevel(int userId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        return masterQryRepository.getAllUserLevel(userLevelIdByUserId);
    }

    @Override
    public List<UserLevel> getUserLevelById(int id) {

        return masterQryRepository.getUserLevelById(id);
    }

    @Override
    public List<DistrictBoundaryDto> getAllDistrict(int userId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<DistrictBoundaryDto> districtInfo = new ArrayList<>();
        if (userLevelIdByUserId == 1) {
            districtInfo = masterQryRepository.getAllDistrict();
        } else {
            List<Integer> listOfDistrictIdsByUserId = masterQryRepository.getListOfDistrictIdsByUserId(userId);
            districtInfo = masterQryRepository.getAllDistInfoById(listOfDistrictIdsByUserId);
        }
        return districtInfo;
    }
    @Override
    public List<CircleDto> getCircleList(int userId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<CircleDto> circleInfo = new ArrayList<>();
        if(userLevelIdByUserId ==1) {
            circleInfo = masterQryRepository.getAllCircle();
        } else {
            List<Integer> listOfDistrictIdsByUserId = masterQryRepository.getListOfDistrictIdsByUserId(userId);
            circleInfo = masterQryRepository.getCircleByDistrictIds(listOfDistrictIdsByUserId);
        }

        return circleInfo;
    }

    @Override
    public List<DistrictBoundaryDto> getAllDistrictGeoJson(int userId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<DistrictBoundaryDto> districtInfo = new ArrayList<>();
        if (userLevelIdByUserId == 1) {
            districtInfo = masterQryRepository.getAllDistrictGeoJson();
        } else {
            List<Integer> listOfDistrictIdsByUserId = masterQryRepository.getListOfDistrictIdsByUserId(userId);
            districtInfo = masterQryRepository.getAllDistInfoGeoJsonById(listOfDistrictIdsByUserId);
        }
        return districtInfo;
    }

    @Override
    public List<DistrictBoundaryDto> getAllDistrictGeoJsonByDistId(Integer districtId) {
        return masterQryRepository.getAllDistrictGeoJsonByDistId(districtId);
    }

    @Override
    public List<BlockBoundaryDto> getBlocksByDistId(int userId, int distId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<BlockBoundaryDto> blockInfo = new ArrayList<>();
        if (userLevelIdByUserId == 3 || userLevelIdByUserId == 4 || userLevelIdByUserId == 5) {
            List<Integer> listOfBlockIdsByUserId = masterQryRepository.getListOfBlockIdsByUserId(userId, distId);
            blockInfo = masterQryRepository.getAllBlcInfoById(listOfBlockIdsByUserId);
        } else {
            blockInfo = masterQryRepository.getBlocksByDistId(distId);
        }
        return blockInfo;
    }

    @Override
    public List<BlockBoundaryDto> getBlocksByDistIdGeoJson(int userId, int distId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<BlockBoundaryDto> blockInfo = new ArrayList<>();
        if (userLevelIdByUserId == 3 || userLevelIdByUserId == 4 || userLevelIdByUserId == 5) {
            List<Integer> listOfBlockIdsByUserId = masterQryRepository.getListOfBlockIdsByUserId(userId, distId);
            blockInfo = masterQryRepository.getAllBlcInfoByIdGeoJson(listOfBlockIdsByUserId);
        } else {
            blockInfo = masterQryRepository.getBlocksByDistIdGeoJson(distId);
        }
        return blockInfo;
    }

    @Override
    public List<BlockBoundaryDto> getBlocksGeoJsonByBlockId(Integer blockId) {
        return masterQryRepository.getBlocksGeoJsonByBlockId(blockId);
    }

    @Override
    public List<GpBoundaryDto> getGpByBlockId(int userId, int blockId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<GpBoundaryDto> gpInfo = new ArrayList<>();
        if (userLevelIdByUserId == 4 || userLevelIdByUserId == 5) {
            List<Integer> listOfGpIdsByUserId = masterQryRepository.getListOfGpIdsByUserId(userId, blockId);
            gpInfo = masterQryRepository.getAllGpInfoById(listOfGpIdsByUserId);
        } else {
            gpInfo = masterQryRepository.getGpByBlockId(blockId);
        }
        return gpInfo;
    }

    @Override
    public List<GpBoundaryDto> getGpByBlockIdGeoJson(int userId, int blockId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<GpBoundaryDto> gpInfo = new ArrayList<>();
        if (userLevelIdByUserId == 4 || userLevelIdByUserId == 5) {
            List<Integer> listOfGpIdsByUserId = masterQryRepository.getListOfGpIdsByUserId(userId, blockId);
            gpInfo = masterQryRepository.getAllGpInfoByIdGeoJson(listOfGpIdsByUserId);
        } else {
            gpInfo = masterQryRepository.getGpByBlockIdGeoJson(blockId);
        }
        return gpInfo;
    }

    @Override
    public List<GpBoundaryDto> getGpGeoJsonByGpId(Integer gpId) {
        return masterQryRepository.getGpByGeoJsonGpId(gpId);
    }

    @Override
    public List<VillageBoundaryDto> getVillageByGpId(int userId, int gpId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<VillageBoundaryDto> villageInfo = new ArrayList<>();
        if (userLevelIdByUserId == 5) {
            List<Integer> listOfVillageIdsByUserId = masterQryRepository.getListOfVillageIdsByUserId(userId, gpId);
            villageInfo = masterQryRepository.getAllVillageInfoById(listOfVillageIdsByUserId);
        } else {
            villageInfo = masterQryRepository.getVillagesByGpId(gpId);
        }
        return villageInfo;
    }

    @Override
    public List<VillageBoundaryDto> getVillageByGpIdGeoJson(int userId, int gpId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<VillageBoundaryDto> villageInfo = new ArrayList<>();
        if (userLevelIdByUserId == 5) {
            List<Integer> listOfVillageIdsByUserId = masterQryRepository.getListOfVillageIdsByUserId(userId, gpId);
            villageInfo = masterQryRepository.getAllVillageInfoByIdGeoJson(listOfVillageIdsByUserId);
        } else {
            villageInfo = masterQryRepository.getVillagesByGpIdGeoJson(gpId);
        }
        return villageInfo;
    }

    @Override
    public List<VillageBoundaryDto> getVillageGeoJsonByGpId(Integer villageId) {
        return masterQryRepository.getVillagesGeoJsonByVillageId(villageId);
    }

    @Override
    public List<DivisionBoundaryDto> getDivisionByDistId(int userId, int distId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<DivisionBoundaryDto> divisionInfo = new ArrayList<>();
        if (userLevelIdByUserId == 6 || userLevelIdByUserId == 7 || userLevelIdByUserId == 8) {
            List<Integer> listOfDivisionIdsByUserId = masterQryRepository.getListOfDivisionByUserId(userId, distId);
            divisionInfo = masterQryRepository.getAllDivisionInfoById(listOfDivisionIdsByUserId);
        } else {
            divisionInfo = masterQryRepository.getDivisionByDistId(distId);
        }
        return divisionInfo;
    }

    @Override
    public List<DivisionBoundaryDto> getDivisionByDistIdGeoJson(int userId, int distId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<DivisionBoundaryDto> divisionInfo = new ArrayList<>();
        if (userLevelIdByUserId == 6 || userLevelIdByUserId == 7 || userLevelIdByUserId == 8) {
            List<Integer> listOfDivisionIdsByUserId = masterQryRepository.getListOfDivisionByUserId(userId, distId);
            divisionInfo = masterQryRepository.getAllDivisionInfoByIdGeoJson(listOfDivisionIdsByUserId);
        } else {
            divisionInfo = masterQryRepository.getDivisionByDistIdGeoJson(distId);
        }
        return divisionInfo;
    }

    @Override
    public List<SubDivisionBoundaryDto> getSubDivisionByDivisionId(int userId, int divisionId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<SubDivisionBoundaryDto> subDivisionInfo = new ArrayList<>();
        if (userLevelIdByUserId == 7 || userLevelIdByUserId == 8) {
            List<Integer> listOfSubDivisionIdsByUserId = masterQryRepository.getListOfSubDivisionByUserId(userId, divisionId);
            subDivisionInfo = masterQryRepository.getAllSubDivisionInfoById(listOfSubDivisionIdsByUserId);
        } else {
            subDivisionInfo = masterQryRepository.getSubDivisionByDivisionId(divisionId);
        }
        return subDivisionInfo;
    }

    @Override
    public List<SubDivisionBoundaryDto> getSubDivisionByDivisionIdGeoJson(int userId, int divisionId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<SubDivisionBoundaryDto> subDivisionInfo = new ArrayList<>();
        if (userLevelIdByUserId == 7 || userLevelIdByUserId == 8) {
            List<Integer> listOfSubDivisionIdsByUserId = masterQryRepository.getListOfSubDivisionByUserId(userId, divisionId);
            subDivisionInfo = masterQryRepository.getAllSubDivisionInfoByIdGeoJson(listOfSubDivisionIdsByUserId);
        } else {
            subDivisionInfo = masterQryRepository.getSubDivisionByDivisionIdGeoJson(divisionId);
        }
        return subDivisionInfo;
    }

    @Override
    public List<SectionBoundaryDto> getSectionBySubDivisionId(int userId, int subDivisionId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<SectionBoundaryDto> sectionInfo = new ArrayList<>();
        if (userLevelIdByUserId == 8) {
            List<Integer> listOfSectionIdsByUserId = masterQryRepository.getListOfSectionByUserId(userId, subDivisionId);
            sectionInfo = masterQryRepository.getAllSectionInfoById(listOfSectionIdsByUserId);
        } else {
            sectionInfo = masterQryRepository.getSectionBySubDivisionId(subDivisionId);
        }
        return sectionInfo;
    }

    @Override
    public List<SectionBoundaryDto> getSectionBySubDivisionIdGeoJson(int userId, int subDivisionId) {
        Integer userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
        List<SectionBoundaryDto> sectionInfo = new ArrayList<>();
        if (userLevelIdByUserId == 8) {
            List<Integer> listOfSectionIdsByUserId = masterQryRepository.getListOfSectionByUserId(userId, subDivisionId);
            sectionInfo = masterQryRepository.getAllSectionInfoByIdGeoJson(listOfSectionIdsByUserId);
        } else {
            sectionInfo = masterQryRepository.getSectionBySubDivisionIdGeoJson(subDivisionId);
        }
        return sectionInfo;
    }

    @Override
    public List<DesignationDto> getDesignationByUserLevelId(int userLevelId, int deptId) {
        return masterQryRepository.getDesignationByUserLevelId(userLevelId, deptId);
    }

    @Override
    public List<DesignationDto> getAllDesignationByUserLevelId(int userLevelId, int deptId) {
       return  masterQryRepository.getAllDesignationByUserLevelId(userLevelId, deptId);
    }

    @Override
    public List<DesignationDto> getDesignationInfoById(int id,boolean flag) {
        return masterQryRepository.getDesignationInfoBylId(id,flag);
    }

    @Override
    public DesignationMaster updateDesignation(int id, DesignationMaster designationMaster) {
        DesignationMaster existingDesignationMaster = designationRepository.findDesignationById(id);
        if (existingDesignationMaster == null) {
            throw new RecordNotFoundException("DesignationMaster", "id", id);
        }
        existingDesignationMaster.setName(designationMaster.getName());
        existingDesignationMaster.setDescription(designationMaster.getDescription());
        existingDesignationMaster.setParentId(designationMaster.getParentId());
        existingDesignationMaster.setSubDeptId(designationMaster.getSubDeptId());
        existingDesignationMaster.setUserLevelId(designationMaster.getUserLevelId());
        existingDesignationMaster.setDeptId(designationMaster.getDeptId());
        existingDesignationMaster.setUpdatedBy(designationMaster.getUpdatedBy());
        return designationRepository.save(existingDesignationMaster);
    }

    @Override
    public Role saveRole(Role role) {
        role.setActive(true);
        return roleRepo.save(role);
    }

    @Override
    public List<RoleDto> getRoleByUserId(int userId) {
        return null;
    }


    @Override
    public Role updateRole(int id, Role role) {
        Role existingRole = roleRepo.findRoleById(id);
        if (existingRole == null) {
            throw new RecordNotFoundException("Role", "id", id);
        }
        existingRole.setRoleName(role.getRoleName());
        existingRole.setDescription(role.getDescription());
        existingRole.setParentRoleId(role.getParentRoleId());
        existingRole.setCanEdit(role.isCanEdit());
        existingRole.setCanView(role.isCanView());
        existingRole.setCanAdd(role.isCanAdd());
        existingRole.setCanDelete(role.isCanDelete());
        existingRole.setCanApprove(role.isCanApprove());
        existingRole.setDeletionRequestAccess(role.isDeletionRequestAccess());
        existingRole.setDeletionApprovalAccess(role.isDeletionApprovalAccess());
        existingRole.setAdditionRequestAccess(role.isAdditionRequestAccess());
        existingRole.setAdditionApprovalAccess(role.isAdditionApprovalAccess());
        existingRole.setSurveyAccess(role.isSurveyAccess());
        existingRole.setIssueResolutionAccess(role.isIssueResolutionAccess());
        existingRole.setPermissionAccess(role.isPermissionAccess());
        existingRole.setUpdatedBy(role.getUpdatedBy());

        return roleRepo.save(existingRole);
    }

    @Override
    public ContractMaster updateContractById(Integer contractId, ContractDto contractMaster) {
        ContractMaster existingContract = contractRepository.findContractById(contractId);
        if (existingContract == null) {
            throw new RecordNotFoundException("Contract", "id", contractId);
        }
//        existingContract.setFinyrId(contractMaster.getFinyrId());
        existingContract.setContractName(contractMaster.getContractName());
        existingContract.setContractCode(contractMaster.getContractNumber());
        existingContract.setContractName(contractMaster.getContractNumber());
        existingContract.setContractStatusId(contractMaster.getContractStatusId());
//        existingContract.setTenderId(contractMaster.getTenderId());
     /*   if(contractMaster.getTypeId()==0) {
            existingContract.setContractTypeId(null);
        }
        else{
            existingContract.setContractTypeId(contractMaster.getTypeId());
        }*/
        if( contractMaster.getContractLevelId()==0){
            existingContract.setContractLevelId(null);
        }
        else {
            existingContract.setContractLevelId(contractMaster.getContractLevelId());
        }
      /*  existingContract.setDistId(contractMaster.getDistId());
        existingContract.setBlockId(contractMaster.getBlockId());
        existingContract.setDivisionId(contractMaster.getDivisionId());
        existingContract.setSubDivisionId(contractMaster.getSubDivisionId());
        existingContract.setSectionId(contractMaster.getSectionId());*/
        existingContract.setAreaId(contractMaster.getAreaId());
        existingContract.setProcurementMadeFor(contractMaster.getProcurementMadeFor());
        existingContract.setZone(contractMaster.getZone());
//        existingContract.setFileNo(contractMaster.getFileNo());
        existingContract.setApprovalOrder(contractMaster.getApprovalOrder());
//        existingContract.setTechnicalSanctionNo(contractMaster.getTechnicalSanctionNo());
//        existingContract.setWorkId(contractMaster.getWorkId());
//        existingContract.setWorkTypeId(contractMaster.getWorkTypeId());
        existingContract.setWorkDescription(contractMaster.getWorkDescription());
//        existingContract.setRfbId(contractMaster.getRfbId());
//        existingContract.setRfdIssuedDate(contractMaster.getRfdIssuedDate());
//        existingContract.setRfdReceivedDate(contractMaster.getRfdReceivedDate());
        existingContract.setEstimatedCost(contractMaster.getEstimatedCost());
        existingContract.setEoiId(contractMaster.getEoiId());
        existingContract.setDateEoi(contractMaster.getDateEoi());
        existingContract.setAgencyId(contractMaster.getAgencyId());
        existingContract.setRfpIssuedOn(contractMaster.getRfpIssuedOn());
        existingContract.setRfpReceivedOn(contractMaster.getRfpReceivedOn());
        existingContract.setCorrespondenceFileNo(contractMaster.getCorrespondenceFileNo());
        existingContract.setContractNumber(contractMaster.getContractNumber());
        existingContract.setStipulatedDateOfComencement(contractMaster.getStipulatedDateOfComencement());
        existingContract.setStipulatedDateOfCompletion(contractMaster.getStipulatedDateOfCompletion());
        existingContract.setApprovalOrder(contractMaster.getApprovalOrder());
        existingContract.setTachnicalSanctionNo(contractMaster.getTachnicalSanctionNo());
        existingContract.setWorkId(contractMaster.getWorkId());
//        existingContract.setEstimatedCost(contractMaster.getEstimatedCost());
        existingContract.setAwardedAs(contractMaster.getAwardedAs());
        existingContract.setAgreementNumber(contractMaster.getAgreementNumber());

//        existingContract.setMobile(contractMaster.getMobile());
//        existingContract.setAwardedTo(contractMaster.getAwardedTo());
//        existingContract.setRateQuoted(contractMaster.getRateQuoted());
        existingContract.setLoaIssuedNo(contractMaster.getLoaIssuedNo());
        existingContract.setLoaIssuedDate(contractMaster.getLoaIssuedDate());
        existingContract.setRateOfPremium(contractMaster.getRateOfPremium());
        existingContract.setActualDateOfCommencement(contractMaster.getActualDateOfCommencement());
        existingContract.setActualDateOfCompletion(contractMaster.getActualDateOfCompletion());
//        existingContract.setContractNo(contractMaster.getContractNo());
       existingContract.setContractDate(contractMaster.getContractDate());
        existingContract.setContractAmount(contractMaster.getContractAmount());
        existingContract.setGst(contractMaster.getGst());
//        existingContract.setTotalContractAmt(contractMaster.getTotalContractAmt());
//       existingContract.setContractSigned(contractMaster.getContractSigned());
//        existingContract.setAgreementNo(contractMaster.getAgreementNo());
//        existingContract.setAgreementAmt(contractMaster.getAgreementAmt());
//        existingContract.setGst(contractMaster.getGst());
//        existingContract.setComencementDate(contractMaster.getComencementDate());
//        existingContract.setCompletionDate(contractMaster.getCompletionDate());
//        existingContract.setCompletionPeriod(contractMaster.getCompletionPeriod());
//        existingContract.setEot1SanctionedUpto(contractMaster.getEot1SanctionedUpto());
//        existingContract.setEot2SanctionedUpto(contractMaster.getEot2SanctionedUpto());
//        existingContract.setActivityId(contractMaster.getActivityId());
//        existingContract.setTankId(contractMaster.getTankId());
//        existingContract.setLevelId(contractMaster.getLevelId());
        if(existingContract.getTypeId()!=null && existingContract.getTypeId()>0){
            if(existingContract.getTypeId()==1){
                TenderInfo tender=  tenderRepositoryImpl.viewTenderByTenderId(existingContract.getTenderId());
                existingContract.setWorkTypeId(tender.getTenderTypeId());
            }
        }

        if(existingContract.getEstimateId()!=null && existingContract.getEstimateId()>0){
            ActivityEstimateResponseDto activityEstimate = activityQryRepository.getActivityEstimateByID(existingContract.getEstimateId());

            existingContract.setWorkTypeId(activityEstimate.getWorkType());
        }
        existingContract.setUpdatedBy(contractMaster.getUpdatedBy());
        existingContract.setContractTypeId(existingContract.getWorkTypeId());

        return contractRepository.save(existingContract);

    }

    public Boolean deactivateContractMapping(Integer id) {
        return contractMappingRepositoryImpl.deactivateContractById(id);
    }

    public Boolean deactivateContractDocument(Integer id) {
        return contractMappingRepositoryImpl.deactivateContractDocument(id);
    }

    @Override
    public List<TankInfo> getTankInfoJson(Integer distId, Integer blockId, Integer gpId, Integer villageId, Integer divisionId, Integer subDivisionId, Integer sectionId) {
        return masterQryRepository.getTankInfoJson(distId, blockId, gpId, villageId, divisionId, subDivisionId, sectionId);
    }

    @Override
    public DepartmentMaster saveDept(DepartmentMaster department) {
        department.setActive(true);
        DepartmentMaster dept = new DepartmentMaster();
        BeanUtils.copyProperties(department, dept);
        return deptRepository.save(dept);
    }

    @Override
    public DepartmentMaster updateDept(int id, DepartmentMaster departmentMaster) {
        DepartmentMaster existingDept = deptRepository.findDepartmentById(id);
        if (existingDept == null) {
            throw new RecordNotFoundException("Department", "id", id);
        }
        existingDept.setName(departmentMaster.getName());
        existingDept.setUpdatedBy(departmentMaster.getUpdatedBy());
        return deptRepository.save(existingDept);
    }

    @Override
    public UserLevel saveUserLevel(UserLevel userLevel) {
        userLevel.setActive(true);
        UserLevel userLevel1 = new UserLevel();
        BeanUtils.copyProperties(userLevel, userLevel1);
        return userLevelRepository.save(userLevel1);
    }

    @Override
    public UserLevel updateUserLevel(int id, UserLevel userLevel) {
        UserLevel existingUserLevel = userLevelRepository.findUserLevelById(id);
        if (existingUserLevel == null) {
            throw new RecordNotFoundException("UserLevel", "id", id);
        }
        existingUserLevel.setName(userLevel.getName());
        existingUserLevel.setParentId(userLevel.getParentId());
        existingUserLevel.setUpdatedBy(userLevel.getUpdatedBy());
        return userLevelRepository.save(existingUserLevel);
    }

    @Override
    public MenuMaster saveMenu(MenuMaster menuMaster) {
        menuMaster.setActive(true);
        MenuMaster menuMaster1 = new MenuMaster();
        BeanUtils.copyProperties(menuMaster, menuMaster1);
        return menuRepository.save(menuMaster1);
    }

    @Override
    public MenuMaster updateMenu(int id, MenuMaster menuMaster) {
        MenuMaster existingMenu = menuRepository.findMenuById(id);
        if (existingMenu == null) {
            throw new RecordNotFoundException("MenuMaster", "id", id);
        }
        existingMenu.setName(menuMaster.getName());
        existingMenu.setParentId(menuMaster.getParentId());
        existingMenu.setModule(menuMaster.getModule());
        existingMenu.setUpdatedBy(menuMaster.getUpdatedBy());
        return menuRepository.save(existingMenu);
    }

    @Override
    public UnitMaster saveUnit(UnitMaster unitMaster) {
        unitMaster.setActive(true);
        UnitMaster unitMaster1 = new UnitMaster();
        BeanUtils.copyProperties(unitMaster, unitMaster1);
        return unitRepository.save(unitMaster1);
    }

    @Override
    public UnitMaster updateUnit(int id, UnitMaster unitMaster) {
        UnitMaster existingUnit = unitRepository.findUnitById(id);
        if (existingUnit == null) {
            throw new RecordNotFoundException("UnitMaster", "id", id);
        }
        existingUnit.setName(unitMaster.getName());
        existingUnit.setUpdatedBy(unitMaster.getUpdatedBy());
        return unitRepository.save(existingUnit);
    }

    @Override
    public AgencyMaster saveAgency(AgencyDto agencyDto) throws ParseException {
        agencyDto.setActive(true);
        AgencyMaster agencyMaster = new AgencyMaster();

        BeanUtils.copyProperties(agencyDto, agencyMaster);
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date licenseValidity = new Date();

        if (null != agencyDto.getLicenseValidity()) {
            licenseValidity = dtFormat.parse(agencyDto.getLicenseValidity());
        }
        System.out.println(licenseValidity);
        agencyMaster.setLicenseValidity(licenseValidity);

        return agencyRepository.save(agencyMaster);
    }

    @Override
    public WorkTypeMaster saveWorkType(WorkTypeMaster workTypeMaster) {
        workTypeMaster.setActive(true);
        WorkTypeMaster workTypeMaster1 = new WorkTypeMaster();
        BeanUtils.copyProperties(workTypeMaster, workTypeMaster1);
        return workTypeRepository.save(workTypeMaster1);
    }

    @Override
    public SubDepartmentMaster saveSubDepartment(SubDepartmentMaster subDepartmentMaster) {
        subDepartmentMaster.setActive(true);
        SubDepartmentMaster subDepartmentMaster1 = new SubDepartmentMaster();
        BeanUtils.copyProperties(subDepartmentMaster, subDepartmentMaster1);
        return subDepartmentRepository.save(subDepartmentMaster1);
    }

    @Override
    public DesignationMaster saveDesignation(DesignationMaster designationMaster) {
        designationMaster.setActive(true);
        DesignationMaster designationMaster1 = new DesignationMaster();
        BeanUtils.copyProperties(designationMaster, designationMaster1);
        return designationRepository.save(designationMaster1);
    }

    @Override
    public List<RoleMenuMaster> saveRoleMenu(RoleMenuInfo roleMenuInfo) {
        List<RoleMenuMaster> roleMenuMaster = new ArrayList<>();
        for (int j = 0; j < roleMenuInfo.getMenuId().size(); j++) {
            RoleMenuMaster roleMenu = new RoleMenuMaster();
            roleMenu.setMenuId(roleMenuInfo.getMenuId().get(j));
            roleMenu.setActive(true);
            roleMenu.setCreatedBy(roleMenuInfo.getCreatedBy());
            roleMenu.setRoleId(roleMenuInfo.getRoleId());
            roleMenuMaster.add(roleMenu);
        }
        return roleMenuRepository.saveAll(roleMenuMaster);
    }

    @Override
    public RoleMenuMaster updateRoleMenu(RoleMenuInfo roleMenuInfo, Integer menuId) {
//        List<RoleMenuMaster> roleMenuMaster = new ArrayList<>();
//        for (int j = 0; j < roleMenuInfo.getMenuId().size(); j++) {
        RoleMenuMaster roleMenu = new RoleMenuMaster();
        roleMenu.setMenuId(menuId);
        roleMenu.setActive(true);
        roleMenu.setCreatedBy(roleMenuInfo.getCreatedBy());
        roleMenu.setRoleId(roleMenuInfo.getRoleId());
//            roleMenuMaster.add(roleMenu);
//        }
        return roleMenuRepository.save(roleMenu);
    }


    @Autowired
    ExpenditureQueryRepo expenditureQueryRepo;

    @Override
    public ContractMaster saveContract(ContractDto contractMaster) {
        //contractMaster.setActive(true);
        contractMaster.setActive(true);
        try{
            contractMaster.setFinyrId(expenditureQueryRepo.getFinYear(contractMaster.getContractDate()));
        }catch (Exception e){
            e.printStackTrace();
        }
        ContractMaster contractMaster1 = new ContractMaster();
        BeanUtils.copyProperties(contractMaster, contractMaster1);
        if(contractMaster.getTypeId()!=null && contractMaster.getTypeId()>0){
            if(contractMaster.getTypeId()==1){
                TenderInfo tender=  tenderRepositoryImpl.viewTenderByTenderId(contractMaster.getTenderId());
                contractMaster1.setWorkTypeId(tender.getTenderTypeId());
                //contractMaster1.setContractTypeId(tender.getTenderTypeId());
            }
        }
        if(contractMaster.getEstimateId()!=null && contractMaster.getEstimateId()>0){
            ActivityEstimateResponseDto activityEstimate = activityQryRepository.getActivityEstimateByID(contractMaster.getEstimateId());

            contractMaster1.setWorkTypeId(activityEstimate.getWorkType());
            //contractMaster1.setContractTypeId(activityEstimate.getWorkType());
        }

        contractMaster1.setContractTypeId(contractMaster1.getWorkTypeId());
        contractMaster1.setContractCode(contractMaster.getContractNumber());
        contractMaster1.setContractName(contractMaster.getContractNumber());
        if( contractMaster.getContractLevelId()==0){
            contractMaster1.setContractLevelId(null);
        }
       // contractMaster1.setContractTypeId(contractMaster.getTypeId());

        return contractRepository.save(contractMaster1);
    }

    @Autowired
    private ShlcMeetingRepository shlcMeetingRepository;
    @Autowired
    private ShlcMeetingMembersRepo shlcMeetingMembersRepo;
    @Autowired
    private ShlcMeetingProceedingsRepo shlcMeetingProceedingsRepo;
    @Override
    public ShlcMeetingEntity saveShlcMeeting(ShlcMeetingDto shlcMeetingDto) {
        ShlcMeetingEntity model = new ShlcMeetingEntity();
        model.setMeetingSerialNo(shlcMeetingDto.getMeetingSerialNo());
        model.setMeetingSequenceNo(shlcMeetingDto.getMeetingSequenceNo());
        model.setDateOfMeeting(shlcMeetingDto.getDateOfMeeting());
        model.setProceedingsIssuedLtNc(shlcMeetingDto.getProceedingsIssuedLtNc());
        model.setCommitteeFormedDate(shlcMeetingDto.getCommitteeFormedDate());
        model.setVoteOfThanks(shlcMeetingDto.getVoteOfThanks());
        model.setIsActive(true);
        model.setCreatedBy(shlcMeetingDto.getCreatedBy());
        model.setUpdatedBy(shlcMeetingDto.getUpdatedBy());
        model.setProceedingsIssueLetterDate(shlcMeetingDto.getProceedingsIssuedDate());
        return shlcMeetingRepository.save(model);
    }

    @Override
    public List<ShlcMeetingMembersEntity> saveShlcMeetingMembersEntity(List<ShlcMeetingMembersDto> shlcMeetingMembersDto,  ShlcMeetingEntity shlcMeeting) {
        List<ShlcMeetingMembersEntity> model = new ArrayList<>();



        for (ShlcMeetingMembersDto item : shlcMeetingMembersDto){

            Integer designationId = masterRepositoryImpl.getDesignationIdByUserId(item.getUserId());

            ShlcMeetingMembersEntity data = new ShlcMeetingMembersEntity();
            data.setShlcMeetingId(shlcMeeting.getId());
            data.setUserId(item.getUserId());
            data.setDesignationId(designationId);
            data.setIsActive(true);
            data.setCreatedBy(shlcMeeting.getCreatedBy());
            data.setUpdatedBy(shlcMeeting.getUpdatedBy());
            model.add(data);
        }
        return shlcMeetingMembersRepo.saveAll(model);
    }

    @Override
    public List<ShlcMeetingProceedingsEntity> saveShlcMeetingProceedings(List<ShlcMeetingProceedingsDto> shlcMeetingProceedingsDto,  ShlcMeetingEntity shlcMeeting) {
        List<ShlcMeetingProceedingsEntity> model = new ArrayList<>();
        for(ShlcMeetingProceedingsDto item : shlcMeetingProceedingsDto){
            ShlcMeetingProceedingsEntity data = new ShlcMeetingProceedingsEntity();
            data.setShlcMeetingId(shlcMeeting.getId());
            data.setMomendrum(item.getMemorandumForDiscussion());
            data.setDetailsDiscussion(item.getDetailsForDiscussion());
            data.setCommitteeProceedings(item.getCommitteeProceedings());
            data.setIsActive(true);
            data.setCreatedBy(shlcMeeting.getCreatedBy());
            data.setUpdatedBy(shlcMeeting.getUpdatedBy());
            model.add(data);
        }
        return shlcMeetingProceedingsRepo.saveAll(model);
    }


    @Override
    public List<ContractMappingModel> saveContractMapping(List<ContractMappingDto> contractDto, Integer id,  ContractMaster contractMObj) {
        List<ContractMappingModel> contractMapping=new ArrayList<>();
        if(contractMObj.getTypeId()==1){
            List<TankInfo> tankData = masterQryRepository.getTankByWorkId(contractMObj.getWorkId());
            for(int i=0;i<tankData.size();i++){
                ContractMappingModel cmm=new ContractMappingModel();
                cmm.setContractId(contractMObj.getId());
                cmm.setTankId(tankData.get(i).getTankId());
                cmm.setTenderId(contractMObj.getTenderId());
                cmm.setTenderNoticeId(contractMObj.getWorkId());
                cmm.setActivityId(contractMObj.getActivityId());
                cmm.setCreatedBy(contractMObj.getCreatedBy());
                cmm.setTankWiseContractAmount(contractMObj.getContractAmount()/tankData.size());
                cmm.setActive(true);
                contractMapping.add(cmm);
            }
        }
        else {
            for (ContractMappingDto contractMappingDto1 : contractDto) {
//        contractMappingModel1.setContractId(id);
                ContractMappingModel contractMappingModel1 = new ContractMappingModel();
                BeanUtils.copyProperties(contractMappingDto1, contractMappingModel1);
                contractMappingModel1.setContractId(id);
                contractMappingModel1.setActive(true);
                contractMapping.add(contractMappingModel1);
            }
        }
        return contractMappingRepository.saveAll(contractMapping);
    }

    @Override
    public List<ContractMappingModel> updateContractMapping(ContractMaster contractMObj) {
        List<ContractMappingModel> mapping=masterQryRepository.getContractMappingCountByContractId(contractMObj.getId());
        Double contractAmountTankWise=contractMObj.getContractAmount()/mapping.size();
        for(int i=0;i<mapping.size();i++){
            mapping.get(i).setTankWiseContractAmount(contractAmountTankWise);
            mapping.get(i).setUpdatedBy(contractMObj.getCreatedBy());
            mapping.get(i).setActive(true);
        }
        //contractMappingRepositoryImpl.deactivateContractById(contractMObj.getId());
        return contractMappingRepository.saveAll(mapping);
    }

    @Override
    public ContractMappingModel updateContractMappingValue(Integer id,ContractMappingModel mapping) {
        ContractMappingModel contractMapping=contractMappingRepository.findContractMappingById(id);
        contractMapping.setTankWiseContractAmount(mapping.getTankWiseContractAmount());
        contractMapping.setUpdatedBy(mapping.getUpdatedBy());
         contractMappingRepository.save(contractMapping);
        return contractMapping;
    }
    @Override
    public ActivityEstimateTankMappingEntity updateEstimateMappingValue(Integer id,ActivityEstimateTankMappingEntity mapping) {
        ActivityEstimateTankMappingEntity estimateTankMapping=activityEstimateTankMappingRepository.findEstimateMappingById(id);
        estimateTankMapping.setTankWiseAmount(mapping.getTankWiseAmount());
        estimateTankMapping.setUpdatedBy(mapping.getUpdatedBy());
        estimateTankMapping.setActiveFlag(true);
        activityEstimateTankMappingRepository.save(estimateTankMapping);
        Double estimateAmount=activityQryRepository.getEstimateValueTankWiseSum(mapping.getEstimateId());
        ActivityAddEntity estimate=activityEstimatedRepository.getEstimateById(mapping.getEstimateId());
        estimate.setEstimatedAmount(estimateAmount);
        activityEstimatedRepository.save(estimate);
        return estimateTankMapping;
    }

    @Override
    public List<ContractDocumentModel> saveContractDocument(List<ContractDocumentDto>  contractDocumentModel, Integer id, MultipartFile[] files) {
//        for (MultipartFile multipart : files) {
//            contractDocumentModel.setDocName(multipart.getOriginalFilename());
//        }
//        contractDocumentModel.setDocPath("https://oiipcra.s3.ap-south-1.amazonaws.com/ContractDocument");
//        ContractDocumentModel contractDocumentModel1 = new ContractDocumentModel();
////        contractDocumentModel1.setContractId(id);
//        BeanUtils.copyProperties(contractDocumentModel, contractDocumentModel1);
//        contractDocumentModel1.setContractId(id);
//        return contractDocumentRepository.save(contractDocumentModel1);

      /*  BeanUtils.copyProperties(contractDocumentModel, contractDocument);*/
        List<ContractDocumentModel> contractDocumentModel1=new ArrayList<>();
        for (MultipartFile multipart : files) {
            ContractDocumentModel contractDocument = new ContractDocumentModel();
            contractDocument.setDocName(multipart.getOriginalFilename());
            contractDocument.setDocPath("https://oiipcra.s3.ap-south-1.amazonaws.com/ContractDocument");
            contractDocument.setContractId(id);
            contractDocument.setCreatedBy(contractDocumentModel.get(0).getCreatedBy());
            contractDocument.setActive(true);
            contractDocumentModel1.add(contractDocument);
        }
        return  contractDocumentRepository.saveAll(contractDocumentModel1);
    }

    @Override
    public List<DepartmentDto> getDepartment(Integer userId, Integer id,Boolean flag) {
        List<DepartmentDto> dept = masterQryRepository.getDepartment(userId, id,flag);
        return dept;
    }

    @Override
    public List<MenuDto> getMenu(Integer userId, Integer id) {
        List<MenuDto> menu = masterQryRepository.getMenu(userId, id);
        return menu;
    }

    @Override
    public List<UnitDto> getUnit(Integer userId, Integer id,Boolean flag) {
        List<UnitDto> unit = masterQryRepository.getUnit(userId, id,flag);
        return unit;
    }

    @Override
    public List<ContractStatusDto> getAllContractStatus() {
        List<ContractStatusDto> status = masterQryRepository.getAllContractStatus();
        return status;
    }

    @Override
    public List<ContractStatusDto> getAllContractNumber() {
        List<ContractStatusDto> contractNumber = masterQryRepository.getAllContractNumber();
        return contractNumber;
    }

    @Override
    public List<ContractStatusDto> getAllContractType() {
        List<ContractStatusDto> type = masterQryRepository.getAllContractType();
        return type;
    }

    @Override
    public List<TenderCodeResponse> getAllTenderCode(int userId) {
        Integer userLevelId = userQueryRepository.getUserLevelIdByUserId(userId);
        List<TenderCodeResponse> code = masterQryRepository.getAllTenderCode(userLevelId);
        return code;
    }


    @Override
    public Page<AgencyInfo> getAllAgencyList(AgencyListDto agencyListDto) {
        Page<AgencyInfo> agency = masterQryRepository.getAllAgencyList(agencyListDto);
        return agency;
    }

    @Override
    public List<AgencyInfo> getAllPanNo() {
        return masterQryRepository.getAllPanNo();
    }


    @Override
    public List<AgencyInfo> getAgency(Integer userId,Integer agencyId,String panNo) {
        List<AgencyInfo> agency = masterQryRepository.getAgency(userId, agencyId, panNo);
        return agency;
    }

    @Override
    public AgencyMaster updateAgency(int id, AgencyMaster agencyMaster) {
        AgencyMaster existingAgency = agencyRepository.findAgencyById(id);
        if (existingAgency == null) {
            throw new RecordNotFoundException("AgencyMaster", "id", id);
        }

        existingAgency.setName(agencyMaster.getName());
        existingAgency.setAddress(agencyMaster.getAddress());
        existingAgency.setPhone(agencyMaster.getPhone());
        existingAgency.setPanNo(agencyMaster.getPanNo());
        existingAgency.setDistId(agencyMaster.getDistId());
        existingAgency.setExemptId(agencyMaster.getExemptId());
        existingAgency.setGstinNo(agencyMaster.getGstinNo());
        existingAgency.setLicenseClassId(agencyMaster.getLicenseClassId());
        existingAgency.setLicenseValidity(agencyMaster.getLicenseValidity());
        if (agencyMaster.getImageName() == null || agencyMaster.getImageName().trim().isEmpty()) {
            existingAgency.setImageName(existingAgency.getImageName());
        } else {
            existingAgency.setImageName(agencyMaster.getImageName());
        }
        existingAgency.setImagePath(existingAgency.getImagePath());
        existingAgency.setPincode(agencyMaster.getPincode());
        existingAgency.setPost(agencyMaster.getPost());
        existingAgency.setUpdatedBy(agencyMaster.getUpdatedBy());
        return agencyRepository.save(existingAgency);
    }

    @Override
    public List<WorkTypeDto> getWorkType(Integer userId, Integer id,boolean flag) {
        List<WorkTypeDto> worktype = masterQryRepository.getWorkType(userId, id,flag);
        return worktype;
    }

    @Override
    public WorkTypeMaster updateWorkType(int id, WorkTypeMaster workTypeMaster) {
        WorkTypeMaster existingWorkType = workTypeRepository.findWorkTypeById(id);
        if (existingWorkType == null) {
            throw new RecordNotFoundException("WorkTypeMaster", "id", id);
        }
        existingWorkType.setName(workTypeMaster.getName());
        return workTypeRepository.save(existingWorkType);
    }

    @Override
    public List<RoleMenuDto> getAllMenuByRoleId(Integer userId, Integer roleId) {
        List<RoleMenuDto> roleMenu = masterQryRepository.getRoleMenu(userId, roleId);
        return roleMenu;
    }

    @Override
    public List<RoleMenuDto> getAllMenuByRoleIds(Integer userId, Integer roleId) {
        List<RoleMenuDto> roleMenu = masterQryRepository.getRoleMenus(userId, roleId);
        return roleMenu;
    }

    @Override
    public List<ParentMenuInfo> getMenuHierarchyByRole(Integer userId, Integer roleId) {
        List<RoleMenuDto> allMenuByRoleId = getAllMenuByRoleId(userId, roleId);
        List<Integer> menuIdsByRoleId = new ArrayList<>();
        for (int i = 0; i < allMenuByRoleId.size(); i++) {
            menuIdsByRoleId.add(allMenuByRoleId.get(i).getMenuId());
        }
        List<ParentMenuInfo> parentMenuList = masterQryRepository.getAllParentMenu(roleId);
        List<ParentMenuInfo> finalList = new ArrayList<>();
        Integer cnt = 0;
        for (ParentMenuInfo parentMenuInfo : parentMenuList) {
            if (menuIdsByRoleId.contains(parentMenuInfo.getValue())) {
                List<HierarchyMenuInfo> childMenuList = masterQryRepository.getHierarchyMenuListById(parentMenuInfo.getValue(), roleId);
                if (childMenuList.size() > 0) {
                    List<HierarchyMenuInfo> finalChildList = new ArrayList<>();
                    int childCnt = 0;
                    for (HierarchyMenuInfo hierarchyMenuInfo : childMenuList) {
                        if (menuIdsByRoleId.contains(hierarchyMenuInfo.getValue())) {
                            finalChildList.add(childCnt, hierarchyMenuInfo);
                        }
                    }
                    parentMenuInfo.setChildren(finalChildList);
                } else {
                    parentMenuInfo.setChildren(new ArrayList<>());
                }
                finalList.add(cnt++, parentMenuInfo);
            }
        }
        return finalList;
    }

    @Override
    public List<ParentMenuInfo> getMenuHierarchyWithoutRoleId(Integer userId) {
        List<ParentMenuInfo> parentMenuList = masterQryRepository.getAllParentMenuWithoutRoleId();
        List<ParentMenuInfo> finalList = new ArrayList<>();
        Integer cnt = 0;
        for (ParentMenuInfo parentMenuInfo : parentMenuList) {
                List<HierarchyMenuInfo> childMenuList = masterQryRepository.getHierarchyMenuListByIdWithoutRoleId(parentMenuInfo.getValue());
                if (childMenuList.size() > 0) {
                    List<HierarchyMenuInfo> finalChildList = new ArrayList<>();
                    int childCnt = 0;
                    for (HierarchyMenuInfo hierarchyMenuInfo : childMenuList) {
                            finalChildList.add(childCnt, hierarchyMenuInfo);
                    }
                    parentMenuInfo.setChildren(finalChildList);
                } else {
                    parentMenuInfo.setChildren(new ArrayList<>());
                }
                finalList.add(cnt++, parentMenuInfo);
            }
        return finalList;
    }


    @Override
    public List<ParentMenuInfo> getMenuHierarchy(Integer roleId) {
        List<ParentMenuInfo> parentMenuList = masterQryRepository.getAllParentMenu(roleId);

        for (ParentMenuInfo parentMenuInfo : parentMenuList) {
            List<HierarchyMenuInfo> menuListByParentId = masterQryRepository.getHierarchyMenuListById(parentMenuInfo.getValue(), roleId);
            if (menuListByParentId.size() > 0) {
                parentMenuInfo.setChildren(menuListByParentId);
            } else {
                parentMenuInfo.setChildren(new ArrayList<>());
            }
        }
        return parentMenuList;
    }

    @Override
    public RoleMenuMaster updateRoleMenu(int id, RoleMenuMaster roleMenuMaster) {
        RoleMenuMaster existingRoleMenu = roleMenuRepository.findRoleMenuById(id);
        if (existingRoleMenu == null) {
            throw new RecordNotFoundException("RoleMenuMaster", "id", id);
        }
        existingRoleMenu.setRoleId(roleMenuMaster.getRoleId());
        existingRoleMenu.setMenuId(roleMenuMaster.getMenuId());
        existingRoleMenu.setUpdatedBy(roleMenuMaster.getUpdatedBy());
        return roleMenuRepository.save(existingRoleMenu);
    }

    @Override
    public Boolean deactivateMenu(int roleId, int menuId, boolean isActive) {
        return masterQryRepository.deactivateMenu(roleId, menuId, isActive);
    }

    @Override
    public List<RoleDto> getRoleByUserLevelId(Integer userLevelId) {
        List<RoleDto> role = masterQryRepository.getRoleByUserLevelId(userLevelId);
        return role;
    }

    @Override
    public List<RoleDto> getRoleByRoleId(Integer id) {
        List<RoleDto> role = masterQryRepository.getRoleByRoleId(id);
        return role;
    }

    @Override
    public ContractInfo getContract(Integer id) {
        ContractInfo contract = masterQryRepository.getContract(id);
        return contract;
    }

    @Override
    public AgencyInfo getAgencyById(Integer id) {
        AgencyInfo agency = masterQryRepository.getAgencyById(id);
        return agency;
    }

    @Override
    public List<ContractMappingDto> getContractMapping(Integer id) {
        List<ContractMappingDto> contract = masterQryRepository.getContractMapping(id);
        return contract;
    }

    @Override
    public List<ContractDocumentDto> getContractDocument(Integer id) {
        List<ContractDocumentDto> contract = masterQryRepository.getContractDocument(id);
        return contract;
    }

    @Override
    public OIIPCRAResponse getApprovalStatus(int currentApprovalId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            List<ApprovalStatusInfo> approvalStatus = approvalStatusRepository.getApprovalStatus(currentApprovalId);
            if (approvalStatus != null) {
                response.setStatus(1);
                response.setMessage("Success");
                response.setData(approvalStatus);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                log.info("success");
            } else {
                response.setMessage(env.getProperty(Constant.RECORD_NOT_FOUND));
                response.setStatus(0);
                response.setData(Collections.EMPTY_LIST);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } catch (Exception e) {
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            log.info(e.getMessage());
            response.setData(Collections.EMPTY_LIST);
        }
        return response;

    }

    @Override
    public OIIPCRAResponse getProgressStatusMaster() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            List<ApprovalStatusInfo> approvalStatus = masterRepository.getProgressStatusMaster();
            if (approvalStatus != null) {
                response.setStatus(1);
                response.setMessage("Success");
                response.setData(approvalStatus);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                log.info("success");
            } else {
                response.setMessage(env.getProperty(Constant.RECORD_NOT_FOUND));
                response.setStatus(0);
                response.setData(Collections.EMPTY_LIST);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } catch (Exception e) {
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            log.info(e.getMessage());
            response.setData(Collections.EMPTY_LIST);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getDistrictByDivisionId(int divId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            List<DistrictInfo> distList = masterQryRepository.getDistrictListByDivisionId(divId);
            if (distList != null) {
                response.setStatus(1);
                response.setMessage("Success");
                response.setData(distList);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                log.info("success");
            } else {
                response.setMessage(env.getProperty(Constant.RECORD_NOT_FOUND));
                response.setStatus(0);
                response.setData(Collections.EMPTY_LIST);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            }
        } catch (Exception e) {
            response.setMessage(env.getProperty(Constant.EXCEPTION_IN_SERVER));
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            log.info(e.getMessage());
            response.setData(Collections.EMPTY_LIST);
        }
        return response;
    }

    @Override
    public Boolean activateAndDeactivateMasterDataById(int masterId, int id) {
        return masterQryRepository.activateAndDeactivateMasterDataById(masterId, id);
    }

    @Override
    public LicenseMaster saveLicense(LicenseMaster licenseMaster) {
        licenseMaster.setActive(true);
        LicenseMaster licenseMaster1 = new LicenseMaster();
        BeanUtils.copyProperties(licenseMaster, licenseMaster1);
        return licenseRepository.save(licenseMaster1);
    }

    @Override
    public LicenseMaster updateLicense(int id, LicenseMaster licenseMaster) {
        LicenseMaster existingLicenseMaster = licenseRepository.findLicenseById(id);
        if (existingLicenseMaster == null) {
            throw new RecordNotFoundException("LicenseMaster", "id", id);
        }
        existingLicenseMaster.setName(licenseMaster.getName());
        existingLicenseMaster.setTenderLimit(licenseMaster.getTenderLimit());
        existingLicenseMaster.setUpdatedBy(licenseMaster.getUpdatedBy());
        return licenseRepository.save(existingLicenseMaster);
    }

    @Override
    public List<LicenseDto> getAllLicense() {
        List<LicenseDto> license = masterQryRepository.getAllLicense();
        return license;
    }

    @Override
    public List<AgencyExemptDto> getAgencyExempt() {
        List<AgencyExemptDto> agencyExempt = masterQryRepository.getAgencyExempt();
        return agencyExempt;
    }
    @Override
    public List<ActivityStatusDto> getAllActivityStatus() {
        List<ActivityStatusDto> activityStatus = masterQryRepository.getAllActivityStatus();
        return activityStatus;
    }
    @Override
    public List<ActivityStatusDto> getAllApprovedStatus() {
        List<ActivityStatusDto> approvedStatus = masterQryRepository.getAllApprovedStatus();
        return approvedStatus;
    }
    @Override
    public List<ActivityStatusDto> getAllActivityEstimateLevel() {
        List<ActivityStatusDto> activityLevel = masterQryRepository.getAllActivityEstimateLevel();
        return activityLevel;
    }
    @Override
    public List<FinYrDto> getAllFinancialYear() {
        List<FinYrDto> year = masterRepositoryImpl.getAllFinancialYear();
        return year;
    }
    @Override
    public List<ProcurementTypeDto> getAllProcurementType() {
        List<ProcurementTypeDto> procurement = masterRepositoryImpl.getAllProcurementType();
        return procurement;
    }
    @Override
    public List<ActivityStatusDto> getAllEstimateType() {
        List<ActivityStatusDto> estimateType = masterRepositoryImpl.getAllEstimateType();
        return estimateType;
    }
    @Override
    public List<TankInfo> getAllTank(Integer gpId) {
        List<TankInfo> tank = masterRepositoryImpl.getAllTankForList(gpId);
        return tank;
    }



    @Override
    public List<MonthDto> getAllMonth() {
        List<MonthDto> month = masterQryRepository.getAllMonth();
        return month;
    }
    public Integer getDistinctActivityId(Integer contractId) {
      return  masterQryRepository.getDistinctActivityId(contractId);

    }

    @Override
    public List<DistrictBoundaryDto> getDistrictListByEstimateId(int estimateId) {
        Integer levelId = masterQryRepository.getLevelId(estimateId);
        List<DistrictBoundaryDto> districtListByEstimateId = new ArrayList<>();
        if(levelId == 2){
            districtListByEstimateId = masterQryRepository.getDistrictListByEstimateId(estimateId);
        }
        if(levelId == 3){
            List<Integer> blockIdsByEstimateId = masterQryRepository.getBlockIdsByEstimateId(estimateId);
            districtListByEstimateId = masterQryRepository.getDistrictListByBlockIds(blockIdsByEstimateId);
        }
        if(levelId == 4){
            List<Integer> tankIdsByEstimateId = masterQryRepository.getTankIdsByEstimateId(estimateId);
            districtListByEstimateId = masterQryRepository.getDistrictByTankIds(tankIdsByEstimateId);
        }
        return districtListByEstimateId;
    }

    @Override
    public List<BlockBoundaryDto> getBlockListByEstimateAndDistId(Integer estimateId, Integer distId) {
        Integer levelId = masterQryRepository.getLevelId(estimateId);
        List<BlockBoundaryDto> blockListByEstimateAnDistId = new ArrayList<>();
        if(levelId == 2){
            List<Integer> districtListByEstimateIdAndDistId = masterQryRepository.districtListByEstimateIdAndDistId(estimateId,distId);
            blockListByEstimateAnDistId = masterQryRepository.getBlockListByDistId(districtListByEstimateIdAndDistId);
        }
        if(levelId == 3){
            List<Integer> blockIdsByEstimateId = masterQryRepository.getBlockIdsByEstimateId(estimateId);
            blockListByEstimateAnDistId = masterQryRepository.getBlockListByBlockIdsAndEstimateIds(blockIdsByEstimateId,estimateId);
        }
        if(levelId == 4){
            List<Integer> tankIdsByEstimateId = masterQryRepository.getTankIdsByEstimateIdAndTankId(estimateId,distId);
            blockListByEstimateAnDistId = masterQryRepository.getBlockIdsByEstimateIdAndTankId(tankIdsByEstimateId);
        }
        return blockListByEstimateAnDistId;
    }

    @Override
    public List<TankInfo> getTankListByEstimateAndDistId(int estimateId, int distId, int blockId) {
        Integer levelId = masterQryRepository.getLevelId(estimateId);
        List<TankInfo> tankListByEstimateDistAndBlock = new ArrayList<>();
        if(levelId == 2){
            List<Integer> districtListByEstimateIdAndDistId = masterQryRepository.districtListByEstimateIdAndDistId(estimateId,distId);
            tankListByEstimateDistAndBlock = masterQryRepository.getTankIdsListByDistId(districtListByEstimateIdAndDistId);
        }
        if(levelId == 3){
            List<Integer> blockIdsByEstimateId = masterQryRepository.getBlockIdsByEstimateId(estimateId);
            tankListByEstimateDistAndBlock = masterQryRepository.getTankIdsListByBlockId(blockIdsByEstimateId);
        }
        if(levelId == 4){
            tankListByEstimateDistAndBlock = masterQryRepository.getTankIdsByEstimateIdAndDistAndBlock(estimateId, distId, blockId);
        }
        return tankListByEstimateDistAndBlock;
    }

    @Override
    public List<DistrictBoundaryDto> getDistrictByEstimateId(Integer estimateId) {
        List<DistrictBoundaryDto> districtList=masterQryRepository.getDistrictByEstimateId(estimateId);
        return districtList;
    }
    @Override
    public List<TenderDto> getTenderByEstimateId(Integer estimateId) {
        List<TenderDto> tenderList=masterQryRepository.getTenderByEstimateId(estimateId);
        return tenderList;
    }
    @Override
    public List<TankContractDto> getTankByBlockId(Integer blockId) {
        List<TankContractDto> tankList=masterQryRepository.getTankByBlockId(blockId);
        return tankList;
    }
    @Override
    public List<TankContractDto> getTankByEstimateIdForContract(Integer estimateId) {
        Integer levelId = masterQryRepository.getLevelId(estimateId);
        List<TankContractDto> tankListByEstimate = new ArrayList<>();
        if(levelId == 2){
            List<Integer> districtListByEstimateIdAndDistId = masterQryRepository.districtListByEstimateIdAndDistId(estimateId,0);
            tankListByEstimate = masterQryRepository.getTankIdsListByDistIdForContract(districtListByEstimateIdAndDistId);
        }
        if(levelId == 3){
            List<Integer> blockIdsByEstimateId = masterQryRepository.getBlockIdsByEstimateId(estimateId);
            tankListByEstimate = masterQryRepository.getTankIdsListByBlockIdForContract(blockIdsByEstimateId);
        }
        if(levelId == 9){
            tankListByEstimate = masterQryRepository.getTankIdsListByEstimateIdForContract(estimateId);
        }

        return tankListByEstimate;
    }

    @Override

    public OIIPCRAResponse panNoExistOrNot(String panNo) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Integer res=masterQryRepository.getPanExistOrNot(panNo);
        if (res==1) {
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("PanNo Already Exist");
        }
        else{
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("PanNo DoesNot Exist");
        }
        return response;
    }
    @Override
    public OIIPCRAResponse workIdentificationCodeExistOrNot(Integer tenderId,String workIdentificationCode) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Integer res=masterQryRepository.workIdentificationCodeExistOrNot(tenderId,workIdentificationCode);
        if (res>=1) {
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("WorkIdentificationCode Already Exist");
        }
        else{
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("WorkIdentificationCode DoesNot Exist");
        }
        return response;
    }
    @Override
    public OIIPCRAResponse getPanNoAndAgencyName(String panNo, Integer agencyId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        AgencyDto agency=masterQryRepository.getPanNoAndAgencyName(panNo,agencyId);
        if (agency!=null) {
            response.setData(agency);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Agency Data");
        }
        else{
            response.setData(agency);
            response.setStatus(0);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Agency Or Pan No Doesn't Exist");
        }
        return response;
    }

    @Override
    public DivisionBoundaryDto getDivisionById(Integer divisionId) {
        return masterQryRepository.getDivisionById(divisionId);
    }
    public BigDecimal expenditureAmountByComponentId(Integer componentId, Integer yearId, Integer workType) {
        List<Integer> contractIds=dashboardRepositoryImpl.getContractIds(workType,componentId);
        List<Integer> estimateIds=dashboardRepositoryImpl.getEstimateIds(workType,componentId);
        return dashboardRepositoryImpl.expenditureAmountByComponentIdForWork(yearId,contractIds,estimateIds);
    }

    public BigDecimal expenditureAmountByComponentId2(Integer componentId, Integer yearId, Integer workType) {
        List<Integer> contractIds=dashboardRepositoryImpl.getContractIds(workType,componentId);
        List<Integer> estimateIds=dashboardRepositoryImpl.getEstimateIds(workType,componentId);
        return dashboardRepositoryImpl.expenditureAmountByComponentIdForWork(yearId,contractIds,estimateIds);
    }

    public BigDecimal expenditureAmountByComponentIdForAgriculture(Integer componentId, Integer yearId, Integer workType,Integer distId) {
        List<Integer> activityIds=dashboardRepositoryImpl.getActivityIds(workType,componentId);
        List<Integer> estimateIds=dashboardRepositoryImpl.getEstimateIds(workType,componentId);
        return dashboardRepositoryImpl.expenditureAmountByComponentIdForAgriculture(componentId,yearId,activityIds,estimateIds);
    }
    public  List<DenormalizedAchievement>  saveDenormalizedAchievement(List<ApiDto> allData){
        Integer count=masterRepositoryImpl.truncatePhysicalAchievement();
        List<DenormalizedAchievement> allDenormalizedAchievements=new ArrayList<>();
   int i=0;
        for(ApiDto api:allData){

            DenormalizedAchievement denormalizedAchievement = new DenormalizedAchievement();
            BeanUtils.copyProperties(api, denormalizedAchievement);
            if(api.getNoofBeneficiaries()==null){
                denormalizedAchievement.setNoofBeneficiaries(0);
            }
     /*       if(api.getComponentName().contains("Ha") || api.getComponentName().contains("ha")){
                denormalizedAchievement.setUnitCost(8);
            }
            else if(api.getComponentName().contains("nos") ){
                denormalizedAchievement.setUnitCost(13);
            }
            else{
                denormalizedAchievement.setUnitCost(13);
            }*/
             //
            denormalizedAchievement.setAdaptDistId(masterRepositoryImpl.getDistrictId(denormalizedAchievement.getDistrictName()));
            denormalizedAchievement.setYearId(masterRepositoryImpl.getYearId(String.valueOf(denormalizedAchievement.getYear())));
            denormalizedAchievement.setDeptId(masterRepositoryImpl.getDeptId(denormalizedAchievement.getDirectorate()));
            denormalizedAchievement.setAdaptBlockId(masterRepositoryImpl.getBlockId(denormalizedAchievement.getBlockName()));
            denormalizedAchievement.setAdaptGpId(masterRepositoryImpl.getGpId(denormalizedAchievement.getGpName(),denormalizedAchievement.getBlockName(),denormalizedAchievement.getDistrictName()));
            //to set schemeId
            List<AdaptFinancialSchemeDto> adaptScheme=masterRepositoryImpl.getAllSchemeNam();
            Integer schemeId=0;
            for(int j=0;j<adaptScheme.size();j++){
                if(denormalizedAchievement.getSchemeName().contains(adaptScheme.get(j).getSchemeName())){
                    schemeId=adaptScheme.get(j).getId();
                    break;
                }
            }
            denormalizedAchievement.setSchemeId(schemeId);
            //to set componentId
            List<AdaptComponentDto> adaptComponent=masterRepositoryImpl.getAllComponent();
            Integer componentId=0;
            for(int j=0;j<adaptComponent.size();j++){
                if(denormalizedAchievement.getComponentName().replace(" ","").equals(adaptComponent.get(j).getComponentName().replace(" ",""))){
                    componentId=adaptComponent.get(j).getId();
                    break;
                }
            }
            denormalizedAchievement.setComponentId(componentId);
            //to set ActivityId
            List<AdaptFinancialSchemeDto> adaptPhysicalMaster=masterRepositoryImpl.getAllPhysicalScheme();
            Integer activityId=0;
            for(int j=0;j<adaptPhysicalMaster.size();j++){
                if(denormalizedAchievement.getMasterComponent().contains(adaptPhysicalMaster.get(j).getSchemeName())){
                    System.out.println(denormalizedAchievement.getMasterComponent());
                    activityId=adaptPhysicalMaster.get(j).getActivityId();
                    break;
                }
            }
            denormalizedAchievement.setActivityId(activityId);
            //to get unitId
            List<AdaptUnitDto> adaptUnitMaster=masterRepositoryImpl.getAllUnit();
            Integer unitId=0;
            for(int k=0;k<adaptUnitMaster.size();k++){
                if(denormalizedAchievement.getComponentName().toLowerCase().contains(adaptUnitMaster.get(k).getAdaptUnitName())){
                    unitId=adaptUnitMaster.get(k).getUnitId();
                    break;
                }
            }
            denormalizedAchievement.setUnitId(unitId);
//            if(denormalizedAchievement.getComponentName().toLowerCase().contains(" ha ") || denormalizedAchievement.getComponentName().toLowerCase().contains(" ha") || denormalizedAchievement.getComponentName().toLowerCase().contains(" ha.")){
//                denormalizedAchievement.setUnitId(8);
//            }
//            else if(denormalizedAchievement.getComponentName().toLowerCase().contains(" nos ") || denormalizedAchievement.getComponentName().toLowerCase().contains(" nos") || denormalizedAchievement.getComponentName().toLowerCase().contains(" nos.") ){
//                denormalizedAchievement.setUnitId(13);
//            }
//            else{
//                denormalizedAchievement.setUnitId(null);
//            }
            denormalizedAchievementRepository.save(denormalizedAchievement);
            System.out.println(++i);
            allDenormalizedAchievements.add(denormalizedAchievement);
        }

        return allDenormalizedAchievements;
    }
    public  List<DenormalizedFinancialAchievement>  saveDenormalizedFinancialAchievement(List<DenormalizedFinancialAchievementDto> allData){
        masterRepositoryImpl.truncateFinancialAchievement();
        masterRepositoryImpl.truncateAdaptFinancialActivityEstimateMapping();
        masterRepositoryImpl.truncateAdaptFinancialActivityEstimate();

        List<DenormalizedFinancialAchievement> allDenormalizedFinancialAchievements=new ArrayList<>();
        int k=0;
        for(DenormalizedFinancialAchievementDto data:allData){
            DenormalizedFinancialAchievement denormalizedFinancialAchievement = new DenormalizedFinancialAchievement();
            BeanUtils.copyProperties(data, denormalizedFinancialAchievement);
            //to get district
            Integer districtId=masterRepositoryImpl.getDistrictId(denormalizedFinancialAchievement.getDistrictName());
            denormalizedFinancialAchievement.setAdaptDistId(districtId);
            //to get year
            Integer yearId=masterRepositoryImpl.getYearId(denormalizedFinancialAchievement.getYear());
            denormalizedFinancialAchievement.setYearId(yearId);
            //to get deptId
            denormalizedFinancialAchievement.setDeptId(masterRepositoryImpl.getDeptId(denormalizedFinancialAchievement.getDirectorate()));
            //to get activityId
            List<AdaptFinancialSchemeDto> adaptFinancialMaster=masterRepositoryImpl.getAllFinancialScheme();
            Integer activityId=null;
            for(int i=0;i<adaptFinancialMaster.size();i++){
                if(denormalizedFinancialAchievement.getSchemeName().contains(adaptFinancialMaster.get(i).getSchemeName())){
                  activityId=adaptFinancialMaster.get(i).getActivityId();
                  System.out.println(activityId);
                  break;
                }
            }

          //  Integer activityId=masterRepositoryImpl.getActivityIdBySchemeNameForFinancial(denormalizedFinancialAchievement.getSchemeName());
            denormalizedFinancialAchievement.setActivityId(activityId);
            //to save the data in agriculture table
            AgricultureEstimate estimate=new AgricultureEstimate();
            estimate.setActivityId(activityId);
            estimate.setEstimatedAmount(denormalizedFinancialAchievement.getFinancialAllocationInApp());
            estimate.setFinyrId(yearId);
            estimate.setActiveFlag(true);
            AgricultureEstimate savedEstimate=agricultureEstimateRepository.save(estimate);
            //to save the data in agriculture estimate mapping table
              AgricultureEstimateMapping estimateMapping=new AgricultureEstimateMapping();
              estimateMapping.setEstimateId(savedEstimate.getId());
              estimateMapping.setDistId(districtId);
              estimateMapping.setActiveFlag(true);
            denormalizedFinancialAchievement.setEstimateId(savedEstimate.getId());
            AgricultureEstimateMapping savedEstimateMapping=agricultureEstimateMappingRepository.save(estimateMapping);
            denormalizedFinancialAchievementRepository.save(denormalizedFinancialAchievement);
            System.out.println(++k);
            allDenormalizedFinancialAchievements.add(denormalizedFinancialAchievement);
        }

        return allDenormalizedFinancialAchievements;
    }
    public  List<FardFinancialAchievementEntity>  saveFardFinancialAchievement(List<FardFinacialDto> allData){
        masterRepositoryImpl.truncateFardFinancialAchievement();
        /*masterRepositoryImpl.truncateAdaptFinancialActivityEstimateMapping();
        masterRepositoryImpl.truncateAdaptFinancialActivityEstimate();*/

        List<FardFinancialAchievementEntity> allFardFinancialAchievements=new ArrayList<>();
        int k=0;
        for(FardFinacialDto data:allData){
            FardFinancialAchievementEntity financialAchievement = new FardFinancialAchievementEntity();
            BeanUtils.copyProperties(data, financialAchievement);
            //to get district
            Integer districtId=masterRepositoryImpl.getFardDistrictId(financialAchievement.getDistrictName());
            financialAchievement.setAdaptDistId(districtId);
            //to get year
            Integer yearId=masterRepositoryImpl.getYearId(financialAchievement.getYear());
            financialAchievement.setYearId(yearId);
            //to get deptId
            //financialAchievement.setDeptId(masterRepositoryImpl.getDeptId(financialAchievement.getDirectorate()));
            financialAchievement.setDeptId(4);
            //to get activityId
            List<AdaptFinancialSchemeDto> adaptFinancialMaster=masterRepositoryImpl.getAllFardFinancialScheme();
            Integer activityId=0;
            for(int i=0;i<adaptFinancialMaster.size();i++){
                if(financialAchievement.getSchemeName().replace(" ","").equals(adaptFinancialMaster.get(i).getSchemeName().replace(" ",""))){
                    activityId=adaptFinancialMaster.get(i).getActivityId();
                    break;
                }

            }
            financialAchievement.setActivityId(activityId);

          /*  //to save the data in fard agriculture table
            FardEstimate estimate=new FardEstimate();
            estimate.setActivityId(activityId);
            estimate.setEstimatedAmount(financialAchievement.getFinancialAllocationInApp());
            estimate.setFinyrId(yearId);
            estimate.setActiveFlag(true);
            AgricultureEstimate savedEstimate=agricultureEstimateRepository.save(estimate);
            //to save the data in agriculture estimate mapping table
            FardEstimateMapping estimateMapping=new FardEstimateMapping();
            estimateMapping.setEstimateId(savedEstimate.getId());
            estimateMapping.setDistId(districtId);
            estimateMapping.setActiveFlag(true);
            financialAchievement.setEstimateId(savedEstimate.getId());
            AgricultureEstimateMapping savedEstimateMapping=agricultureEstimateMappingRepository.save(estimateMapping);*/
            fardFinancialAchievementRepository.save(financialAchievement);
            System.out.println( k++);
            allFardFinancialAchievements.add(financialAchievement);

        }

        return allFardFinancialAchievements;
    }
    public List<FardPhysicalAchievementEntity> saveFardPhysicalAchievement(List<FardPhysicalDto> fard){

          Integer count=masterRepositoryImpl.truncatePhysicalAchievementFard();
            List<FardPhysicalAchievementEntity> fardPhysicalAchievementList=new ArrayList<>();
            int i=0;
            for(FardPhysicalDto api:fard){

                FardPhysicalAchievementEntity fardPhysicalAchievement = new FardPhysicalAchievementEntity();
                BeanUtils.copyProperties(api, fardPhysicalAchievement);
                if(api.getNoofBeneficiaries()==null){
                    fardPhysicalAchievement.setNoofBeneficiaries(0);
                }
                /*fardPhysicalAchievement.setAchievement(Integer.valueOf(api.getAchievement()));
                fardPhysicalAchievement.setTarget(Integer.valueOf(api.getTarget()));*/
                fardPhysicalAchievement.setDistId(masterRepositoryImpl.getDistrictId(api.getDistrictName()));
                fardPhysicalAchievement.setYearId(masterRepositoryImpl.getYearId(api.getYear()));
                fardPhysicalAchievement.setDeptId(4);
                fardPhysicalAchievement.setBlockId(masterRepositoryImpl.getBlockId(api.getBlockName()));
                fardPhysicalAchievement.setGpId(masterRepositoryImpl.getGpId(api.getGpName(),api.getBlockName(),api.getDistrictName()));
                List<AdaptFinancialSchemeDto> adaptPhysicalMaster=masterRepositoryImpl.getAllFardPhysicalScheme();
                //to get activityId
                Integer activityId=0;
                Integer schemeId=0;
                for(int j=0;j<adaptPhysicalMaster.size();j++){
                    if(fardPhysicalAchievement.getSchemeName().replace(" ","").equals(adaptPhysicalMaster.get(j).getSchemeName().replace(" ",""))){
                        activityId=adaptPhysicalMaster.get(j).getActivityId();
                        schemeId=adaptPhysicalMaster.get(j).getId();
                        break;
                    }

                }
                fardPhysicalAchievement.setActivityId(activityId);
                fardPhysicalAchievement.setSchemeId(schemeId);

                fardPhysicalAchievement.setActive(true);

                fardPhysicalAchievementRepository.save(fardPhysicalAchievement);
                System.out.println(++i);
                fardPhysicalAchievementList.add(fardPhysicalAchievement);
            }

            return fardPhysicalAchievementList;
        }



    public Page<AdaptPhysicalDto> getOiipcraDenormalizedPhysicalAchievementDataList(AdaptFilterDto adaptDto) {

        Page<AdaptPhysicalDto> adaptPhysicalProgressList=  masterRepositoryImpl.getOiipcraDenormalizedPhysicalAchievementDataList(adaptDto);
        return adaptPhysicalProgressList;
    }

    public Page<AdaptFinancialDto> getOiipcraDenormalizedFinancialAchievementDataList(AdaptFilterDto adaptDto) {

        Page<AdaptFinancialDto> adaptFinancialProgressList=  masterRepositoryImpl.getOiipcraDenormalizedFinancialAchievementDataList(adaptDto);
        return adaptFinancialProgressList;
    }
    public Page<AdaptFinancialDto> getOiipcraFardFinancialAchievementList(AdaptFilterDto adaptDto) {

        Page<AdaptFinancialDto> adaptFinancialProgressList=  masterRepositoryImpl.getOiipcraFardFinancialAchievementList(adaptDto);
        return adaptFinancialProgressList;
    }
    public List<AdaptFinancialDto> getOiipcraDenormalizedFinancialAchievementAbstractData(AdaptFilterDto adaptDto) {

        List<AdaptFinancialDto> adaptFinancialProgressList=  masterRepositoryImpl.getOiipcraDenormalizedFinancialAchievementAbstractData(adaptDto);
        return adaptFinancialProgressList;
    }
    public List<AdaptFinancialDto> getFinancialAchievementGraphDataForHorticulture() {

        List<AdaptFinancialDto> horticulture=  masterRepositoryImpl.getFinancialAchievementGraphDataForHorticulture();
        return horticulture;
    }
    public List<AdaptFinancialDto> getFinancialAchievementGraphDataFisheries() {

        List<AdaptFinancialDto> financial=  masterRepositoryImpl.getFinancialAchievementGraphDataFisheries();
        return financial;
    }
    public List<AdaptFinancialDto> getFinancialAchievementGraphDataForAgriculture() {

        List<AdaptFinancialDto> horticulture=  masterRepositoryImpl.getFinancialAchievementGraphDataForAgriculture();
        return horticulture;
    }
    public List<AdaptPhysicalDto> getPhysicalAchievementGraphDataForHorticulture() {

        List<AdaptPhysicalDto> horticulture=  masterRepositoryImpl.getPhysicalAchievementGraphDataForHorticulture();
        return horticulture;
    }
    public List<AdaptPhysicalDto> getPhysicalAchievementGraphDataForAgriculture() {

        List<AdaptPhysicalDto> horticulture=  masterRepositoryImpl.getPhysicalAchievementGraphDataForAgriculture();
        return horticulture;
    }


    public Boolean updateDenormalizedFinancialAchievement(Integer id, Integer activityId) {
        return masterRepositoryImpl.updateDenormalizedFinancialAchievement(id,activityId);
    }

    public boolean updateAgricultureEstimate(Integer estimateId, Integer activityId) {
        return masterQryRepository.updateAgricultureEstimate(estimateId,activityId);
    }

    public Integer noOfBeneficiariesByComponentId(Integer componentId) {
        return masterQryRepository.noOfBeneficiariesByComponentId(componentId);

    }

    public boolean updateDenormalizedAchievement(Integer id, Integer activityId) {
        return masterQryRepository.updateDenormalizedAchievement(id,activityId);
    }
    public List<PhysicalProgressUpdateDto> savePhysicalProgressForWork(List<PhysicalProgressUpdateDto> physicalData,Integer contractId) {
        List<PhysicalProgressUpdateDto> savedData=new ArrayList<>();
        List<PhysicalProgressPlanned> planned=new ArrayList<>();
        List<PhysicalProgressExecuted> executed=new ArrayList<>();
       // Integer projectId=masterQryRepository.getTankDataByid(physicalData.get(0).getTankId());
        for(PhysicalProgressUpdateDto physical:physicalData){
            physical.setContractId(contractId);
            PhysicalProgressPlanned planned1=new PhysicalProgressPlanned();
            PhysicalProgressExecuted executed1=new PhysicalProgressExecuted();
            //adding all data to return which are saved
            savedData.add(physical);
            //forPhysicalWrkProgressPlaned DataSet
            planned1.setContractId(contractId);
            planned1.setTankMId(physical.getTankId());
            planned1.setTankId(physical.getProjectId());
            planned1.setNoOfCdStructuresToBeRepared(physical.getNoOfCdStructuresToBeRepared());
            planned1.setTotalLengthOfCad(physical.getTotalLengthOfCad());
            planned1.setTotalLengthOfCanalAsPerEstimate(physical.getTotalLengthOfCanalAsPerEstimate());
            planned1.setIsActive(true);
            PhysicalProgressPlanned pd=physicalProgressPlannedRepository.save(planned1);
            //adding saved data to the list for save
            planned.add(planned1);
            //forPhysicalWrkProgressPlaned DataSet
            executed1.setContractId(contractId);
            executed1.setTankMId(physical.getTankId());
            executed1.setTankId(physical.getProjectId());
            executed1.setPlannedId(pd.getId());
            executed1.setLengthOfCanalImproved(physical.getLengthOfCanalImproved());
            executed1.setNoOfOutletConstructed(physical.getNoOfOutletConstructed());
            executed1.setTotalLengthOfCad(physical.getTotalLengthOfCadExecuted());
            executed1.setNoOfCdStructuresRepared(physical.getNoOfCdStructuresRepared());
            executed1.setIsActive(true);
            //adding executed data to the list for save
            executed.add(executed1);
        }

        physicalProgressExecutedRepository.saveAll(executed);

        return savedData;
    }

    public List<PhysicalProgressConsultancy> savePhysicalProgressForConsultancy(List<PhysicalProgressConsultancy> physicalProgressConsultancy,Integer contractId) {
        List<PhysicalProgressConsultancy> saveData=new ArrayList<>();
        for(PhysicalProgressConsultancy p:physicalProgressConsultancy){
            p.setContractId(contractId);
            p.setIsActive(true);
            saveData.add(p);
        }
        List<PhysicalProgressConsultancy> saved=physicalProgressConsultancyRepository.saveAll(saveData);
        return saved;
    }
    public List<PhysicalProgressConsultancy> updatePhysicalProgressByContractId(List<PhysicalProgressConsultancy> physicalProgressConsultancy,Integer contractId) {
        List<PhysicalProgressConsultancy> updateData=new ArrayList<>();
        for(PhysicalProgressConsultancy p:physicalProgressConsultancy){
            p.setContractId(contractId);
            p.setIsActive(true);
            updateData.add(p);
        }
        List<PhysicalProgressConsultancy> updated=physicalProgressConsultancyRepository.saveAll(updateData);
        return updated;
    }
    public List<PhysicalProgressConsultancyDto> getPhysicalProgressDetailsForConsultancyById(Integer contractId) {
        List<PhysicalProgressConsultancyDto> data=masterQryRepository.getPhysicalProgressDetailsForConsultancyById(contractId);

        return data;
    }
    public List<UnitDto> getPhysicalProgressUnit() {
        List<UnitDto> data=masterQryRepository.getPhysicalProgressUnit();

        return data;
    }
    public List<UnitDto> getPhysicalProgressStatus() {
        List<UnitDto> data=masterQryRepository.getPhysicalProgressStatus();

        return data;
    }
    public List<ContractMappingDto> getTankByContractId(Integer contractId) {
        List<ContractMappingDto> data=masterQryRepository.getTankByContractId(contractId);

        return data;
    }
    public List<TankInfo> getTankByWorkId(Integer workId) {
        List<TankInfo> data=masterQryRepository.getTankByWorkId(workId);

        return data;
    }

    public List<ComponentDto> expenditureAmountDistWise(Integer componentId,Integer yearId) {
        List<Integer> contractIds=dashboardRepositoryImpl.getContractIdsGoodWise(componentId);
        List<Integer> estimateIds=dashboardRepositoryImpl.getEstimateIdsGoodWise(componentId);
        return dashboardRepositoryImpl.getGoodWisExpenditureAmount(componentId,contractIds,estimateIds,yearId);
    }

    public List<ComponentDto> getWorkWiseExpenditureDistWise(Integer componentId,Integer yearId) {
        List<Integer> contractIds=dashboardRepositoryImpl.getContractIdsWorkWise(componentId);
        List<Integer> estimateIds=dashboardRepositoryImpl.getEstimateIdsWorkWise(componentId);
        return dashboardRepositoryImpl.getWorkWisExpenditureAmount(componentId,contractIds,estimateIds,yearId);
    }

    public List<ComponentDto> getConsultancyWiseExpDistWise(Integer componentId,Integer yearId) {
        List<Integer> contractIds=dashboardRepositoryImpl.getContractIdsConWise(componentId);
        List<Integer> estimateIds=dashboardRepositoryImpl.getEstimateIdsConWise(componentId);
        return dashboardRepositoryImpl.getConWisExpenditureAmount(componentId,contractIds,estimateIds,yearId);
    }
    public  Object saveProcurement(ContractDto contract,ContractMaster contractMObj){
        if(contractMObj.getProcurementTypeId()!=null && contractMObj.getProcurementTypeId()>0) {
            if (contractMObj.getProcurementTypeId() == 2) {
                if(contract.getDirData()!=null) {
                    contract.getDirData().setContractId(contractMObj.getId());
                    contract.getDirData().setActivityId(contractMObj.getActivityId());
                    return dirRepository.save(contract.getDirData());
                }
                else{
                    return null;
                }
            }
            if (contractMObj.getProcurementTypeId() == 3) {
                if(contract.getQcbsData()!=null) {
                    contract.getQcbsData().setContractId(contractMObj.getId());
                    contract.getQcbsData().setActivityId(contractMObj.getActivityId());
                    return qcbsRepository.save(contract.getQcbsData());
                }
                else{
                    return null;
                }
            }
            if (contractMObj.getProcurementTypeId() == 4) {
                if(contract.getCdsData()!=null) {
                    contract.getCdsData().setContractId(contractMObj.getId());
                    contract.getCdsData().setActivityId(contractMObj.getActivityId());
                    return cdsRepository.save(contract.getCdsData());
                }
                else{
                    return null;
                }
            }
            if (contractMObj.getProcurementTypeId() == 5) {
                if(contract.getRfqData()!=null) {
                    contract.getRfqData().setContractId(contractMObj.getId());
                    contract.getRfqData().setActivityId(contractMObj.getActivityId());
                    return rfqRepository.save(contract.getRfqData());
                }
                else{
                    return null;
                }
            }
        }
            return null;

    }

    public List<AdaptPhysicalDto> downloadExcelForPhysicalProgress(AdaptFilterDto adaptDto) {
       return masterRepositoryImpl.downloadExcelForPhysicalProgress(adaptDto);
    }
    @Override
    public List<ActivityInformationDto> getAllActivityByDepartment(Integer id) {
        List<ActivityInformationDto> activityList = masterQryRepository.getAllActivityByDepartment(id);
        return activityList;
    }

    public List<AdaptFinancialDto> downloadFisheriesReport(AdaptFilterDto adaptDto) {
        return masterRepositoryImpl.downloadFisheriesReport(adaptDto);

    }

    public List<AdaptFinancialDto> getOiipcraFisheriesAbstractData(AdaptFilterDto adaptDto) {
        return masterRepositoryImpl.getOiipcraFisheriesAbstractData(adaptDto);
    }

    public List<AdaptFinancialDto> downloadForFisheriesExcelReport(AdaptFilterDto adaptDto) {
        return masterRepositoryImpl.downloadForFisheriesExcelReport(adaptDto);
    }

    public List<AdaptPhysicalDto> activityWisePhysicalProgressExcelReport(AdaptFilterDto adaptDto) {
        return masterRepositoryImpl.activityWisePhysicalProgressExcelReport(adaptDto);
    }

    public List<AdaptFinancialDto> getOiipcraActivityWiseFinancialAchievementDataList(AdaptFilterDto adaptDto) {
        List<AdaptFinancialDto> activityWiseFinancialProgressList=  masterRepositoryImpl.getActivityWiseFinancialAchievementDataList(adaptDto);
        return activityWiseFinancialProgressList;
    }

    public List<AdaptPhysicalBeneficiary> savePhysicalBeneficiaryData(List<PhysicalBeneficiaryDto> allData) {
        Integer count=masterRepositoryImpl.truncateAdaptPhysicalBenificiaryData();
        List<AdaptPhysicalBeneficiary>physicalBeneficiaries = new ArrayList<>();
        int k=0;

        for(PhysicalBeneficiaryDto data:allData){
            AdaptPhysicalBeneficiary physicalBeneficiary = new AdaptPhysicalBeneficiary();
            BeanUtils.copyProperties(data, physicalBeneficiary);

            physicalBeneficiary.setYearId(masterRepositoryImpl.getYearId(String.valueOf(data.getYear())));
            physicalBeneficiary.setDeptId(masterRepositoryImpl.getDeptId(String.valueOf(data.getDirectorate())));
            physicalBeneficiary.setDistrictId(masterRepositoryImpl.getDistrictId(physicalBeneficiary.getDistrictName()));
            physicalBeneficiary.setBlockId(masterRepositoryImpl.getBlockId(physicalBeneficiary.getBlockName()));
            physicalBeneficiary.setGpId(masterRepositoryImpl.getGpId(physicalBeneficiary.getGpName(),physicalBeneficiary.getBlockName(),physicalBeneficiary.getDistrictName()));
            //to save schemeId
            List<AdaptFinancialSchemeDto> adaptPhysicalMaster=masterRepositoryImpl.getAllSchemeNam();
            Integer schemeId=0;
            for(int j=0;j<adaptPhysicalMaster.size();j++){
                if(physicalBeneficiary.getSchemeName().contains(adaptPhysicalMaster.get(j).getSchemeName())){
                    schemeId=adaptPhysicalMaster.get(j).getId();

                    break;
                }
            }
            physicalBeneficiary.setSchemeId(schemeId);
            //to save componentId
            List<AdaptComponentDto> adaptComponent=masterRepositoryImpl.getAllComponent();
            Integer componentId=0;
            for(int j=0;j<adaptComponent.size();j++){
                if(physicalBeneficiary.getComponentName().replace(" ","").equals(adaptComponent.get(j).getComponentName().replace(" ",""))){
                    componentId=adaptComponent.get(j).getId();
                    break;
                }
            }
            physicalBeneficiary.setComponentId(componentId);
            physicalBeneficiary.setIsActive(true);
          //  physicalBeneficiary.setSchemeId(masterRepositoryImpl.getSchemeId(data.getSchemeName()));
            adaptPhysicalBeneficiaryRepository.save(physicalBeneficiary);
            System.out.println(++k);
            physicalBeneficiaries.add(physicalBeneficiary);

        }

        return physicalBeneficiaries;
    }

    public Page<FardPhysicalAchievementDto> getOiipcraFardPhysicalAchievementList(AdaptFilterDto adaptDto) {

        Page<FardPhysicalAchievementDto> adaptPhysicalAchievementList=  masterRepositoryImpl.getOiipcraFardPhysicalAchievementList(adaptDto);
        return adaptPhysicalAchievementList;
    }

    public List<FardPhysicalAchievementDto> activityWiseFardPhysicalAchievementExcelReport(AdaptFilterDto adaptDto) {
        return masterRepositoryImpl.activityWiseFardPhysicalAchievementExcelReport(adaptDto);
    }

    public Page<AdaptPhysicalBeneficiaryDto> getAdaptPhysicalBeneficiaryList(AdaptPhysicalBeneficiaryFilterDto adaptPhysicalBeneficiaryFilterDto) {
        Page<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDtoPageList=  masterRepositoryImpl.getAdaptPhysicalBeneficiaryList(adaptPhysicalBeneficiaryFilterDto);
        return adaptPhysicalBeneficiaryDtoPageList;
    }
    public  AdaptPhysicalBeneficiaryDto getAdaptPhysicalBeneficiaryById(Integer id){
       AdaptPhysicalBeneficiaryDto adaptPhysicalBeneficiary=  masterRepositoryImpl.getAdaptPhysicalBeneficiaryById(id);

       if(adaptPhysicalBeneficiary!=null){
           String[] latLong=adaptPhysicalBeneficiary.getLatLong().replace("lat:","").replace("long:","").replace(" ","").split(",",2);
           for (int i=0; i<latLong.length; i++) {
               if(i==0){
                  adaptPhysicalBeneficiary.setLatitude(Double.valueOf(latLong[i]));
                }
               else{
                   adaptPhysicalBeneficiary.setLongitude(Double.valueOf(latLong[i]));
               }
           }
       }

        return adaptPhysicalBeneficiary;
    }


    public List<AdaptPhysicalDto> getOiipcraPhysicalAchievementReport(AdaptFilterDto adaptDto) {
        List<AdaptPhysicalDto> adaptPhysicalDtoList=  masterRepositoryImpl.getPhysicalAchievementDataList(adaptDto);
        return adaptPhysicalDtoList;
    }


    public List<AdaptPhysicalBeneficiaryDto> getDenormalizedAchievementDataById(Integer id) {
        AdaptPhysicalDto dto = masterRepositoryImpl.getDenormalizedAchievementDataById(id);
        AdaptPhysicalDto adaptPhysicalDto = new AdaptPhysicalDto();
        AdaptPhysicalBeneficiaryFilterDto adaptPhysicalBeneficiaryFilterDto = new AdaptPhysicalBeneficiaryFilterDto();
        adaptPhysicalBeneficiaryFilterDto.setYearId(dto.getYearId());
        adaptPhysicalBeneficiaryFilterDto.setDeptId(dto.getDeptId());
        adaptPhysicalBeneficiaryFilterDto.setDistrictId(dto.getDistrictId());
        adaptPhysicalBeneficiaryFilterDto.setBlockId(dto.getBlockId());
        adaptPhysicalBeneficiaryFilterDto.setGpId(dto.getGpId());
        adaptPhysicalBeneficiaryFilterDto.setSchemeName(dto.getSchemeName());
        adaptPhysicalBeneficiaryFilterDto.setSchemeId(dto.getSchemeId());
        adaptPhysicalBeneficiaryFilterDto.setComponentName(dto.getComponentName());
        adaptPhysicalBeneficiaryFilterDto.setComponentId(dto.getComponentId());
        adaptPhysicalBeneficiaryFilterDto.setSize(10000);
        adaptPhysicalBeneficiaryFilterDto.setPage(0);
        adaptPhysicalBeneficiaryFilterDto.setSortOrder("DESC");
        adaptPhysicalBeneficiaryFilterDto.setSortBy("year_id");

        Page<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDtoPageList=  masterRepositoryImpl.getAdaptPhysicalBeneficiaryList(adaptPhysicalBeneficiaryFilterDto);
        List<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDatas =adaptPhysicalBeneficiaryDtoPageList.getContent();
        for(int i=0;i<adaptPhysicalBeneficiaryDatas.size();i++){
            String[] latLong=adaptPhysicalBeneficiaryDatas.get(i).getLatLong().replace("lat:","").replace("long:","").replace(" ","").split(",",2);
            for (int j=0; j<latLong.length; j++) {
                if(j==0){
                    adaptPhysicalBeneficiaryDatas.get(i).setLatitude(Double.valueOf(latLong[j]));
                }
                else{
                    adaptPhysicalBeneficiaryDatas.get(i).setLongitude(Double.valueOf(latLong[j]));
                }
            }
        }

        return adaptPhysicalBeneficiaryDatas;
    }


    public List<FardPhysicalAchievementDto> getFardPhysicalAchievementGraphData() {
        List<FardPhysicalAchievementDto> fisheries=  masterRepositoryImpl.getFardPhysicalAchievementGraphData();
        return fisheries;
    }
    @Override
    public List<AgencyDto> getAgencyByContractType(Integer typeId) {
        List<AgencyDto> agencyList = masterQryRepository.getAgencyByContractType(typeId);
        return agencyList;


    }
}