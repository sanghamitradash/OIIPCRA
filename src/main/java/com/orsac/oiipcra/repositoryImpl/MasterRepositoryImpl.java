package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.repository.MasterRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MasterRepositoryImpl implements MasterRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    private UserQueryRepository userQryRepo;
    public int count(String qryStr, MapSqlParameterSource sqlParam) {
        String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
        Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        if (null != intRes) {
            return intRes;
        }
        return 0;
    }

    @Override
    public List<DeptInfo> getDepartment(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_DEPARTMENT = "SELECT id, name FROM oiipcra_oltp.dept_m WHERE is_active=true AND id != -1";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            GET_DEPARTMENT += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        GET_DEPARTMENT += " ORDER BY name";

        return namedJdbc.query(GET_DEPARTMENT, sqlParam, new BeanPropertyRowMapper<>(DeptInfo.class));

    }

    @Override
    public List<ProjectInfo> getProjectList(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_PROJECT_LIST = "SELECT id, name, start_date, end_date FROM oiipcra_oltp.project_m where is_active=true";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            GET_PROJECT_LIST += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        GET_PROJECT_LIST += " ORDER BY name";
        return namedJdbc.query(GET_PROJECT_LIST, sqlParam, new BeanPropertyRowMapper<>(ProjectInfo.class));
    }

    @Override
    public List<AreaInfo> getAreaList(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_AREA_LIST = "SELECT id, area_type, area_id, parent_id FROM oiipcra_oltp.area_m where is_active=true";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            GET_AREA_LIST += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        GET_AREA_LIST += " ORDER BY area_type";
        return namedJdbc.query(GET_AREA_LIST, sqlParam, new BeanPropertyRowMapper<>(AreaInfo.class));

    }

    @Override
    public List<WorkStatusInfo> getWorkStatus(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String WORK_STATUS_LIST = "SELECT id, name FROM oiipcra_oltp.master_work_status where is_active=true";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            WORK_STATUS_LIST += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        WORK_STATUS_LIST += " ORDER BY name";
        return namedJdbc.query(WORK_STATUS_LIST, sqlParam, new BeanPropertyRowMapper<>(WorkStatusInfo.class));
    }

    @Override
    public List<RoleInfo> getRole(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String WORK_ROLE_LIST = "SELECT id, name, parent_role_id, can_edit, can_view, can_add, can_delete, can_approve, is_deletion_request_access as deletionRequestAccess, is_deletion_approval_access as deletionApprovalAccess," +
                " is_addition_request_access as additionRequestAccess, is_addition_approval_access as additionApprovalAccess, is_survey_access as surveyAccess " +
                " FROM oiipcra_oltp.role_m where is_active=true";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            WORK_ROLE_LIST += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        WORK_ROLE_LIST += " ORDER BY name";
        return namedJdbc.query(WORK_ROLE_LIST, sqlParam, new BeanPropertyRowMapper<>(RoleInfo.class));
    }

    @Override
    public List<ApprovalStatusInfo> getApprovalStatus(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String APPROVAL_STATUS = "SELECT id, name FROM oiipcra_oltp.approval_status_m where is_active=true";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            APPROVAL_STATUS += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        APPROVAL_STATUS += " ORDER BY name";
        return namedJdbc.query(APPROVAL_STATUS, sqlParam, new BeanPropertyRowMapper<>(ApprovalStatusInfo.class));
    }

    @Override
    public List<DesignationInfo> getDesignation(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String APPROVAL_STATUS = "SELECT id, name, dept_id FROM oiipcra_oltp.designation_m where is_active=true";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            APPROVAL_STATUS += " AND date(updated_on) >= :lastUpdateDate ";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        APPROVAL_STATUS += " ORDER BY name";
        return namedJdbc.query(APPROVAL_STATUS, sqlParam, new BeanPropertyRowMapper<>(DesignationInfo.class));
    }

    @Override
    public List<DivisionInfo> getDivision() {
        String GET_DIVISION = "SELECT id,mi_division_name, dist_id,mi_division_id FROM oiipcra_oltp.mi_division_m where is_active=true";
        return namedJdbc.query(GET_DIVISION, new BeanPropertyRowMapper<>(DivisionInfo.class));
    }

    @Override
    public List<SubDivisionInfo> getSubDivision() {
        String GET_SUB_DIVISION = "SELECT id,mi_sub_division_name, dist_id, mi_division_id, mi_sub_division_id " +
                " FROM oiipcra_oltp.mi_subdivision_m where is_active=true";
        return namedJdbc.query(GET_SUB_DIVISION, new BeanPropertyRowMapper<>(SubDivisionInfo.class));
    }

    @Override
    public List<SectionInfo> getSection() {
        String GET_SECTION = "SELECT id, mi_section_name, dist_id, mi_division_id, mi_sub_division_id, section_id " +
                " FROM oiipcra_oltp.mi_section_m  where is_active=true";
        return namedJdbc.query(GET_SECTION, new BeanPropertyRowMapper<>(SectionInfo.class));
    }

    @Override
    public List<DistrictInfo> getDistrict() {
        String GET_DISTRICT = "SELECT dist_id,district_name FROM oiipcra_oltp.district_boundary";
        return namedJdbc.query(GET_DISTRICT, new BeanPropertyRowMapper<>(DistrictInfo.class));
    }

    @Override
    public List<BlockInfo> getBlock() {
        String GET_BLOCK = "SELECT block_name,district_name,dist_id, block_id FROM oiipcra_oltp.block_boundary";
        return namedJdbc.query(GET_BLOCK, new BeanPropertyRowMapper<>(BlockInfo.class));
    }

    @Override
    public List<GramPanchayatInfo> getGp() {
        String GET_GP = "SELECT grampanchayat_name as gpName, block_name,district_name, dist_id, block_id, gp_id " +
                " FROM oiipcra_oltp.gp_boundary";
        return namedJdbc.query(GET_GP, new BeanPropertyRowMapper<>(GramPanchayatInfo.class));
    }

    @Override
    public List<VillageInfo> getVillage() {
        String GET_VILLAGE = "SELECT  revenue_village_name AS villageName,grampanchayat_name as gpName,block_name,district_name, village_id,dist_id,block_id,gp_id " +
                " FROM oiipcra_oltp.village_boundary";
        return namedJdbc.query(GET_VILLAGE, new BeanPropertyRowMapper<>(VillageInfo.class));
    }

    @Override
    public List<SubDeptInfo> getSubDept() {
        String SUB_DEPT = "SELECT id, name, dept_id FROM oiipcra_oltp.sub_dept_m";
        return namedJdbc.query(SUB_DEPT, new BeanPropertyRowMapper<>(SubDeptInfo.class));
    }

    @Override
    public List<TankInfo> getAllTank(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(userId);
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(userId);
/*

        queryString = "SELECT id, project_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id, name_of_the_m_i_p as tankName, m_i_p_id as mipId, latitude, longitude, category, category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha, certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id, water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m, type_of_dam_weir, type_of_dam_weir_id, remarks, tank_id " +
                " FROM oiipcra_oltp.tank_m_id where is_active=true";
*/
        queryString = "SELECT distinct tank.id, project_id, tank.dept_dist_name, tank.dist_id, tank.dept_block_name, tank.block_id, tank.dept_gp_name, tank.gp_id, tank.name_of_the_m_i_p as tankName, tank.m_i_p_id, tank.latitude, tank.longitude, tank.category, tank.category_id, tank.type, tank.type_id, tank.catchment_area_sqkm," +
                " tank.designed_cca_kharif_ha, tank.designed_cca_rabi_ha as designedCcaRabiHa, tank.certified_ayacut_kharif_ha, tank.certified_ayacut_rabi_ha, tank.river_basin, tank.river_basin_id, tank.mi_division_name, tank.mi_division_id, tank.water_surface_area_ha, tank.height_of_dam_weir_in_m," +
                " tank.length_of_dam_weir_in_m, tank.type_of_dam_weir, tank.type_of_dam_weir_id, tank.remarks, tank.is_active, tank.created_by, tank.created_on, tank.updated_by, tank.updated_on, tank.revised_start_date, tank.revised_end_date, tank.no_of_benificiaries as noOfBeneficiaries, tank.geom, tank.village_id, " +
                " tank.sub_division_id as subDivisionId,bm.dept_mi_sub_division_name as subDivisionName,tank.section_id,bm.dept_mi_section_name as sectionName, tank.tank_id, tank.village_name " +
                " FROM oiipcra_oltp.tank_m_id as tank" +
                " left join oiipcra_oltp.block_mapping as bm on bm.block_id = tank.block_id where tank.is_active=true";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And tank.dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And tank.block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And tank.gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And tank.village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And tank.mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And tank.sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And tank.section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

        }
            return namedJdbc.query(queryString, sqlParam,new BeanPropertyRowMapper<>(TankInfo.class));
    }

    @Override
    public List<StatusOfTankInfo> getStatusOfTank(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_STATUS_OF_TANK = "SELECT id, name FROM status_of_tank_m WHERE is_active=true ";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            GET_STATUS_OF_TANK += " AND date(updated_on) >= :lastUpdateDate";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        GET_STATUS_OF_TANK += " ORDER BY name";

        return namedJdbc.query(GET_STATUS_OF_TANK, sqlParam, new BeanPropertyRowMapper<>(StatusOfTankInfo.class));
    }

    @Override
    public List<TurbidityInfo> getTurbidity(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_TURBIDITY = "SELECT id, name FROM turbidity_m WHERE is_active=true ";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            GET_TURBIDITY += " AND date(updated_on) >= :lastUpdateDate";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        GET_TURBIDITY += " ORDER BY name";

        return namedJdbc.query(GET_TURBIDITY, sqlParam, new BeanPropertyRowMapper<>(TurbidityInfo.class));
    }

    @Override
    public List<UsageInfo> getUsage(String lastUpdateDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_USAGES = "SELECT id, name FROM usage_m WHERE is_active=true ";

        if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
            GET_USAGES += " AND date(updated_on) >= :lastUpdateDate";
            sqlParam.addValue("lastUpdateDate", lastUpdateDate, Types.DATE);
        }
        GET_USAGES += " ORDER BY name";

        return namedJdbc.query(GET_USAGES, sqlParam, new BeanPropertyRowMapper<>(UsageInfo.class));
    }

    @Override
    public List<ApprovalStatusInfo> getProgressStatusMaster() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String APPROVAL_STATUS = "SELECT id, name FROM oiipcra_oltp.approval_status_m where is_active=true";

        APPROVAL_STATUS += " ORDER BY name";
        return namedJdbc.query(APPROVAL_STATUS, sqlParam, new BeanPropertyRowMapper<>(ApprovalStatusInfo.class));
    }

    public List<FinYrDto> getFinYrList(int finYrId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_FIN_YR = "SELECT id,start_date, end_date,name FROM oiipcra_oltp.fin_year_m where is_active=true and id=:finYrId ORDER BY id";
        sqlParam.addValue("finYrId", finYrId);
        return namedJdbc.query(GET_FIN_YR, sqlParam, new BeanPropertyRowMapper<>(FinYrDto.class));
    }
    public List<FinYrDto> getAllFinancialYear() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_FIN_YR = "SELECT id,start_date, end_date,name FROM oiipcra_oltp.fin_year_m where is_active=true   ORDER BY name, id";
        return namedJdbc.query(GET_FIN_YR, sqlParam, new BeanPropertyRowMapper<>(FinYrDto.class));
    }
    public List<ProcurementTypeDto> getAllProcurementType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_PROCUREMENT = "SELECT id, name, is_active as active, created_by " +
                " FROM oiipcra_oltp.procurement_type_m";
        return namedJdbc.query(GET_PROCUREMENT, sqlParam, new BeanPropertyRowMapper<>(ProcurementTypeDto.class));
    }
    public List<ActivityStatusDto> getAllEstimateType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_PROCUREMENT = "SELECT id, name  " +
                " FROM oiipcra_oltp.estimate_type ";
        return namedJdbc.query(GET_PROCUREMENT, sqlParam, new BeanPropertyRowMapper<>(ActivityStatusDto.class));
    }
    public List<TankInfo> getAllTankForList(Integer gpId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_ALL_TANK = "SELECT  id,name_of_the_m_i_p as tankName from oiipcra_oltp.tank_m_id WHERE gp_id=:gpId";
        return namedJdbc.query(GET_ALL_TANK, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }



    public List<BlockMappingInfo> getSectionByBlockId(int blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String BLOCK_SECTION = "SELECT id, dept_dist_name, dist_id, dept_mi_division_name, division_id, dept_mi_sub_division_name, sub_division_id, dept_mi_section_name, section_id, block_name, block_id, is_active " +
                " FROM oiipcra_oltp.block_mapping WHERE block_id=:blockId";
        sqlParam.addValue("blockId", blockId);
        return namedJdbc.query(BLOCK_SECTION, sqlParam, new BeanPropertyRowMapper<>(BlockMappingInfo.class));
    }

    /**
     * Master Synch user level Api district to gp
     */

    public List<VillageInfo> getMasterSynLevelInfo(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        List<Integer> authorityIdList = new ArrayList<>();
        Integer userLevelIdByUserId = 0;
        List<UserAreaMappingDto> userAreaMappingList = null;
        if (userId > 0) {
            userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
            userAreaMappingList = userQryRepo.getUserAuthority(userId);
        }
        List<Integer> distId =new ArrayList<>();
        List<Integer> blockId = new ArrayList<>();
        List<Integer> gpId = new ArrayList<>();
        List<Integer> villageId = new ArrayList<>();

        userAreaMappingList.forEach(area -> distId.add(area.getDistrict_id()));
        userAreaMappingList.forEach(area -> blockId.add(area.getBlock_id()));
        userAreaMappingList.forEach(area -> gpId.add(area.getGp_id()));
        userAreaMappingList.forEach(area -> villageId.add(area.getVillage_id()));


        String queryString = "SELECT  revenue_village_name AS villageName,grampanchayat_name as gpName,block_name,district_name,village_id,dist_id,block_id,gp_id " +
                " FROM oiipcra_oltp.odisha_revenue_village_boundary_2021 where in_oiipcra=1 ";

        switch (userLevelIdByUserId) {

            case 2:
                userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                queryString += " AND dist_id IN (:authorityIdList)";
                sqlParam.addValue("authorityIdList", authorityIdList);
                break;

            case 3:
                userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                queryString += "AND dist_id IN (:distId) AND block_id IN (:authorityIdList)";
                sqlParam.addValue("authorityIdList", authorityIdList);
                sqlParam.addValue("distId", distId);
                break;

            case 4:
                userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                queryString += " AND dist_id IN (:distId) AND block_id IN (:blockId) AND gp_id IN (:authorityIdList)";
                sqlParam.addValue("authorityIdList", authorityIdList);
                sqlParam.addValue("distId", distId);
                sqlParam.addValue("blockId", blockId);
                break;

            case 5:
                userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                queryString += "  AND dist_id IN (:distId) AND block_id IN (:blockId) AND gp_id IN (:gpId) AND village_id IN (:authorityIdList)";
                sqlParam.addValue("authorityIdList", authorityIdList);
                sqlParam.addValue("distId", distId);
                sqlParam.addValue("blockId", blockId);
                sqlParam.addValue("gpId", gpId);
                break;

            case 1:
            default:
                break;
        }

        return namedJdbc.query(queryString,sqlParam,new BeanPropertyRowMapper<>(VillageInfo.class));

    }


    /**
     * Master Synch user level Api division to section
     */
    @Transactional
    public List<VillageInfo> getMasterSynDivisionToSection(int userId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        List<Integer> authorityIdList = new ArrayList<>();
        Integer userLevelIdByUserId = 0;
        List<UserAreaMappingDto> userAreaMappingList = null;
        if (userId > 0) {
            userLevelIdByUserId = masterQryRepository.getUserLevelIdByUserId(userId);
            userAreaMappingList = userQryRepo.getUserAuthority(userId);
        }

        List<Integer> distId =new ArrayList<>();
        List<Integer> divisionId =new ArrayList<>();
        List<Integer> subdivisionId =new ArrayList<>();
        List<Integer> sectionId =new ArrayList<>();

        userAreaMappingList.forEach(area -> distId.add(area.getDistrict_id()));
        userAreaMappingList.forEach(area -> divisionId.add(area.getDivision_id()));
        userAreaMappingList.forEach(area -> subdivisionId.add(area.getSub_division_id()));
        userAreaMappingList.forEach(area -> sectionId.add(area.getSection_id()));

        String queryStringDiv = "SELECT id, dept_dist_name as districtName, dist_id, dept_mi_division_name as divisionName, division_id , dept_mi_sub_division_name as subDivisionName, sub_division_id, dept_mi_section_name as sectionName, section_id, block_name, block_id, is_active " +
                " FROM oiipcra_oltp.block_mapping WHERE is_active=true ";

        switch (userLevelIdByUserId) {

            case 6:
                userAreaMappingList.forEach(area->authorityIdList.add(area.getDivision_id()));
                queryStringDiv += " AND dist_id IN (:distId) AND division_id IN (:authorityIdList)";
                sqlParam.addValue("distId", distId);
                sqlParam.addValue("authorityIdList", authorityIdList);
                break;

            case 7:
                userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                queryStringDiv += " AND dist_id IN (:distId) AND division_id IN (:divisionId) AND sub_division_id IN(:authorityIdList)";
                sqlParam.addValue("distId", distId);
                sqlParam.addValue("divisionId", divisionId);
                sqlParam.addValue("authorityIdList", authorityIdList);
                break;

            case 8:
                userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                queryStringDiv += " AND dist_id IN (:distId) AND division_id IN (:divisionId) AND sub_division_id IN (:subdivisionId) AND section_id IN(:authorityIdList)";
                sqlParam.addValue("distId", distId);
                sqlParam.addValue("divisionId", divisionId);
                sqlParam.addValue("subdivisionId", subdivisionId);
                sqlParam.addValue("authorityIdList", authorityIdList);
                break;
        }
        return namedJdbc.query(queryStringDiv,sqlParam,new BeanPropertyRowMapper<>(VillageInfo.class));

    }

    public Page<AdaptPhysicalDto> getOiipcraDenormalizedPhysicalAchievementDataList(AdaptFilterDto adaptData ) throws ArrayIndexOutOfBoundsException {
        PageRequest pageable = PageRequest.of(adaptData.getPage(), adaptData.getSize(), Sort.Direction.fromString(adaptData.getSortOrder()), adaptData.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "year_id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();

        queryString += " select physicalAchievement.id,directorate,master_component,scheme_name,component_name,target,achievement,no_of_beneficiaries as noofBeneficiaries,achievement_percentage, activity_id,  " +
                "master.name as activityName, " +
                "year.name as year,district.district_name as districtName,block.block_name as blockName,gp.grampanchayat_name as gpName,year.id as yearId, physicalAchievement.unit_id, unit.name as unitName,  " +
                "physicalAchievement.adapt_dist_id as districtId,physicalAchievement.dept_id as deptId,physicalAchievement.adapt_block_id as blockId,physicalAchievement.adapt_gp_id as gpId,physicalAchievement.scheme_id,physicalAchievement.component_id  "+
                "from oiipcra_oltp.denormalized_achievement as physicalAchi evement " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.adapt_dist_id " +
                "left join oiipcra_oltp.fin_year_m as year on year.id=physicalAchievement.year_id " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=physicalAchievement.adapt_block_id " +
                "left join oiipcra_oltp.master_head_details as master on master.id = physicalAchievement.activity_id " +
                "left join oiipcra_oltp.unit_m as unit on unit.id = physicalAchievement.unit_id " +
                "left join oiipcra_oltp.gp_boundary as gp on gp.gp_id=physicalAchievement.adapt_gp_id where true and district.in_oiipcra=1 ";

        if(adaptData.getDistId()!=null && adaptData.getDistId()>0){
            queryString +=" and physicalAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptData.getDistId());
        }
        if(adaptData.getFinYear()!=null && adaptData.getFinYear()>0){
            queryString +=" and physicalAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptData.getFinYear());
        }
        if(adaptData.getDeptId()!=null && adaptData.getDeptId()>0){
            queryString +=" and physicalAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptData.getDeptId());
        }
        if(adaptData.getBlockId()!=null && adaptData.getBlockId()>0){
            queryString +=" and physicalAchievement.adapt_block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptData.getBlockId());
        }
        if(adaptData.getGpId()!=null && adaptData.getGpId()>0){
            queryString +=" and physicalAchievement.adapt_gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptData.getGpId());
        }
        if(adaptData.getActivityId()!=null && adaptData.getActivityId()>0){
            queryString +=" and physicalAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptData.getActivityId());
        }
      /*  if(adaptData.getInOiipcra()!=null && adaptData.getInOiipcra()>0){
            queryString +=" and district.in_oiipcra=1 " ;
        }*/
       // queryString += " ORDER BY physicalAchievement." + order.getProperty() + " " + order.getDirection().name();
        queryString += " ORDER BY district." + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<AdaptPhysicalDto> adaptDataList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
        return new PageImpl<>(adaptDataList, pageable, resultCount);

    }
    public AdaptPhysicalDto getOiipcraDenormalizedPhysicalAchievementData(AdaptFilterDto adaptData ) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "select  sum(achievement) as achievement,sum(no_of_beneficiaries) as noofBeneficiaries,sum(target) as target " +
                "    from oiipcra_oltp.denormalized_achievement as physicalAchievement\n" +
                " left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.adapt_dist_id \n" +
                "  left join oiipcra_oltp.fin_year_m as year on year.id=physicalAchievement.year_id \n" +
                " left join oiipcra_oltp.block_boundary as block on block.block_id=physicalAchievement.adapt_block_id \n" +
                "left join oiipcra_oltp.gp_boundary as gp on gp.gp_id=physicalAchievement.adapt_gp_id where true and district.in_oiipcra=1" ;

        if(adaptData.getDistId()!=null && adaptData.getDistId()>0){
            queryString +=" and physicalAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptData.getDistId());
        }
        if(adaptData.getFinYear()!=null && adaptData.getFinYear()>0){
            queryString +=" and physicalAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptData.getFinYear());
        }
        if(adaptData.getDeptId()!=null && adaptData.getDeptId()>0){
            queryString +=" and physicalAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptData.getDeptId());
        }
        if(adaptData.getBlockId()!=null && adaptData.getBlockId()>0){
            queryString +=" and physicalAchievement.adapt_block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptData.getBlockId());
        }
        if(adaptData.getGpId()!=null && adaptData.getGpId()>0){
            queryString +=" and physicalAchievement.adapt_gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptData.getGpId());
        }
        if(adaptData.getActivityId()!=null && adaptData.getActivityId()>0){
            queryString +=" and physicalAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptData.getActivityId());
        }
       /* if(adaptData.getInOiipcra()!=null && adaptData.getInOiipcra()>0){
            queryString +=" and district.in_oiipcra=1 " ;
        }*/
        return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));

    }
    public Page<AdaptFinancialDto> getOiipcraDenormalizedFinancialAchievementDataList(AdaptFilterDto adaptData ) throws ArrayIndexOutOfBoundsException {
        PageRequest pageable = PageRequest.of(adaptData.getPage(), adaptData.getSize(), Sort.Direction.fromString(adaptData.getSortOrder()), adaptData.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "year_id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "select financialAchievement.id,directorate,scheme_name,financial_allocation_in_app,actual_fund_allocated,\n" +
                "financialAchievement.activity_id,master.name as activityName,financialAchievement.estimate_id, \n" +
                " case when expenditure=0 then '0.00' when actual_fund_allocated=0 then '0.00' \n" +
                " else (expenditure*100)/actual_fund_allocated end as percentageAllocated,\n" +
                "expenditure,district.district_name as districtName,year.name as year,year.id as yearId, \n" +
                "financialAchievement.adapt_dist_id as districtId,financialAchievement.dept_id as deptId \n" +
                "from oiipcra_oltp.denormalized_financial_achievement as financialAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id \n" +
                "left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id\n" +
                "left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true and district.in_oiipcra=1 ";
        if(adaptData.getDistId()!=null && adaptData.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptData.getDistId());
        }
        if(adaptData.getFinYear()!=null && adaptData.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptData.getFinYear());
        }
        if(adaptData.getDeptId()!=null && adaptData.getDeptId()>0){
            queryString +=" and financialAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptData.getDeptId());
        }
        if(adaptData.getActivityId()!=null && adaptData.getActivityId()>0){
            queryString +=" and financialAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptData.getActivityId());
        }
       /* if(adaptData.getInOiipcra()!=null && adaptData.getInOiipcra()>0){
            queryString +=" and district.in_oiipcra=1 " ;
        }*/


          // queryString += " ORDER BY financialAchievement.  " + order.getProperty() + " " + order.getDirection().name();
        queryString += " ORDER BY district." + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<AdaptFinancialDto> adaptDataList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
        return new PageImpl<>(adaptDataList, pageable, resultCount);

    }
    public Page<AdaptFinancialDto> getOiipcraFardFinancialAchievementList(AdaptFilterDto adaptData ) throws ArrayIndexOutOfBoundsException {
        PageRequest pageable = PageRequest.of(adaptData.getPage(), adaptData.getSize(), Sort.Direction.fromString(adaptData.getSortOrder()), adaptData.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "year_id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "select financialAchievement.id,directorate,scheme_name,financial_allocation_in_app,actual_fund_allocated,\n" +
                "financialAchievement.activity_id,master.name as activityName,financialAchievement.estimate_id, \n" +
                " case when expenditure=0 then '0.00' when actual_fund_allocated=0 then '0.00' \n" +
                " else (expenditure*100)/actual_fund_allocated end as percentageAllocated,\n" +
                "expenditure,district.district_name as districtName,year.name as year,year.id as yearId, \n" +
                "financialAchievement.adapt_dist_id as districtId,financialAchievement.dept_id as deptId \n" +
                "from oiipcra_oltp.fard_financial_achievement as financialAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id \n" +
                "left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id\n" +
                "left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true and district.in_oiipcra=1 ";
        if(adaptData.getDistId()!=null && adaptData.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptData.getDistId());
        }
        if(adaptData.getFinYear()!=null && adaptData.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptData.getFinYear());
        }
        if(adaptData.getDeptId()!=null && adaptData.getDeptId()>0){
            queryString +=" and financialAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptData.getDeptId());
        }
        queryString += " ORDER BY district." + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<AdaptFinancialDto> adaptDataList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
        return new PageImpl<>(adaptDataList, pageable, resultCount);

    }
    public List<AdaptFinancialDto> getOiipcraDenormalizedFinancialAchievementAbstractData(AdaptFilterDto adaptData){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select directorate,round(sum(financial_allocation_in_app),2) as financialAllocationInApp,round(sum(actual_fund_allocated),2) as actualFundAllocated,\n" +
                "round(case when sum(expenditure)=0 then '0.00' when sum(actual_fund_allocated)=0 then '0.00' \n" +
                "else (sum(expenditure)*100)/sum(actual_fund_allocated) end,2) as percentageAllocated,\n" +
                " round(sum(expenditure),2) as expenditure,district.district_name as districtName\n" +
                "from oiipcra_oltp.denormalized_financial_achievement as financialAchievement\n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id \n" +
                "left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id \n" +
                "left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true \n" +
                "and district.in_oiipcra=1 ";
        if(adaptData.getDistId()!=null && adaptData.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptData.getDistId());
        }
        if(adaptData.getFinYear()!=null && adaptData.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptData.getFinYear());
        }
        if(adaptData.getDeptId()!=null && adaptData.getDeptId()>0){
            queryString +=" and financialAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptData.getDeptId());
        }

        queryString+="  group by directorate,district.district_name order by district.district_name  ";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }
    public AdaptFinancialDto getOiipcraDenormalizedFinancialAchievementData(AdaptFilterDto adaptData ){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "select sum(actual_fund_allocated) as actualFundAllocated," +
                "sum(expenditure) as expenditure " +
                "from oiipcra_oltp.denormalized_financial_achievement as financialAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id \n" +
                "left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true and district.in_oiipcra=1 ";
        if(adaptData.getDistId()!=null && adaptData.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptData.getDistId());
        }
        if(adaptData.getFinYear()!=null && adaptData.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptData.getFinYear());
        }
        if(adaptData.getDeptId()!=null && adaptData.getDeptId()>0){
            queryString +=" and financialAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptData.getDeptId());
        }
        if(adaptData.getActivityId()!=null && adaptData.getActivityId()>0){
            queryString +=" and financialAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptData.getActivityId());
        }
    /*    if(adaptData.getInOiipcra()!=null && adaptData.getInOiipcra()>0){
            queryString +=" and district.in_oiipcra=1 " ;
        }*/

        return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }
    public List<AdaptFinancialSchemeDto> getAllFinancialScheme(){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT id, scheme_name, activity_id " +
                "FROM oiipcra_oltp.adapt_financial_scheme  order by id asc ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialSchemeDto.class));
    }
    public List<AdaptFinancialSchemeDto> getAllFardFinancialScheme(){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT id, scheme_name, activity_id " +
                "FROM oiipcra_oltp.fard_scheme_master  order by id asc ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialSchemeDto.class));
    }
    public List<AdaptFinancialSchemeDto> getAllPhysicalScheme(){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT id, scheme_name, activity_id " +
                "FROM oiipcra_oltp.adapt_physical_scheme  order by id asc ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialSchemeDto.class));
    }
    public List<AdaptComponentDto> getAllComponent(){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT id, component_name FROM oiipcra_oltp.adapt_component  order by id asc ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptComponentDto.class));
    }
    public List<AdaptFinancialSchemeDto> getAllFardPhysicalScheme(){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT id, scheme_name, activity_id " +
                "FROM oiipcra_oltp.fard_scheme_master  order by id asc ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialSchemeDto.class));
    }

    public List<AdaptUnitDto> getAllUnit(){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT id, adapt_unit_name as adaptUnitName, unit_id as unitId " +
                "\tFROM oiipcra_oltp.adapt_unit_m order by id asc ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptUnitDto.class));
    }
    public WorkTypeDto getWorkTypeForTenderOrEstimate(Integer id,Integer typeId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        if (typeId == 2){
            queryString += "select id, tender_type as workTypeId from oiipcra_oltp.tender_m where id=:tenderId";
            sqlParam.addValue("tenderId",id);
    }
        else{
            queryString += "select id, work_type as workTypeId from oiipcra_oltp.activity_estimate_mapping where id=:estimateId";
            sqlParam.addValue("estimateId",id);
        }
        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(WorkTypeDto.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public CdsDto getCdsDataByContractIdAndEstimateId(Integer estimateId,Integer contractId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
            queryString += "Select * from oiipcra_oltp.cds_contract_details where  contract_id=:contractId ";
            if(estimateId!=null && estimateId>0) {
                queryString += " and estimate_id=:estimateId ";
                sqlParam.addValue("estimateId", estimateId);
            }
            sqlParam.addValue("contractId",contractId);

        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(CdsDto.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public CdsDifferenceDto getCdsDifference(Integer estimateId,Integer contractId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "select terms_of_reference_planned - terms_of_reference_actual as termsOfReferenceDifference,\n" +
                "justification_for_direct_selection_planned-justification_for_direct_selection_actual as justificationForDirectSelectionDifference,\n" +
                "invitation_identified_consultant_planned-invitation_to_identified_consultant_actual as invitationToIdentifiedConsultantDifference,\n" +
                "amendments_to_terms_of_reference_planned-amendments_to_terms_of_reference_actual as amendmentsToTermsOfReferenceDifference,\n" +
                "draft_negotiated_contract_planned-draft_negotiated_contract_actual as draftNegotiatedContractDifference,\n" +
                "notification_of_intention_of_award_planned-notification_of_intention_of_award_actual as notificationOfIntentOfAwarDifference,\n" +
                "signed_contract_planned-signed_contract_actual as signedContractDifference,\n" +
                "contract_completion_planned-contract_completion_actual as contractCompletionPlanned from oiipcra_oltp.cds_contract_details where  contract_id=:contractId ";
        if(estimateId!=null && estimateId>0) {
            queryString += " and estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", estimateId);
        }
        sqlParam.addValue("contractId",contractId);

        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(CdsDifferenceDto.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public List<QcbsDifferenceDto> getQcbsDifference(Integer estimateId,Integer contractId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " select terms_of_reference_planned-terms_of_reference_actual as termsOfReferenceDifference ,\n" +
                "expression_of_interest_planned-expression_of_interest_actual as expressionOfInterestDifference,\n" +
                "short_list_of_consultants_planned-short_list_of_consultants_actual as shortListOfConsultantsDifference,\n" +
                "request_for_proposals_as_issued_planned-request_for_proposals_as_issued_actual as requestForProposalsAsIssuedDifference,\n" +
                "amendments_to_request_for_proposals_planned-amendments_to_request_for_proposals_actual as amendmentsToRequestForProposalsDifference,\n" +
                "opening_of_technical_proposals_minutes_planned-opening_of_technical_proposals_minutes_actual as openingOfTechnicalProposalMinutesDifference,\n" +
                "evaluation_of_technical_proposals_planned-evaluation_of_technical_proposals_actual as evaluationOfTechnicalProposalsDifference,\n" +
                "opening_of_financial_proposals_minutes_planned-opening_of_financial_proposals_minutes_actual as openingOfFinancialProposalsMinutesDifference,\n" +
                "draft_negotiated_contract_planned-draft_negotiated_contract_actual as draftNegotiatedContractDifference,\n" +
                "notification_of_intention_of_award_planned-notification_of_intention_of_award_actual as notificationOfIntentionOfAwardDifference,\n" +
                "signed_contract_planned-signed_contract_actual as signedContractDifference,\n" +
                "contract_completion_planned-contract_completion_actual as contractCompletionDifference\n" +
                "from oiipcra_oltp.qcbs_contract_details ";
        queryString += " where  contract_id=:contractId ";
        if(estimateId!=null && estimateId>0) {
            queryString += " and estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", estimateId);
        }
        sqlParam.addValue("contractId",contractId);

        try {
            return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(QcbsDifferenceDto.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public QcbsDto getQcbcDataByContractIdAndEstimateId(Integer estimateId,Integer contractId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "Select * from oiipcra_oltp.qcbs_contract_details where contract_id=:contractId ";
        if(estimateId!=null && estimateId>0) {
            queryString += " and estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", estimateId);
        }

        sqlParam.addValue("contractId",contractId);

        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(QcbsDto.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public DirDto getDirDataByContractIdAndEstimateId(Integer estimateId,Integer contractId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "Select * from oiipcra_oltp.dir_contract_details where contract_id=:contractId ";
        if(estimateId!=null && estimateId>0) {
            queryString += " and estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", estimateId);
        }

        sqlParam.addValue("contractId",contractId);

        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(DirDto.class));
        }
        catch (Exception e){
            return null;
        }
    }
    public RfqDto getRfqDataByContractIdAndEstimateId(Integer estimateId,Integer contractId ) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "Select * from oiipcra_oltp.rfq_contract_details where  contract_id=:contractId ";
        if(estimateId!=null && estimateId>0) {
            queryString += " and estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", estimateId);
        }

        sqlParam.addValue("contractId",contractId);

        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(RfqDto.class));
        }
        catch (Exception e){
            return null;
        }
    }



    public Integer truncateFinancialAchievement(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.denormalized_financial_achievement  ";
        return namedJdbc.update(queryString,sqlParam);
    }
    public Integer truncateFardFinancialAchievement(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.fard_financial_achievement  ";
        return namedJdbc.update(queryString,sqlParam);
    }

    public Integer truncateAdaptFinancialActivityEstimate(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.agriculture_estimate ";
        return namedJdbc.update(queryString,sqlParam);
    }
    public Integer truncateAdaptFinancialActivityEstimateMapping() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.agriculture_estimate_mapping ";
        return namedJdbc.update(queryString, sqlParam);
    }
        public Integer truncatePhysicalAchievement(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.denormalized_achievement  ";
        return namedJdbc.update(queryString,sqlParam);
    }
    public Integer truncatePhysicalAchievementFard(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.fard_physical_achievement ";
        return namedJdbc.update(queryString,sqlParam);
    }
    public Integer truncateAdaptPhysicalBenificiaryData(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "TRUNCATE TABLE  oiipcra_oltp.adapt_physical_beneficiary  ";
        return namedJdbc.update(queryString,sqlParam);
    }
    public Integer getDistrictId(String districtName){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "Select dist_id from oiipcra_oltp.adapt_district_m where district_name LIKE :districtName ";
        sqlParam.addValue("districtName",districtName);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }
    public Integer getFardDistrictId(String districtName){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "Select dist_id from oiipcra_oltp.fard_district_m where district_name LIKE :districtName";
        sqlParam.addValue("districtName",districtName);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }
    public Integer getYearId(String year){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "Select year_id from  oiipcra_oltp.adapt_finyr_m where name like :year";
        sqlParam.addValue("year",year);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }

    public Integer getDeptId(String department){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "Select dept_id from  oiipcra_oltp.adapt_dept_m where name like :department ";
        sqlParam.addValue("department",department);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }
    public Integer getActivityIdBySchemeNameForFinancial(String schemeName){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        sqlParam.addValue("schemeName",schemeName);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }
    public Integer getActivityIdBySchemeNameForPhysical(String schemeName){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        sqlParam.addValue("schemeName",schemeName);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }

    public Integer getBlockId(String blockName){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select block_id from oiipcra_oltp.adapt_block_m where block_name like :blockName ";
        sqlParam.addValue("blockName",blockName);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    } public Integer getGpId(String gpName,String blockName,String districtName){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select gp_id from oiipcra_oltp.adapt_gp_m where gp_name like :gpName ";
        if(blockName!=null){
            queryString+="and block_name like :blockName ";
            sqlParam.addValue("blockName",blockName);
        }
        if(districtName!=null){
            queryString+="and district_name like :districtName ";
            sqlParam.addValue("districtName",districtName);
        }

        sqlParam.addValue("gpName",gpName);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }


    public Boolean updateDenormalizedFinancialAchievement(Integer id, Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.denormalized_financial_achievement  " +
                "SET activity_id=:activityId WHERE id=:id ";
        sqlParam.addValue("id",id);
        sqlParam.addValue("activityId",activityId);
        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }
    public List<AdaptFinancialDto> getFinancialAchievementGraphDataForHorticulture(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString +=  "select directorate,SUM(financial_allocation_in_app) as financialAllocationInApp,SUM(actual_fund_allocated) as actualFundAllocated,\n" +
                " (sum(expenditure)*100)/sum(actual_fund_allocated)  as percentageAllocated,\n" +
                "SUM(expenditure) as expenditure,district.district_name as districtName,\n" +
                "financialAchievement.adapt_dist_id as districtId,financialAchievement.dept_id as deptId\n" +
                "From oiipcra_oltp.denormalized_financial_achievement as financialAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id\n" +
                " where true and district.in_oiipcra=1  AND financialAchievement.dept_id=13\n" +
                " GROUP BY district.district_name,financialAchievement.adapt_dist_id,directorate,financialAchievement.dept_id order by district.district_name asc";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }
    public List<AdaptFinancialDto> getFinancialAchievementGraphDataFisheries() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString +="select directorate,SUM(financial_allocation_in_app) as financialAllocationInApp,SUM(actual_fund_allocated) as actualFundAllocated,\n" +
                " case when  sum(financial_allocation_in_app) > 0 and sum(expenditure)>0 then(sum(expenditure)*100)/sum(financial_allocation_in_app) \n" +
                " else 0 end as percentageAllocated,\n" +
                "SUM(expenditure) as expenditure,district.district_name as districtName,\n" +
                "financialAchievement.adapt_dist_id as districtId,financialAchievement.dept_id as deptId\n" +
                "From oiipcra_oltp.fard_financial_achievement as financialAchievement\n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id\n" +
                " where true and district.in_oiipcra=1  \n" +
                " GROUP BY district.district_name,financialAchievement.adapt_dist_id,\n" +
                " directorate,financialAchievement.dept_id order by district.district_name asc";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }
        public List<AdaptFinancialDto> getFinancialAchievementGraphDataForAgriculture(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "select directorate,SUM(financial_allocation_in_app) as financialAllocationInApp,SUM(actual_fund_allocated) as actualFundAllocated,\n" +
                " (sum(expenditure)*100)/sum(actual_fund_allocated)  as percentageAllocated,\n" +
                "SUM(expenditure) as expenditure,district.district_name as districtName,\n" +
                "financialAchievement.adapt_dist_id as districtId,financialAchievement.dept_id as deptId\n" +
                "From oiipcra_oltp.denormalized_financial_achievement as financialAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id\n" +
                " where true and district.in_oiipcra=1  AND financialAchievement.dept_id=3\n" +
                " GROUP BY district.district_name,financialAchievement.adapt_dist_id,directorate,financialAchievement.dept_id order by district.district_name asc";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }
    public List<AdaptPhysicalDto> getPhysicalAchievementGraphDataForHorticulture(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString +=  "select directorate,sum(target)as target,sum(achievement) as achievement,sum(no_of_beneficiaries) as noofBeneficiaries,\n" +
                "(sum(achievement)*100)/sum(target) as achievementPercentage,\n" +
                "district.district_name as districtName,physicalAchievement.adapt_dist_id as districtId,\n" +
                "physicalAchievement.dept_id as deptId\n" +
                "from oiipcra_oltp.denormalized_achievement as physicalAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.adapt_dist_id \n" +
                " where true and district.in_oiipcra=1 AND physicalAchievement.dept_id=13\n" +
                " group by directorate,district.district_name ,physicalAchievement.adapt_dist_id ,\n" +
                "physicalAchievement.dept_id order by district.district_name asc ";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
    }
    public List<AdaptPhysicalDto> getPhysicalAchievementGraphDataForAgriculture(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "select directorate,sum(target)as target,sum(achievement) as achievement,sum(no_of_beneficiaries) as noofBeneficiaries,\n" +
                "(sum(achievement)*100)/sum(target) as achievementPercentage,\n" +
                "district.district_name as districtName,physicalAchievement.adapt_dist_id as districtId,\n" +
                "physicalAchievement.dept_id as deptId\n" +
                "from oiipcra_oltp.denormalized_achievement as physicalAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.adapt_dist_id \n" +
                " where true and district.in_oiipcra=1 AND physicalAchievement.dept_id=3\n" +
                " group by directorate,district.district_name ,physicalAchievement.adapt_dist_id ,\n" +
                "physicalAchievement.dept_id order by district.district_name asc";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
    }

    public List<AdaptPhysicalDto> downloadExcelForPhysicalProgress(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString= " ";
        queryString += "select directorate,sum(target)as target,sum(achievement) as achievement,sum(no_of_beneficiaries) as noOfBeneficiaries, " +
                "(sum(achievement)*100)/sum(target) as achievementPercentage, " +
                "district.district_name as districtName  " +
                "from oiipcra_oltp.denormalized_achievement as physicalAchievement " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.adapt_dist_id " +
                "where true and district.in_oiipcra=1  " ;


        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and physicalAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and physicalAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }
        if(adaptDto.getDeptId()!=null && adaptDto.getDeptId()>0){
            queryString +=" and physicalAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptDto.getDeptId());
        }
        if(adaptDto.getBlockId()!=null && adaptDto.getBlockId()>0){
            queryString +=" and physicalAchievement.adapt_block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptDto.getBlockId());
        }
        if(adaptDto.getGpId()!=null && adaptDto.getGpId()>0){
            queryString +=" and physicalAchievement.adapt_gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptDto.getGpId());
        }

        queryString += "  GROUP by directorate,district.district_name order by district.district_name asc    ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
    }

    public Integer getDesignationIdByUserId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry= " select designation_id from oiipcra_oltp.user_m where id=:userId ";
        sqlParam.addValue("userId", userId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);

    }

    public List<AdaptFinancialDto> downloadFisheriesReport(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString= " ";
        queryString += "select financialAchievement.id,directorate,scheme_name,financial_allocation_in_app,actual_fund_allocated, " +
                "financialAchievement.activity_id,master.name as activityName,financialAchievement.estimate_id,   " +
                "case when expenditure=0 then '0.00' when actual_fund_allocated=0 then '0.00'   " +
                "else (expenditure*100)/actual_fund_allocated end as percentageAllocated,  " +
                "expenditure,district.district_name as districtName,year.name as year,year.id as yearId,  " +
                "financialAchievement.adapt_dist_id as districtId,financialAchievement.dept_id as deptId   " +
                "from oiipcra_oltp.fard_financial_achievement as financialAchievement   " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id   " +
                "left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true and district.in_oiipcra=1  ";

        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId " ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId " ;
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }

        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and financialAchievement.activity_id=:activityId " ;
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }

        queryString += "  ORDER BY district.district_name asc    ";

        return namedJdbc.query(queryString,sqlParam,new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }

    public List<AdaptFinancialDto> downloadForFisheriesExcelReport(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String  queryString = "select directorate,master.name as activityName,round(sum(financial_allocation_in_app),2) as financialAllocationInApp,round(sum(actual_fund_allocated),2) as actualFundAllocated,  " +
                "round(case when sum(expenditure)=0 then '0.00' when sum(actual_fund_allocated)=0 then '0.00'   " +
                "else (sum(expenditure)*100)/sum(actual_fund_allocated) end,2) as percentageAllocated,  " +
                "round(sum(expenditure),2) as expenditure,district.district_name as districtName  " +
                "from oiipcra_oltp.fard_financial_achievement as financialAchievement  " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id   " +
                "left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true   " +
                "and district.in_oiipcra=1  ";

        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId " ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }

        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId " ;
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }

        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and financialAchievement.activity_id=:activityId " ;
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }


        queryString += "  group by directorate,district.district_name,master.name  " +
                       "  order by district.district_name   ";

        return namedJdbc.query(queryString,sqlParam,new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }

    public List<AdaptPhysicalDto> activityWisePhysicalProgressExcelReport(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "select directorate,master.name as activityName,physicalAchievement.activity_id,sum(target)as target,sum(achievement) as achievement,sum(no_of_beneficiaries) as noOfBeneficiaries,  " +
                "(sum(achievement)*100)/sum(target) as achievementPercentage   " +
                "from oiipcra_oltp.denormalized_achievement as physicalAchievement  " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.adapt_dist_id  " +
                "left join oiipcra_oltp.master_head_details as master on master.id = physicalAchievement.activity_id  " +
                "where true and district.in_oiipcra=1  ";

        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and physicalAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and physicalAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }
        if(adaptDto.getDeptId()!=null && adaptDto.getDeptId()>0){
            queryString +=" and physicalAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptDto.getDeptId());
        }
        if(adaptDto.getBlockId()!=null && adaptDto.getBlockId()>0){
            queryString +=" and physicalAchievement.adapt_block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptDto.getBlockId());
        }
        if(adaptDto.getGpId()!=null && adaptDto.getGpId()>0){
            queryString +=" and physicalAchievement.adapt_gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptDto.getGpId());
        }

        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and physicalAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }

        queryString += "  GROUP by directorate,master.name,physicalAchievement.activity_id order by master.name asc   ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
    }

    public List<AdaptFinancialDto> getOiipcraFisheriesAbstractData(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select directorate,round(sum(financial_allocation_in_app),2) as financialAllocationInApp,round(sum(actual_fund_allocated),2) as actualFundAllocated,\n" +
                " round(case when sum(expenditure)=0 then '0.00' when sum(actual_fund_allocated)=0 then '0.00' \n" +
                " else (sum(expenditure)*100)/sum(actual_fund_allocated) end,2) as percentageAllocated,\n" +
                " round(sum(expenditure),2) as expenditure,district.district_name as districtName\n" +
                " from oiipcra_oltp.fard_financial_achievement as financialAchievement\n" +
                " left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id\n" +
                " left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id \n" +
                " left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true \n" +
                " and district.in_oiipcra=1";
        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }
        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and financialAchievement.activity_id=:activityId" ;
            sqlParam.addValue("deptId",adaptDto.getDeptId());
        }
        queryString+="  group by directorate,district.district_name order by district.district_name  ";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }

    public List<AdaptFinancialDto> getActivityWiseFinancialAchievementDataList(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " select directorate,round(sum(financial_allocation_in_app),2) as financialAllocationInApp,round(sum(actual_fund_allocated),2) as actualFundAllocated,master.name as activityName,\n" +
                " round(case when sum(expenditure)=0 then '0.00' when sum(actual_fund_allocated)=0 then '0.00' \n" +
                " else (sum(expenditure)*100)/sum(actual_fund_allocated) end,2) as percentageAllocated,\n" +
                " round(sum(expenditure),2) as expenditure\n" +
                " from oiipcra_oltp.denormalized_financial_achievement as financialAchievement\n" +
                " left join oiipcra_oltp.district_boundary as district on district.dist_id=financialAchievement.adapt_dist_id\n" +
                " left join oiipcra_oltp.master_head_details as master on master.id = financialAchievement.activity_id \n" +
                " left join oiipcra_oltp.fin_year_m as year on year.id=financialAchievement.year_id where true \n" +
                " and district.in_oiipcra=1";


        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and financialAchievement.adapt_dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and financialAchievement.year_id=:finYear" ;
            sqlParam.addValue("finYear",adaptDto.getFinYear());
        }
        if(adaptDto.getDeptId()!=null && adaptDto.getDeptId()>0){
            queryString +=" and financialAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptDto.getDeptId());
        }
        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and financialAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }
        queryString+="  \t\t\t\tgroup by directorate,master.name order by master.name\n ";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
    }

   /* public Page<AdaptPhysicalBeneficiaryDto> getAdaptPhysicalBeneficiaryList(AdaptPhysicalBeneficiaryFilterDto adaptPhysicalBeneficiaryFilterDto) {
        PageRequest pageable = PageRequest.of(adaptPhysicalBeneficiaryFilterDto.getPage(), adaptPhysicalBeneficiaryFilterDto.getSize(), Sort.Direction.fromString(adaptPhysicalBeneficiaryFilterDto.getSortOrder()), adaptPhysicalBeneficiaryFilterDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "year_id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();

        queryString +=  "select  finyr.name as year,finyr.year_id,dept_id,district_id,block_id,gp_id,village_id,village_name,scheme_id,scheme_name,component_id,\n" +
                "component_name,mobile_number,farmer_name,aadhar_number,lat_long,district_name,block_name,gp_name from oiipcra_oltp.adapt_physical_beneficiary \n" +
                "as beneficiary left join oiipcra_oltp.adapt_finyr_m as finyr on finyr.year_id=beneficiary.year_id where beneficiary.is_active=true ";

        if(adaptPhysicalBeneficiaryFilterDto.getYearId()!=null && adaptPhysicalBeneficiaryFilterDto.getYearId()>0){
            queryString +=" and finyr.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptPhysicalBeneficiaryFilterDto.getYearId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getDeptId()!=null && adaptPhysicalBeneficiaryFilterDto.getDeptId()>0){
            queryString +=" and beneficiary.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptPhysicalBeneficiaryFilterDto.getDeptId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getDistrictId()!=null && adaptPhysicalBeneficiaryFilterDto.getDistrictId()>0){
            queryString +=" and beneficiary.district_id=:districtId" ;
            sqlParam.addValue("districtId",adaptPhysicalBeneficiaryFilterDto.getDistrictId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getBlockId()!=null && adaptPhysicalBeneficiaryFilterDto.getBlockId()>0){
            queryString +=" and beneficiary.block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptPhysicalBeneficiaryFilterDto.getBlockId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getGpId()!=null && adaptPhysicalBeneficiaryFilterDto.getGpId()>0){
            queryString +=" and beneficiary.gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptPhysicalBeneficiaryFilterDto.getGpId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getSchemeName()!=null && adaptPhysicalBeneficiaryFilterDto.getSchemeName().isEmpty()){
            queryString +=" and beneficiary.scheme_name Like :schemeName" ;
            sqlParam.addValue("schemeName",adaptPhysicalBeneficiaryFilterDto.getSchemeName());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getComponentName()!=null && adaptPhysicalBeneficiaryFilterDto.getComponentName().isEmpty()){
            queryString +=" and beneficiary.component_name=:componentName" ;
            sqlParam.addValue("componentName",adaptPhysicalBeneficiaryFilterDto.getComponentName());
        }

        queryString += " ORDER BY beneficiary. " + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDtoList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalBeneficiaryDto.class));
        return new PageImpl<>(adaptPhysicalBeneficiaryDtoList, pageable, resultCount);

    }*/

    /*public List<AdaptPhysicalDto> getPhysicalAchievementDataList(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select directorate,sum(target)as target,sum(achievement) as achievement,sum(no_of_beneficiaries) as noOfBeneficiaries, \n" +
                "                (sum(achievement)*100)/sum(target) as achievementPercentage, \n" +
                "                district.district_name as districtName  \n" +
                "                from oiipcra_oltp.fard_physical_achievement as physicalAchievement \n" +
                "                left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.dist_id \n" +
                "                where true and district.in_oiipcra=1";
        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and physicalAchievement.dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getBlockId()!=null && adaptDto.getBlockId()>0){
            queryString +=" and physicalAchievement.block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptDto.getBlockId());
        }
        if(adaptDto.getGpId()!=null && adaptDto.getGpId()>0){
            queryString +=" and physicalAchievement.gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptDto.getGpId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and physicalAchievement.year_id=:yearId";
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }
        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and physicalAchievement.activity_id=:activityId";
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }

        queryString+="  GROUP by directorate,district.district_name order by district.district_name asc  ";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
    }
*/
    public Integer getSchemeId(String schemeId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "Select id from oiipcra_oltp.adapt_physical_scheme  where scheme_name like :schemeName";
        sqlParam.addValue("schemeName",schemeId);
        try {
            return namedJdbc.queryForObject(queryString, sqlParam,Integer.class);
        }
        catch(Exception e){
            return null;
        }
    }

    public Page<FardPhysicalAchievementDto> getOiipcraFardPhysicalAchievementList(AdaptFilterDto adaptDto) {
        PageRequest pageable = PageRequest.of(adaptDto.getPage(), adaptDto.getSize(), Sort.Direction.fromString(adaptDto.getSortOrder()), adaptDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "year_id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT fardPhysicalAchievement.id,fardPhysicalAchievement.district_name,fardPhysicalAchievement.block_name,  " +
                "fardPhysicalAchievement.gp_name,fardPhysicalAchievement.directorate,fardPhysicalAchievement.scheme_name,fardPhysicalAchievement.target, " +
                "fardPhysicalAchievement.achievement,fardPhysicalAchievement.no_of_beneficiaries,fardPhysicalAchievement.achievement_percentage, " +
                "fardPhysicalAchievement.year,fardPhysicalAchievement.dist_id as distId ,fardPhysicalAchievement.block_id,master.name as activityName,  " +
                "fardPhysicalAchievement.gp_id,fardPhysicalAchievement.year_id,fardPhysicalAchievement.dept_id,fardPhysicalAchievement.activity_id,fardPhysicalAchievement.scheme_id  " +
                "from oiipcra_oltp.fard_physical_achievement as fardPhysicalAchievement  " +
                "left join oiipcra_oltp.master_head_details as master on master.id = fardPhysicalAchievement.activity_id  " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=fardPhysicalAchievement.dist_id  " +
                "where fardPhysicalAchievement.is_active=true and district.in_oiipcra=1 ";


        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and fardPhysicalAchievement.dist_id=:distId" ;
            sqlParam.addValue("distId",adaptDto.getDistId());
        }

        if(adaptDto.getBlockId()!=null && adaptDto.getBlockId()>0){
            queryString +=" and fardPhysicalAchievement.block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptDto.getBlockId());
        }

        if(adaptDto.getGpId()!=null && adaptDto.getGpId()>0){
            queryString +=" and fardPhysicalAchievement.gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptDto.getGpId());
        }

        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and fardPhysicalAchievement.year_id=:finYear" ;
            sqlParam.addValue("finYear",adaptDto.getFinYear());
        }

        if(adaptDto.getDeptId()!=null && adaptDto.getDeptId()>0){
            queryString +=" and fardPhysicalAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptDto.getDeptId());
        }

        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and fardPhysicalAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }
        queryString += " ORDER BY fardPhysicalAchievement." + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<FardPhysicalAchievementDto> adaptDataList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(FardPhysicalAchievementDto.class));
        return new PageImpl<>(adaptDataList, pageable, resultCount);

    }

    public List<FardPhysicalAchievementDto> activityWiseFardPhysicalAchievementExcelReport(AdaptFilterDto adaptDto) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "select directorate,master.name as activityName,fardPhysicalAchievement.activity_id,sum(target)as target,sum(achievement) as achievement,sum(no_of_beneficiaries) as noOfBeneficiaries,  " +
                "(sum(achievement)*100)/sum(target) as achievementPercentage  " +
                "from oiipcra_oltp.fard_physical_achievement as fardPhysicalAchievement  " +
                "left join oiipcra_oltp.master_head_details as master on master.id = fardPhysicalAchievement.activity_id  " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=fardPhysicalAchievement.dist_id  " +
                "where true and district.in_oiipcra=1 ";

        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and fardPhysicalAchievement.dist_id=:distId" ;
            sqlParam.addValue("distId",adaptDto.getDistId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and fardPhysicalAchievement.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }
        if(adaptDto.getDeptId()!=null && adaptDto.getDeptId()>0){
            queryString +=" and fardPhysicalAchievement.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptDto.getDeptId());
        }
        if(adaptDto.getBlockId()!=null && adaptDto.getBlockId()>0){
            queryString +=" and fardPhysicalAchievement.block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptDto.getBlockId());
        }
        if(adaptDto.getGpId()!=null && adaptDto.getGpId()>0){
            queryString +=" and fardPhysicalAchievement.gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptDto.getGpId());
        }

        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and fardPhysicalAchievement.activity_id=:activityId" ;
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }

        queryString += "  GROUP by directorate,master.name,fardPhysicalAchievement.activity_id order by master.name asc   ";

        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(FardPhysicalAchievementDto.class));


    }

    public Page<AdaptPhysicalBeneficiaryDto> getAdaptPhysicalBeneficiaryList(AdaptPhysicalBeneficiaryFilterDto adaptPhysicalBeneficiaryFilterDto) {
        PageRequest pageable = PageRequest.of(adaptPhysicalBeneficiaryFilterDto.getPage(), adaptPhysicalBeneficiaryFilterDto.getSize(), Sort.Direction.fromString(adaptPhysicalBeneficiaryFilterDto.getSortOrder()), adaptPhysicalBeneficiaryFilterDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "year_id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();
        queryString +=  "select  beneficiary.id,finyr.name as year,finyr.year_id,dept_id,district_id,block_id,gp_id,village_id,village_name,scheme_id,scheme_name,component_id,\n" +
                "component_name,mobile_number,farmer_name,aadhar_number,lat_long,district_name,block_name,gp_name from oiipcra_oltp.adapt_physical_beneficiary \n" +
                "as beneficiary left join oiipcra_oltp.adapt_finyr_m as finyr on finyr.year_id=beneficiary.year_id where beneficiary.is_active=true ";

        if(adaptPhysicalBeneficiaryFilterDto.getYearId()!=null && adaptPhysicalBeneficiaryFilterDto.getYearId()>0){
            queryString +=" and finyr.year_id=:yearId" ;
            sqlParam.addValue("yearId",adaptPhysicalBeneficiaryFilterDto.getYearId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getDeptId()!=null && adaptPhysicalBeneficiaryFilterDto.getDeptId()>0){
            queryString +=" and beneficiary.dept_id=:deptId" ;
            sqlParam.addValue("deptId",adaptPhysicalBeneficiaryFilterDto.getDeptId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getDistrictId()!=null && adaptPhysicalBeneficiaryFilterDto.getDistrictId()>0){
            queryString +=" and beneficiary.district_id=:districtId" ;
            sqlParam.addValue("districtId",adaptPhysicalBeneficiaryFilterDto.getDistrictId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getBlockId()!=null && adaptPhysicalBeneficiaryFilterDto.getBlockId()>0){
            queryString +=" and beneficiary.block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptPhysicalBeneficiaryFilterDto.getBlockId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getGpId()!=null && adaptPhysicalBeneficiaryFilterDto.getGpId()>0){
            queryString +=" and beneficiary.gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptPhysicalBeneficiaryFilterDto.getGpId());
        }
       /* if(adaptPhysicalBeneficiaryFilterDto.getSchemeName()!=null){
            queryString +=" and beneficiary.scheme_name Like :schemeName" ;
            sqlParam.addValue("schemeName",adaptPhysicalBeneficiaryFilterDto.getSchemeName());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getComponentName()!=null){
            queryString +=" and beneficiary.component_name like :componentName" ;
            sqlParam.addValue("componentName",adaptPhysicalBeneficiaryFilterDto.getComponentName());
        }*/
        if(adaptPhysicalBeneficiaryFilterDto.getSchemeId()!=null && adaptPhysicalBeneficiaryFilterDto.getSchemeId()>0){
            queryString +=" and beneficiary.scheme_id=:schemeId" ;
            sqlParam.addValue("schemeId",adaptPhysicalBeneficiaryFilterDto.getSchemeId());
        }
        if(adaptPhysicalBeneficiaryFilterDto.getComponentId()!=null && adaptPhysicalBeneficiaryFilterDto.getComponentId()>0) {
            queryString += " and beneficiary.component_id=:componentId";
            sqlParam.addValue("componentId", adaptPhysicalBeneficiaryFilterDto.getComponentId());
        }
        queryString += " ORDER BY beneficiary. " + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDtoList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalBeneficiaryDto.class));
        return new PageImpl<>(adaptPhysicalBeneficiaryDtoList, pageable, resultCount);
    }
    public AdaptPhysicalBeneficiaryDto getAdaptPhysicalBeneficiaryById(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString +=  "select  beneficiary.id,finyr.name as year,finyr.year_id,dept_id,district_id,block_id,gp_id,village_id,village_name,scheme_id,scheme_name,component_id,\n" +
                "component_name,mobile_number,farmer_name,aadhar_number,lat_long,district_name,block_name,gp_name from oiipcra_oltp.adapt_physical_beneficiary \n" +
                "as beneficiary left join oiipcra_oltp.adapt_finyr_m as finyr on finyr.year_id=beneficiary.year_id where beneficiary.is_active=true ";
        if(id!=null  && id>0){
            queryString+=" and beneficiary.id=:id";
            sqlParam.addValue("id",id);
        }
        try {
            return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalBeneficiaryDto.class));
        }
        catch(Exception e){
            return null;
        }

    }

    public List<AdaptPhysicalDto> getPhysicalAchievementDataList(AdaptFilterDto adaptDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select directorate,\n" +
                "case when sum(target) is null then '0' else  sum(target) end \n" +
                "as target,case when sum(achievement) is null then '0' else sum(achievement) end as achievement,sum(no_of_beneficiaries) as noOfBeneficiaries, \n" +
                "case when (sum(achievement)*100)/sum(target) is null then '0' else (sum(achievement)*100)/sum(target) end as achievementPercentage, \n" +
                "district.district_name as districtName \n" +
                "from oiipcra_oltp.fard_physical_achievement as physicalAchievement \n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.dist_id\n" +
                "where true and district.in_oiipcra=1";
        if(adaptDto.getDistId()!=null && adaptDto.getDistId()>0){
            queryString +=" and physicalAchievement.dist_id=:districtId" ;
            sqlParam.addValue("districtId",adaptDto.getDistId());
        }
        if(adaptDto.getBlockId()!=null && adaptDto.getBlockId()>0){
            queryString +=" and physicalAchievement.block_id=:blockId" ;
            sqlParam.addValue("blockId",adaptDto.getBlockId());
        }
        if(adaptDto.getGpId()!=null && adaptDto.getGpId()>0){
            queryString +=" and physicalAchievement.gp_id=:gpId" ;
            sqlParam.addValue("gpId",adaptDto.getGpId());
        }
        if(adaptDto.getFinYear()!=null && adaptDto.getFinYear()>0){
            queryString +=" and physicalAchievement.year_id=:yearId";
            sqlParam.addValue("yearId",adaptDto.getFinYear());
        }
        if(adaptDto.getActivityId()!=null && adaptDto.getActivityId()>0){
            queryString +=" and physicalAchievement.activity_id=:activityId";
            sqlParam.addValue("activityId",adaptDto.getActivityId());
        }

        queryString+="  GROUP by directorate,district.district_name order by district.district_name asc  ";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));
    }

    public List<AdaptFinancialSchemeDto> getAllSchemeNam() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT  id,scheme_name,activity_id  " +
                " from oiipcra_oltp.adapt_financial_scheme order by id asc  ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialSchemeDto.class));
    }


    public AdaptPhysicalDto getDenormalizedAchievementDataById(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =  "SELECT denormalizedAchievement.id as id,denormalizedAchievement.district_name as districtName,denormalizedAchievement.block_name as blockName,denormalizedAchievement.gp_name as gpName, " +
                "denormalizedAchievement.directorate as directorate,denormalizedAchievement.master_component as masterComponent,denormalizedAchievement.scheme_name as schemeName, " +
                "denormalizedAchievement.component_name as componentName,denormalizedAchievement.target as target,denormalizedAchievement.achievement as achievement, " +
                "denormalizedAchievement.no_of_beneficiaries as noofBeneficiaries,denormalizedAchievement.achievement_percentage as achievementPercentage, " +
                "denormalizedAchievement.year,denormalizedAchievement.adapt_dist_id as districtId,denormalizedAchievement.adapt_block_id as blockId,  " +
                "denormalizedAchievement.scheme_id as schemeId ,denormalizedAchievement.component_id as componentId, " +
                "denormalizedAchievement.adapt_gp_id as gpId,denormalizedAchievement.year_id as yearId,denormalizedAchievement.dept_id as deptId,denormalizedAchievement.activity_id as activityId, " +
                "denormalizedAchievement.unit_id as unitId  " +
                "from oiipcra_oltp.denormalized_achievement as denormalizedAchievement  " +
                "where denormalizedAchievement.is_active= true and denormalizedAchievement.id=:id ";

        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(AdaptPhysicalDto.class));


    }

    public List<FardPhysicalAchievementDto> getFardPhysicalAchievementGraphData() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        queryString += "select directorate,case when sum(target)is null then '0' else  sum(target) end as target,case when sum(achievement) is null then '0'else sum(achievement) end as achievement,case when sum(no_of_beneficiaries) is null then'0' else sum(no_of_beneficiaries) end as noOfBeneficiaries,\n" +
                "                case when (sum(achievement)*100)/sum(target) is null then '0'else (sum(achievement)*100)/sum(target) end as achievementPercentage,\n" +
                "            district.district_name as districtName,physicalAchievement.dist_id as distId,\n" +
                "            physicalAchievement.dept_id as deptId\n" +
                "            from oiipcra_oltp.fard_physical_achievement as physicalAchievement \n" +
                "               left join oiipcra_oltp.district_boundary as district on district.dist_id=physicalAchievement.dist_id \n" +
                "             where true and district.in_oiipcra=1 \n" +
                "               group by directorate,district.district_name ,physicalAchievement.dist_id ,\n" +
                "               physicalAchievement.dept_id order by district.district_name asc";
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(FardPhysicalAchievementDto.class));
    }
}