package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.*;

import java.util.List;

public interface MasterRepository {
    List<DeptInfo> getDepartment(String lastUpdateDate);
    List<ProjectInfo> getProjectList(String lastUpdateDate);
    List<AreaInfo> getAreaList(String lastUpdateDate);
    List<WorkStatusInfo> getWorkStatus(String lastUpdateDate);
    List<RoleInfo> getRole(String lastUpdateDate);
    List<ApprovalStatusInfo> getApprovalStatus(String lastUpdateDate);
    List<DesignationInfo> getDesignation(String lastUpdateDate);
    List<DivisionInfo> getDivision();
    List<SubDivisionInfo> getSubDivision();
    List<SectionInfo> getSection();
    List<DistrictInfo> getDistrict();
    List<BlockInfo> getBlock();
    List<GramPanchayatInfo> getGp();
    List<VillageInfo> getVillage();
    List<SubDeptInfo> getSubDept();
    List<TankInfo> getAllTank(int userId);
    List<StatusOfTankInfo> getStatusOfTank(String lastUpdateDate);
    List<TurbidityInfo> getTurbidity(String lastUpdateDate);
    List<UsageInfo> getUsage(String lastUpdateDate);
    List<ApprovalStatusInfo> getProgressStatusMaster();
    List<BlockMappingInfo> getSectionByBlockId(int blockId);
    List<VillageInfo> getMasterSynLevelInfo(int userId);
    List<VillageInfo> getMasterSynDivisionToSection(int userId);



}
