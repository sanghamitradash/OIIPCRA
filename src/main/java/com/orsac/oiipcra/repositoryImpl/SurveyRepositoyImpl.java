package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.Tender;
import com.orsac.oiipcra.repository.SurveyRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Repository
public class SurveyRepositoyImpl implements SurveyRepository {

    @Value("${accessImagePath}")
    private String accessImagePath;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    private UserQueryRepository userQryRepo;


    @PersistenceContext
    private EntityManager entityManager;


    public int count(String qryStr, MapSqlParameterSource sqlParam) {
        String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
        Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        if (null != intRes) {
            return intRes;
        }
        return 0;
    }

    /**
     * Search Tank survey data List
     */
    @Override
    public Page<TankSurveyInfoResponse> getTankSurveySearchList(SurveyListRequest surveyListRequest,List<Integer> tankIdsByIssueId,List<Integer>tankIdsByContractId,List<Integer>tankIdsByEstimateId) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(surveyListRequest.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("id");

      /*  List<Integer> userIdList = new ArrayList<>();
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getSize(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }*/
        UserInfoDto userInfoById = null;
        userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());

        List<UserLevelDto> userLevel = null;
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }

        List<Integer> authorityIdList = new ArrayList<>();
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

        PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "created_on");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

      /*  queryString += "SELECT tank.id,tank.tank_id,tank.project_id,tank.created_by as surveyBy,tank.district_id,village.district_name, tank.block_id,village.block_name, " +
                " tank.gp_id,village.grampanchayat_name as gpName,tank.village_id, village.revenue_village_name as villageName,tank.division_id,tank.sub_division_id," +
                " division.mi_division_name as divisionName,subdivision.mi_sub_division_name as subDivisionName,sectionm.mi_section_name as sectionName, " +
                //" bm.dept_mi_division_name as divisionName,bm.dept_mi_sub_division_name as subDivisionName,bm.dept_mi_section_name as sectionName, " +
                " tank.section_id,approve.name as progressStatus,tankm.name_of_the_m_i_p as tankName,tank.created_on FROM oiipcra_oltp.tank_survey_data as tank" +
                " left join oiipcra_oltp.village_boundary as village on village.village_id=tank.village_id" +
               *//* " left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=tank.division_id" +
                " left join oiipcra_oltp.mi_subdivision_m as subdiv on subdiv.mi_sub_division_id=tank.sub_division_id" +
                " left join oiipcra_oltp.mi_section_m as sec on sec.section_id=tank.section_id" +*//*
                //" left join oiipcra_oltp.block_mapping as bm on bm.section_id=tank.section_id" +
                " left join oiipcra_oltp.mi_division_m as division on tank.division_id = division.mi_division_id" +
                " left join oiipcra_oltp.mi_subdivision_m as subdivision on tank.sub_division_id = subdivision.mi_sub_division_id" +
                " left join oiipcra_oltp.mi_section_m as sectionm on tank.section_id = sectionm.section_id" +
                " left join oiipcra_oltp.approval_status_m as approve on approve.id=tank.progress_status_id" +
                " left join oiipcra_oltp.tank_m_id as tankm on tank.tank_id=tankm.tank_id where tank.is_active=true";*/
        queryString +=" SELECT tank.id,tank.tank_id,tank.project_id,tank.created_by as surveyBy,tank.district_id,village.district_name,\n" +
                " tank.block_id,village.block_name,  tank.gp_id,village.grampanchayat_name as gpName,tank.village_id, \n" +
                " village.revenue_village_name as villageName,tank.division_id,tank.sub_division_id, division.mi_division_name as divisionName,\n" +
                " subdivision.mi_sub_division_name as subDivisionName,sectionm.mi_section_name as sectionName,  tank.section_id,\n" +
                " approve.name as progressStatus,tankm.name_of_the_m_i_p as tankName,tank.created_on,userM.name as surveyorName " +
                " FROM oiipcra_oltp.tank_survey_data as tank \n" +
                " left join oiipcra_oltp.village_boundary as village on village.village_id=tank.village_id \n" +
                " left join oiipcra_oltp.mi_division_m as division on tank.division_id = division.mi_division_id \n" +
                " left join oiipcra_oltp.mi_subdivision_m as subdivision on tank.sub_division_id = subdivision.mi_sub_division_id\n" +
                " left join oiipcra_oltp.mi_section_m as sectionm on tank.section_id = sectionm.section_id\n" +
                " left join oiipcra_oltp.approval_status_m as approve on approve.id=tank.progress_status_id \n" +
                " left join oiipcra_oltp.tank_m_id as tankm on tank.tank_id=tankm.tank_id \n" +
                " left join oiipcra_oltp.user_m as userM on userM.id=tank.created_by\n" +
                " where tank.is_active=true ";


        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
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
                    queryString += " And tank.division_id IN (:authorityIdList)";
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
            if (surveyListRequest.getVillageId()!=null && surveyListRequest.getVillageId() > 0) {
                queryString += " AND  tank.village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }
            if (surveyListRequest.getGpId()!=null && surveyListRequest.getGpId() > 0) {
                queryString += " AND  tank.gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId() !=null && surveyListRequest.getBlockId() > 0) {
                queryString += "  AND tank.block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId()!=null && surveyListRequest.getDistrictId() > 0) {
                queryString += " AND tank.district_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId()!=null && surveyListRequest.getDivisionId() > 0) {
                queryString += " AND tank.division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId()!=null && surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND tank.sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId()!=null && surveyListRequest.getSectionId() > 0) {
                queryString += " AND tank.section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            if (surveyListRequest.getProgressStatus()!=null && surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND tank.progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }
            if (surveyListRequest.getTankId()!=null && surveyListRequest.getTankId() > 0) {
                queryString += " AND tank.tank_id=:tankId";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
            if (surveyListRequest.getProjectId()!=null && surveyListRequest.getProjectId() > 0) {
                queryString += " AND tank.project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (surveyListRequest.getId()!=null &&surveyListRequest.getId() > 0 ) {
                queryString += " AND tank.id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }
            if (tankIdsByIssueId != null && tankIdsByIssueId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByIssueId)";
                sqlParam.addValue("tankIdsByIssueId", tankIdsByIssueId);
            }

            if (tankIdsByContractId != null && tankIdsByContractId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByEstimateId != null && tankIdsByEstimateId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByEstimateId) ";
                sqlParam.addValue("tankIdsByEstimateId", tankIdsByEstimateId);
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (surveyListRequest.getUploadFromDate() != null && !surveyListRequest.getUploadFromDate().isEmpty()) {
                queryString += " AND date(tank.created_on) >= :uploadFromDate";
                Date uploadFromDate = null;
                try {
                    uploadFromDate = format.parse(surveyListRequest.getUploadFromDate());
                } catch (Exception exception) {
                    log.info("From Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
            }
            if (surveyListRequest.getUploadToDate() != null && !surveyListRequest.getUploadToDate().isEmpty()) {
                queryString += " AND date(tank.created_on) <= :uploadToDate";
                Date uploadToDate = null;
                try {
                    uploadToDate = format.parse(surveyListRequest.getUploadToDate());
                } catch (Exception exception) {
                    log.info("To Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
            }

            if (userInfoById.getSurveyor()) {
                queryString += " AND tank.created_by=:userId";
                sqlParam.addValue("userId", surveyListRequest.getUserId());
            }/* else {
                    queryString += " AND tank.created_by IN (:userIdList)";
                    sqlParam.addValue("userIdList", userIdList);
                }*/
        }
      //   queryString +=" and tank.created_by not in (2) ";
        queryString += " ORDER BY tank.created_on DESC ";
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<TankSurveyInfoResponse> surveyList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankSurveyInfoResponse.class));
        return new PageImpl<>(surveyList, pageable, resultCount);
    }

    /**
     * Search Activity List
     */
    @Override
    public Page<ActivitySurveyListInfo> searchActivityList(ActivitySearchRequest activitySearchRequest) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(activitySearchRequest.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("id");

        List<Integer> userIdList = new ArrayList<>();
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getSize(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }

        UserInfoDto userInfoById = userQryRepo.getUserById(activitySearchRequest.getUserId());

        PageRequest pageable = PageRequest.of(activitySearchRequest.getPage(), activitySearchRequest.getSize(), Sort.Direction.fromString(activitySearchRequest.getSortOrder()), activitySearchRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";


        queryString += "select master.name as activityName,contract.contract_number as contractNumber,tender.bid_id as bid,status.name as workStatus,activity.created_on as uploadDate," +
                "tank.name_of_the_m_i_p as tankName,notice.id as workId,tenderActivity.activity_id as activityId,type.name as workType " +
                "from oiipcra_oltp.activity_survey as activity " +
                "left join oiipcra_oltp.master_head_details as master on master.id=activity.activity_id " +
                "left join oiipcra_oltp.master_work_status as status on status.id=activity.work_status_id " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=activity.contract_id " +
                "left join oiipcra_oltp.tender_m_new as tender on tender.id=contract.tender_id " +
                "left join oiipcra_oltp.tender_notice as notice on notice.tender_id=tender.id  " +
                "left join oiipcra_oltp.contract_type as type on type.id=contract.contract_type_id " +
                "left join oiipcra_oltp.tender_level_mapping as tenderActivity on tenderActivity.tender_id=contract.tender_id " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id=activity.tank_id where activity.is_active=true ";
        if (activitySearchRequest.getContractNumber() != null) {
            queryString += " AND  contract.contract_number LIKE(:contractNumber)";
            sqlParam.addValue("contractNumber", activitySearchRequest.getContractNumber());
        }
        if (activitySearchRequest.getAgencyId() > 0) {
            queryString += " AND  activity.agencyId=:agencyId";
            sqlParam.addValue("agencyId", activitySearchRequest.getAgencyId());
        }
        if (activitySearchRequest.getWorkType() > 0) {
            queryString += " AND  contract.contract_type=:type";
            sqlParam.addValue("type", activitySearchRequest.getWorkType());
        }
//        if (activitySearchRequest.getTankCode() > 0) {
//            queryString += " AND activity.tank_id=:tankId";
//            sqlParam.addValue("tankId", activitySearchRequest.getTankId());
//        }
        if (activitySearchRequest.getWorkStatusId() > 0) {
            queryString += " AND  activity.work_status_id=:workStatusId";
            sqlParam.addValue("workStatusId", activitySearchRequest.getWorkStatusId());
        }
        if (activitySearchRequest.getActivityId() > 0) {
            queryString += " AND  activity.activity_id=:activityId";
            sqlParam.addValue("activityId", activitySearchRequest.getActivityId());
        }
        if (activitySearchRequest.getBidId() != null) {
            queryString += " AND  tender_m_new.bid_id LIKE(:bid)";
            sqlParam.addValue("bid", activitySearchRequest.getBidId());
        }
        if (activitySearchRequest.getWorkId() > 0) {
            queryString += " AND  tender_notice.id=:workId";
            sqlParam.addValue("workId", activitySearchRequest.getWorkId());
        }
//        if (activitySearchRequest.getStatusId() > 0) {
//            queryString += " AND  activity.status_id=:statusId";
//            sqlParam.addValue("statusId", activitySearchRequest.getStatusId());
//        }
//        if (activitySearchRequest.getVillageId() > 0) {
//            queryString += " AND  activity.village_id=:villageId";
//            sqlParam.addValue("villageId", activitySearchRequest.getVillageId());
//        }
//        if (activitySearchRequest.getGpId() > 0) {
//            queryString += " AND  activity.gp_id=:gpId";
//            sqlParam.addValue("gpId", activitySearchRequest.getGpId());
//        }
        if (activitySearchRequest.getBlockId() > 0) {
            queryString += "  AND activity.block_id=:blockId ";
            sqlParam.addValue("blockId", activitySearchRequest.getBlockId());
        }
        if (activitySearchRequest.getDistrictId() > 0) {
            queryString += " AND activity.district_id=:distId ";
            sqlParam.addValue("distId", activitySearchRequest.getDistrictId());
        }
//        if (activitySearchRequest.getDivisionId() > 0) {
//            queryString += " AND activity.division_id=:divisionId ";
//            sqlParam.addValue("divisionId", activitySearchRequest.getDivisionId());
//        }
//        if (activitySearchRequest.getSubDivisionId() > 0) {
//            queryString += " AND activity.sub_division_id=:subDivisionId ";
//            sqlParam.addValue("subDivisionId", activitySearchRequest.getSubDivisionId());
//        }
//        if (activitySearchRequest.getSectionId() > 0) {
//            queryString += " AND activity.section_id=:sectionId ";
//            sqlParam.addValue("sectionId", activitySearchRequest.getSectionId());
//        }
//        if (activitySearchRequest.getProgressStatus() > 0) {
//            queryString += " AND activity.progress_status=:progressStatus";
//            sqlParam.addValue("progressStatus", activitySearchRequest.getProgressStatus());
        //}
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (activitySearchRequest.getUploadFromDate() != null && !activitySearchRequest.getUploadFromDate().isEmpty()) {
            queryString += " AND date(activity.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(activitySearchRequest.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("onSubmitFromDate", uploadFromDate, Types.DATE);
        }
        if (activitySearchRequest.getUploadToDate() != null && !activitySearchRequest.getUploadToDate().isEmpty()) {
            queryString += " AND date(activity.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(activitySearchRequest.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("onSubmitToDate", uploadToDate, Types.DATE);
        }
        if (userInfoById.getSurveyor()) {
            queryString += " AND tank.created_by IN (:userId)";
            sqlParam.addValue("userId", activitySearchRequest.getUserId());

        } else {
            queryString += " AND tank.created_by IN (:userIdList)";
            sqlParam.addValue("userIdList", userIdList);
        }

        queryString += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ActivitySurveyListInfo> workProgress = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(ActivitySurveyListInfo.class));

        return new PageImpl<>(workProgress, pageable, resultCount);
    }


    public Integer getUserLevelIdByUserId(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetUserLevelQuery = " select user_level_id from oiipcra_oltp.user_m  where id=:userId";

        sqlParam.addValue("userId", userId);
        return namedJdbc.queryForObject(GetUserLevelQuery, sqlParam, Integer.class);
    }

    public List<BoundaryDto> getUserListByUserLevelId(int userLevelId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        switch (userLevelId) {
            case 1:
                queryString = "select state_id, state_name from district_boundary";
                break;
            case 2:
                queryString = "select dist_id, district_name from district_boundary";
                break;
            case 3:
                queryString = "select block_id, block_name from block_boundary";
                break;
            case 4:
                queryString = "select gp_id, grampanchayat_name from gp_boundary";
                break;
            case 5:
                queryString = "select village_id, revenue_village_name from village_boundary";
                break;
            case 6:
                queryString = "select section_id, mi_division_name from mi_division_m";
                break;
            case 7:
                queryString = "select mi_sub_division_id, mi_sub_division_name from mi_subdivision_m";
                break;
        }
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(BoundaryDto.class));
    }

    @Override
    public Page<ContractInfoListing> getContractList(ContractListRequestDto contractInfo, List<Integer> activityIds, List<Integer> estimateIds, List<Integer> tenderIds, List<Integer> tenderNoticeIds, List<Integer> contractIdsByInvoiceId, List<Integer> contractIdsByExpenditureId) throws ArrayIndexOutOfBoundsException {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(contractInfo.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("contract_date");
        userListRequest.setSize(1000);

        PageRequest pageable = PageRequest.of(contractInfo.getPage(), contractInfo.getSize(), Sort.Direction.fromString(contractInfo.getSortOrder()), contractInfo.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "contract_date");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();
       /* userIdList.add(contractInfo.getUserId());
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getTotalElements(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }*/
        userIdList = userQryRepo.getSubUsers(contractInfo.getUserId());
        userIdList.add(contractInfo.getUserId());

        UserInfoDto userInfoById = userQryRepo.getUserById(contractInfo.getUserId());
        queryString += "SELECT Distinct(contract.id) as contractId,contract.contract_number as contractNumber,contract.work_description as workDescription," +
                " contract.contract_date as contractDate,level.level_name as contractLevel,type.name as ContractType,status.name as contractStatus," +
                "agency.name as agencyName,contract.contract_name as contractName,contract.work_type_id as workTypeId," +
                " wm.name as workTypeName  from oiipcra_oltp.contract_m as contract " +
                "left join oiipcra_oltp.contract_level_master as level on level.id=contract.contract_level_id " +
                "left join oiipcra_oltp.contract_type as type on type.id=contract.contract_type_id  " +
                "left join oiipcra_oltp.contract_status as status on status.id=contract.contract_status_id " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=contract.agency_id  " +
                "left join oiipcra_oltp.contract_mapping as cm on cm.contract_id=contract.id " +
                "left join oiipcra_oltp.tender_m as tender on cm.tender_id=tender.id " +
                " left join oiipcra_oltp.work_type_m as wm on wm.id=contract.work_type_id " +
                "WHERE contract.is_active=true ";

//        if (activityIds != null && !activityIds.isEmpty()) {
//            queryString += " AND cm.tank_id IN (:activityIds)";
//            sqlParam.addValue("activityIds", activityIds);
//        }
        if (estimateIds != null && !estimateIds.isEmpty()) {
            queryString += " AND cm.tank_id IN (:estimateIds)";
            sqlParam.addValue("estimateIds", estimateIds);
        }
        if (tenderIds != null && !tenderIds.isEmpty()) {
            queryString += " AND cm.tender_id IN (:tenderIds)";
            sqlParam.addValue("tenderIds", tenderIds);
        }
        if (tenderNoticeIds != null && !tenderNoticeIds.isEmpty()) {
            queryString += " AND cm.tender_notice_id IN (:tenderNoticeIds)";
            sqlParam.addValue("tenderNoticeIds", tenderNoticeIds);
        }
        if (contractInfo.getTankId() != null && contractInfo.getTankId() > 0) {
            queryString += " AND cm.tank_id=:tankId";
            sqlParam.addValue("tankId", contractInfo.getTankId());
        }
        if (contractInfo.getTypeId() != null && contractInfo.getTypeId() > 0) {
            queryString += " AND type.id=:typeId ";
            sqlParam.addValue("typeId", contractInfo.getTypeId());
        }

        if (contractInfo.getAgencyId() != null && contractInfo.getAgencyId() > 0) {
            queryString += " AND agency.id=:agencyId ";
            sqlParam.addValue("agencyId", contractInfo.getAgencyId());
        }
        if (contractInfo.getStatusId() != null && contractInfo.getStatusId() > 0) {
            queryString += "  AND status.id=:statusId ";
            sqlParam.addValue("statusId", contractInfo.getStatusId());
        }
        if (contractInfo.getContractNo() != null && !contractInfo.getContractNo().equals("")) {
            queryString += " AND contract.contract_number LIKE :contractNo ";
            sqlParam.addValue("contractNo", contractInfo.getContractNo());
        }
        if (contractInfo.getTenderCode() != null) {
            queryString += " AND tender.bid_id LIKE :tenderCode";
            sqlParam.addValue("tenderCode", contractInfo.getTenderCode());
        }
        if (contractInfo.getActivityId() != null && contractInfo.getActivityId() > 0) {
            queryString += " AND cm.activity_id=:activityId";
            sqlParam.addValue("activityId", contractInfo.getActivityId());
        }
        if (contractInfo.getDistId() != null && contractInfo.getDistId() > 0) {
            queryString += " AND cm.dist_id=:distId";
            sqlParam.addValue("distId", contractInfo.getDistId());
        }
        if (contractInfo.getBlockId() !=null && contractInfo.getBlockId() > 0) {
            queryString += " AND cm.block_id=:blockId";
            sqlParam.addValue("blockId", contractInfo.getBlockId());
        }
        if (contractInfo.getDivisionId() != null && contractInfo.getDivisionId() > 0) {
            queryString += " AND cm.division_id=:divisionId";
            sqlParam.addValue("divisionId", contractInfo.getDivisionId());
        }

        if (contractInfo.getTankId() != null && contractInfo.getTankId() > 0) {
            queryString += " AND cm.tank_id=:tank_id";
            sqlParam.addValue("tank_id", contractInfo.getTankId());
        }
//        if (contractInfo.getTankId() != null && contractInfo.getTankId() > 0) {
//            queryString += " AND cm.tank_id=:tankId";
//            sqlParam.addValue("tankId", contractInfo.getTankId());
//        }

        if (contractIdsByInvoiceId != null && contractIdsByInvoiceId.size() > 0) {
            queryString += " AND contract.id IN (:contractIdsByInvoiceId)";
            sqlParam.addValue("contractIdsByInvoiceId", contractIdsByInvoiceId);
        }


        if (contractIdsByExpenditureId != null && contractIdsByExpenditureId.size() > 0) {
            queryString += " AND contract.id IN (:contractIdsByExpenditureId)";
            sqlParam.addValue("contractIdsByExpenditureId", contractIdsByExpenditureId);
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (contractInfo.getUploadFromDate() != null && !contractInfo.getUploadFromDate().isEmpty()) {
            queryString += " AND date(contract.contract_date) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(contractInfo.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (contractInfo.getUploadToDate() != null && !contractInfo.getUploadToDate().isEmpty()) {
            queryString += " AND date(contract.contract_date) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(contractInfo.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }


        /* if(userInfoById.getRoleId()!=1 && userInfoById.getRoleId()!=3) {
             if (userInfoById.getSurveyor()) {
                 queryString += " AND contract.created_by IN (:userId)";
                 sqlParam.addValue("userId", contractInfo.getUserId());
             }
             else {
                 queryString += " AND contract.created_by IN (:userIdList)";
                 sqlParam.addValue("userIdList", userIdList);
             }
        }
        if(userInfoById.getRoleId()==1 || userInfoById.getRoleId()==3 ){
            queryString += " ";
        }*/
        if(contractInfo.getUserId()!=null && contractInfo.getUserId()>0){
            if(userInfoById.getRoleId()<4){
                queryString += " ";
            }
            else{
                queryString += " AND contract.created_by IN (:userIdList)";
                sqlParam.addValue("userIdList", userIdList);
            }
        }

        queryString += " ORDER BY contract." + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ContractInfoListing> contractList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(ContractInfoListing.class));
        return new PageImpl<>(contractList, pageable, resultCount);

    }




    /**
     * 538 tank Search List
     */
    public Page<TankInfo> getTankSearchList(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId, List<Integer> tankIdsByEstimateId,List<Integer> tankIdsByIssueId,List<Integer> cadTankIds,List<Integer>surveyTankIds,List<Integer>depthTankIds,List<Integer>feederTankIds ,List<Integer> civilTankIds,List<Integer> fopTankIds,List<Integer> droppedTankIds,List<Integer> proposedToBeDroppedTankIds,List<Integer> progressTankIds) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

        PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        queryString = "SELECT id, project_id,tank_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id,village_id,village_name," +
//                " mi_division_id,mi_division_name,name_of_the_m_i_p as tankName, m_i_p_id,sub_division_id,section_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

//        queryString = "SELECT id, project_id,tank_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id,village_id,village_name,  " +
//                "category,category_id,type,type_id,latitude,longitude,catchment_area_sqkm,certified_ayacut_kharif_ha,certified_ayacut_rabi_ha,designed_cca_kharif_ha,  " +
//                "designed_cca_rabi_ha,river_basin,river_basin_id,  " +
//                " mi_division_id,mi_division_name,name_of_the_m_i_p as tankName, m_i_p_id,sub_division_id,section_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        queryString = "SELECT distinct tank.id, tank.project_id,tank.tank_id, tank.dept_dist_name, tank.dist_id, tank.dept_block_name, \n" +
                "tank.block_id, tank.dept_gp_name, tank.gp_id,tank.village_id,tank.village_name,  \n" +
                "tank.category,tank.category_id,tank.type,tank.type_id,tank.latitude,tank.longitude,tank.catchment_area_sqkm,\n" +
                "tank.certified_ayacut_kharif_ha,tank.certified_ayacut_rabi_ha,tank.designed_cca_kharif_ha, \n" +
                "tank.designed_cca_rabi_ha,tank.river_basin,river_basin_id,  \n" +
                "tank.mi_division_id,tank.mi_division_name,tank.name_of_the_m_i_p as tankName, tank.m_i_p_id,\n" +
                "tank.sub_division_id,tank.section_id \n" +
                "FROM oiipcra_oltp.tank_m_id as tank " +
                /*"left join oiipcra_oltp.tank_survey_data as tankdata on tankdata.tank_id=tank.tank_id  " +
                "left join oiipcra_oltp.approval_status_m as app on app.id= tankdata.progress_status_id  " +*/
                "WHERE tank.is_active=true " ;

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

            if (surveyListRequest.getVillageId()!=null &&surveyListRequest.getVillageId() > 0) {
                queryString += " AND tank.village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId()!=null && surveyListRequest.getGpId() > 0) {
                queryString += " AND  tank.gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId()!= null && surveyListRequest.getBlockId() > 0) {
                queryString += "  AND tank.block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId()!=null && surveyListRequest.getDistrictId() > 0) {
                queryString += " AND tank.dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId()!=null && surveyListRequest.getDivisionId() > 0) {
                queryString += " AND tank.mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId()!=null && surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND tank.sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId()!=null && surveyListRequest.getSectionId() > 0) {
                queryString += " AND tank.section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            if (surveyListRequest.getProgressStatus()!=null && surveyListRequest.getProgressStatus() > 0 ) {
                if( progressTankIds.size()>0) {
                    queryString += " AND tank.id in (:tankIds)";
                    sqlParam.addValue("tankIds",progressTankIds);
                }

                else{
                    queryString += " AND tank.id in (0)";
                }
            }

            if (surveyListRequest.getId()!=null && surveyListRequest.getId() > 0) {
                queryString += " AND tank.id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId()!=null && surveyListRequest.getProjectId() > 0) {
                queryString += " AND tank.project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }

            if (tankIdsByIssueId != null && tankIdsByIssueId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByIssueId)";
                sqlParam.addValue("tankIdsByIssueId", tankIdsByIssueId);
            }

            if (tankIdsByEstimateId != null && tankIdsByEstimateId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByEstimateId)";
                sqlParam.addValue("tankIdsByEstimateId", tankIdsByEstimateId);
            }

            if (surveyListRequest.getTankId()!=null && surveyListRequest.getTankId() > 0) {
                queryString += " AND tank.tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
            if(surveyListRequest.getEstimateId()!=null && surveyListRequest.getEstimateId()>0 &&  tankIdsByEstimateId.size()==0){
                queryString += " AND tank.tank_id=0";
            }
            if(surveyListRequest.getEstimateId()!=null  &&  tankIdsByEstimateId.size()>0){
                queryString += " AND tank.tank_id IN (:tankIdsByEstimateId)";
                sqlParam.addValue("tankIdsByEstimateId", tankIdsByEstimateId);
            }
            if(surveyListRequest.getIsCadSurveyed() != null && cadTankIds.size()>0){
                queryString += " AND tank.tank_id IN (:cadTankIds)";
                sqlParam.addValue("cadTankIds", cadTankIds);
            }
            if(surveyListRequest.getIsTankSurveyed() != null && surveyTankIds.size()>0 ){
                queryString += " AND tank.tank_id IN (:surveyTankIds)";
                sqlParam.addValue("surveyTankIds", surveyTankIds);
            }
            if(surveyListRequest.getIsDepthSurveyed() != null && depthTankIds.size()>0 ){
                queryString += " AND tank.tank_id IN (:depthTankIds)";
                sqlParam.addValue("depthTankIds", depthTankIds);
            }

            if(surveyListRequest.getIsFeederSurveyed() != null && feederTankIds.size()>0){
                queryString += " AND tank.tank_id IN (:feederTankIds)";
                sqlParam.addValue("feederTankIds", feederTankIds);
            }
            if(surveyListRequest.getIsCivilWorkCompleted() != null && surveyListRequest.getIsCivilWorkCompleted()!=0){
                if(civilTankIds.size()>0) {
                    queryString += " AND tank.tank_id IN (:civilTankIds)";
                    sqlParam.addValue("civilTankIds", civilTankIds);
                }
                else{
                    queryString += " AND tank.tank_id IN (0)";
                }
            }
            if(surveyListRequest.getIsFpoAdded() != null && fopTankIds.size()>0){
                queryString += " AND tank.tank_id IN (:fopTankIds)";
                sqlParam.addValue("fopTankIds", fopTankIds);
            }
            if(surveyListRequest.getIsDropped() != null && surveyListRequest.getIsDropped()!=0){
                if(droppedTankIds.size()>0) {
                    queryString += " AND tank.tank_id IN (:droppedIds)";
                    sqlParam.addValue("droppedIds", droppedTankIds);
                }
                else{
                    queryString += " AND tank.tank_id IN (0)";
                }
            }
            if(surveyListRequest.getProposedToBeDroppedTank() != null && surveyListRequest.getProposedToBeDroppedTank() !=0){
                if(proposedToBeDroppedTankIds.size()>0) {
                    queryString += " AND tank.tank_id IN (:proposedIds)";
                    sqlParam.addValue("proposedIds", proposedToBeDroppedTankIds);
                }
                else{
                    queryString += " AND tank.tank_id IN (0)";
                }
            }

        }
        if (surveyListRequest.getIsRequestForProjectDashboard()!=null){
            queryString += " ORDER BY tank.name_of_the_m_i_p ASC";
        }
        else {
            queryString += " ORDER BY tank.dept_dist_name,tank.dept_block_name,tank.dept_gp_name,tank.village_name,tank.name_of_the_m_i_p ASC";
        }


        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<TankInfo> tanklist = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
        return new PageImpl<>(tanklist, pageable, resultCount);
    }
    public Page<TankInfo> tankSearchListForWebsite(Integer blockId,Integer start,Integer length,Integer draw){
        String queryString = " ";
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        /*List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());*/

        int pageNo = start/length;
        PageRequest pageable = PageRequest.of(pageNo, length, Sort.Direction.fromString("asc"), "id");
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");


//        queryString = "SELECT id, project_id,tank_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id,village_id,village_name," +
//                " mi_division_id,mi_division_name,name_of_the_m_i_p as tankName, m_i_p_id,sub_division_id,section_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        queryString = "SELECT id, project_id,tank_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id,village_id,village_name,  " +
                "category,category_id,type,type_id,latitude,longitude,catchment_area_sqkm,certified_ayacut_kharif_ha,certified_ayacut_rabi_ha,designed_cca_kharif_ha,  " +
                "designed_cca_rabi_ha,river_basin,river_basin_id,  " +
                " mi_division_id,mi_division_name,name_of_the_m_i_p as tankName, m_i_p_id,sub_division_id,section_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

       /* if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

            if (surveyListRequest.getVillageId()!=null &&surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId()!=null && surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId()!= null && surveyListRequest.getBlockId() > 0) {
                queryString += "  AND block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId()!=null && surveyListRequest.getDistrictId() > 0) {
                queryString += " AND dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId()!=null && surveyListRequest.getDivisionId() > 0) {
                queryString += " AND mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId()!=null && surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId()!=null &&surveyListRequest.getSectionId() > 0) {
                queryString += " AND section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            *//*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*//*

            if (surveyListRequest.getId()!=null && surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId()!=null && surveyListRequest.getProjectId() > 0) {
                queryString += " AND project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }
            if (surveyListRequest.getTankId()!=null && surveyListRequest.getTankId() > 0) {
                queryString += " AND tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
        }*/
        if (blockId!= null && blockId > 0) {
            queryString += "  AND block_id=:blockId ";
            sqlParam.addValue("blockId", blockId);
        }

        queryString += " ORDER BY dept_dist_name,dept_block_name,dept_gp_name,village_name,name_of_the_m_i_p ASC";
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<TankInfo> tanklist = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
        return new PageImpl<>(tanklist, pageable, resultCount);
    }

    public List<TankInfo> tankCountByDistrict(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

     /*   PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");*/
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = "SELECT Count(id) as tankCount, dept_dist_name, dist_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

            if (surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId() > 0) {
                queryString += "  AND block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId() > 0) {
                queryString += " AND dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId() > 0) {
                queryString += " AND mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId() > 0) {
                queryString += " AND section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            /*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*/

            if (surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId() > 0) {
                queryString += " AND project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }
            if (surveyListRequest.getTankId() > 0) {
                queryString += " AND tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
        }
/*

        if (surveyListRequest.getBlockId() > 0) {
            queryString += "  GROUP BY block_id";
        }
        if (surveyListRequest.getDistrictId() > 0) {
            queryString += " GROUP BY  dist_id";
        }
        if (surveyListRequest.getDivisionId() > 0) {
            queryString += "GROUP BY  mi_division_id ";

        }
        if (surveyListRequest.getDivisionId() > 0 && surveyListRequest.getDistrictId() > 0 && surveyListRequest.getBlockId() > 0) {
            queryString += "GROUP BY  dist_id ";

        }
*/

      //  queryString += " ORDER BY dept_dist_name,dept_block_name,dept_gp_name,village_name,name_of_the_m_i_p ASC";
        queryString+=" GROUP BY dist_id,dept_dist_name ORDER BY dept_dist_name asc";

      return   namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public List<TankInfo> tankCountByBlock(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

     /*   PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");*/
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = "SELECT Count(id) as tankCount, dept_dist_name, dist_id, dept_block_name,block_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

            if (surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId() > 0) {
                queryString += "  AND block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId() > 0) {
                queryString += " AND dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId() > 0) {
                queryString += " AND mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId() > 0) {
                queryString += " AND section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            /*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*/

            if (surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId() > 0) {
                queryString += " AND project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }
            if (surveyListRequest.getTankId() > 0) {
                queryString += " AND tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
        }

        queryString+=" GROUP BY dist_id,dept_dist_name, dept_block_name,block_id ORDER BY dist_id,dept_block_name asc";

        return   namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public List<TankInfo> tankCountByCircle(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

     /*   PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");*/
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = "SELECT Count(id) as tankCount,circle_id,circle_name  FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

            if (surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId() > 0) {
                queryString += "  AND block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId() > 0) {
                queryString += " AND dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId() > 0) {
                queryString += " AND mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId() > 0) {
                queryString += " AND section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            /*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*/

            if (surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId() > 0) {
                queryString += " AND project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }
            if (surveyListRequest.getTankId() > 0) {
                queryString += " AND tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
        }

        queryString+=" GROUP BY circle_id,circle_name ORDER BY circle_name asc ";

        return   namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public List<TankInfo> tankCountByDivision(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

     /*   PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");*/
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = "SELECT Count(id) as tankCount,mi_division_id,mi_division_name,circle_id,circle_name FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

            if (surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId() > 0) {
                queryString += "  AND block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId() > 0) {
                queryString += " AND dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId() > 0) {
                queryString += " AND mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId() > 0) {
                queryString += " AND section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            /*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*/

            if (surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId() > 0) {
                queryString += " AND project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }

            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }
            if (surveyListRequest.getTankId() > 0) {
                queryString += " AND tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
        }

        queryString+=" GROUP BY mi_division_id,mi_division_name,circle_id,circle_name ORDER BY circle_id,mi_division_name asc";

        return   namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }





    //Activity Info Search List

    //Step 1 - To Find the estimates with respect to the activity id
    public List<Integer> getEstimatesByActivityId(int activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT aem.id FROM oiipcra_oltp.activity_estimate_mapping as aem " +
                " WHERE aem.status_id in(1,2) and aem.activity_id =:activityId";
        sqlParam.addValue("activityId", activityId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTendersByEstimateIds(List<Integer> estimatesByActivityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id FROM oiipcra_oltp.tender_m WHERE estimate_id IN(:estimatesByActivityId)";
        sqlParam.addValue("estimatesByActivityId", estimatesByActivityId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByTenderId(List<Integer> tenderIdsByEstimateIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank_id FROM oiipcra_oltp.work_project_mapping WHERE tender_id IN(:tenderIdsByEstimateIds)";
        sqlParam.addValue("tenderIdsByEstimateIds", tenderIdsByEstimateIds);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIds(List<Integer> estimatesByActivityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE tank_id IS not null AND estimate_id IN(:estimatesByActivityId)";
        sqlParam.addValue("estimatesByActivityId", estimatesByActivityId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getDistIds(List<Integer> estimatesByActivityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE tank_id IS null AND estimate_id IN(:estimatesByActivityId)";
        sqlParam.addValue("estimatesByActivityId", estimatesByActivityId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getBlockIds(List<Integer> estimatesByActivityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT block_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE tank_id IS null AND estimate_id IN(:estimatesByActivityId)";
        sqlParam.addValue("estimatesByActivityId", estimatesByActivityId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByDistIds(List<Integer> distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank_id FROM oiipcra_oltp.tank_m_id WHERE dist_id IN(:distId)";
        sqlParam.addValue("distId", distId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByBlockIds(List<Integer> blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank_id FROM oiipcra_oltp.tank_m_id WHERE block_id IN(:blockId)";
        sqlParam.addValue("blockId", blockId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }


    public Page<TankInfo> getTankSearchListWithTankIds(SurveyListRequest surveyListRequest, List<Integer> allTankIds) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

        PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = "SELECT id, project_id,tank_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id,village_id,village_name," +
                " mi_division_id,mi_division_name,name_of_the_m_i_p as tankName, m_i_p_id,sub_division_id,section_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        if (allTankIds.size() > 0) {
            queryString += " AND tank_id IN(:allTankIds)";
            sqlParam.addValue("allTankIds", allTankIds);
        }

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }

            if (surveyListRequest.getVillageId() != null && surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId() != null && surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }
            if (surveyListRequest.getBlockId() != null && surveyListRequest.getBlockId() > 0) {
                queryString += "  AND block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId() != null && surveyListRequest.getDistrictId() > 0) {
                queryString += " AND dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId() != null && surveyListRequest.getDivisionId() > 0) {
                queryString += " AND mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId() != null && surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId() != null && surveyListRequest.getSectionId() > 0) {
                queryString += " AND section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            /*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*/

            if (surveyListRequest.getId() != null && surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }

            if (surveyListRequest.getProjectId() != null && surveyListRequest.getProjectId() > 0) {
                queryString += " AND project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }

        }

        queryString += " ORDER BY dept_dist_name,dept_block_name,dept_gp_name,village_name,name_of_the_m_i_p ASC";
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<TankInfo> tanklist = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
        return new PageImpl<>(tanklist, pageable, resultCount);
    }


    @Override
    public Page<invoiceListingInfo> getInvoiceList(InvoiceListRequestDto invoiceListRequestDto, List<Integer> terminalList) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(invoiceListRequestDto.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("invoice_date");
        userListRequest.setSize(14);

        PageRequest pageable = PageRequest.of(invoiceListRequestDto.getPage(), invoiceListRequestDto.getSize(), Sort.Direction.fromString(invoiceListRequestDto.getSortOrder()), invoiceListRequestDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "invoice_date");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        Integer  resultCount = 0;
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(invoiceListRequestDto.getUserId());
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getSize(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }

        UserInfoDto userInfoById = userQryRepo.getUserById(invoiceListRequestDto.getUserId());

        queryString += "select  distinct invoice.id,invoice_no, invoice_date,invoice_amount,invoice.contract_id,cm.contract_number,work.name as workType,   " +
                "tm.id as tenderId, tm.bid_id,tn.id as workId, agency.name as agencyName, status.name as invoiceStatus   " +
                "from oiipcra_oltp.invoice_m as invoice  " +
                "left join oiipcra_oltp.contract_m as cm on invoice.contract_id =cm.id    " +
                "left join oiipcra_oltp.contract_mapping as mapping on mapping.contract_id = cm.id    " +
                "left join oiipcra_oltp.type_m as type on type.id = cm.contract_type_id    " +
                "left join oiipcra_oltp.invoice_status as status on invoice.status = status.id    " +
                "left join oiipcra_oltp.expenditure_mapping as map on map.invoice_id = invoice.id   " +
                "left join oiipcra_oltp.expenditure_data as ed on map.expenditure_id = ed.id  " +
                "left join oiipcra_oltp.tender_m as tm on mapping.tender_id = tm.id   " +
                "left join oiipcra_oltp.tender_notice as tn on mapping.tender_notice_id = tn.id   " +
                "left join oiipcra_oltp.agency_m as agency on agency.id= invoice.agency_id   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = map.tank_id   " +
                "left join oiipcra_oltp.work_type_m as work on cm.contract_type_id = work.id   " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id =mapping.block_id   " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id = mapping.dist_id   " +
                "left join oiipcra_oltp.mi_division_m as div on div.id= mapping.division_id  " +
                "where invoice.is_active=true  ";

        if (invoiceListRequestDto.getActivityId() != null && invoiceListRequestDto.getActivityId() > 0) {
            queryString += " AND  mapping.activity_id=:activityId ";
            sqlParam.addValue("activityId", invoiceListRequestDto.getActivityId());
        }

        if (invoiceListRequestDto.getEstimateId() != null && invoiceListRequestDto.getEstimateId() > 0) {
            queryString += " AND  mapping.estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", invoiceListRequestDto.getEstimateId());
        }

        if (invoiceListRequestDto.getBlockId() != null && invoiceListRequestDto.getBlockId() > 0) {
            queryString += " AND  mapping.block_id=:blockId ";
            sqlParam.addValue("blockId", invoiceListRequestDto.getBlockId());
        }

        if (invoiceListRequestDto.getDistId() != null && invoiceListRequestDto.getDistId() > 0) {
            queryString += " AND  mapping.dist_id=:distId ";
            sqlParam.addValue("distId", invoiceListRequestDto.getDistId());
        }

        if (invoiceListRequestDto.getDivisionId() != null && invoiceListRequestDto.getDivisionId() > 0) {
            queryString += " AND  mapping.division_id=:division_id ";
            sqlParam.addValue("division_id", invoiceListRequestDto.getDivisionId());
        }


        if (invoiceListRequestDto.getWorkId() != null && invoiceListRequestDto.getWorkId() > 0) {
            queryString += " AND tn.id=:workId ";
            sqlParam.addValue("workId", invoiceListRequestDto.getWorkId());
        }

        if (invoiceListRequestDto.getWorkTypeId() != null && invoiceListRequestDto.getWorkTypeId() > 0) {
            queryString += " AND work.id=:workTypeId ";
            sqlParam.addValue("workTypeId", invoiceListRequestDto.getWorkTypeId());
        }
        if (invoiceListRequestDto.getContractId() != null && invoiceListRequestDto.getContractId() > 0) {
            queryString += " AND invoice.contract_id=:contractId ";
            sqlParam.addValue("contractId", invoiceListRequestDto.getContractId());
        }
        if (invoiceListRequestDto.getAgencyId() != null && invoiceListRequestDto.getAgencyId() > 0) {
            queryString += " AND invoice.agency_id=:agencyId ";
            sqlParam.addValue("agencyId", invoiceListRequestDto.getAgencyId());
        }
        if (invoiceListRequestDto.getBidId() != null && invoiceListRequestDto.getBidId() > 0) {
            queryString += " AND tm.bid_id=:bidId ";
            sqlParam.addValue("bidId", invoiceListRequestDto.getBidId());
        }
        if (invoiceListRequestDto.getStatusId() != null && invoiceListRequestDto.getStatusId() > 0) {
            queryString += " AND invoice.status=:statusId ";
            sqlParam.addValue("statusId", invoiceListRequestDto.getStatusId());
        }

        if (invoiceListRequestDto.getExpenditureId() != null && invoiceListRequestDto.getExpenditureId() > 0) {
            queryString += " AND ed.id=:expenditureId ";
            sqlParam.addValue("expenditureId", invoiceListRequestDto.getExpenditureId());
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (invoiceListRequestDto.getUploadFromDate() != null && !invoiceListRequestDto.getUploadFromDate().isEmpty()) {
            queryString += " AND date(invoice.invoice_date) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(invoiceListRequestDto.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (invoiceListRequestDto.getUploadToDate() != null && !invoiceListRequestDto.getUploadToDate().isEmpty()) {
            queryString += " AND date(invoice.invoice_date) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(invoiceListRequestDto.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }
        if (invoiceListRequestDto.getTankId() > 0) {
            queryString += " AND map.tank_id=:tankId ";
            sqlParam.addValue("tankId", invoiceListRequestDto.getTankId());
        }

        //Check SPMU
        if (userInfoById.getRoleId() > 4) {
            if (userIdList != null) {
                queryString += " AND invoice.created_by IN (:userIdList)";
                sqlParam.addValue("userIdList", userIdList);
            }
        }


       /* if(terminalList.size() > 0){
            queryString += " AND ed.activity_id IN(:terminalLIst)";
            sqlParam.addValue("terminalLIst", terminalList);
        }*/
        queryString += " ORDER BY invoice." + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<invoiceListingInfo>invoiceListingInfos =(namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(invoiceListingInfo.class)));
        return new PageImpl<>(invoiceListingInfos,pageable,resultCount);
    }

    public List<TankInfo> getTankNameAndProjectId(int userId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(userId);
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(userId);

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = "SELECT id, project_id,tank_id,name_of_the_m_i_p as tankName,nwbis_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }
            queryString += " ORDER BY name_of_the_m_i_p ASC";
        }
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));

    }

    public List<TankInfo> getSurveyTankNameAndProjectId(int userId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(userId);
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(userId);

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        queryString = " SELECT tank.id, tank.project_id,tank.tank_id,tank.name_of_the_m_i_p as tankName FROM oiipcra_oltp.tank_m_id as tank " +
                " WHERE tank.id IN (SELECT tank_id from oiipcra_oltp.tank_survey_data where is_active=true) ";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDistrict_id()));
                    queryString += " And dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:

                default:
                    break;
            }
        }
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));

    }

    public List<TankInfo> getTankDetailsById(int id, int flagId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = null;
        if (flagId == 0) {
            queryString = "SELECT tmid.id, project_id, tmid.dept_dist_name, tmid.dist_id, dept_block_name, tmid.block_id, dept_gp_name, gp_id,village_id,village_name, name_of_the_m_i_p as tankName, m_i_p_id," +
                    " latitude, longitude, category, category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha," +
                    " certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id, water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m," +
                    " proposed_to_be_dropped as proposedToBeDropped,is_dropped as isDropped, " +
                    " type_of_dam_weir, type_of_dam_weir_id, remarks, tmid.is_active, created_by, created_on, updated_by, updated_on, tank_id, revised_start_date, revised_end_date," +
                    " no_of_benificiaries, geom ,tmid.sub_division_id,bm.dept_mi_sub_division_name as subDivisionName,tmid.section_id,bm.dept_mi_section_name as sectionName,is_civil_work_completed as civilWorkCompleted " +
                    " FROM oiipcra_oltp.tank_m_id as tmid " +
                    " left join oiipcra_oltp.block_mapping as bm on tmid.block_id=bm.block_id " +
                    " where tmid.id=:id";
        } else {
            queryString = "SELECT public.st_asgeojson(geom) as geojson, tmid.id, project_id, tmid.dept_dist_name, tmid.dist_id, dept_block_name, tmid.block_id, dept_gp_name, gp_id,village_id,village_name, name_of_the_m_i_p as tankName, m_i_p_id," +
                    " latitude, longitude, category, category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha," +
                    " certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id, water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m," +
                    " type_of_dam_weir, type_of_dam_weir_id, remarks, tmid.is_active, created_by, created_on, updated_by, updated_on, tank_id, revised_start_date, revised_end_date," +
                    " no_of_benificiaries, geom ,tmid.sub_division_id,bm.dept_mi_sub_division_name as subDivisionName,tmid.section_id,bm.dept_mi_section_name as sectionName,is_civil_work_completed as civilWorkCompleted " +
                    " FROM oiipcra_oltp.tank_m_id as tmid" +
                    " left join oiipcra_oltp.block_mapping as bm on tmid.block_id=bm.block_id" +
                    " where tmid.id=:id";
        }
        sqlParam.addValue("id", id);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }

    public List<TankInfo> getTankDetailsByProjectId(int projectId, int flagId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = null;
        if (flagId == 0) {
            queryString = "SELECT tmid.id, project_id, tmid.dept_dist_name, tmid.dist_id, dept_block_name, tmid.block_id, dept_gp_name, gp_id,village_id,village_name, name_of_the_m_i_p as tankName, m_i_p_id," +
                    " latitude, longitude, category, category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha," +
                    " certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id, water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m," +
                    " type_of_dam_weir, type_of_dam_weir_id, remarks, tmid.is_active, created_by, created_on, updated_by, updated_on, tank_id, revised_start_date, revised_end_date," +
                    " no_of_benificiaries, geom ,tmid.sub_division_id,bm.dept_mi_sub_division_name as subDivisionName,tmid.section_id,bm.dept_mi_section_name as sectionName" +
                    " FROM oiipcra_oltp.tank_m_id as tmid " +
                    " left join oiipcra_oltp.block_mapping as bm on tmid.block_id=bm.block_id " +
                    " where project_id=:projectId";
        } else {
            queryString = "SELECT public.st_asgeojson(geom) as geojson, tmid.id, project_id, tmid.dept_dist_name, tmid.dist_id, dept_block_name, tmid.block_id, dept_gp_name, gp_id,village_id,village_name, name_of_the_m_i_p as tankName, m_i_p_id," +
                    " latitude, longitude, category, category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha," +
                    " certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id, water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m," +
                    " type_of_dam_weir, type_of_dam_weir_id, remarks, tmid.is_active, created_by, created_on, updated_by, updated_on, tank_id, revised_start_date, revised_end_date," +
                    " no_of_benificiaries, geom ,tmid.sub_division_id,bm.dept_mi_sub_division_name as subDivisionName,tmid.section_id,bm.dept_mi_section_name as sectionName" +
                    " FROM oiipcra_oltp.tank_m_id as tmid" +
                    " left join oiipcra_oltp.block_mapping as bm on tmid.block_id=bm.block_id" +
                    " where project_id=:projectId";
        }
        sqlParam.addValue("projectId", projectId);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }

    public TankSurveyInfo getSurveyInfoGeoJsonById(int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String imagePath = accessImagePath + id + "/";
        String queryString = "SELECT  public.st_asgeojson(survey.geom) as geojson, survey.id, survey.tank_id, survey.project_id, survey.catchment_area_sqkm as catchmentArea, survey.cca_kharif_ha as ccakharif, survey.cca_rabi_ha as ccaRabi, survey.water_spread_area_ha as waterSpreadArea," +
                " survey.tank_water_level_max_meter as tankWaterLevelMax, survey.tank_water_level_min_meter as tankWaterLevelMin, survey.ground_water_level_meter as groundWaterLevel, survey.turbidity, solar_pump_installed, aquatic_vegetation_cover_percent," +
                " status_of_tank, no_of_beneficiary, recharge_staff_installation as rechargeShaftInstallation, no_of_recharge_staff_installed as noOfRechargeShaftInstalled, embankment, usage, training_conducted, no_of_trainee," +
                " longitude_surveyed, latitude_surveyed,altitude,accuracy, surveyed_by_dept, progress_status, approved_by, approved_on, survey.created_by, survey.created_on, survey.updated_by, survey.updated_on," +
                " CONCAT ('" + imagePath + "',survey.surveyor_image) AS surveyorImage, CONCAT ('" + imagePath + "',survey.training_image) AS trainingImage, survey.state_id, survey.district_id,village.district_name, survey.block_id,village.block_name, survey.gp_id,village.grampanchayat_name as gpName, survey.village_id," +
                " village.revenue_village_name as villageName,progress_status_id, approve.name as progressStatus, survey.division_id, survey.sub_division_id, survey.section_id, survey.is_mobile_tagged," +
                " tank.name_of_the_m_i_p as tankName" +
                " FROM oiipcra_oltp.tank_survey_data as survey" +
                " left join oiipcra_oltp.tank_m_id as tank on survey.tank_id = tank.tank_id" +
                " left join oiipcra_oltp.village_boundary as village on survey.village_id=village.village_id" +
                " left join oiipcra_oltp.approval_status_m as approve on survey.progress_status_id = approve.id" +
                " left join oiipcra_oltp.mi_division_m as division on survey.division_id = division.mi_division_id" +
                " left join oiipcra_oltp.mi_subdivision_m as subdivision on survey.sub_division_id = subdivision.mi_sub_division_id" +
                " left join oiipcra_oltp.mi_section_m as sectionm on survey.section_id = sectionm.section_id where survey.id=:id and survey.is_active=true";

        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(TankSurveyInfo.class));
    }

    public TankSurveyInfoResponse getSurveyInfoById(int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String imagePath = accessImagePath + id + "/";
        String queryString = "SELECT survey.id, survey.tank_id,tank.name_of_the_m_i_p as tankName, survey.project_id, survey.catchment_area_sqkm as catchmentArea, survey.cca_kharif_ha as ccaKharif, survey.cca_rabi_ha as ccaRabi, survey.water_spread_area_ha as waterSpreadArea, survey.tank_water_level_max_meter as tankWaterLevelMax, survey.tank_water_level_min_meter as tankWaterLevelMin," +
                " survey.ground_water_level_meter as groundWaterLevel,survey.turbidity, survey.solar_pump_installed, survey.aquatic_vegetation_cover_percent as aquaticVegetationCover, survey.status_of_tank, survey.no_of_beneficiary, survey.recharge_staff_installation as rechargeShaftInstallation, survey.no_of_recharge_staff_installed as noOfRechargeShaftInstalled," +
                " survey.embankment, survey.usage,survey.training_conducted, survey.no_of_trainee, survey.longitude_surveyed, survey.latitude_surveyed, survey.surveyed_by_dept, survey.progress_status, survey.approved_by, userm1.name as approverName, survey.approved_on, survey.is_active, survey.created_by,userm.name as surveyedBy, survey.created_on, survey.updated_by,survey.updated_on," +
                " CONCAT ('" + imagePath + "',survey.surveyor_image) AS surveyorImage, CONCAT ('" + imagePath + "',survey.training_image) AS trainingImage, state_id, survey.district_id,village.district_name, survey.block_id,village.block_name, survey.gp_id,village.grampanchayat_name as gpName, survey.village_id,village.revenue_village_name as villageName, survey.progress_status_id,approve.name as approvalStatus," +
                " survey.division_id,survey.sub_division_id, survey.section_id, survey.is_mobile_tagged,survey.altitude,survey.accuracy," +
                " division.mi_division_name as divisionName,subdivision.mi_sub_division_name as subDivisionName,sectionm.mi_section_name as sectionName" +
                //" bm.dept_mi_division_name as divisionName,bm.dept_mi_sub_division_name as subDivisionName,bm.dept_mi_section_name as sectionName"+
                " FROM oiipcra_oltp.tank_survey_data as survey" +
                " left join oiipcra_oltp.tank_m_id as tank on survey.tank_id = tank.tank_id" +
                " left join oiipcra_oltp.village_boundary as village on survey.village_id=village.village_id" +
                " left join oiipcra_oltp.approval_status_m as approve on survey.progress_status_id = approve.id" +
             /*   " left join oiipcra_oltp.mi_division_m as division on survey.division_id = division.mi_division_id" +
                " left join oiipcra_oltp.mi_subdivision_m as subdivision on survey.sub_division_id = subdivision.mi_sub_division_id" +
                " left join oiipcra_oltp.mi_section_m as sectionm on survey.section_id = sectionm.section_id" +*/
                //" left join oiipcra_oltp.block_mapping as bm on survey.section_id=bm.section_id"+
                " left join oiipcra_oltp.user_m as userm on survey.created_by = userm.id" +
                " left join oiipcra_oltp.user_m as userm1 on survey.approved_by = userm1.id" +
                " left join oiipcra_oltp.mi_division_m as division on survey.division_id = division.mi_division_id" +
                " left join oiipcra_oltp.mi_subdivision_m as subdivision on survey.sub_division_id = subdivision.mi_sub_division_id" +
                " left join oiipcra_oltp.mi_section_m as sectionm on survey.section_id = sectionm.section_id" +
                " where survey.id=:id and survey.is_active=true";

        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(TankSurveyInfoResponse.class));

    }

    /**
     * update tank survey data by jdbc template
     */
    @Override
    @Transactional
    public int updateSurveyTankData(TankSurveyInfo tankSurveyInfo, int surveyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String UPDATE_SURVEY_DATA = "UPDATE oiipcra_oltp.tank_survey_data" +
                " SET catchment_area_sqkm=:catchment_area_sqkm, cca_kharif_ha=:cca_kharif_ha, cca_rabi_ha=:cca_rabi_ha, water_spread_area_ha=:water_spread_area_ha, tank_water_level_max_meter=:tank_water_level_max_meter, tank_water_level_min_meter=:tank_water_level_min_meter, " +
                " ground_water_level_meter=:ground_water_level_meter, turbidity=:turbidity, solar_pump_installed=:solar_pump_installed, aquatic_vegetation_cover_percent=:aquatic_vegetation_cover_percent, status_of_tank=:status_of_tank, no_of_beneficiary=:no_of_beneficiary," +
                " recharge_staff_installation=:recharge_staff_installation, no_of_recharge_staff_installed=:no_of_recharge_staff_installed, embankment=:embankment, usage=:usage, training_conducted=:training_conducted, no_of_trainee=:no_of_trainee,approved_by=:approved_by, approved_on=NOW()," +
                " updated_by=:updated_by, updated_on=NOw(),progress_status_id=:progress_status_id,approval_level=:approval_level" +
                " WHERE id=:surveyId and is_active=true";

        sqlParam.addValue("surveyId", surveyId);
        sqlParam.addValue("catchment_area_sqkm", tankSurveyInfo.getCatchmentArea());
        sqlParam.addValue("cca_kharif_ha", tankSurveyInfo.getCcaKharif());
        sqlParam.addValue("cca_rabi_ha", tankSurveyInfo.getCcaRabi());
        sqlParam.addValue("water_spread_area_ha", tankSurveyInfo.getWaterSpreadArea());
        sqlParam.addValue("tank_water_level_max_meter", tankSurveyInfo.getTankWaterLevelMax());
        sqlParam.addValue("tank_water_level_min_meter", tankSurveyInfo.getTankWaterLevelMin());
        sqlParam.addValue("ground_water_level_meter", tankSurveyInfo.getGroundWaterLevel());
        sqlParam.addValue("turbidity", tankSurveyInfo.getTurbidity());
        sqlParam.addValue("solar_pump_installed", tankSurveyInfo.isSolarPumpInstalled());
        sqlParam.addValue("aquatic_vegetation_cover_percent", tankSurveyInfo.getAquaticVegetationCover());
        sqlParam.addValue("status_of_tank", tankSurveyInfo.getStatusOfTank());
        sqlParam.addValue("no_of_beneficiary", tankSurveyInfo.getNoOfBeneficiary());
        sqlParam.addValue("recharge_staff_installation", tankSurveyInfo.isRechargeShaftInstallation());
        sqlParam.addValue("no_of_recharge_staff_installed", tankSurveyInfo.getNoOfRechargeShaftInstalled());
        sqlParam.addValue("embankment", tankSurveyInfo.isEmbankment());
        sqlParam.addValue("usage", tankSurveyInfo.getUsage());
        sqlParam.addValue("progress_status_id",tankSurveyInfo.getProgressStatusId());
        sqlParam.addValue("training_conducted", tankSurveyInfo.isTrainingConducted());
        sqlParam.addValue("no_of_trainee", tankSurveyInfo.getNoOfTrainee());
        sqlParam.addValue("approved_by", tankSurveyInfo.getUpdatedBy());
        sqlParam.addValue("updated_by", tankSurveyInfo.getUpdatedBy());
        sqlParam.addValue("approval_level", tankSurveyInfo.getApprovalLevel());
        return namedJdbc.update(UPDATE_SURVEY_DATA, sqlParam);
    }

    public Page<WorkProgressInfo> getWorkProgressList(WorkProgressDto workProgressDto, List<Integer> activityIds, List<Integer> estimateIds, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId,List<Integer>contractIds,List<Integer>tenderIds) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(workProgressDto.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("id");
        userListRequest.setSize(14);

        PageRequest pageable = PageRequest.of(workProgressDto.getPage(), workProgressDto.getSize(), Sort.Direction.fromString(workProgressDto.getSortOrder()), workProgressDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";
        List<Integer> userIdList = new ArrayList<>();
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getSize(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }

        UserInfoDto userInfoById = userQryRepo.getUserById(workProgressDto.getUserId());

        queryString += " select tm.id as workProgressId ,mapping.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id as projectId,mapping.dist_id,dist.district_name as distName,   mapping.block_id,block.block_name as blockName,tm.id as tenderId,tm.bid_id, tn.id as workId " +
                " from oiipcra_oltp.tender_m as tm  " +
                " left join oiipcra_oltp.tender_notice as tn on tn.tender_id = tm.id   " +
                " left join oiipcra_oltp.tender_notice_level_mapping as mapping on mapping.tender_notice_id = tn.id " +
                " left join oiipcra_oltp.tank_m_id as tank on tank.id = mapping.tank_id   " +
                " left join oiipcra_oltp.block_boundary as block on block.block_id=mapping.block_id  " +
                " left join oiipcra_oltp.district_boundary as dist on dist.dist_id = mapping.dist_id  " +
                " left join oiipcra_oltp.contract_mapping as cm on cm.tender_id = tm.id " +
                " left join oiipcra_oltp.contract_m as contract on contract.id = cm.contract_id " +
                " Where tm.is_active=true  ";

 /*       queryString += "SELECT tank.id, tank.project_id,tank.tank_id,tank.dept_dist_name as distName, tank.dist_id, tank.dept_block_name as blockName, tank.block_id, tank.dept_gp_name, tank.gp_id,tank.village_id,tank.village_name, " +
                "tank.mi_division_id,tank.mi_division_name,tank.name_of_the_m_i_p as tankName,tank. m_i_p_id,tank.sub_division_id,tank.section_id FROM oiipcra_oltp.tank_m_id as tank  " +
                "WHERE tank.is_active = true";*/

        if (activityIds != null && !activityIds.isEmpty()) {
            queryString += " AND contract.activity_id IN (:activityIds)";
            sqlParam.addValue("activityIds", activityIds);
        }
        if (estimateIds != null && !estimateIds.isEmpty()) {
            queryString += " AND contract.estimate_id IN (:estimateIds)";
            sqlParam.addValue("estimateIds", estimateIds);
        }

        if (workProgressDto.getTankId() > 0) {
            queryString += " AND tank.id=:tankId ";
            sqlParam.addValue("tankId", workProgressDto.getTankId());
        }

        if(tenderIds!=null && tenderIds.size() > 0){
            queryString += " AND tank.tank_id IN (:tenderIds)";
            sqlParam.addValue("tenderIds",tenderIds);
        }

        if(contractIds != null && contractIds.size() > 0){
            queryString += " AND tank.tank_id IN (:contractIds)";
            sqlParam.addValue("contractIds",contractIds);
        }
        if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
            queryString += " AND tank.tank_id IN (:tankIdsByExpenditureId)";
            sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
        }

        if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
            queryString += " AND tank.tank_id IN (:tankIdsByInvoiceId)";
            sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
        }

        if(workProgressDto.getContractId()!=null && workProgressDto.getContractId()>0){
            queryString += " AND contract.id=:contractId";
            sqlParam.addValue("contractId", workProgressDto.getContractId());
        }

        if (userInfoById.getRoleId() > 4) {
            if (userInfoById.getSurveyor()) {
                queryString += " AND tank.created_by IN (:userId)";
                sqlParam.addValue("userId", workProgressDto.getUserId());
            } else {
                queryString += " AND tank.created_by IN (:userIdList)";
                sqlParam.addValue("userIdList", userIdList);
            }
        }


        queryString += " ORDER BY tank." + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();


        List<WorkProgressInfo> workProgress = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(WorkProgressInfo.class));
        return new PageImpl<>(workProgress, pageable, resultCount);


    }

    public List<Tender> getAllClosedBidId() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAllClosedBidId = "SELECT tm.id, tm.bid_id from oiipcra_oltp.tender_m as tm where tm.tender_status = 3";
        return namedJdbc.query(getAllClosedBidId, sqlParam, new BeanPropertyRowMapper<>(Tender.class));
    }

    public WorkStatusDto getWorkStatusDetails(Integer tenderId, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getWorkStatusDetails = "SELECT cm.id,cm.finyr_id,year.name as finYear ,mapping.dist_id, dist.district_name,mapping.division_id,  " +
                "div.mi_division_name as divisionName, cm.approval_order, cm.tachnical_sanction_no,   " +
                "tn.project_id, project.name as projectScheme, cm.work_description, mapping.tender_notice_id as workId,mapping.tender_id,tm.bid_id as rfbId,cm.estimated_cost, agency.name as agencyName,agency.pan_no, agency.phone, cm.awarded_as,award.name as awardedType,  cm.loa_issued_no, cm.loa_issued_date, cm.contract_amount+(cm.contract_amount*cm.gst/100)as agreementAmount,cm.actual_date_of_completion,  cm.created_on from oiipcra_oltp.contract_m as cm  " +
                "left join oiipcra_oltp.contract_mapping as mapping on mapping.contract_id = cm.id   " +
                "left join oiipcra_oltp.fin_year_m as year on year.id = cm.finyr_id   " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id =mapping.dist_id   " +
                "left join oiipcra_oltp.mi_division_m as div on div.id =mapping.division_id   " +
                "left join oiipcra_oltp.award_type as award on award.id = cm.awarded_as   " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = mapping.tender_notice_id    " +
                "left join oiipcra_oltp.project_m as project on project.id = tn.project_id   " +
                "left join oiipcra_oltp.tender_m as tm on tm.id =mapping.tender_id   " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = cm.agency_id   " +
                "WHERE mapping.tender_id =:tenderId and mapping.tender_notice_id=:workId ";

        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("workId", workId);
        return namedJdbc.queryForObject(getWorkStatusDetails, sqlParam, new BeanPropertyRowMapper<>(WorkStatusDto.class));
    }

    public WorkStatusDto getWorkStatusById(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getWorkStatusById = "SELECT cm.id,cm.finyr_id,year.name as finYear ,mapping.dist_id, dist.district_name,mapping.division_id, div.mi_division_name as divisionName, " +
                "cm.approval_order, cm.tachnical_sanction_no, tn.project_id, project.name as projectScheme, cm.work_description, cm.work_id,cm.tender_id,  " +
                "tm.bid_id as rfbId,cm.estimated_cost, agency.name as agencyName,agency.pan_no, agency.phone, cm.awarded_as,award.name as awardedType,  " +
                "cm.loa_issued_no, cm.loa_issued_date, cm.contract_amount+(cm.contract_amount*cm.gst/100)as agreementAmount,cm.actual_date_of_completion,  " +
                "cm.created_on from oiipcra_oltp.contract_m as cm  " +
                "left join oiipcra_oltp.contract_mapping as mapping on mapping.contract_id = cm.id  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id = cm.finyr_id  " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id =mapping.dist_id  " +
                "left join oiipcra_oltp.mi_division_m as div on div.id =mapping.division_id  " +
                "left join oiipcra_oltp.award_type as award on award.id = cm.awarded_as  " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = mapping.tender_notice_id  " +
                "left join oiipcra_oltp.project_m as project on project.id = tn.project_id  " +
                "left join oiipcra_oltp.tender_m as tm on tm.id =mapping.tender_id  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = cm.agency_id  " +
                "WHERE cm.id =:id  ";

        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(getWorkStatusById, sqlParam, new BeanPropertyRowMapper<>(WorkStatusDto.class));
    }

    public TenderDto checkBidId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String checkBidId = "select * from oiipcra_oltp.tender_m as tm  " +
                "where tm.id =:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(checkBidId, sqlParam, new BeanPropertyRowMapper<>(TenderDto.class));
    }

    public List<ContractMappingDto> existBidId(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String existBidId = "select * from oiipcra_oltp.contract_mapping as cm  " +
                "where cm.tender_id =:id";
        sqlParam.addValue("id", id);
        return namedJdbc.query(existBidId, sqlParam, new BeanPropertyRowMapper<>(ContractMappingDto.class));
    }

    public List<Integer> getContractIdsByTenderIds(List<Integer> tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract_id FROM oiipcra_oltp.contract_mapping WHERE tender_id IN(:tenderId)";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByContractId(int contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT tank_id FROM oiipcra_oltp.contract_mapping WHERE contract_id =:contractId and is_active= true ";
        sqlParam.addValue("contractId", contractId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByExpenditureId(int expenditureId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT tank_id FROM oiipcra_oltp.expenditure_mapping WHERE expenditure_id =:expenditureId and is_active= true ";
        sqlParam.addValue("expenditureId", expenditureId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Tender> getAllResultDeclaredBidId() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllResultDeclaredBidId = "SELECT tm.id, tm.bid_id from oiipcra_oltp.tender_m as tm where tm.tender_status = 4";
        return namedJdbc.query(GetAllResultDeclaredBidId, sqlParam, new BeanPropertyRowMapper<>(Tender.class));
    }

    public List<Integer> getActivityIdsByTankId(WorkProgressDto workProgressDto) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select activity_id from oiipcra_oltp.activity_estimate_mapping as estimate " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tank on estimate.id=tank.estimate_id " +
                "where tank.tank_id=:tankId and tank.is_active=true";
        sqlParam.addValue("tankId", workProgressDto.getTankId());
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsByInvoiceId(int invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT map.tank_id FROM oiipcra_oltp.expenditure_mapping as map  " +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.id  =map.invoice_id   " +
                "where invoice.id =:invoiceId and invoice.is_active=true ";
        sqlParam.addValue("invoiceId", invoiceId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getTankIdsByDeptId(int deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct tank_id from oiipcra_oltp.issue_tracker where dept_id in (:deptId) ";
        sqlParam.addValue("deptId", deptId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<TankInfo> getTankDetailsById(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = null;
        queryString = "SELECT tmid.id, project_id, tmid.dept_dist_name, tmid.dist_id, dept_block_name, tmid.block_id, dept_gp_name, gp_id,village_id,village_name, name_of_the_m_i_p as tankName, m_i_p_id,  " +
                "latitude, longitude, category, category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha,   " +
                "certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id, water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m,type_of_dam_weir, type_of_dam_weir_id, remarks, tmid.is_active, created_by, created_on, updated_by, updated_on, tank_id, revised_start_date, revised_end_date,   " +
                "no_of_benificiaries, geom ,tmid.sub_division_id,bm.dept_mi_sub_division_name as subDivisionName,tmid.section_id,bm.dept_mi_section_name as sectionName   " +
                "FROM oiipcra_oltp.tank_m_id as tmid  " +
                "left join oiipcra_oltp.block_mapping as bm on tmid.block_id=bm.block_id " +
                "WHERE tmid.id=:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public List<ContractMappingDto> getContractMapping(int contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = null;
        queryString = "Select cm.tank_id as tankId,tank.name_of_the_m_i_p as tankName,tender.id as tenderId,tender.bid_id as bidId from oiipcra_oltp.contract_mapping as cm " +
                "left join oiipcra_oltp.tender_m as tender on cm.tender_id=tender.id "+
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=cm.tank_id where cm.is_active=true and cm.contract_id=:contractId";
        sqlParam.addValue("contractId", contractId);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(ContractMappingDto.class));
    }

    public List<TankCropCycleMasterDto> getTankCropDetailsByProjectId(Integer projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project=String.valueOf(projectId);
//        String qry = "SELECT crop.id,crop.year_id,crop.single_crop,crop.double_crop,crop.project_id,crop.tripple_crop,  " +
//                " crop.command_area_ha,crop.ci_gee,ci_gis,crop.ci4_permanent,year.name as yearName ,tank.name_of_the_m_i_p as tankName  " +
//                " from oiipcra_oltp.tank_crop_cycle_master as crop  " +
//                " left join oiipcra_oltp.fin_year_m as year on year.id=crop.year_id  " +
//                " left join oiipcra_oltp.tank_m_id as tank on tank.project_id=crop.project_id where crop.project_Id=:projectId ";

        String qry = "select cropArea.crop_cycle as yearName,coalesce(cropArea.singleCrop,0.0) as singleCrop,coalesce(cropArea.doubleCrop,0.0) as doubleCrop, " +
                "coalesce(cropArea.tripleCrop,0.0) as tripleCrop,ayacutBoundary.area as commandAreaHa,  " +
                "coalesce(((coalesce(cropArea.singleCrop,0.0)+coalesce(cropArea.doubleCrop,0.0)+ coalesce(cropArea.tripleCrop,0.0))*100/ayacutBoundary.area),0.0) as ciGis "+
                "from (select distinct crop_cycle,project_id,sum(area) filter (where gridcode=10) as singleCrop,sum(area) filter (where gridcode=20) as doubleCrop,  " +
                "sum(area) filter (where gridcode=30) as tripleCrop  " +
                "from oiipcra_oltp.crop_cycle_ayacut group by  crop_cycle,project_id) as cropArea  " +
                "left join oiipcra_oltp.ayacut_area_boundary as ayacutBoundary on ayacutBoundary.project_id=cropArea.project_id  " +
                "where ayacutBoundary.project_id=:projectId  ";

        sqlParam.addValue("projectId",project);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(TankCropCycleMasterDto.class));
    }

    public MasterData538Dto getMasterDataByProjectId(Integer projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry="SELECT id, project_id, percentage_filling_status_as_per_google_earth, tanks_takenup_in_baseline_assessment_53,  " +
                "category, type, catchment_in_sq_km, dcca_khariff_ha, dcca_rabi_ha, ca_khariff, ca_rabi, river_basin,  " +
                "length_left_canal_km, length_right_canal_km, no_of_pps, no_of_pps_for_which_election_completed, command_area_delineated_ha,  " +
                "date_of_election_of_pp, name_of_pp, president_of_pp, secretary_of_pp, status_of_pp, cluster, occ_dpr_recieved, dpr_acceptable_or_not,  " +
                "unit_cost_per_ha_for_dpr, total_amount_for_dpr, technical_sanction_no_and_date, hydrological_data_pending, hydrological_assesment_done,  " +
                "bid_id, package_id, total_cost_of_package_in_rs, tender_prticipation_status, rate_quoted, tender_premium,  " +
                "ongoing_agreement_amount_with_gst, ongoing_projects_agreement_amount_with_gst, cost_per_ha_in_lakhs_rs,   " +
                "cumulative_expenditure_by_12_2022_in_rs_lakh, work_if_awarded, agt_no, date_of_commencement, stipulated_date_of_completion,  " +
                "project_if_completed, project_proposed_to_be_dropped, balance_cost_as_on_12_2022_in_rs_lakh, list_for_survey_of_reservior_counter_no,  " +
                "list_for_survey_of_reservior_counter_recieved, hyd_data_nos_43, hyd_data_recieved_43, hyd_data_nos_54, hyd_data_recieved_54,  " +
                "hyd_data_nos_74, hyd_data_recieved_74, reservoir_estimate_nos_45, reservoir_estimate_recieved_45, submission_of_estimates_nos_84,  " +
                "submission_of_estimates_recieved_84, reservoir_estimate_nos_54, reservoir_estimate_recieved_54, agmt_value_approx_estimate_cost,  " +
                "estimated_amount, head_works_dam, head_works_rip_rap, head_works_surplus, head_works_shutter, head_works_hr,  " +
                "imp_of_approach_channel_to_tank, canal, cad2, date_recieved, if_estimate_recieved, hw_distributi_on_system, cad,  " +
                "estimated_amount_including_gst, cost_per_hectare, remarks_fro_drop, if_drop_proposal_accepted, sought_for_complaimce,  " +
                "technical_sanction, schematic_diagram, component_as_per_schematic, drawings, head_works_earth_dam, head_works_structures,  " +
                "head_works_se, head_works_hr2, head_works_shutter2, ds_canal, ds_structures, cad_30_30, cad_40_40,  " +
                "surplus_escape_parameters_existing_width_of_escape, surplus_escape_parameters_width_to_be_adopted,  " +
                "surplus_escape_parameters_existing_tbl, surplus_escape_parameters_change_if_required_in_tbl,  " +
                "surplus_escape_parameters_if_modification_required, present_cropping_khariff, present_cropping_rabi, proposed_cropping_khariff,  " +
                "proposed_cropping_rabi, no_of_pps_2, ayacut_area_covered_in_area, amount_required_for_election, no_of_pps_for_which_voterlist_published,  " +
                "publication_of_final_voter_list, no_of_pps_which_election_completed, pp_remarks, is_active, created_by, created_on, updated_by,  " +
                "updated_on, period_for_completion FROM oiipcra_oltp.master_data_538 WHERE project_id =:projectId ";
        sqlParam.addValue("projectId",projectId);
        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(MasterData538Dto.class));
    }

    public List<Integer> getTankIdsByEstimateId(int estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tank_id from oiipcra_oltp.activity_estimate_tank_mapping where estimate_id =:estimateId and is_active= true ";
        sqlParam.addValue("estimateId", estimateId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public Page<MasterData538Dto> tank538masterSearchList(SurveyListRequest surveyListRequest, List<Integer> tankIdsByContractId) {
        String queryString = " ";
        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(surveyListRequest.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(surveyListRequest.getUserId());

        PageRequest pageable = PageRequest.of(surveyListRequest.getPage(), surveyListRequest.getSize(), Sort.Direction.fromString(surveyListRequest.getSortOrder()), surveyListRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        queryString = "SELECT id, project_id,tank_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id,village_id,village_name," +
//                " mi_division_id,mi_division_name,name_of_the_m_i_p as tankName, m_i_p_id,sub_division_id,section_id FROM oiipcra_oltp.tank_m_id WHERE is_active=true ";

        queryString = "SELECT master.id, master.project_id, percentage_filling_status_as_per_google_earth as percentage_filling_status_as_per_google_earth, tanks_takenup_in_baseline_assessment_53," +
                "master.category, master.type, catchment_in_sq_km, dcca_khariff_ha, dcca_rabi_ha, ca_khariff, ca_rabi, master.river_basin," +
                "length_left_canal_km, length_right_canal_km, no_of_pps, no_of_pps_for_which_election_completed, command_area_delineated_ha," +
                "date_of_election_of_pp, name_of_pp, president_of_pp, secretary_of_pp, status_of_pp, cluster, occ_dpr_recieved, dpr_acceptable_or_not," +
                "unit_cost_per_ha_for_dpr, total_amount_for_dpr, technical_sanction_no_and_date, hydrological_data_pending, hydrological_assesment_done," +
                "bid_id, package_id, total_cost_of_package_in_rs, tender_prticipation_status, rate_quoted, tender_premium," +
                "ongoing_agreement_amount_with_gst, ongoing_projects_agreement_amount_with_gst, cost_per_ha_in_lakhs_rs," +
                "cumulative_expenditure_by_12_2022_in_rs_lakh, work_if_awarded, agt_no, date_of_commencement, stipulated_date_of_completion," +
                "project_if_completed, project_proposed_to_be_dropped, balance_cost_as_on_12_2022_in_rs_lakh, list_for_survey_of_reservior_counter_no," +
                "list_for_survey_of_reservior_counter_recieved, hyd_data_nos_43, hyd_data_recieved_43, hyd_data_nos_54, hyd_data_recieved_54," +
                "hyd_data_nos_74, hyd_data_recieved_74, reservoir_estimate_nos_45, reservoir_estimate_recieved_45, submission_of_estimates_nos_84," +
                "submission_of_estimates_recieved_84, reservoir_estimate_nos_54, reservoir_estimate_recieved_54, agmt_value_approx_estimate_cost," +
                "estimated_amount, head_works_dam, head_works_rip_rap, head_works_surplus, head_works_shutter, head_works_hr," +
                "imp_of_approach_channel_to_tank, canal, cad2, date_recieved, if_estimate_recieved, hw_distributi_on_system, cad," +
                "estimated_amount_including_gst, cost_per_hectare, remarks_fro_drop, if_drop_proposal_accepted, sought_for_complaimce," +
                "technical_sanction, schematic_diagram, component_as_per_schematic, drawings, head_works_earth_dam, head_works_structures," +
                "head_works_se, head_works_hr2, head_works_shutter2, ds_canal, ds_structures, cad_30_30, cad_40_40," +
                "surplus_escape_parameters_existing_width_of_escape, surplus_escape_parameters_width_to_be_adopted," +
                "surplus_escape_parameters_existing_tbl, surplus_escape_parameters_change_if_required_in_tbl," +
                "surplus_escape_parameters_if_modification_required, present_cropping_khariff, present_cropping_rabi, proposed_cropping_khariff," +
                "proposed_cropping_rabi, no_of_pps_2, ayacut_area_covered_in_area, amount_required_for_election, no_of_pps_for_which_voterlist_published," +
                "publication_of_final_voter_list, no_of_pps_which_election_completed, pp_remarks, master.is_active, master.created_by, master.created_on, master.updated_by," +
                "master.updated_on, period_for_completion,tank.tank_id as tankId,tank.name_of_the_m_i_p as tankName,dept_dist_name as districtName,dept_block_name as blockName,mi_division_name as divisionName FROM oiipcra_oltp.master_data_538 as master " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.project_id=master.project_id WHERE master.is_active=true ";

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

           /* if (surveyListRequest.getVillageId()!=null &&surveyListRequest.getVillageId() > 0) {
                queryString += " AND  village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }

            if (surveyListRequest.getGpId()!=null && surveyListRequest.getGpId() > 0) {
                queryString += " AND  gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }*/
            if (surveyListRequest.getBlockId()!= null && surveyListRequest.getBlockId() > 0) {
                queryString += "  AND tank.block_id=:blockId ";
                sqlParam.addValue("blockId", surveyListRequest.getBlockId());
            }
            if (surveyListRequest.getDistrictId()!=null && surveyListRequest.getDistrictId() > 0) {
                queryString += " AND tank.dist_id=:distId ";
                sqlParam.addValue("distId", surveyListRequest.getDistrictId());
            }
            if (surveyListRequest.getDivisionId()!=null && surveyListRequest.getDivisionId() > 0) {
                queryString += " AND tank.mi_division_id=:divisionId ";
                sqlParam.addValue("divisionId", surveyListRequest.getDivisionId());
            }
            if (surveyListRequest.getSubDivisionId()!=null && surveyListRequest.getSubDivisionId() > 0) {
                queryString += " AND tank.sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", surveyListRequest.getSubDivisionId());
            }
            if (surveyListRequest.getSectionId()!=null &&surveyListRequest.getSectionId() > 0) {
                queryString += " AND tank.section_id=:sectionId ";
                sqlParam.addValue("sectionId", surveyListRequest.getSectionId());
            }
            /*if (surveyListRequest.getProgressStatus() > 0) {
                queryString += " AND progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", surveyListRequest.getProgressStatus());
            }*/

          /*  if (surveyListRequest.getId()!=null && surveyListRequest.getId() > 0) {
                queryString += " AND id=:id";
                sqlParam.addValue("id", surveyListRequest.getId());
            }*/

            if (surveyListRequest.getProjectId()!=null && surveyListRequest.getProjectId() > 0) {
                queryString += " AND tank.project_id=:projectId";
                sqlParam.addValue("projectId", surveyListRequest.getProjectId());
            }
            if (surveyListRequest.getBidId()!=null) {
                queryString += " AND master.bid_id=:bidId";
                sqlParam.addValue("bidId", surveyListRequest.getBidId());
            }
           if (tankIdsByContractId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByContractId)";
                sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
            }
/*
            if (tankIdsByExpenditureId != null && tankIdsByExpenditureId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByExpenditureId)";
                sqlParam.addValue("tankIdsByExpenditureId", tankIdsByExpenditureId);
            }

            if (tankIdsByInvoiceId != null && tankIdsByInvoiceId.size() > 0) {
                queryString += " AND tank_id IN (:tankIdsByInvoiceId)";
                sqlParam.addValue("tankIdsByInvoiceId", tankIdsByInvoiceId);
            }*/
            if (surveyListRequest.getTankId()!=null && surveyListRequest.getTankId() > 0) {
                queryString += " AND tank.tank_id=:tankId ";
                sqlParam.addValue("tankId", surveyListRequest.getTankId());
            }
          /*  if(surveyListRequest.getEstimateId()!=null && surveyListRequest.getEstimateId()>0) {
                if (tankIdsByEstimateId != null && tankIdsByEstimateId.size() > 0) {
                    queryString += " AND tank_id IN (:tankIdsByEstimateId)";
                    sqlParam.addValue("tankIdsByEstimateId", tankIdsByEstimateId);
                }
                if (tankIdsByEstimateId == null && tankIdsByEstimateId.size() == 0) {
                    queryString += " AND tank_id IN (0)";
                    sqlParam.addValue("tankIdsByEstimateId", tankIdsByEstimateId);
                }
            }*/
        }

        queryString += " ORDER BY dept_dist_name,dept_block_name,dept_gp_name,village_name,name_of_the_m_i_p ASC";
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<MasterData538Dto> tanklist = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(MasterData538Dto.class));
        return new PageImpl<>(tanklist, pageable, resultCount);
    }





    public List<DepthDto> getDepthIdByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,image,surveyor_image as surveyorImage from oiipcra_oltp.depth_m where tank_id=:tankId ";
        sqlParam.addValue("tankId",tankId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(DepthDto.class));
    }

    public List<DepthImageDto> getDepthImagesByDepthId(Integer depthId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="SELECT id,image from oiipcra_oltp.depth_m where id =:depthId ";
        sqlParam.addValue("depthId",depthId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(DepthImageDto.class));
    }

    public List<FeederDto> getFeederDetails(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =" SELECT  fm.id,fm.tank_id,tank.project_id as projectId,tank.name_of_the_m_i_p as tankName,fm.start_date as startDateString,fm.end_date as endDateString,fm.surveyed_by,fm.upload_on,um.name as surveyedName,um.designation_id,\n" +
                "dm.name as designationName, public.st_asgeojson(fm.geom) as geom " +
                "FROM oiipcra_oltp.feeder_m as fm " +
                "left join oiipcra_oltp.user_m as um on fm.surveyed_by = um.id  " +
                "left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= fm.tank_id " +
                "where fm.tank_id=:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(FeederDto.class));
    }

    public List<FeederDto> getFeederDetailsWithGeom(Integer tankId, Integer typeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =" SELECT  fm.id,fm.tank_id,fm.start_date as startDateString, " +
                " fm.end_date as endDateString,fm.surveyed_by,fm.upload_on,  fm.surveyor_image, public.st_asgeojson(fm.geom) as geom, fm.is_active, fm.created_by, fm.created_on,  " +
                " fm.updated_by, fm.updated_on, tank.project_id as projectId " +
                " FROM oiipcra_oltp.feeder_m as fm   " +
                " left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= fm.tank_id " +
                " where fm.tank_id=:tankId and fm.geom is not null";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(FeederDto.class));
    }

    public List<FeederImageDto> getFeederImageByTankId(Integer feederId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =" SELECT id, feeder_id, image_name, is_active, created_by, created_on, updated_by, updated_on " +
                " FROM oiipcra_oltp.feeder_image where feeder_id=:feederId ";
        sqlParam.addValue("feederId", feederId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(FeederImageDto.class));
    }

    public List<FeederLocationDto> getFeederLocation(Integer feederId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =" SELECT id, feeder_id, latitude, longitude, altitude, accuracy, is_active, created_by, created_on, updated_by, updated_on " +
                " FROM oiipcra_oltp.feeder_location where feeder_id in (:feederId) ";
        sqlParam.addValue("feederId", feederId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(FeederLocationDto.class));
    }

    public List<CadDto> getCadDetailsByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cad.id,cad.tank_id,tank.project_id as projectId,tank.name_of_the_m_i_p as tankName,cad.start_date as startDateString,cad.end_date as endDateString,cad.surveyed_by,um.name as surveyedName,um.designation_id, " +
                "cad.upload_on,cad.surveyor_image,dm.name as designationName, public.st_asgeojson(cad.geom) as  geomm  " +
                "from oiipcra_oltp.cad_m as cad  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= cad.tank_id "+
                "left join oiipcra_oltp.user_m as um on cad.surveyed_by = um.id " +
                "left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id  " +
                "where cad.tank_id =:tankId ";
        sqlParam.addValue("tankId",tankId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CadDto.class));
    }

    public List<CadDto> getCadDetailsByTankIdWithGeom(Integer tankId, Integer typeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT cad.id,cad.tank_id, cad.start_date as startDateString,  \n" +
                " cad.end_date as endDateString, cad.surveyed_by, cad.upload_on,cad.surveyor_image, public.st_asgeojson(cad.geom) as  geomm,  \n" +
                " cad.is_active, cad.created_by, cad.created_on, cad.updated_by, cad.updated_on, tank.project_id as projectId  \n" +
                " from oiipcra_oltp.cad_m as cad " +
                " left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= cad.tank_id " +
                " where cad.tank_id =:tankId and cad.geom is not null ";
        sqlParam.addValue("tankId",tankId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CadDto.class));
    }

    public List<CadLocationDto> getCadLocationByTankId(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cd.id,cd.cad_id,cd.latitude,cd.longitude,cd.altitude,cd.accuracy from oiipcra_oltp.cad_location as cd " +
                "where cd.is_active = true and cd.cad_id in (:id) ";
        sqlParam.addValue("id",id);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CadLocationDto.class));
    }

    public List<CadImageDto> getCadImageDetails(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cad.id,cad.cad_id,cad.image_name from oiipcra_oltp.cad_image as cad where cad.is_active = true and  " +
                     "cad.cad_id in (:id) ";
        sqlParam.addValue("id",id);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CadImageDto.class));
    }

    public List<CadDto> getCadDetailsByCadId(Integer cadId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="SELECT cad.id,cad.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id,tank.dist_id,dist.district_name as distName, " +
                "tank.block_id,block.block_name as blockName,tank.gp_id,gm.grampanchayat_name,tank.village_id,tank.village_name,  " +
                "tank.latitude,tank.longitude,tank.category,tank.catchment_area_sqkm as catchmentAreaSqkm,tank.certified_ayacut_kharif_ha as certifiedAyacutKharifHa, " +
                "tank.certified_ayacut_rabi_ha as certifiedAyacutRabiHa, " +
                "tank.length_of_dam_weir_in_m as lengthOfDamWeirInM, " +
                "cad.start_date as startDateString,cad.end_date as endDateString,cad.surveyed_by,um.name as surveyedName,um.designation_id, " +
                "cad.upload_on,cad.surveyor_image,dm.name as designationName, public.st_asgeojson(cad.geom) as geomm " +
                "from oiipcra_oltp.cad_m as cad  " +
                "left join oiipcra_oltp.user_m as um on cad.surveyed_by = um.id " +
                "left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= cad.tank_id " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id = tank.dist_id  " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=tank.block_id  " +
                "left join oiipcra_oltp.gp_boundary as gm on gm.gp_id =tank.gp_id " ;
        if(cadId != null && cadId > 0){
            qry += "  where cad.id =:cadId ";
            sqlParam.addValue("cadId",cadId);
        }
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CadDto.class));
    }

    public List<FeederDto> getFeederDetailsById(Integer feederId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT fm.id,fm.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id,tank.dist_id,dist.district_name as distName,  " +
                "tank.block_id,block.block_name as blockName,tank.gp_id,gm.grampanchayat_name,tank.village_id,tank.village_name,   " +
                "tank.latitude,tank.longitude,tank.category,tank.catchment_area_sqkm as catchmentAreaSqkm,tank.certified_ayacut_kharif_ha as certifiedAyacutKharifHa,  " +
                "tank.certified_ayacut_rabi_ha as certifiedAyacutRabiHa,  " +
                "tank.length_of_dam_weir_in_m as lengthOfDamWeirInM,  " +
                "fm.start_date as startDateString,fm.end_date as endDateString,fm.surveyed_by,um.name as surveyedName,um.designation_id,  " +
                "fm.upload_on,fm.surveyor_image,dm.name as designationName, public.st_asgeojson(fm.geom) as geom  " +
                "from oiipcra_oltp.feeder_m as fm   " +
                "left join oiipcra_oltp.user_m as um on fm.surveyed_by = um.id  " +
                "left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= fm.tank_id  " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id = tank.dist_id   " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=tank.block_id   " +
                "left join oiipcra_oltp.gp_boundary as gm on gm.gp_id =tank.gp_id  " ;
        if(feederId != null && feederId > 0){
            qry += " where fm.id =:feederId ";
            sqlParam.addValue("feederId",feederId);
        }
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(FeederDto.class));
    }

    public List<FeederDto> getAllFeederDetails() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT fm.id,fm.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id,tank.dist_id,dist.district_name as distName,  " +
                "tank.block_id,block.block_name as blockName,tank.gp_id,gm.grampanchayat_name,tank.village_id,tank.village_name,   " +
                "tank.latitude,tank.longitude,tank.category,tank.catchment_area_sqkm as catchmentAreaSqkm,tank.certified_ayacut_kharif_ha as certifiedAyacutKharifHa,  " +
                "tank.certified_ayacut_rabi_ha as certifiedAyacutRabiHa,  " +
                "tank.length_of_dam_weir_in_m as lengthOfDamWeirInM,  " +
                "fm.start_date as startDateString,fm.end_date as endDateString,fm.surveyed_by,um.name as surveyedName,um.designation_id,  " +
                "fm.upload_on,fm.surveyor_image,dm.name as designationName, public.st_asgeojson(fm.geom) as geom  " +
                "from oiipcra_oltp.feeder_m as fm   " +
                "left join oiipcra_oltp.user_m as um on fm.surveyed_by = um.id  " +
                "left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= fm.tank_id  " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id = tank.dist_id   " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=tank.block_id   " +
                "left join oiipcra_oltp.gp_boundary as gm on gm.gp_id =tank.gp_id where fm.geom is not null " ;
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(FeederDto.class));
    }

    public List<CadDto> getAllCadDetails() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT cad.id,cad.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id,tank.dist_id,dist.district_name as distName,  \n" +
                " tank.block_id,block.block_name as blockName,tank.gp_id,gm.grampanchayat_name,tank.village_id,tank.village_name,   \n" +
                " tank.latitude,tank.longitude,tank.category,tank.catchment_area_sqkm as catchmentAreaSqkm,tank.certified_ayacut_kharif_ha as certifiedAyacutKharifHa, \n" +
                " tank.certified_ayacut_rabi_ha as certifiedAyacutRabiHa, \n" +
                " tank.length_of_dam_weir_in_m as lengthOfDamWeirInM,  \n" +
                " cad.start_date as startDateString,cad.end_date as endDateString,cad.surveyed_by,um.name as surveyedName,um.designation_id,  \n" +
                " cad.upload_on,cad.surveyor_image,dm.name as designationName, public.st_asgeojson(cad.geom) as geomm  \n" +
                " from oiipcra_oltp.cad_m as cad   \n" +
                " left join oiipcra_oltp.user_m as um on cad.surveyed_by = um.id  \n" +
                " left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id  \n" +
                " left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= cad.tank_id  \n" +
                " left join oiipcra_oltp.district_boundary as dist on dist.dist_id = tank.dist_id   \n" +
                " left join oiipcra_oltp.block_boundary as block on block.block_id=tank.block_id   \n" +
                " left join oiipcra_oltp.gp_boundary as gm on gm.gp_id =tank.gp_id  \n" +
                " where cad.geom is not null " ;
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CadDto.class));
    }

    public List<Integer> getTankIdsByIssueId(Integer issueId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tank_id from oiipcra_oltp.issue_tracker as issue where issue.id =:issueId and is_active=true ";
        sqlParam.addValue("issueId", issueId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public BigDecimal getMaxWaterSpreadData(String projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select round(MAX(area),3) as maxValue from oiipcra_oltp.oiipcra_water_spread where project_id=:projectId ";
        sqlParam.addValue("projectId", projectId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.00);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal getMinWaterSpreadData(String projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select round(MIN(area),3) as minValue from oiipcra_oltp.oiipcra_water_spread where project_id=:projectId ";
        sqlParam.addValue("projectId", projectId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.00);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal getAvgWaterSpreadData(String projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select round(sum(area)/( select count(*) from (select DISTINCT month, year  from oiipcra_oltp.oiipcra_water_spread) as count),3) as average from oiipcra_oltp.oiipcra_water_spread where project_id =:projectId  ";
        sqlParam.addValue("projectId", projectId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.00);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal getTotalWaterSpreadData(String projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select (st_area(st_transform(geom,32645))/10000) as totalWsa from oiipcra_oltp.project_waterbody where oiipcra_id=:projectId ";
        sqlParam.addValue("projectId", projectId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.00);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal getLessThan50WaterSpreadData(String projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select count(month) as lessThan50 from oiipcra_oltp.oiipcra_water_spread \n" +
                "where project_id=:projectId and area < (select (st_area(st_transform(geom,32645))/10000) as totalWsa from oiipcra_oltp.project_waterbody where oiipcra_id=:projectId)/2 ";
        sqlParam.addValue("projectId", projectId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.00);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public List<ContractTypeDto> getAllContractType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,name from oiipcra_oltp.contract_type where is_active=true  ";
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(ContractTypeDto.class));
    }

    public List<PhysicalProgressPlannedDto> getPlannedDetails(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT planned.id,planned.tank_id, tank.name_of_the_m_i_p as tankName,planned.total_length_of_canal_as_per_estimate,planned.no_of_cd_structures_to_be_repared,planned.total_length_of_cad,planned.contract_id,  " +
                "planned.tank_m_id as tankMId,planned.estimate_id,planned.progress_status as progressStatusId,status.name as progressStatusName from oiipcra_oltp.physical_progress_planned  as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = planned.tank_m_id  " +
                "left join oiipcra_oltp.progress_status_m as status on status.id=planned.progress_status "+
                "where planned.contract_id =:contractId ";
        sqlParam.addValue("contractId",contractId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(PhysicalProgressPlannedDto.class));
    }

    public List<PhysicalProgressExecutedDto> getExecutedDetails(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry =  "SELECT executed.id,executed.tank_id, tank.name_of_the_m_i_p as tankName,executed.length_of_canal_improved,executed.no_of_cd_structures_repared,executed.total_length_of_cad,executed.contract_id,  " +
                "executed.tank_m_id as tankMId,executed.no_of_outlet_constructed,executed.progress_status as progressStatusId,status.name as progressStatusName,executed.planned_id as plannedId  from oiipcra_oltp.physical_progress_executed as executed  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = executed.tank_m_id  " +
                "left join oiipcra_oltp.progress_status_m as status on status.id=executed.progress_status \n" +
                "where executed.contract_id =:contractId ";
        sqlParam.addValue("contractId",contractId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(PhysicalProgressExecutedDto.class));
    }

    public List<TankViewDto> downloadSurveyTankExcel(SurveyListRequest surveyListRequest) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select id, project_id as projectId, dept_dist_name as Description from oiipcra_oltp.tank_m_id where id=:id ";
        sqlParam.addValue("id", surveyListRequest.getId());
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankViewDto.class));
    }

    public List<CadDto> getLatLongByCadId(Integer cadId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select latitude, longitude, altitude, accuracy from oiipcra_oltp.cad_location " +
                "where cad_id=:cadId ";
        sqlParam.addValue("cadId", cadId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CadDto.class));
    }

    public Integer updateCadGeom(List<CadDto> cadDto, Integer cadId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String update = "";
        String qry = "";
        String geom_type = "";
        String str = "";


        str += "LINESTRING(";
        if (cadDto.size()> 1) {

            for (int i = 0; i < cadDto.size(); i++) {
                str += cadDto.get(i).getLongitude() + " " + cadDto.get(i).getLatitude() + ",";

            }
            str = str.substring(0, str.length() - 1);
            str += ")";
            update = " UPDATE oiipcra_oltp.cad_m  set geom=(ST_GeomFromText('" + str + "',4326)) where id="+cadId+" ";
//            update = "UPDATE rdvts_oltp.geo_construction_m set geom = ST_GeomFromText('" + str + "',4326)  where id=" + roadId + "";
            sqlParam.addValue("cadId", cadId);
            namedJdbc.update(update, sqlParam);
        }
        return 1;
    }

    public List<FeederDto> getLatLongByFeederId(Integer feederId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select latitude,longitude,altitude,accuracy from oiipcra_oltp.feeder_location where feeder_id=:feederId ";
        sqlParam.addValue("feederId", feederId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FeederDto.class));
    }

    public Integer updateFeederGeom(List<FeederDto> feederDto, Integer feederId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String update = "";
        String str = "";


        str += "LINESTRING(";
        if (feederDto.size()> 1) {

            for (int i = 0; i < feederDto.size(); i++) {
                str += feederDto.get(i).getLongitude() + " " + feederDto.get(i).getLatitude() + ",";

            }
            str = str.substring(0, str.length() - 1);
            str += ")";
            update = " update oiipcra_oltp.feeder_m set geom=(ST_GeomFromText('" + str + "',4326)) where id="+feederId+" ";
            sqlParam.addValue("feederId", feederId);
            namedJdbc.update(update, sqlParam);
        }
        return 1;
    }

    public List<Integer> getCadIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select id from oiipcra_oltp.cad_m where tank_id in (:tankId) ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getfeederIdsByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select id from oiipcra_oltp.feeder_m where tank_id in(:tankId) ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public CadDto getAllCadByCadId(Integer cadId, Integer typeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT cad.id,cad.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id,tank.dist_id,dist.district_name as distName,  \n" +
                "  tank.block_id,block.block_name as blockName,tank.gp_id,gm.grampanchayat_name,tank.village_id,tank.village_name,   \n" +
                " tank.latitude,tank.longitude,tank.category,tank.catchment_area_sqkm as catchmentAreaSqkm,tank.certified_ayacut_kharif_ha as certifiedAyacutKharifHa,  \n" +
                " tank.certified_ayacut_rabi_ha as certifiedAyacutRabiHa,  \n" +
                " tank.length_of_dam_weir_in_m as lengthOfDamWeirInM,  \n" +
                " cad.start_date as startDateString,cad.end_date as endDateString,cad.surveyed_by,um.name as surveyedName,um.designation_id,  \n" +
                " cad.upload_on,cad.surveyor_image,dm.name as designationName, public.st_asgeojson(cad.geom) as geomm  \n" +
                " from oiipcra_oltp.cad_m as cad   \n" +
                " left join oiipcra_oltp.user_m as um on cad.surveyed_by = um.id  \n" +
                " left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id  \n" +
                "  left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= cad.tank_id \n" +
                "  left join oiipcra_oltp.district_boundary as dist on dist.dist_id = tank.dist_id  \n" +
                " left join oiipcra_oltp.block_boundary as block on block.block_id=tank.block_id  \n" +
                " left join oiipcra_oltp.gp_boundary as gm on gm.gp_id =tank.gp_id  \n" +
                " where cad.id =:cadId ";
        sqlParam.addValue("cadId", cadId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(CadDto.class));
    }

    public FeederDto getAllFeederByFeederId(Integer feederId, Integer typeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT fm.id,fm.tank_id,tank.name_of_the_m_i_p as tankName,tank.project_id,tank.dist_id,dist.district_name as distName,  " +
                " tank.block_id,block.block_name as blockName,tank.gp_id,gm.grampanchayat_name,tank.village_id,tank.village_name,   " +
                "  tank.latitude,tank.longitude,tank.category,tank.catchment_area_sqkm as catchmentAreaSqkm,tank.certified_ayacut_kharif_ha as certifiedAyacutKharifHa,   " +
                " tank.certified_ayacut_rabi_ha as certifiedAyacutRabiHa,  " +
                "  tank.length_of_dam_weir_in_m as lengthOfDamWeirInM, " +
                " fm.start_date as startDateString,fm.end_date as endDateString,fm.surveyed_by,um.name as surveyedName,um.designation_id,  " +
                "  fm.upload_on,fm.surveyor_image,dm.name as designationName, public.st_asgeojson(fm.geom) as geom   " +
                "  from oiipcra_oltp.feeder_m as fm   " +
                "  left join oiipcra_oltp.user_m as um on fm.surveyed_by = um.id   " +
                "  left join oiipcra_oltp.designation_m as dm on dm.id =um.designation_id   " +
                "  left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= fm.tank_id  " +
                "  left join oiipcra_oltp.district_boundary as dist on dist.dist_id = tank.dist_id    " +
                "  left join oiipcra_oltp.block_boundary as block on block.block_id=tank.block_id   " +
                " left join oiipcra_oltp.gp_boundary as gm on gm.gp_id =tank.gp_id   " +
                " where fm.id =:feederId ";
        sqlParam.addValue("feederId", feederId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(FeederDto.class));
    }

    public List<Integer> getTankIdsForCad() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct tank_id from oiipcra_oltp.cad_m where is_active=true ";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getTankIdsForNotSurveyCad() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct id from oiipcra_oltp.tank_m_id where id not in (select distinct tank_id from oiipcra_oltp.cad_m where is_active=true) ";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getSurveyTankIds() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct tank_id from oiipcra_oltp.tank_survey_data where is_active=true  ";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getNotSurveyTankIds() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct id from oiipcra_oltp.tank_m_id where id not in (select distinct tank_id from oiipcra_oltp.tank_survey_data where is_active=true ) ";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTankIdsForDepth() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tank_id from oiipcra_oltp.depth_m where is_active=true ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForNotSurveyDepth() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct id from oiipcra_oltp.tank_m_id where id not in (select distinct tank_id from oiipcra_oltp.depth_m where is_active=true) ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }


    public List<Integer> getTankIdsForFeeder() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct tank_id from oiipcra_oltp.feeder_m where is_active=true ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForNotSurveyFeeder() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct id from oiipcra_oltp.tank_m_id where id not in (select distinct tank_id from oiipcra_oltp.feeder_m where is_active=true) ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForCivilWork() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT id  FROM oiipcra_oltp.tank_m_id " +
                "WHERE is_civil_work_completed=true ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForCivilWorkNotCompleted() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT id  FROM oiipcra_oltp.tank_m_id " +
                "WHERE is_civil_work_completed=false ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForFop() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT distinct tank_id from oiipcra_oltp.fpo_tank_mapping where is_active=true ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForFopNotAdded() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select distinct id from oiipcra_oltp.tank_m_id where id not in (SELECT distinct tank_id from oiipcra_oltp.fpo_tank_mapping where is_active=true) ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForDropped() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = " SELECT distinct id  FROM oiipcra_oltp.tank_m_id " +
                "WHERE is_dropped=true " ;
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsForNotDropped() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT distinct id  FROM oiipcra_oltp.tank_m_id " +
                " WHERE is_dropped=false";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsProposedToBeDropped() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = " SELECT distinct id FROM oiipcra_oltp.tank_m_id " +
                "WHERE proposed_to_be_dropped=true";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsProposedToBeNotDropped() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = " SELECT distinct id FROM oiipcra_oltp.tank_m_id " +
                "WHERE proposed_to_be_dropped=false ";
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public List<Integer> getTankIdsProgressStatusWise(Integer progressStatus) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="select  distinct tank_id from oiipcra_oltp.tank_survey_data  where is_active=true and progress_status_id=:progressStatus ";
        sqlParam.addValue("progressStatus", progressStatus);
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }

    public List<ContractDto> getContractListForExcel(int id, int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT Distinct(contract.id) as contractId,contract.contract_number as contractNumber,contract.work_description as workDescription,\n" +
                "         contract.contract_date as contractDate,level.level_name as contractLevel,type.name as ContractType,status.name as contractStatus,\n" +
                "        agency.name as agencyName,contract.contract_name as contractName,\n" +
                "        wm.name as workTypeName  from oiipcra_oltp.contract_m as contract \n" +
                "        left join oiipcra_oltp.contract_level_master as level on level.id=contract.contract_level_id\n" +
                "        left join oiipcra_oltp.contract_type as type on type.id=contract.contract_type_id \n" +
                "        left join oiipcra_oltp.contract_status as status on status.id=contract.contract_status_id \n" +
                "        left join oiipcra_oltp.agency_m as agency on agency.id=contract.agency_id \n" +
                "        left join oiipcra_oltp.contract_mapping as cm on cm.contract_id=contract.id \n" +
                "        left join oiipcra_oltp.tender_m as tender on cm.tender_id=tender.id \n" +
                "        left join oiipcra_oltp.work_type_m as wm on wm.id=contract.work_type_id \n" +
                "        WHERE contract.is_active=true  ";
//        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractDto.class));
    }

    public List<TankSurveyInfoResponse> getSurveyListForExcel(int id, int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT tank.id,tank.tank_id,tank.project_id,tank.created_by as surveyBy,tank.district_id,village.district_name, tank.block_id,village.block_name, \n" +
                "         tank.gp_id,village.grampanchayat_name as gpName,tank.village_id, village.revenue_village_name as villageName,tank.division_id,tank.sub_division_id,\n" +
                "         division.mi_division_name as divisionName,subdivision.mi_sub_division_name as subDivisionName,sectionm.mi_section_name as sectionName, \n" +
                "        tank.section_id,approve.name as progressStatus,tankm.name_of_the_m_i_p as tankName,tank.created_on FROM oiipcra_oltp.tank_survey_data as tank \n" +
                "        left join oiipcra_oltp.village_boundary as village on village.village_id=tank.village_id\n" +
                "         left join oiipcra_oltp.mi_division_m as division on tank.division_id = division.mi_division_id\n" +
                "        left join oiipcra_oltp.mi_subdivision_m as subdivision on tank.sub_division_id = subdivision.mi_sub_division_id\n" +
                "        left join oiipcra_oltp.mi_section_m as sectionm on tank.section_id = sectionm.section_id\n" +
                "        left join oiipcra_oltp.approval_status_m as approve on approve.id=tank.progress_status_id\n" +
                "        left join oiipcra_oltp.tank_m_id as tankm on tank.tank_id=tankm.tank_id where tank.is_active=true ";
//        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankSurveyInfoResponse.class));
    }

    public List<ExpenditureInfo> getExpenditureListForExcel(int id, int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select ed.id,tn.work_identification_code as workId,tm.bid_id as bidId,cm.id as contractId,invoice.invoice_no, \n" +
                "        invoice.invoice_date,  \n" +
                "        cm.contract_number, ed.agency_name as agencyName,mhd.id as activityId,mhd.name activityName,\n" +
                "        to_char(round(cm.contract_amount+(cm.contract_amount*(cm.gst/100)),2),'999G999G999G999D99')  as contractAmountChar, to_char(ed.value,'999G999G999G999D99') as valueChar,ed.payment_date, ip.name as paymentType   \n" +
                "        from oiipcra_oltp.expenditure_data as ed\n" +
                "        left join oiipcra_oltp.expenditure_mapping as epm on epm.expenditure_id = ed.id   \n" +
                "        left join oiipcra_oltp.invoice_m as invoice on invoice.id = epm.invoice_id  \n" +
                "        left join oiipcra_oltp.contract_m as cm on cm.id = epm.contract_id   \n" +
                "        left join oiipcra_oltp.invoice_payment_type as ip on ip.id= ed.payment_type    \n" +
                "        left join oiipcra_oltp.invoice_status as invoiceStatus on invoice.status= invoiceStatus.id   \n" +
                "        left join oiipcra_oltp.tender_m as tm on epm.tender_id = tm.id   \n" +
                "         left join oiipcra_oltp.master_head_details as mhd on mhd.id=epm.activity_id \n" +
                "        left join oiipcra_oltp.tender_notice as tn on epm.tender_notice_id= tn.id   \n" +
                "        left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=epm.estimate_id \n" +
                "        where ed.is_active=true and epm.is_active=true ";
//        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ExpenditureInfo.class));
    }
    public List<Integer> getFpoByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select  fpo_id from oiipcra_oltp.fpo_tank_mapping where tank_id=:tankId";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForList(qry, sqlParam,Integer.class);
        }
        catch (Exception e){
            return null;
        }
    }
    public List<FpoDataDto> getTankFpoData(List<Integer> fpoId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select  ftm.tank_id as tankId,ftm.project_id as projectId,ftm.fpo_id as fpoId,fpoM.name as fpoName," +
                "contact_person as contactPerson,designation as designationName," +
                "contact_number1 as contactNumber1,contact_number2 as contactNumber2," +
                "email_id_1 as emailId1,email_id_2 as emailId2,email_id_3 as emailId3 " +
                "from oiipcra_oltp.fpo_tank_mapping as ftm  " +
                "left join oiipcra_oltp.fpo_m as fpoM on fpoM.id=ftm.fpo_id " +
                "left join oiipcra_oltp.fpo_contact_m as fpoContractM on ftm.fpo_id=fpoContractM.fpo_id " +
                "where ftm.fpo_id in(:fpoId)";
         sqlParam.addValue("fpoId", fpoId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FpoDataDto.class));
    }

    public List<StepExcelDto> getStepExcelForQCBS(int typeId, int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select qcbs.*,cm.id from oiipcra_oltp.contract_m as cm \n" +
                "left join oiipcra_oltp.qcbs_contract_details as qcbs on qcbs.contract_id=cm.id\n" +
                "left join oiipcra_oltp.contract_mapping as cmap on cm.id=cmap.contract_id\n" +
                "left join oiipcra_oltp.activity_estimate_mapping as estimate on cmap.estimate_id=estimate.id\n" +
                "where estimate.procurement_type=3 ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StepExcelDto.class));
    }

    public List<StepExcelDto> getStepExcelForCDS(int typeId, int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT cds.id, cds.contract_id, cds.activity_reference_no_description, cds.activity_id, cds.in_process, \n" +
                "cds.loan_credit_no, cds.component, cds.review_type, cds.category, cds.market_approach, cds.estimated_amount, \n" +
                "cds.process_status, cds.activity_status, cds.terms_of_reference_planned, cds.terms_of_reference_actual, \n" +
                "cds.justification_for_direct_selection_planned, cds.justification_for_direct_selection_actual, \n" +
                "cds.invitation_identified_consultant_planned, cds.invitation_to_identified_consultant_actual, \n" +
                "cds.amendments_to_terms_of_reference_planned, cds.amendments_to_terms_of_reference_actual, cds.draft_negotiated_contract_planned, \n" +
                "cds.draft_negotiated_contract_actual, cds.notification_of_intention_of_award_planned, cds.notification_of_intention_of_award_actual, \n" +
                "cds.signed_contract_planned, cds.signed_contract_actual, cds.contract_amendments_actual, cds.contract_completion_planned, cds.contract_completion_actual, \n" +
                "cds.contract_termination_actual, cds.is_active, cds.created_by, cds.created_on, cds.updated_by, cds.updated_on, cds.estimate_id\n" +
                " ,cm.id from oiipcra_oltp.contract_m as cm \n" +
                "left join oiipcra_oltp.cds_contract_details as cds on cds.contract_id=cm.id\n" +
                "left join oiipcra_oltp.contract_mapping as cmap on cm.id=cmap.contract_id\n" +
                "left join oiipcra_oltp.activity_estimate_mapping as estimate on cmap.estimate_id=estimate.id\n" +
                "where estimate.procurement_type=4 ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StepExcelDto.class));
    }
    public Integer getLatestSurveyData(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select id from oiipcra_oltp.tank_survey_data where tank_id=:tankId order by id desc limit 1  ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam,Integer.class);
        }
        catch (Exception e){
            return null;
        }
    }
}
