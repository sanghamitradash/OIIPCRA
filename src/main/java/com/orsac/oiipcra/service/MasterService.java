package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MasterService {

    CompletableFuture<OIIPCRAResponse> masterSync(int userId, String lastUpdateDate);

    List<DistrictBoundaryDto> getAllDistrict(int userId);
    List<CircleDto> getCircleList(int userId);

    List<DistrictBoundaryDto> getAllDistrictGeoJson(int userId);

    List<DistrictBoundaryDto> getAllDistrictGeoJsonByDistId(Integer districtId);

    List<BlockBoundaryDto> getBlocksByDistId(int userId, int dist_id);

    List<BlockBoundaryDto> getBlocksByDistIdGeoJson(int userId, int dist_id);

    List<BlockBoundaryDto> getBlocksGeoJsonByBlockId(Integer blockId);

    List<GpBoundaryDto> getGpByBlockId(int userId, int blockId);

    List<GpBoundaryDto> getGpByBlockIdGeoJson(int userId, int blockId);

    List<GpBoundaryDto> getGpGeoJsonByGpId(Integer gpId);

    List<VillageBoundaryDto> getVillageByGpId(int userId, int gpId);

    List<VillageBoundaryDto> getVillageByGpIdGeoJson(int userId, int gpId);

    List<VillageBoundaryDto> getVillageGeoJsonByGpId(Integer villageId);

    List<DivisionBoundaryDto> getDivisionByDistId(int userId, int distId);

    List<DivisionBoundaryDto> getDivisionByDistIdGeoJson(int userId, int distId);

    List<SubDivisionBoundaryDto> getSubDivisionByDivisionId(int userId, int divisionId);

    List<SubDivisionBoundaryDto> getSubDivisionByDivisionIdGeoJson(int userId, int divisionId);

    List<SectionBoundaryDto> getSectionBySubDivisionId(int userId, int subDivisionId);

    List<SectionBoundaryDto> getSectionBySubDivisionIdGeoJson(int userId, int subDivisionId);

    OIIPCRAResponse getDistrictByDivisionId(int divId);

    Boolean activateAndDeactivateMasterDataById(int masterId, int id);


    //Role CRUD
    Role saveRole(Role role);

    List<RoleDto> getRoleByUserId(int userId);

    List<RoleDto> getRoleByUserLevelId(Integer userLevelId);

    List<RoleDto> getRoleByRoleId(Integer id);

    Role updateRole(int id, Role role);

    //Department CRUD
    DepartmentMaster saveDept(DepartmentMaster department);

    List<DepartmentDto> getDepartment(Integer userId, Integer id, Boolean flag);

    DepartmentMaster updateDept(int id, DepartmentMaster departmentMaster);

    List<DepartmentMaster> getAllDepartment();
    List<ActivityInformationDto> getAllActivityByDepartment(Integer id);
    List<DepartmentMaster> getAllAdaptDepartment();

    //Sub Department Master CRUD
    SubDepartmentMaster saveSubDepartment(SubDepartmentMaster subDepartmentMaster);

    List<SubDepartmentDto> getSubDepartmentByDeptId(int deptId);

    List<SubDepartmentDto> getSubDepartmentById(int id);

    SubDepartmentMaster updateSubDept(int id, SubDepartmentMaster subDepartmentMaster);

    //User Level CRUD
    UserLevel saveUserLevel(UserLevel userLevel);

    List<UserLevel> getAllUserLevel(int userId);

    List<UserLevel> getUserLevelById(int id);

    UserLevel updateUserLevel(int id, UserLevel userLevel);

    //Menu Master CRUD
    MenuMaster saveMenu(MenuMaster menuMaster);

    List<MenuDto> getMenu(Integer userId, Integer id);

    OIIPCRAResponse getApprovalStatus(int currentApprovalId);


    //List<MenuDto> getMenu(Integer userId);
    MenuMaster updateMenu(int id, MenuMaster menuMaster);

    //Unit Master CRUD
    UnitMaster saveUnit(UnitMaster unitMaster);

    List<UnitDto> getUnit(Integer userId, Integer id, Boolean flag);

    List<ContractStatusDto> getAllContractStatus();

    List<ContractStatusDto> getAllContractNumber();

    List<ContractStatusDto> getAllContractType();

    List<TenderCodeResponse> getAllTenderCode(int userId);

    UnitMaster updateUnit(int id, UnitMaster unitMaster);

    //Agency Master CRUD
    AgencyMaster saveAgency(AgencyDto agencyDto) throws ParseException;

    List<AgencyInfo> getAgency(Integer userId,Integer agencyId,String panNo);

  //  Page<AgencyInfo> getAgency(Integer userId, Integer agencyId, String panNo, Integer disId, Integer licenseClassId, Integer page, Integer size, String sortOrder, String sortBy);

    AgencyMaster updateAgency(int id, AgencyMaster agencyMaster);

    //Work Type Master CRUD
    WorkTypeMaster saveWorkType(WorkTypeMaster workTypeMaster);

    List<WorkTypeDto> getWorkType(Integer userId, Integer id, boolean flag);

    WorkTypeMaster updateWorkType(int id, WorkTypeMaster workTypeMaster);


    //Designation Master CRUD
    DesignationMaster saveDesignation(DesignationMaster designationMaster);

    List<DesignationDto> getDesignationByUserLevelId(int userLevelId, int deptId);
    //Designation

    List<DesignationDto> getAllDesignationByUserLevelId(int userLevelId, int deptId);

    List<DesignationDto> getDesignationInfoById(int id, boolean flag);

    DesignationMaster updateDesignation(int id, DesignationMaster designationMaster);

    //  Role Menu Master CRUD
    List<RoleMenuMaster> saveRoleMenu(RoleMenuInfo roleMenuInfo);

    RoleMenuMaster updateRoleMenu(RoleMenuInfo roleMenuInfo, Integer menuId);
    List<RoleMenuDto> getAllMenuByRoleId(Integer userId, Integer id);
    List<RoleMenuDto> getAllMenuByRoleIds(Integer userId,Integer roleId);
    List<ParentMenuInfo> getMenuHierarchyByRole(Integer userId, Integer roleId);

    List<ParentMenuInfo> getMenuHierarchyWithoutRoleId(Integer userId);
    List<ParentMenuInfo> getMenuHierarchy(Integer roleId);

    RoleMenuMaster updateRoleMenu(int id, RoleMenuMaster roleMenuMaster);

    Boolean deactivateMenu(int roleId, int menuId, boolean isActive);

    //Contract CRUD
    ContractMaster saveContract(ContractDto contractMaster);

    List<ContractMappingModel> saveContractMapping(List<ContractMappingDto> contractMappingDto, Integer id,  ContractMaster contractMObj);
    List<ContractMappingModel> updateContractMapping ( ContractMaster contractMObj);
    ContractMappingModel updateContractMappingValue(Integer id,ContractMappingModel mapping);
    ActivityEstimateTankMappingEntity updateEstimateMappingValue(Integer id,ActivityEstimateTankMappingEntity mapping);

    List<ContractDocumentModel> saveContractDocument(List<ContractDocumentDto> contractDocumentModel, Integer id, MultipartFile[] files);

    ContractInfo getContract(Integer id);

    AgencyInfo getAgencyById(Integer id);

    List<ContractMappingDto> getContractMapping(Integer id);

    OIIPCRAResponse getProgressStatusMaster();

    List<ContractDocumentDto> getContractDocument(Integer id);

    ContractMaster updateContractById(Integer contractId, ContractDto contractMaster);

    Boolean deactivateContractMapping(Integer id);

    Boolean deactivateContractDocument(Integer id);

    List<TankInfo> getTankInfoJson(Integer distId, Integer blockId, Integer gpId, Integer villageId, Integer divisionId, Integer subDivisionId, Integer sectionId);

    //License Curd

    LicenseMaster saveLicense(LicenseMaster licenseMaster);

    List<LicenseDto> getAllLicense();

    LicenseMaster updateLicense(int id, LicenseMaster licenseMaster);

    List<AgencyExemptDto> getAgencyExempt();

    List<ActivityStatusDto> getAllActivityStatus();

    List<ActivityStatusDto> getAllApprovedStatus();

    List<ActivityStatusDto> getAllActivityEstimateLevel();

    List<FinYrDto> getAllFinancialYear();

    List<ProcurementTypeDto> getAllProcurementType();
    List<ActivityStatusDto> getAllEstimateType();

    List<TankInfo> getAllTank(Integer gpId);



    List<MonthDto> getAllMonth();

    List<DistrictBoundaryDto> getDistrictListByEstimateId(int estimateId);

    List<BlockBoundaryDto> getBlockListByEstimateAndDistId(Integer estimateId, Integer distId);

    List<TankInfo> getTankListByEstimateAndDistId(int estimateId, int distId, int blockId);
    List<DistrictBoundaryDto> getDistrictByEstimateId(Integer estimateId);
    List<TenderDto> getTenderByEstimateId(Integer estimateId);
    List<TankContractDto> getTankByBlockId(Integer blockId);
    List<TankContractDto> getTankByEstimateIdForContract(Integer estimateId);
    OIIPCRAResponse panNoExistOrNot(String bidId);
    OIIPCRAResponse workIdentificationCodeExistOrNot(Integer tenderId,String workIdentificationCode);


    OIIPCRAResponse getPanNoAndAgencyName(String panNo,Integer agencyId);

    DivisionBoundaryDto getDivisionById(Integer divisionId);

    Page<AgencyInfo> getAllAgencyList(AgencyListDto agencyListDto);


    List<AgencyInfo> getAllPanNo();

    ShlcMeetingEntity saveShlcMeeting(ShlcMeetingDto shlcMeetingDto);

    List<ShlcMeetingMembersEntity> saveShlcMeetingMembersEntity(List<ShlcMeetingMembersDto> shlcMeetingMembersDto,  ShlcMeetingEntity saveShlcMeeting);

    List<ShlcMeetingProceedingsEntity> saveShlcMeetingProceedings(List<ShlcMeetingProceedingsDto> shlcMeetingProceedingsDto,  ShlcMeetingEntity saveShlcMeeting);

    List<AgencyDto> getAgencyByContractType(Integer typeId);
}