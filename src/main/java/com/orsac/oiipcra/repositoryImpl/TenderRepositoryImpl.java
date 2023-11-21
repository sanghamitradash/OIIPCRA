package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.entities.AnnualFinancialTurnoverMaster;
import com.orsac.oiipcra.entities.CompletionOfSimilarTypeOfWork;
import com.orsac.oiipcra.entities.Tender;
import com.orsac.oiipcra.entities.TenderNotice;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.service.UserService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class TenderRepositoryImpl {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    private UserQueryRepository userQryRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    UserQueryRepository userQueryRepository;

    public int count(String qryStr, MapSqlParameterSource sqlParam) {
        String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
        Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        if (null != intRes) {
            return intRes;
        }
        return 0;
    }


    /**
     * Tender Listing with search filter
     */

    public Page<TenderResponse> tenderSearchList(TenderDto tenderDto) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(tenderDto.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("id");

       /* List<Integer> userIdList = new ArrayList<>();
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
        for (int i = 0; i < userList.getSize(); i++) {
            userIdList.add(userList.getContent().get(i).getId());
        }*/
        UserInfoDto userInfoById = null;
        userInfoById = userQryRepo.getUserById(tenderDto.getUserId());

        List<UserLevelDto> userLevel = null;
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }

        List<Integer> authorityIdList = new ArrayList<>();
        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(tenderDto.getUserId());

        PageRequest pageable = PageRequest.of(tenderDto.getPage(), tenderDto.getSize(), Sort.Direction.fromString(tenderDto.getSortOrder()), tenderDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "created_on");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = " ";

        queryString += "SELECT tm.id, tm.tender_name, tm.code as tenderCode, tm.publication_date, tm.issuing_authority, tm.tender_status_id, ts.name tenderStatus," +
                " tm.is_active, tm.created_by, tm.created_on, tm.finyr_id,fy.name FROM oiipcra_oltp.tender_m as tm " +
                " left join oiipcra_oltp.tender_status as ts on ts.id=tm.tender_status_id " +
                " left join oiipcra_oltp.fin_year_m as fy on fy.id=tm.finyr_id " +
                " left join oiipcra_oltp.tender_level_mapping as tlm on tlm.tender_id =tm.id" +
                " where tm.is_active=true ";

        if (userAreaMappingList != null) {
            switch (userLevel.get(0).getId()) {

                case 2:
                    queryString += " And tlm.dist_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 3:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getBlock_id()));
                    queryString += " And tlm.block_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 4:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getGp_id()));
                    queryString += " And tlm.gp_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 5:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getVillage_id()));
                    queryString += " And tlm.village_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 6:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getDivision_id()));
                    queryString += " And tlm.mi_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 7:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSub_division_id()));
                    queryString += " And tlm.sub_division_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 8:
                    userAreaMappingList.forEach(area -> authorityIdList.add(area.getSection_id()));
                    queryString += " And tlm.section_id IN (:authorityIdList)";
                    sqlParam.addValue("authorityIdList", authorityIdList);
                    break;

                case 1:
                default:
                    break;
            }
           /* if (tenderDto.getVillageId() > 0) {
                queryString += " AND  tank.village_id=:villageId";
                sqlParam.addValue("villageId", surveyListRequest.getVillageId());
            }
            if (surveyListRequest.getGpId() > 0) {
                queryString += " AND  tank.gp_id=:gpId";
                sqlParam.addValue("gpId", surveyListRequest.getGpId());
            }*/
            if (tenderDto.getBlockId() > 0) {
                queryString += "  AND tlm.block_id=:blockId ";
                sqlParam.addValue("blockId", tenderDto.getBlockId());
            }
            if (tenderDto.getDistrictId() > 0) {
                queryString += " AND tlm.district_id=:distId ";
                sqlParam.addValue("distId", tenderDto.getDistrictId());
            }
           /* if (tenderDto.getDivisionId() > 0) {
                queryString += " AND tank.division_id=:divisionId ";
                sqlParam.addValue("divisionId", tenderDto.getDivisionId());
            }
            if (tenderDto.getSubDivisionId() > 0) {
                queryString += " AND tank.sub_division_id=:subDivisionId ";
                sqlParam.addValue("subDivisionId", tenderDto.getSubDivisionId());
            }
            if (tenderDto.getSectionId() > 0) {
                queryString += " AND tank.section_id=:sectionId ";
                sqlParam.addValue("sectionId", tenderDto.getSectionId());
            }
            if (tenderDto.getProgressStatus() > 0) {
                queryString += " AND tank.progress_status_id=:progressStatus";
                sqlParam.addValue("progressStatus", tenderDto.getProgressStatus());
            }*/
            if (tenderDto.getTankId() > 0) {
                queryString += " AND tm.tank_id=:tankId";
                sqlParam.addValue("tankId", tenderDto.getTankId());
            }
            if (tenderDto.getId() > 0) {
                queryString += " AND tm.id=:id";
                sqlParam.addValue("id", tenderDto.getId());
            }
            if (tenderDto.getTenderLevelId() > 0) {
                queryString += " AND tm.tender_level_id=:tenderLevelId";
                sqlParam.addValue("tenderLevelId", tenderDto.getTenderLevelId());
            }
            if (tenderDto.getTenderCode() != null) {
                queryString += " AND tm.code=':tenderCode'";
                sqlParam.addValue("tenderCode", tenderDto.getTenderCode());
            }
            if (tenderDto.getTenderStatusId() > 0) {
                queryString += " AND tm.tender_status_id=:tenderStatusId";
                sqlParam.addValue("tenderStatusId", tenderDto.getTenderStatusId());
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (tenderDto.getUploadFromDate() != null && !tenderDto.getUploadFromDate().isEmpty()) {
                queryString += " AND date(tm.created_on) >= :uploadFromDate";
                Date uploadFromDate = null;
                try {
                    uploadFromDate = format.parse(tenderDto.getUploadFromDate());
                } catch (Exception exception) {
                    log.info("From Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
            }
            if (tenderDto.getUploadToDate() != null && !tenderDto.getUploadToDate().isEmpty()) {
                queryString += " AND date(tm.created_on) <= :uploadToDate";
                Date uploadToDate = null;
                try {
                    uploadToDate = format.parse(tenderDto.getUploadToDate());
                } catch (Exception exception) {
                    log.info("To Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
            }

            if (userInfoById.getSurveyor()) {
                queryString += " AND tm.created_by=:userId";
                sqlParam.addValue("userId", tenderDto.getUserId());
            }/* else {
                    queryString += " AND tank.created_by IN (:userIdList)";
                    sqlParam.addValue("userIdList", userIdList);
                }*/
        }

        queryString += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        int resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<TenderResponse> tenderList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TenderResponse.class));
        return new PageImpl<>(tenderList, pageable, resultCount);
    }


    public TenderMasterDto getTenderNoticeDate(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetTenderNoticeDate = "select tender.approval_for_procurement_date as approvalForProcurementDate,tender.technical_bid_opening_date_revised   " +
                "as technicalBidOpeningDateRevised, tender.bid_submission_date as bidSubmissionDate, tender.bid_submission_date_revised  " +
                "as bidSubmissionDateRevised,tender.publication_period_upto as publicationPeriodUpto, " +
                "tender.tender_publication_date as tenderPublicationDate,tender.financial_bid_opening_date as financialBidOpeningDate,  " +
                "tender.pre_bid_meeting_date as preBidMeetingDate, tender.technical_bid_opening_date as technicalBidOpeningDate, tender.name_of_work as nameOfWork, " +
                "tender.pre_bid_meeting_type as preBidMeetingType, pbt.name " +
                "from oiipcra_oltp.tender_m as tender " +
                "left join oiipcra_oltp.pre_bid_meeting_type as pbt on pbt.id = tender.pre_bid_meeting_type " +
                "Where tender.id=:tenderId ";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(GetTenderNoticeDate, sqlParam, new BeanPropertyRowMapper<>(TenderMasterDto.class));
    }

    public Integer getWorkSlNo(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String workSlNo = "Select count(id)+1 from oiipcra_oltp.tender_notice where tender_id=:tenderId ";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(workSlNo, sqlParam, Integer.class);
    }

    public Integer getTotalNotice(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String workSlNo = "Select count(id)  from oiipcra_oltp.tender_notice where tender_id=:tenderId ";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(workSlNo, sqlParam, Integer.class);
    }

    public List<WorksBiddingTypeDto> getBiddingType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetBiddingType = "Select wb.id, wb.name from oiipcra_oltp.works_bidding_type as wb";
        return namedJdbc.query(GetBiddingType, sqlParam, new BeanPropertyRowMapper<>(WorksBiddingTypeDto.class));
    }

    public List<TenderWorkTypeDto> getTenderWorkType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderWorkType = "Select tw.id, tw.name from oiipcra_oltp.tender_work_type as tw";
        return namedJdbc.query(GetTenderWorkType, sqlParam, new BeanPropertyRowMapper<>(TenderWorkTypeDto.class));
    }

  /*  public List<TenderNoticeDto> getAllTenderNoticeByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam= new MapSqlParameterSource();
        String GetTenderNotice = "Select tn.id,tn.tender_id, tn.bidding_type,tn.type_of_work, tn.work_sl_no_in_tcn, tn.work_identification_code,  " +
                "tn.name_of_work, tn.dist_id, tn.block_id, tn.tender_amount, tn.paper_cost, tn.emd_to_be_deposited, " +
                "tn.time_for_completion, tn.contact_no, tn.tender_level_id, tn.circle_id, tn.division_id, tn.ee_id, " +
                "tn.sub_division_id, tn.sub_division_officer, tn.project_id from oiipcra_oltp.tender_notice as tn " ;

        if (tenderId == -1) {
            return namedJdbc.query(GetTenderNotice, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class));
        } else {
            GetTenderNotice += "where tn.tender_id= :tenderId ";
            sqlParam.addValue("tenderId", tenderId);
            return namedJdbc.query(GetTenderNotice, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class));
        }
    }*/

    public List<TenderStipulationDto> getAllTenderStipulationByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulation = "Select ts.id, ts.tender_id, ts.work_id, ts.similar_work_value, ts.similar_work_completion, ts.annual_financial_turnover,  " +
                "ts.previous_yr_weightage, ts.previous_yr_weightage, ts.credit_lines_amount, ts.bid_capacity_turnover,   " +
                "ts.completion_of_work_value_target, ts.turnover_target, ts.liquid_asset_target from oiipcra_oltp.tender_stipulation  " +
                "as ts";

        if (tenderId == -1) {
            return namedJdbc.query(GetTenderStipulation, sqlParam, new BeanPropertyRowMapper<>(TenderStipulationDto.class));
        } else {
            GetTenderStipulation += "where  ts.tender_id =:tenderId ";
            sqlParam.addValue("tenderId", tenderId);
            return namedJdbc.query(GetTenderStipulation, sqlParam, new BeanPropertyRowMapper<>(TenderStipulationDto.class));
        }
    }

    public TenderInfo viewTenderByTenderId(Integer tenderId) {
        TenderInfo tender = new TenderInfo();
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderByTenderId = "SELECT tm.id, tm.bid_id, tm.approval_for_procurement_date, tm.technical_bid_opening_date," +
                " tm.technical_bid_opening_date_revised, tm.bid_submission_date, tm.bid_submission_date_revised,tm.pre_bid_meeting_date as preBidMeetingDate," +
                " tm.financial_bid_opening_date, tm.name_of_work, tm.pre_bid_meeting_type,meeting.name as meetingType," +
                " tm.tender_publication_date, tm.publication_period_upto, tm.tender_type as tenderTypeId," +
                " tm.tender_type, tender.name as tenderType, tm.tender_level_id,level.level_name as tenderlLevel, tm.finyr_id,year.name as financialYear," +
                " tm.tender_opening_date, tm.date_of_tender_notice,tm.activity_id, tm.tender_status, ts.name as status," +
                " tm.meeting_location, ml.name as location from oiipcra_oltp.tender_m as tm" +
                " left join oiipcra_oltp.meeting_type as meeting on meeting.id = tm.pre_bid_meeting_type" +
                " left join oiipcra_oltp.tender_type as tender on tender.id =tm.tender_type" +
                " left join oiipcra_oltp.tender_level_master as level on level.id = tm.tender_level_id" +
                " left join oiipcra_oltp.fin_year_m as year on year.id = tm.finyr_id" +
                " left join oiipcra_oltp.tender_status as ts on ts.id =tm.tender_status" +
                " left join oiipcra_oltp.meeting_location as ml on ml.id =tm.meeting_location" +
                /*" left join oiipcra_oltp.tender_corrigendum as corrigendum on corrigendum.tender_id=tm.id" +*/
                " WHERE tm.id =:tenderId AND tm.is_active=true";

        sqlParam.addValue("tenderId", tenderId);
        try {
            tender = namedJdbc.queryForObject(GetTenderByTenderId, sqlParam, new BeanPropertyRowMapper<>(TenderInfo.class));
        } catch (EmptyResultDataAccessException e) {
            return tender;
        }
        return tender;
    }

    public TenderInfo getBidIdAndTechnicalDate(Integer tenderId) {
        TenderInfo tender = new TenderInfo();
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderByTenderId = "select bidId,to_char(techBidDate,'DD-MM-YYYY') as techBidDate from(select bid_id as bidId, case when technical_bid_opening_date_revised is not null then technical_bid_opening_date_revised\n" +
                "else technical_bid_opening_date end as techBidDate from oiipcra_oltp.tender_m where id=:tenderId) as a";

        sqlParam.addValue("tenderId", tenderId);
        try {
            tender = namedJdbc.queryForObject(GetTenderByTenderId, sqlParam, new BeanPropertyRowMapper<>(TenderInfo.class));
        } catch (EmptyResultDataAccessException e) {
            return tender;
        }
        return tender;
    }

    public List<ActivityEstimate> getEstimateIdAndWork(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getEstimateIdAndWork = "SELECT id as estimateId,name_of_work as workName, approval_date as dateOfApproval " +
                "FROM oiipcra_oltp.activity_estimate_mapping where is_active=true ";
        if (id == -1) {
            getEstimateIdAndWork += " ";
        }
        if (id > 0) {
            getEstimateIdAndWork += " AND id=:id";
            sqlParam.addValue("id", id);
        }
        return namedJdbc.query(getEstimateIdAndWork, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimate.class));

    }

    public List<ActivityEstimate> getEstimateForTender(Integer userId, Integer estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getEstimateIdAndWork = "SELECT id as estimateId,name_of_work as workName, approval_date as dateOfApproval " +
                "FROM oiipcra_oltp.activity_estimate_mapping where is_active=true ";
        if (estimateId == -1) {
            getEstimateIdAndWork += " AND approved_status=2 ";
        }
        if (estimateId > 0) {
            getEstimateIdAndWork += " AND id=:id";
            sqlParam.addValue("id", estimateId);
        }
        return namedJdbc.query(getEstimateIdAndWork, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimate.class));

    }

    public List<MeetingDto> getAllMeetingType(Integer roleId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getMeetingType = "SELECT id, name FROM oiipcra_oltp.meeting_location where is_active=true ";
        if (roleId == 1 || roleId == 2) {
            getMeetingType += " ";
        } else {
            getMeetingType += " and id !=1 ";
        }

        return namedJdbc.query(getMeetingType, sqlParam, new BeanPropertyRowMapper<>(MeetingDto.class));
    }

    public List<TenderPublishTypeDto> getAllTenderPublishType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderPublishType = "SELECT id, name as tenderPublishType FROM oiipcra_oltp.tender_published_type where is_active=true ";

        return namedJdbc.query(getTenderPublishType, sqlParam, new BeanPropertyRowMapper<>(TenderPublishTypeDto.class));
    }

    public Page<TenderNoticeDto> getTenderNoticeList(TenderListDto tenderListDto) {
        tenderListDto.setSortOrder("DESC");
        tenderListDto.setSortBy("id");

        PageRequest pageable = PageRequest.of(tenderListDto.getPage(), tenderListDto.getSize(), Sort.Direction.fromString(tenderListDto.getSortOrder()), tenderListDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        UserInfoDto userInfoById = userService.getUserById(tenderListDto.getUserId());

        String GetTenderNotice = "Select tn.id,tn.tender_id as bidId, tn.bidding_type,tn.type_of_work, tn.work_sl_no_in_tcn, tn.work_identification_code,  " +
                "tn.name_of_work, tn.dist_id, tn.block_id, tn.tender_amount, tn.paper_cost, tn.emd_to_be_deposited,tn.is_active as active, " +
                "tn.time_for_completion, tn.contact_no, tn.tender_level_id, tn.circle_id, tn.division_id, tn.ee_id, " +
                "tn.sub_division_id, tn.sub_division_officer, tn.project_id from oiipcra_oltp.tender_notice as tn " +
                "WHERE tn.is_active=true ";

        if (tenderListDto.getTenderId() > 0) {
            GetTenderNotice += " and tn.tender_id= :tenderId ";
            sqlParam.addValue("tenderId", tenderListDto.getTenderId());
        }
        if (userInfoById.getRoleId() > 4) {
            GetTenderNotice += " and tn.created_by =:userId ";
            sqlParam.addValue("userId", userInfoById.getUserId());
        }
        GetTenderNotice += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        GetTenderNotice += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        return new PageImpl<>(namedJdbc.query(GetTenderNotice, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class)));
    }

    public Page<TenderStipulationInfo> getTenderStipulationList(TenderStipulationList tenderStipulationList) {
        tenderStipulationList.setSortOrder("DESC");
        tenderStipulationList.setSortBy("id");

        PageRequest pageable = PageRequest.of(tenderStipulationList.getPage(), tenderStipulationList.getSize(), Sort.Direction.fromString(tenderStipulationList.getSortOrder()), tenderStipulationList.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        UserInfoDto userInfoById = userService.getUserById(tenderStipulationList.getUserId());

        String GetTenderStipulation = "Select ts.id, ts.tender_id,tender.bid_id as bidId,tender.technical_bid_opening_date, tender.financial_bid_opening_date,  " +
                "ts.similar_work_value, ts.similar_work_completion, ts.annual_financial_turnover,  " +
                "ts.previous_yr_weightage, ts.previous_yr_weightage, ts.credit_lines_amount, ts.bid_capacity_turnover,  " +
                "ts.completion_of_work_value_target, ts.turnover_target, ts.liquid_asset_target from oiipcra_oltp.tender_stipulation  " +
                "as ts " +
                "left join oiipcra_oltp.tender_m as tender on tender.id =ts.tender_id  " +
                "WHERE ts.is_active=true ";

        if (tenderStipulationList.getTenderId() > 0) {
            GetTenderStipulation += " and ts.tender_id= :tenderId ";
            sqlParam.addValue("tenderId", tenderStipulationList.getTenderId());
        }
        if (userInfoById.getRoleId() > 4) {
            GetTenderStipulation += " and ts.created_by =:userId ";
            sqlParam.addValue("userId", userInfoById.getUserId());
        }
        GetTenderStipulation += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        GetTenderStipulation += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        return new PageImpl<>(namedJdbc.query(GetTenderStipulation, sqlParam, new BeanPropertyRowMapper<>(TenderStipulationInfo.class)));
    }

    public Page<Tender> getTenderList(TenderListDto tenderListDto, List<Integer> tenderIds) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(tenderListDto.getUserId());
        tenderListDto.setSortOrder("DESC");
        tenderListDto.setSortBy("id");

        PageRequest pageable = PageRequest.of(tenderListDto.getPage(), tenderListDto.getSize(), Sort.Direction.fromString(tenderListDto.getSortOrder()), tenderListDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        UserInfoDto userInfoById = userService.getUserById(tenderListDto.getUserId());
        String GetTender = "SELECT distinct tm.id , tm.bid_id, " +
                "tm.approval_for_procurement_date, tm.technical_bid_opening_date, tm.created_by as createdBy,   " +
                "tm.technical_bid_opening_date_revised, tm.bid_submission_date, tm.bid_submission_date_revised,  " +
                "tm.financial_bid_opening_date, tm.name_of_work, tm.pre_bid_meeting_type, tm.pre_bid_meeting_date,   " +
                "tm.tender_publication_date, tm.publication_period_upto,  " +
                "tm.tender_type, tm.tender_level_id, tm.finyr_id,  " +
                "tm.tender_opening_date, tm.date_of_tender_notice, tm.estimate_id, tm.tender_status,   " +
                "tm.meeting_location from oiipcra_oltp.tender_m as tm   " +
                "left join oiipcra_oltp.tender_notice as tn on tn.tender_id = tm.id  " +
                "left join oiipcra_oltp.tender_notice_level_mapping as tnlm on tn.id = tnlm.tender_notice_id  " +
                "WHERE tm.is_active=true AND tm.tender_status != 5 ";

        if (tenderListDto.getTenderId() > 0) {
            GetTender += " AND tm.id= :tenderId ";
            sqlParam.addValue("tenderId", tenderListDto.getTenderId());
        }
        if (tenderIds.size() > 0) {
            GetTender += " AND tm.id in (:tenderIdList) ";
            sqlParam.addValue("tenderIdList", tenderIds);
        }
        if (tenderListDto.getTankId() != null && tenderListDto.getTankId() > 0) {
            GetTender += " AND tnlm.tank_id=:tankId ";
            sqlParam.addValue("tankId", tenderListDto.getTankId());
        }
        if (tenderListDto.getEstimateId() != null && tenderListDto.getEstimateId() > 0) {
            GetTender += " AND tm.estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", tenderListDto.getEstimateId());
        }
        if (tenderListDto.getActivityId() != null && tenderListDto.getActivityId() > 0) {
            GetTender += " AND tm.activity_id=:activityId ";
            sqlParam.addValue("activityId", tenderListDto.getActivityId());
        }

        if (userInfoById.getRoleId() > 4) {
            if (userInfoById.getRoleId() == 5 || userInfoById.getRoleId() == 6) {
                List<Integer> divisionId = getDivisionByUserId(tenderListDto.getUserId());
                if (divisionId.size() > 0) {
                    GetTender += " and (tn.division_id in(:divisionId) or tm.created_by =:userId) ";
                    sqlParam.addValue("divisionId", divisionId);
                    sqlParam.addValue("userId", userInfoById.getUserId());
                }
            } else {
                GetTender += " and tm.created_by =:userId ";
                sqlParam.addValue("userId", userInfoById.getUserId());
            }
        }

        GetTender += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(GetTender, sqlParam);
        GetTender += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<Tender> tenderList = namedJdbc.query(GetTender, sqlParam, new BeanPropertyRowMapper<>(Tender.class));
        return new PageImpl<>(tenderList, pageable, resultCount);
    }

    public Page<TenderNoticePublishDto> getPreviousTenderData(@RequestBody TenderNoticePublishListDto tenderData) {

        int pageNo = tenderData.getStart() / tenderData.getLength();
        PageRequest pageable = PageRequest.of(pageNo, tenderData.getLength(), Sort.Direction.fromString("asc"), "id");
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        //UserInfoDto userInfoById = userService.getUserById(tenderListDto.getUserId());
        String GetTender = "SELECT tender.id, draft_tender_notice_id, bid_id, work_name, tender.dist_id, closing_date,district.district_name as districtName ," +
                "draft_tender_notice_doc, bid_document,tender.is_active as active,type.name as typeName,division.mi_division_name as divisionName " +
                "FROM oiipcra_oltp.tender_notice_published as tender " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=tender.dist_id " +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=tender.division_id " +
                "left join oiipcra_oltp.tender_type as type on type.id=tender.type where tender.is_active=true ";


        if (tenderData.getDistId() != null && tenderData.getDistId() > 0) {
            GetTender += " and tender.dist_id =:distId ";
            sqlParam.addValue("distId", tenderData.getDistId());
        }
        if (tenderData.getType() != null && tenderData.getType() > 0) {
            GetTender += " and tender.type =:type ";
            sqlParam.addValue("type", tenderData.getType());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (tenderData.getFromDate() != null && !tenderData.getFromDate().isEmpty()) {
            GetTender += " AND date(tender.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(tenderData.getFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (tenderData.getToDate() != null && !tenderData.getToDate().isEmpty()) {
            GetTender += " AND date(tender.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(tenderData.getToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }

        GetTender += " ORDER BY " + "id DESC";
        resultCount = count(GetTender, sqlParam);
        GetTender += " LIMIT " + tenderData.getLength() + " OFFSET " + tenderData.getStart();

        List<TenderNoticePublishDto> tenderList = namedJdbc.query(GetTender, sqlParam, new BeanPropertyRowMapper<>(TenderNoticePublishDto.class));
        return new PageImpl<>(tenderList, pageable, resultCount);
    }

    public Page<TenderNoticePublishDto> getTenderData(TenderNoticePublishListDto tender) {
        int pageNo = tender.getStart() / tender.getLength();
        PageRequest pageable = PageRequest.of(pageNo, tender.getLength(), Sort.Direction.fromString("asc"), "id");
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct tnp.id,tender.id, draft_tender_notice_id, tnp.bid_id, work_name, tnlm.dist_id, tnp.closing_date,district.district_name as districtName , \n" +
                "tnp.draft_tender_notice_doc, bid_document,tnp.is_active as active,type.name as typeName,division.mi_division_name as divisionName  \n" +
                "FROM oiipcra_oltp.tender_notice_published as tnp\n" +
                "LEFT JOIN oiipcra_oltp.tender_m as tender on tnp.bid_id=tender.bid_id\n" +
                "LEFT JOIN oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id   \n" +
                "LEFT JOIN oiipcra_oltp.tender_notice_level_mapping as tnlm on tnotice.id=tnlm.tender_notice_id\n" +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=tnlm.dist_id\n" +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=tnlm.division_id\n" +
                "left join oiipcra_oltp.tender_type as type on type.id=tnp.type\n" +
                "where tnlm.is_active=true and tender.is_active=true and tnotice.is_active=true ";
        if (tender.getDistId() != null && tender.getDistId() > 0) {
            qry += " and tnlm.dist_id = :distId ";
            sqlParam.addValue("distId", tender.getDistId());
        }
        if (tender.getType() != null && tender.getType() > 0) {
            qry += " and tnp.type =:type ";
            sqlParam.addValue("type", tender.getType());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (tender.getFromDate() != null && !tender.getFromDate().isEmpty()) {
            qry += " AND date(tnp.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(tender.getFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (tender.getToDate() != null && !tender.getToDate().isEmpty()) {
            qry += " AND date(tnp.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(tender.getToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }

        qry += " ORDER BY " + " tnp.id DESC";
        resultCount = count(qry, sqlParam);
        qry += " LIMIT " + tender.getLength() + " OFFSET " + tender.getStart();

        List<TenderNoticePublishDto> tenderList = namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderNoticePublishDto.class));
        return new PageImpl<>(tenderList, pageable, resultCount);
    }

    public boolean deactivateTenderPublish(int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.tender_published SET is_active = false WHERE tender_id=:id";
        sqlParam.addValue("id", id);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    public List<ProjectMasterDto> getAllProject() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getProject = "SELECT project.id , project.name from oiipcra_oltp.project_m as project";

        return namedJdbc.query(getProject, sqlParam, new BeanPropertyRowMapper<>(ProjectMasterDto.class));
    }

    public List<ProjectMasterDto> getAllSchemeOfFunding() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getProject = "SELECT id ,name from oiipcra_oltp.project_m WHERE name='OIIPCRA' ";

        return namedJdbc.query(getProject, sqlParam, new BeanPropertyRowMapper<>(ProjectMasterDto.class));
    }

    public List<TenderInfo> getAllWork() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getWork = "Select tender.id, tender.name_of_work from oiipcra_oltp.tender_m as tender";

        return namedJdbc.query(getWork, sqlParam, new BeanPropertyRowMapper<>(TenderInfo.class));
    }

    public StipulationInfo viewTenderStipulationByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationByTenderId = "select ts.id, ts.tender_id as bidId,tender.technical_bid_opening_date as technicalBidOpeningDate, tender.financial_bid_opening_date as financialBidOpeningDate, ts.similar_work_value, ts.similar_work_completion, ts.annual_financial_turnover,  " +
                "ts.previous_yr_weightage, tender.name_of_work,ts.credit_lines_amount, ts.bid_capacity_turnover, ts.completion_of_work_value_target,  " +
                "ts.turnover_target, ts.liquid_asset_target from oiipcra_oltp.tender_stipulation as ts  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id = ts.tender_id  " +
                "WHERE ts.tender_id = :tenderId";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(GetTenderStipulationByTenderId, sqlParam, new BeanPropertyRowMapper<>(StipulationInfo.class));
    }

    public StipulationInfo viewTenderStipulationById(Integer stipulationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationById = "select ts.id, ts.tender_id as bidId ,tender.technical_bid_opening_date as technicalBidOpeningDate, tender.financial_bid_opening_date as financialBidOpeningDate, ts.similar_work_value, ts.similar_work_completion, ts.annual_financial_turnover, " +
                "ts.previous_yr_weightage, ts.credit_lines_amount, ts.bid_capacity_turnover, ts.completion_of_work_value_target, " +
                "ts.turnover_target, ts.liquid_asset_target from oiipcra_oltp.tender_stipulation as ts  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id = ts.tender_id  " +
                "WHERE ts.id =:stipulationId";


        sqlParam.addValue("stipulationId", stipulationId);
        return namedJdbc.queryForObject(GetTenderStipulationById, sqlParam, new BeanPropertyRowMapper<>(StipulationInfo.class));


    }

    public Integer viewStipulationNextId(Integer nextId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationNextId = "select ts.id  from oiipcra_oltp.tender_stipulation as ts  " +
                "WHERE ts.id > :nextId ORDER BY ts.id LIMIT 1 ";
        sqlParam.addValue("nextId", nextId);
        try {
            return namedJdbc.queryForObject(GetTenderStipulationNextId, sqlParam, Integer.class);
        } catch (Exception e) {
            return null;
        }

    }

    public Integer viewStipulationPreviousId(Integer nextId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationPreviousId = "select ts.id  from oiipcra_oltp.tender_stipulation as ts  " +
                "WHERE ts.id < :nextId  ORDER BY ts.id DESC  LIMIT 1 ";
        sqlParam.addValue("nextId", nextId);
        try {
            return namedJdbc.queryForObject(GetTenderStipulationPreviousId, sqlParam, Integer.class);
        } catch (Exception e) {
            return null;
        }

    }

    public TenderNoticeResponse getTenderNotice(Integer noticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tn.id,tender.bid_id as bid,tn.tender_id,tn.bidding_type as biddingTypeId, ty.name as biddingType,\n" +
                "tn.type_of_work as workTypeId, work.name as workType, tn.work_sl_no_in_tcn as workSlNo, tn.work_identification_code,\n" +
                "tn.name_of_work,tn.tender_amount as tenderAmount, tn.paper_cost as paperCost, tn.emd_to_be_deposited as emdDeposited,\n" +
                "tn.time_for_completion as timeOfCompletion,  tn.is_active as active,tn.contact_no as contactNo,project.name as projectName,\n" +
                "tn.tender_level_id,level.level_name,tn.circle_id,tn.division_id, tn.ee_id as eeId,userm.name as eeName,tn.sub_division_id,tn.section_id, tn.sub_division_officer as subDivisionOfficerId,\n" +
                "tn.other_sub_division_officer as otherSubDivisionOfficerName,usermaster.name as subDivisionOfficerName,\n" +
                "tn.project_id, tn.tender_not_awarded_reason,tn.ee_type as eeType,eeType.name as eeTypeName,tn.other_ee  as otherEe,tn.ee_contact_no as eeContactNo from oiipcra_oltp.tender_notice as tn \n" +
                "left join oiipcra_oltp.works_bidding_type as ty on ty.id = tn.bidding_type  \n" +
                "left join oiipcra_oltp.tender_work_type as work on work.id = tn.type_of_work \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id= tn.tender_id \n" +
                "left join oiipcra_oltp.project_m as project on project.id = tn.project_id \n" +
                "left join oiipcra_oltp.tender_level_master as level on level.id=tn.tender_level_id\n" +
                "left join oiipcra_oltp.user_m as userm on userm.id=tn.ee_id\n" +
                "left join oiipcra_oltp.user_m as usermaster on usermaster.id=tn.sub_division_officer\n" +
                "left join oiipcra_oltp.ee_type as eeType on eeType.id=tn.ee_type \n" +
                "where tn.id=:noticeId";

        sqlParam.addValue("noticeId", noticeId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeResponse.class));
    }

    public Integer getNextId(Integer noticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id from oiipcra_oltp.tender_notice " +
                " WHERE id >:noticeId ORDER BY id LIMIT 1";
        sqlParam.addValue("noticeId", noticeId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getPreviousId(Integer noticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id from oiipcra_oltp.tender_notice " +
                " WHERE id <:noticeId ORDER BY id DESC LIMIT 1";

        sqlParam.addValue("noticeId", noticeId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<workProjectResponse> getWorkProject(Integer noticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT work.id as workId, work.created_by as createdBy,tank.id as tankId,tank.name_of_the_m_i_p as tankName,tender.bid_id as bidId, " +
                "notice.name_of_work as workName,work.is_active as active " +
                "FROM oiipcra_oltp.work_project_mapping as work " +
                "left join oiipcra_oltp.tank_m as tank on tank.id=work.tank_id " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=work.tender_id " +
                "left join oiipcra_oltp.tender_notice as notice on notice.id=work.tender_notice_id " +
                "where work.tender_notice_id=:noticeId";


        sqlParam.addValue("noticeId", noticeId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(workProjectResponse.class));
    }

    public TenderPublishDateCaluculateDto getTenderPublication(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT MIN(published_date) as tenderPublicationDate " +
                "FROM oiipcra_oltp.tender_published where tender_published_type=1 and tender_id=:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TenderPublishDateCaluculateDto.class));
    }

    public List<TenderPublishedInfo> getAllTenderPublication(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT published.id, tender_id, serial_no, tender_published_type,published.name,publishedType.name tenderPublishedName,newspaper_type, published.is_active as active ," +
                "published_date, publication_period_upto,newsPaperType.name as newsPaperTypeName,published.document " +
                "FROM oiipcra_oltp.tender_published as published " +
                "left join oiipcra_oltp.tender_published_type as publishedType on publishedType.id=published.tender_published_type " +
                "left join oiipcra_oltp.newspaper_type as newsPaperType on newsPaperType.id=published.newspaper_type " +
                "where published.tender_id=:tenderId  AND published.is_active= true";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderPublishedInfo.class));
    }

    public TenderPublishDateCaluculateDto getCorrigendumDate(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT MIN(published_date) as dateOfFirstCorrigendum,MAX(published_date) as dateOfSecondCorrigendum " +
                "FROM oiipcra_oltp.tender_published where tender_published_type=2 and tender_id=:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TenderPublishDateCaluculateDto.class));
    }

    public TenderPublishDateCaluculateDto getPublicationPeriodUpto(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT MAX(publication_period_upto) as publicationPeriodUpto FROM oiipcra_oltp.tender_published where tender_id=:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TenderPublishDateCaluculateDto.class));
    }

    public List<WorkProjectDto> getWorkProjectMappingListByTenderNoticeId(Integer tenderNoticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT wpm.id as id, wpm.tender_notice_id as tenderNoticeId, wpm.tank_id as tankId," +
                " wpm.created_by as createdBy, wpm.updated_by as updatedBy, wpm.tender_id as tenderId, " +
                "tank.project_id as projectId,tank.name_of_the_m_i_p as tankName" +
                " FROM oiipcra_oltp.work_project_mapping as wpm" +
                " left join oiipcra_oltp.tank_m_id as tank on tank.id = wpm.tank_id  "
                + " WHERE wpm.tender_notice_id=:tenderNoticeId";
        sqlParam.addValue("tenderNoticeId", tenderNoticeId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WorkProjectDto.class));
    }

    public boolean updateTenderDate(Integer tenderId, TenderPublishDateCaluculateDto type1, TenderPublishDateCaluculateDto type2, TenderPublishDateCaluculateDto type3) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.tender_m SET tender_publication_date=:publicationDate," +
                " publication_period_upto=:periodUpto" +
                //" date_of_first_corrigendum=:first,"+
                //" date_of_second_corrigendum=:second " +
                " WHERE id=:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("publicationDate", type1.getTenderPublicationDate());
        sqlParam.addValue("periodUpto", type3.getPublicationPeriodUpto());
        //sqlParam.addValue("first",type2.getDateOfFirstCorrigendum());
        //sqlParam.addValue("second",type2.getDateOfSecondCorrigendum());
        Integer update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    public BidComparisonDto getBidDetailsForComparison(Integer workId, Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetTenderNoticeDate = "SELECT tm.id as id,tm.bid_id as bidId,tn.work_sl_no_in_tcn as workSlNo,tm.technical_bid_opening_date as dateOfOpening,tn.work_identification_code as workIdentificationCode," +
                "tn.name_of_work as nameOfWork,estimate.estimated_amount as estimatedAmountForTender," +
                "tn.paper_cost as paperCostSpecified,tn.emd_to_be_deposited as emdToBeDeposited,tn.time_for_completion as timeForCompletion," +
                "tm.pre_bid_meeting_date as dateOfPreBidMeeting,tm.tender_publication_date as tenderPublicationDate,work.name as bidType  " +
                "from oiipcra_oltp.tender_m as tm  LEFT join oiipcra_oltp.tender_notice  as tn on tm.id=tn.tender_id " +
                "left join oiipcra_oltp.tender_stipulation as ts on tm.id=ts.tender_id " +
                "left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=tm.estimate_id " +
                "left join oiipcra_oltp.works_bidding_type as work on work.id=tn.bidding_type where tn.id=:workId and tn.tender_id=:tenderId";

        sqlParam.addValue("workId", workId);
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(GetTenderNoticeDate, sqlParam, new BeanPropertyRowMapper<>(BidComparisonDto.class));
    }

    public List<BidComparisonDto> distBlockByWorkSlNo(Integer workId, Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String distBlockByWorkSlNo = "select tnl.dist_id,district.district_name as districtName,tnl.block_id,block.block_name as blockName,division.mi_division_name as divisionName " +
                "FROM oiipcra_oltp.tender_notice  as tn " +
                "left join oiipcra_oltp.tender_notice_level_mapping as tnl on tnl.tender_notice_id=tn.id " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=tnl.dist_id  " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=tnl.block_id " +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=tnl.division_id " +
                "where tn.id=:workId and tn.tender_id=:tenderId";

        sqlParam.addValue("workId", workId);
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(distBlockByWorkSlNo, sqlParam, new BeanPropertyRowMapper<>(BidComparisonDto.class));
    }

    public List<TankInfo> getTankName(Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String distBlockByWorkSlNo = "select mapping.tank_id as tankId,project_id as projectId,name_of_the_m_i_p as tankName " +
                "from oiipcra_oltp.tender_notice_level_mapping as mapping \n" +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=mapping.tank_id\n" +
                "where mapping.is_active=true and mapping.tender_notice_id=:workId";

        sqlParam.addValue("workId", workId);

        return namedJdbc.query(distBlockByWorkSlNo, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }

    public List<BidComparisonDto> getWorkSlNoByBidId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetWorkSlNoByTenderId = " SELECT distinct(work_identification_code) as workIdentificationCode ,id as workId" +
                " from oiipcra_oltp.tender_notice  " +
                " where tender_id=:tenderId";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(GetWorkSlNoByTenderId, sqlParam, new BeanPropertyRowMapper<>(BidComparisonDto.class));
    }

    public List<WorkSlNoAndAgencyDto> getWorkSlNoForOpening(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetWorkSlNoByTenderId = " SELECT distinct(work_identification_code) as workIdentificationCode ,id as workId" +
                " from oiipcra_oltp.tender_notice  " +
                " where tender_id=:tenderId";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(GetWorkSlNoByTenderId, sqlParam, new BeanPropertyRowMapper<>(WorkSlNoAndAgencyDto.class));
    }

    //validation check for FinancialTurnOver
    public Double getTotalTenderAmount(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(tender_amount) from oiipcra_oltp.tender_notice where tender_id=:tenderId Group By tender_id";
        sqlParam.addValue("tenderId", bidId);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public StipulationInfo viewTenderStipulationByBidId(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationByTenderId = "SELECT id, tender_id, similar_work_value, similar_work_completion, annual_financial_turnover, previous_yr_weightage," +
                "credit_lines_amount, bid_capacity_turnover, completion_of_work_value_target, turnover_target, liquid_asset_target, is_active, created_by, created_on, updated_by, updated_on " +
                "FROM oiipcra_oltp.tender_stipulation where tender_id=:tenderId";

        sqlParam.addValue("tenderId", bidId);
        return namedJdbc.queryForObject(GetTenderStipulationByTenderId, sqlParam, new BeanPropertyRowMapper<>(StipulationInfo.class));
    }

    public List<AnnualFinancialTurnoverMaster> getFinancialTurnOver(Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationByTenderId = "SELECT id, agency_id, finyr_id, value, is_maximum, is_active, created_by, created_on, updated_by, updated_on " +
                "FROM oiipcra_oltp.finyr_turnover where agency_id=:agencyId";
        sqlParam.addValue("agencyId", agencyId);
        return namedJdbc.query(GetTenderStipulationByTenderId, sqlParam, new BeanPropertyRowMapper<>(AnnualFinancialTurnoverMaster.class));
    }

    public AnnualFinancialTurnoverMaster getMaximumFinancialTurnOver(Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderStipulationByTenderId = "SELECT id, agency_id, finyr_id, value, is_maximum, is_active, created_by, created_on, updated_by, updated_on " +
                "FROM oiipcra_oltp.finyr_turnover where agency_id=:agencyId and is_maximum=true ";
        sqlParam.addValue("agencyId", agencyId);
        return namedJdbc.queryForObject(GetTenderStipulationByTenderId, sqlParam, new BeanPropertyRowMapper<>(AnnualFinancialTurnoverMaster.class));
    }

    public Boolean updateMaximumBidCapacity(Double maximumBidCapacity, Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.bidder_details SET  max_bid_capacity=:maximumBidCapacity " +
                "WHERE tender_id=:tenderId ";
        sqlParam.addValue("tenderId", bidId);
        sqlParam.addValue("maximumBidCapacity", maximumBidCapacity);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    public List<CompletionOfSimilarTypeOfWork> getAllCompletedWorkByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllCompletedWorkByBidderId = "SELECT id, bidder_id, finyr_id, value, is_maximum, similar_work_amount, completed_amount, percentage_completed, is_active, created_by" +
                " FROM oiipcra_oltp.completion_of_similar_type_work WHERE bidder_id=:bidderId";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(GetAllCompletedWorkByBidderId, sqlParam, new BeanPropertyRowMapper<>(CompletionOfSimilarTypeOfWork.class));
    }

    public CompletionOfSimilarTypeOfWork getMaximumWorkCompleted(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetMaximumWorkCompleted = "SELECT id, bidder_id, finyr_id, value, is_maximum, similar_work_amount, completed_amount, percentage_completed, is_active, created_by" +
                " FROM oiipcra_oltp.completion_of_similar_type_work WHERE bidder_id=:bidderId and is_maximum=true ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.queryForObject(GetMaximumWorkCompleted, sqlParam, new BeanPropertyRowMapper<>(CompletionOfSimilarTypeOfWork.class));
    }

    public List<FinancialBidInfo> getFinancialBidDetailsById(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT fb.id, tm.financial_bid_opening_date,tn.tender_amount, fb.amount_quoted, fb.amount_percentage, fb.additional_performance_sec_required,  " +
                "fb.additional_submitted,fb.balance_aps_required, bidder.max_bid_capacity, fb.work_in_hand, fb.balance_bid_capacity,  " +
                "fb.review_tech_bid_date, fb.review_fin_bid_date, bidder.is_bid_awarded, tn.tender_not_awarded_reason," +
                "bidder.award_type " +
//                " tr.date_of_lottery,tr.acceptance_letter_no, tr.completion_period,tn.time_for_completion, tr.agreement_no,tr.legal_case  " +
                " from oiipcra_oltp.financial_bid_details as fb  " +
                "left join oiipcra_oltp.tender_m as tm on tm.id = fb.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = fb.work_id  " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.id = fb.bidder_id  ";
//                "left join oiipcra_oltp.tender_result as tr on tm.id = tr.tender_id  ";

        if (id > 0) {
            qry += " WHERE fb.id=:id ";
            sqlParam.addValue("id", id);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidInfo.class));
    }

    public List<FinancialBidInfo> getFinancialAbstract(Integer bidId, Integer workId, Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT result.id, result.tender_id, result.work_id, result.bidder_id, lottery_required, date_of_lottery, result.award_type,  " +
                "acceptance_letter_no, contract_id, completion_period,notice.tender_not_awarded_reason,completion_date, agreement_no, legal_case,notice.time_for_completion,    " +
                "remarks, result.is_active,awardType.name as awardedTypeName,fin.additional_performance_sec_required as additionalPerformanceSecRequired,fin.balance_aps_required as balanceApsRequired, fin.additional_submitted as additionalSubmitted,    " +
                "tender.bid_id as bidId,notice.work_sl_no_in_tcn as notice,agency.name as agencyName,result.tender_awarded as tenderAwarded ,result.tender_not_awarded_reason as tenderNotAwardedReason,result.review_fin_bid_date as reviewBidDate " +
                "FROM oiipcra_oltp.tender_result as result " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=result.tender_id " +
                "left join oiipcra_oltp.tender_notice as notice on notice.id=result.work_id " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.id=result.bidder_id " +
                "left join  oiipcra_oltp.award_type as awardType on awardType.id=bidder.award_type " +
                "left join oiipcra_oltp.financial_bid_details as fin on fin.bidder_id = bidder.id  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=bidder.agency_id " +
                "where result.is_active = true AND result.bidder_id=:bidderId AND result.work_id =:workId AND result.tender_id =:tenderId    ";
        sqlParam.addValue("tenderId", bidId);
        sqlParam.addValue("workId", workId);
        sqlParam.addValue("bidderId", bidderId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidInfo.class));
    }

    public List<FinYrDto> getFinancialYearForTenderOpening(String finYr) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select id,name from oiipcra_oltp.fin_year_m where name <=(select name from oiipcra_oltp.fin_year_m where name=(:finYr)) order by name desc limit 4 ";
        sqlParam.addValue("finYr", finYr);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinYrDto.class));
    }

    public List<NoticeListingDto> getTenderNoticeByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderNoticeByTenderId = "SELECT tn.id,tn.bidding_type as biddingTypeId, ty.name as biddingType,tn.type_of_work, work.name as workType, tn.work_sl_no_in_tcn, tn.work_identification_code, tn.name_of_work, " +
                "tn.tender_amount, tn.paper_cost, tn.emd_to_be_deposited, tn.time_for_completion,  " +
                "tn.contact_no, tn.tender_level_id,level.level_name as tenderLevel,tn.circle_id,tn.division_id, tn.ee_id, tn.sub_division_id, tn.sub_division_officer,  " +
                "tn.project_id,p.name as projectName,tn.tender_not_awarded_reason,tn.ee_id,userm.name as eeName,tn.ee_type as eeType,eeType.name as eeTypeName,tn.other_ee  as otherEe," +
                "tn.ee_contact_no as eeContactNo from oiipcra_oltp.tender_notice as tn  " +
                "left join oiipcra_oltp.tender_level_master as level on level.id =tn.tender_level_id  " +
                "left join oiipcra_oltp.works_bidding_type as ty on ty.id = tn.bidding_type  " +
                "left join oiipcra_oltp.tender_work_type as work on work.id = tn.type_of_work " +
                "left join oiipcra_oltp.project_m as p on p.id=tn.project_id " +
                "left join oiipcra_oltp.user_m as userm on userm.id=tn.ee_id " +
                "left join oiipcra_oltp.ee_type as eeType on eeType.id=tn.ee_type " +
                "WHERE tn.tender_id =:tenderId ";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(getTenderNoticeByTenderId, sqlParam, new BeanPropertyRowMapper<>(NoticeListingDto.class));
    }

    public Page<NoticeListingDto> getTenderNoticeByTenderIdListing(TenderNoticeListingDto tenderNotice) {
        PageRequest pageable = PageRequest.of(tenderNotice.getPage(), tenderNotice.getSize(), Sort.Direction.fromString(tenderNotice.getSortOrder()), tenderNotice.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String getTenderNoticeByTenderId = "SELECT tn.id,tn.bidding_type as biddingTypeId, ty.name as biddingType,tn.type_of_work, work.name as workType, tn.work_sl_no_in_tcn, tn.work_identification_code, tn.name_of_work, " +
                "tn.tender_amount, tn.paper_cost, tn.emd_to_be_deposited, tn.time_for_completion,  " +
                "tn.contact_no, tn.tender_level_id,level.level_name as tenderLevel,tn.circle_id,tn.division_id, tn.ee_id, tn.sub_division_id, tn.sub_division_officer,  " +
                "tn.project_id,p.name as projectName,tn.tender_not_awarded_reason,tn.ee_id,userm.name as eeName,tn.ee_type as eeType,eeType.name as eeTypeName,tn.other_ee  as otherEe," +
                "tn.ee_contact_no as eeContactNo from oiipcra_oltp.tender_notice as tn  " +
                "left join oiipcra_oltp.tender_level_master as level on level.id =tn.tender_level_id  " +
                "left join oiipcra_oltp.works_bidding_type as ty on ty.id = tn.bidding_type  " +
                "left join oiipcra_oltp.tender_work_type as work on work.id = tn.type_of_work " +
                "left join oiipcra_oltp.project_m as p on p.id=tn.project_id " +
                "left join oiipcra_oltp.user_m as userm on userm.id=tn.ee_id " +
                "left join oiipcra_oltp.ee_type as eeType on eeType.id=tn.ee_type " +
                "WHERE tn.tender_id =:tenderId ";
        sqlParam.addValue("tenderId", tenderNotice.getTenderId());
        resultCount = count(getTenderNoticeByTenderId, sqlParam);

        getTenderNoticeByTenderId += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();

        getTenderNoticeByTenderId += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<NoticeListingDto> tenderList = namedJdbc.query(getTenderNoticeByTenderId, sqlParam, new BeanPropertyRowMapper<>(NoticeListingDto.class));
        return new PageImpl<>(tenderList, pageable, resultCount);
    }


    public List<NoticeLevelMappingDto> getTenderLevelByNoticeId(Integer tenderNoticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderNoticeByTenderId = "select level.id, level.tender_notice_id, level.dist_id, dist.district_name as distName, level.block_id, block.block_name as blockName,  " +
                "level.tank_id as tankId,tank. name_of_the_m_i_p as tankName,tank. project_id as projectId from oiipcra_oltp.tender_notice_level_mapping as level " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id = level.dist_id  " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id = level.block_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = level.tank_id  " +
                "WHERE level.tender_notice_id =:tenderNoticeId AND level.is_active =true ";

        sqlParam.addValue("tenderNoticeId", tenderNoticeId);
        return namedJdbc.query(getTenderNoticeByTenderId, sqlParam, new BeanPropertyRowMapper<>(NoticeLevelMappingDto.class));
    }

    public TenderNotice getTenderNoticeDetailsByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderNoticeDetailsByTenderId = "SELECT  sum(tn.emd_to_be_deposited) as emdToBeDeposited from oiipcra_oltp.tender_notice as tn  " +
                "WHERE tn.tender_id =:tenderId ";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(getTenderNoticeDetailsByTenderId, sqlParam, new BeanPropertyRowMapper<>(TenderNotice.class));
    }


    public TenderNotice getTenderNoticeById(Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderNoticeById = "SELECT tn.id, tn.bidding_type, tn.type_of_work, tn.work_sl_no_in_tcn, tn.work_identification_code, tn.name_of_work, " +
                "tn.tender_amount, tn.paper_cost, tn.emd_to_be_deposited, tn.time_for_completion,  " +
                "tn.contact_no, tn.tender_level_id,tn.circle_id,tn.division_id, tn.ee_id, tn.sub_division_id, tn.sub_division_officer,  " +
                "tn.project_id, tn.tender_not_awarded_reason from oiipcra_oltp.tender_notice as tn  " +
                "WHERE tn.id =:workId ";

        sqlParam.addValue("workId", workId);
        return namedJdbc.queryForObject(getTenderNoticeById, sqlParam, new BeanPropertyRowMapper<>(TenderNotice.class));
    }


    public List<BidderDetailsDto> getBidderDetailsByTenderId(Integer tenderId, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getBidderDetailsByTenderId = "SELECT bidder.id,bidder.tender_id,bidder.work_id,bidder.agency_id,agency.name as agencyName,bidder.license_validity," +
                "completion_work_value_qualified," +
                "turn_over_qualified,liquid_asset_qualified from oiipcra_oltp.bidder_details  as bidder " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id " +
                "WHERE bidder.tender_id =:tenderId and bidder.work_id=:workId";

        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("workId", workId);
        return namedJdbc.query(getBidderDetailsByTenderId, sqlParam, new BeanPropertyRowMapper<>(BidderDetailsDto.class));

    }

    public List<AgencyMaster> getAllAgencyName(Integer tenderId, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAllAgencyName = "select id,name from oiipcra_oltp.agency_m ";
/*        " where id NOT IN " +
        "(select agency_id from oiipcra_oltp.bidder_details where tender_id=:tenderId and work_id=:workId)";*/
        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("workId", workId);
        return namedJdbc.query(getAllAgencyName, sqlParam, new BeanPropertyRowMapper<>(AgencyMaster.class));
    }

    public List<AgencyDto> getAgencyDetailsById(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAgencyDetailsById = "SELECT DISTINCT agency.id,bidder.id as bidderId, agency.license_class_id, license.name as classOfLicense, agency.license_validity, agency.gstin_no,  " +
                "agency.pan_no as panNo,agency.phone,agency.name as name,bidder.work_id as workId from oiipcra_oltp.agency_m as agency  " +
                "left join oiipcra_oltp.license_class as license on license.id = agency.license_class_id   " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.agency_id = agency.id  " +
                "WHERE agency.id=:id";
        sqlParam.addValue("id", id);
        return namedJdbc.query(getAgencyDetailsById, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public Integer getBidderIdExistsOrNotByAgencyIdAndNoticeId(Integer agencyId, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAgencyDetailsById = "SELECT max(id) from oiipcra_oltp.bidder_details where agency_id=:agencyId and work_id=:workId";
        sqlParam.addValue("agencyId", agencyId);
        sqlParam.addValue("workId", workId);
        return namedJdbc.queryForObject(getAgencyDetailsById, sqlParam, Integer.class);
    }


    public AgencyDto getAgencyDetailsById2(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAgencyDetailsById = "SELECT agency.id, agency.license_class_id, license.name as classOfLicense, agency.license_validity, agency.gstin_no,   " +
                "agency.phone from oiipcra_oltp.agency_m as agency  " +
                "left join oiipcra_oltp.license_class as license on license.id = agency.license_class_id  " +
                "WHERE agency.id=:id";
        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(getAgencyDetailsById, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public List<BidderCategoryDto> getAllBidderCategory() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAllBidderCategory = "SELECT id, name from oiipcra_oltp.bidder_category_m";
        return namedJdbc.query(getAllBidderCategory, sqlParam, new BeanPropertyRowMapper<>(BidderCategoryDto.class));
    }

    public List<EmdDepositTypeDto> getDepositType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getDepositType = "SELECT id, name from oiipcra_oltp.emd_deposit_type";
        return namedJdbc.query(getDepositType, sqlParam, new BeanPropertyRowMapper<>(EmdDepositTypeDto.class));
    }

    public List<CompletionOfSimilarTypeOfWorkDto> getCompletionOfSimilarWorkTypeByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getCompletionOfSimilarWorkTypeByBidderId = "SELECT similarWorkType.id, bidder_id, finyr_id,year.name as finYrName,round(value, 2) as value, " +
                "similar_work_amount, completed_amount, round(percentage_completed, 2) as percentage_completed ,executed_year as executedYear,yearExecuted.name as executedYearName " +
                "FROM oiipcra_oltp.completion_of_similar_type_work  as similarWorkType  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id=similarWorkType.finyr_id " +
                "left join oiipcra_oltp.fin_year_m as yearExecuted on yearExecuted.id=similarWorkType.executed_year ";
        if (bidderId != null && bidderId > 0) {
            getCompletionOfSimilarWorkTypeByBidderId += " where bidder_id=:bidderId ";
        }
        getCompletionOfSimilarWorkTypeByBidderId += " order by  year.name asc ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(getCompletionOfSimilarWorkTypeByBidderId, sqlParam, new BeanPropertyRowMapper<>(CompletionOfSimilarTypeOfWorkDto.class));
    }

    public List<CompletionOfSimilarTypeOfWorkDto> getCompletionOfSimilarWorkMax(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getCompletionOfSimilarWorkTypeByBidderId = "SELECT similarWorkType.id, bidder_id, finyr_id,year.name as finYrName,value, is_maximum," +
                "similar_work_amount, completed_amount, percentage_completed ,executed_year as executedYear,yearExecuted.name as executedYearName " +
                "FROM oiipcra_oltp.completion_of_similar_type_work  as similarWorkType  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id=similarWorkType.finyr_id " +
                "left join oiipcra_oltp.fin_year_m as yearExecuted on yearExecuted.id=similarWorkType.executed_year " +
                "where bidder_id=:bidderId order by similarWorkType.value LIMIT 1";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(getCompletionOfSimilarWorkTypeByBidderId, sqlParam, new BeanPropertyRowMapper<>(CompletionOfSimilarTypeOfWorkDto.class));
    }

    public CompletionOfSimilarTypeOfWorkDto getCompletionOfSimilarWorkTypeByBidderIds(List<Integer> bidderIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getCompletionOfSimilarWorkTypeByBidderId = "SELECT similarWorkType.id, bidder_id, finyr_id,year.name as finYrName,value, is_maximum, similar_work_amount, completed_amount, percentage_completed " +
                "FROM oiipcra_oltp.completion_of_similar_type_work  as similarWorkType " +
                "left join oiipcra_oltp.fin_year_m as year on year.id=similarWorkType.finyr_id " +
                "where bidder_id IN (:bidderId) ORDER BY similarWorkType.created_on DESC limit 1";
        sqlParam.addValue("bidderId", bidderIds);
        return namedJdbc.queryForObject(getCompletionOfSimilarWorkTypeByBidderId, sqlParam, new BeanPropertyRowMapper<>(CompletionOfSimilarTypeOfWorkDto.class));
    }

    public Integer getAgencyByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAgencyByBidderId = "SELECT agency_id from oiipcra_oltp.bidder_details where id=:bidderId ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.queryForObject(getAgencyByBidderId, sqlParam, Integer.class);
    }

    public AgencyInfo getAgencyIdAndNameByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAgency = "SELECT bidder.agency_id as agencyId,agency.name as agencyName from oiipcra_oltp.bidder_details as bidder " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=bidder.agency_id where bidder.id=:bidderId  ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.queryForObject(getAgency, sqlParam, new BeanPropertyRowMapper<>(AgencyInfo.class));
    }

    public List<Integer> getBidderIdsByAgencyId(Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getBidderIdsByAgencyId = "select Distinct(bidder_id) from oiipcra_oltp.completion_of_similar_type_work where agency_id=:agencyId";
        sqlParam.addValue("agencyId", agencyId);
        return namedJdbc.queryForList(getBidderIdsByAgencyId, sqlParam, Integer.class);
    }

    public List<AnnualFinancialTurnoverMasterDto> getAnnualFinancialTurnOver(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAnnualFinancialTurnOver = " SELECT turnover.id,turnover.bidder_id as bidderId, turnover.finyr_id," +
                " financialYear.name as financialYearName,round(turnover.value, 2) as value,turnover.is_maximum as maximum, " +
                " round(turnover.equivalent, 2) as equivalent,round(turnover.turn_over_required, 2) as turnOverRequired,  " +
                " turnover.is_active FROM oiipcra_oltp.finyr_turnover as turnover " +
                " left join oiipcra_oltp.fin_year_m as financialYear on financialYear.id=turnover.finyr_id where turnover.is_active = true  ";
        /*String getAnnualFinancialTurnOver = "SELECT turnover.id, turnover.finyr_id,financialYear.name as financialYearName," +
                "turnover.value,turnover.is_maximum as maximum,turnover.is_active,turnover.value*stipulation.bid_capacity_turnover as maxBidCapacity " +
                "FROM oiipcra_oltp.finyr_turnover as turnover " +
                "left join oiipcra_oltp.bidder_details as bidder on turnover.bidder_id=bidder.id " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on bidder.tender_id=stipulation.tender_id " +
                "left join oiipcra_oltp.fin_year_m as financialYear on financialYear.id=turnover.finyr_id " +
                "WHERE turnover.agency_id=:agencyId";*/

        if (bidderId > 0 && bidderId != null) {
            getAnnualFinancialTurnOver += " and turnover.bidder_id=:bidderId ";
        }
        getAnnualFinancialTurnOver += " order by financialYear.name asc ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(getAnnualFinancialTurnOver, sqlParam, new BeanPropertyRowMapper<>(AnnualFinancialTurnoverMasterDto.class));
    }

    public List<LiquidAssetAvailability> getLiquidAssetAvailabilityByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getLiquidAssetAvailabilityByBidderId = "SELECT id,bidder_id, bank_name, credit_amount, liquidity_amount, total_liquid_asset, is_active, created_by, created_on, updated_by, updated_on " +
                "FROM oiipcra_oltp.liquid_asset_availability ";
        if (bidderId > 0 && bidderId != null) {
            getLiquidAssetAvailabilityByBidderId += " WHERE bidder_id=:bidderId ";
        }
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(getLiquidAssetAvailabilityByBidderId, sqlParam, new BeanPropertyRowMapper<>(LiquidAssetAvailability.class));
    }

    public List<ExpenditureYearMonthDto> getExpenditureDataByTankId(ExpenditureWorkProgress expenditure) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getExpenditureDataByTankId = " ";
        getExpenditureDataByTankId += "select distinct yearMonth.finyrId,yearMonth.finyrName,yearMonth.monthId, yearMonth.monthName,t.expenditure,cm.agreement_number,am.name as agencyName  " +
                ",cm.contract_amount,cm.stipulated_date_of_completion,cm.stipulated_date_of_comencement, (cm.stipulated_date_of_completion::date-cm.stipulated_date_of_comencement::date) as totalCompletionPeriod from   " +
                "(select finyr.id as finyrId,finyr.name as finyrName,month.id as monthId,month.name as monthName  " +
                "from oiipcra_oltp.month_m as month,oiipcra_oltp.fin_year_m as finyr) as yearMonth   " +
                "left join (select ed.month_id as monthId,ed.finyr_id as finyrId,sum(ed.value) over (partition by month_id,ed.finyr_id) as expenditure  " +
                ",ed.contract_id  " +
                "FROM oiipcra_oltp.expenditure_data as ed  " +
                "left join oiipcra_oltp.expenditure_mapping as edmapping on edmapping.expenditure_id=ed.id   where edmapping.is_active=true  ";

        if (expenditure.getTankId() != null && expenditure.getTankId() > 0) {
            getExpenditureDataByTankId += " and edmapping.tank_id=:tankId ";
            sqlParam.addValue("tankId", expenditure.getTankId());
        }
        if (expenditure.getBidId() != null && expenditure.getBidId() > 0) {
            getExpenditureDataByTankId += " and edmapping.tender_id=:tenderId ";
            sqlParam.addValue("tenderId", expenditure.getBidId());
        }
        if (expenditure.getWorkId() != null && expenditure.getWorkId() > 0) {
            getExpenditureDataByTankId += " and edmapping.tender_notice_id=:tenderNoticeId ";
            sqlParam.addValue("tenderNoticeId", expenditure.getWorkId());
        }

        if (expenditure.getEstimateId() != null && expenditure.getEstimateId() > 0) {
            getExpenditureDataByTankId += " and edmapping.estimate_id=:estimateId ";
            sqlParam.addValue("estimateId", expenditure.getEstimateId());
        }

        if (expenditure.getContractId() != null && expenditure.getContractId() > 0) {
            getExpenditureDataByTankId += " and edmapping.contract_id=:contractId ";
            sqlParam.addValue("contractId", expenditure.getContractId());
        }

//        if (estimateIds != null && !estimateIds.isEmpty()) {
//            getExpenditureDataByTankId += "and edmapping.estimate_id in(:estimateIds)";
//            sqlParam.addValue("estimateIds", estimateIds);
//        }

//        if (contractIds != null && contractIds.size() > 0) {
//            getExpenditureDataByTankId += "and edmapping.contract_id in(:contractIds)";
//            sqlParam.addValue("contractIds", contractIds);
//        }


        getExpenditureDataByTankId += " ) as t on t.monthId=yearMonth.monthId and t.finyrId=yearMonth.finyrId  " +
                "left join oiipcra_oltp.contract_m as cm on cm.id = t.contract_id  " +
                "left join oiipcra_oltp.agency_m as am on am.id = cm.agency_id  " +
                "order By yearMonth.finyrId ,yearMonth.monthId  ";
        return namedJdbc.query(getExpenditureDataByTankId, sqlParam, new BeanPropertyRowMapper<>(ExpenditureYearMonthDto.class));
    }

    public TenderNoticeDto getTenderNoticeByBidId(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderNoticeByBidId = "SELECT sum(tn.tender_amount) as tenderAmount from oiipcra_oltp.tender_notice as tn  " +
                "WHERE tn.tender_id =:bidId ";
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.queryForObject(getTenderNoticeByBidId, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class));
    }


    public TenderStipulationDto getLiquidAssetByBidId(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getLiquidAssetByBidId = "SELECT sum( stipulation.liquid_asset_target) as liquidAssetTarget from oiipcra_oltp.tender_stipulation as stipulation  " +
                "WHERE stipulation.tender_id =:bidId ";
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.queryForObject(getLiquidAssetByBidId, sqlParam, new BeanPropertyRowMapper<>(TenderStipulationDto.class));
    }

    public LiquidAssetAvailability getLiquidAssetByAgencyId(Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getLiquidAssetByBidderId = "SELECT agency_id,total_liquid_asset from oiipcra_oltp.liquid_asset_availability  " +
                "Where agency_id=:agencyId";
        sqlParam.addValue("agencyId", agencyId);
        return namedJdbc.queryForObject(getLiquidAssetByBidderId, sqlParam, new BeanPropertyRowMapper<>(LiquidAssetAvailability.class));
    }

    public BidderDetails getBidderDetailsByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getBidderDetailsByBidderId = "SELECT bidder.id,bidder.emd_amount from oiipcra_oltp.bidder_details as bidder " +
                "where bidder.id =:bidderId";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.queryForObject(getBidderDetailsByBidderId, sqlParam, new BeanPropertyRowMapper<>(BidderDetails.class));
    }

    public BidderDetails getBidderDetailsById(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getBidderDetailsById = "SELECT id, tender_id, work_id,agency_id as agencyId,license_validity,affidavit_validity,paper_cost_amount,  " +
                "paper_cost_submission_type,is_paper_cost_valid,emd_bank_name,emd_amount,emd_deposit_type,is_emd_valid,completion_work_value_qualified,  " +
                "turn_over_qualified,liquid_asset_qualified,is_bid_qualified,max_bid_capacity,work_in_hand,bid_result,award_type,  " +
                "bid_final_rank,bidder_category_id,is_credit_qualified,is_bid_awarded from oiipcra_oltp.bidder_details  " +
                " WHERE id =:bidderId ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.queryForObject(getBidderDetailsById, sqlParam, new BeanPropertyRowMapper<>(BidderDetails.class));

    }

    public List<BidderDetailsDto> getBidderDetailsByIdforPdf(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getBidderDetailsById = "SELECT id, tender_id as tenderId, work_id as workId,agency_id as agencyId,license_validity as licenseValidity,affidavit_validity as affidavitValidity,paper_cost_amount as paperCostAmount ,  " +
                "paper_cost_submission_type as paperCostSubmissionType,is_paper_cost_valid as isPaperCostValid,emd_bank_name as emdBankName,emd_amount as emdAmount,emd_deposit_type as emdDepositType,is_emd_valid as isEmdValid,completion_work_value_qualified as completionWorkValueQualified,  " +
                "turn_over_qualified as turnOverQualified,case when(liquid_asset_qualified=true) then 'Yes' else 'No' end as liquidAssetQualifiedString,is_bid_qualified as isBidQualified,max_bid_capacity as maxBidCapacity,work_in_hand as workInHand,bid_result as bidResult,award_type as awardType,  " +
                "bid_final_rank as bidFinalRank,bidder_category_id as bidderCategoryId,is_credit_qualified as isCreditQualified,is_bid_awarded as isBidAwarded from oiipcra_oltp.bidder_details  " +
                "  ";
        if (bidderId > 0 && bidderId != null) {
            getBidderDetailsById += " WHERE id =:bidderId ";
        }
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(getBidderDetailsById, sqlParam, new BeanPropertyRowMapper<>(BidderDetailsDto.class));

    }

    public List<TenderCorrigendum> getTenderCorrigendumListByTenderId(int tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderCorrigendumByTenderId = "SELECT id, tender_id, technical_bid_opening_date, bid_submission_date, financial_bid_opening_date, name_of_work," +
                " pre_bid_meeting_type, pre_bid_meeting_date, tender_publication_date, publication_period_upto, tender_opening_date, date_of_tender_notice" +
                " from oiipcra_oltp.tender_corrigendum WHERE tender_id =:tenderId  and is_active=true";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(getTenderCorrigendumByTenderId, sqlParam, new BeanPropertyRowMapper<>(TenderCorrigendum.class));
    }


    public DraftTenderNoticeDto getAllTenderNotice(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllTenderNotice = "select distinct tender.id,tender.bid_id,tender.name_of_work,count(tnotice.id) OVER(PARTITION By tnotice.tender_id) as no_of_works,min(tnotice.tender_amount) OVER(PARTITION By tender_id) as varies_from,max(tnotice.tender_amount) OVER(PARTITION By tender_id) as varies_to,  " +
                "SUM(tnotice.tender_amount) over(partition by tender_id) as tender_amount,tender.technical_bid_opening_date as availabilityDocumentFrom,tender.bid_submission_date as availabilityDocumentTo, " +
                "tender.technical_bid_opening_date as receiptBidFrom,tender.bid_submission_date as receiptBidTo " +
                ",tender.pre_bid_meeting_date,tender.tender_opening_date, tender.tender_publication_date " +
                "from oiipcra_oltp.tender_m as tender " +
                "left join oiipcra_oltp.tender_notice as tnotice  on tnotice.tender_id=tender.id " +
                "WHERE tender.id=:tenderId";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(GetAllTenderNotice, sqlParam, new BeanPropertyRowMapper<>(DraftTenderNoticeDto.class));
    }

    public List<FormGDto> getFormGDetails(Integer finYr, String issueNo, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        List<FormGDto> formG = new ArrayList<>();
        String qry = "";
        qry = "select finyr.name as finyr,agency.name as agencyName,license.name as licenseClass,agency.gstin_no,tender.bid_id||' / '||tnotice.work_identification_code as bid_workId,   \n" +
                "tnotice.id,dist.district,scheme.name as scheme,to_char(tender.tender_opening_date,'DD-MM-YYYY'),to_char(tender.technical_bid_opening_date,'DD-MM-YYYY'),tnotice.tender_amount,to_char(tnotice.tender_amount,'999G999G999G999D99') as tenderAmountString,bidder.agency_id,\n" +
                "to_char(sum(tnotice.tender_amount)over (partition by agency.id,tender.finyr_id) ,'999G999G999G999D99') as sumString," +
                " to_char(technical_bid_opening_date,'DD-MM-YYYY')  dateString ,  \n" +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true   \n" +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as validity_of_tender,  \n" +
                "awarded.name as awarded  \n" +
                "from oiipcra_oltp.bidder_details as bidder \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id   \n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id   \n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id   \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id \n" +
                "left join oiipcra_oltp.fin_year_m as finyr on tender.finyr_id=finyr.id  \n" +
                "left join(select string_agg(distinct b.district_name,', ') as district,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.district_boundary as b on a.dist_id=b.dist_id group by a.tender_notice_id) as dist on dist.tender_notice_id=tnotice.id   \n" +
                "left join oiipcra_oltp.project_m as scheme on scheme.id=tnotice.project_id \n" +
                "left join oiipcra_oltp.award_type as awarded on awarded.id=bidder.award_type  ";


        if (agencyId > 0) {
            qry += " where finyr.id=:finYr and agency_id=:agencyId  order by bidder.id desc  ";
        }

        sqlParam.addValue("finYr", finYr);
        sqlParam.addValue("agencyId", agencyId);
        sqlParam.addValue("issueNo", issueNo);

        try {
            formG = namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FormGDto.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return formG;
    }


    public List<TechnicalBidAbstractDto> getTechnicalBidAbstract(Integer bidId, java.sql.Date bidOpeningDate, Integer bidStatus) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTechnicalBidAbstractDetails = "";

        if (bidStatus == 1) {
            getTechnicalBidAbstractDetails += "select tender.bid_id,tender.technical_bid_opening_date,tnotice.work_sl_no_in_tcn,tnotice.work_identification_code as packageId,round(tnotice.tender_amount/100000,2) as tenderAmount,agency.name as agency,license.name as licenseClass, " +
                    "agency.license_validity,to_char(agency.license_validity,'dd-Mon-yyyy')as validity,case when (agency.license_validity>tender.technical_bid_opening_date) then 'License Ok' else 'License Expired' end as license_on_TenderOpening, " +
                    "case when(bidder.is_emd_valid is true) then 'Valid' else 'NotValid' end as emd_validity, case when (completion_work_value_qualified is true) then 'Qualified-2' else 'Not Qualified-2' end as completion_work_value_qualified,  " +
                    "stipulation.turnover_target,case when trim(to_char(maxturnover.maxTurnOver,'999G999G999G999D99'))='.00' then '0.00'  " +
                    "else trim(to_char(maxturnover.maxTurnOver,'999G999G999G999D99')) end as maxTurnOver,case when (turn_over_qualified is true) then 'Qualified-3' else 'Not Qualified-3' end as financialTurnoverValidity,  " +
                    "case when (is_credit_qualified is true) then 'Qualified-4' else 'Not Qualified-4' end as credit_facility_availability, " +
                    "round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnOver, " +
                    "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true " +
//                    " and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true " +
                    " and bidder.is_credit_qualified) then 'Qualified' else 'Not Qualified' end as overall_bid_validity  " +
                    "from oiipcra_oltp.tender_m as tender  " +
                    "left join oiipcra_oltp.bidder_details as bidder on tender.id=bidder.tender_id  " +
                    "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                    "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                    "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                    "left join (select equivalent as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id   " +
                    "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  " +
                    "where tender.id=:bidId and tender.technical_bid_opening_date=:bidOpeningDate and bidder.is_bid_qualified=true  " +
                    "order by tnotice.work_sl_no_in_tcn,agency.name  ";
        }

        if (bidStatus == 0) {
            getTechnicalBidAbstractDetails += "select tender.bid_id,tender.technical_bid_opening_date,tnotice.work_sl_no_in_tcn,tnotice.work_identification_code as packageId,round(tnotice.tender_amount/100000,2) as tenderAmount,agency.name as agency,license.name as licenseClass, " +
                    "agency.license_validity,to_char(agency.license_validity,'dd-Mon-yyyy')as validity,case when (agency.license_validity>tender.technical_bid_opening_date) then 'License Ok' else 'License Expired' end as license_on_TenderOpening, " +
                    "case when(bidder.is_emd_valid is true) then 'Valid' else 'NotValid' end as emd_validity, case when (completion_work_value_qualified is true) then 'Qualified-2' else 'Not Qualified-2' end as completion_work_value_qualified,  " +
                    "stipulation.turnover_target,case when trim(to_char(maxturnover.maxTurnOver,'999G999G999G999D99'))='.00' then '0.00'  " +
                    "else trim(to_char(maxturnover.maxTurnOver,'999G999G999G999D99')) end as maxTurnOver,case when (turn_over_qualified is true) then 'Qualified-3' else 'Not Qualified-3' end as financialTurnoverValidity," +
                    "round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnOver,  " +
                    "case when (is_credit_qualified is true) then 'Qualified-4' else 'Not Qualified-4' end as credit_facility_availability, " +
                    "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true " +
//                    " and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true " +
                    " and bidder.is_credit_qualified) then 'Qualified' else 'Not Qualified' end as overall_bid_validity  " +
                    "from oiipcra_oltp.tender_m as tender  " +
                    "left join oiipcra_oltp.bidder_details as bidder on tender.id=bidder.tender_id  " +
                    "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                    "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                    "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                    "left join (select equivalent as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id   " +
                    "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  " +
                    "where tender.id=:bidId and tender.technical_bid_opening_date=:bidOpeningDate and bidder.is_bid_qualified=false  " +
                    "order by tnotice.work_sl_no_in_tcn, agency.name ";
        }

        if (bidStatus == -1) {
            getTechnicalBidAbstractDetails += " select tender.bid_id,tender.technical_bid_opening_date,tnotice.work_sl_no_in_tcn,tnotice.work_identification_code as packageId,round(tnotice.tender_amount/100000,2) as tenderAmount,agency.name as agency,license.name as licenseClass, " +
                    "agency.license_validity,to_char(agency.license_validity,'dd-Mon-yyyy')as validity,case when (agency.license_validity>tender.technical_bid_opening_date) then 'License Ok' else 'License Expired' end as license_on_TenderOpening, " +
                    "case when(bidder.is_emd_valid is true) then 'Valid' else 'NotValid' end as emd_validity, case when (completion_work_value_qualified is true) then 'Qualified-2' else 'Not Qualified-2' end as completion_work_value_qualified, " +
                    "stipulation.turnover_target,case when trim(to_char(maxturnover.maxTurnOver,'999G999G999G999D99'))='.00' then '0.00'  " +
                    "else trim(to_char(maxturnover.maxTurnOver,'999G999G999G999D99')) end as maxTurnOver,case when (turn_over_qualified is true) then 'Qualified-3' else 'Not Qualified-3' end as financialTurnoverValidity,  " +
                    "case when (is_credit_qualified is true) then 'Qualified-4' else 'Not Qualified-4' end as credit_facility_availability, " +
                    "round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnOver, " +
                    "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true " +
//                    "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true" +
                    " and bidder.is_credit_qualified) then 'Qualified' else 'Not Qualified' end as overall_bid_validity  " +
                    "from oiipcra_oltp.tender_m as tender  " +
                    "left join oiipcra_oltp.bidder_details as bidder on tender.id=bidder.tender_id  " +
                    "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                    "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                    "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                    "left join (select equivalent as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id   " +
                    "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  " +
                    "where tender.id=:bidId and tender.technical_bid_opening_date=:bidOpeningDate  order by tnotice.work_sl_no_in_tcn, agency.name ";
        }
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        sqlParam.addValue("bidStatus", bidStatus);
        return namedJdbc.query(getTechnicalBidAbstractDetails, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAbstractDto.class));
    }

    public List<TechnicalBidAbstractDto> getTenderStipulation(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT similar_work_value AS similarWorkValue, annual_financial_turnover AS annualFinancialTurnover FROM oiipcra_oltp.tender_stipulation WHERE tender_id=:tenderId ";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAbstractDto.class));
    }

    public List<FinancialBidopeningAbstractDto> getFinancialBidOpeningAbstract(Integer bidId, java.sql.Date tenderOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select tender.bid_id,tender.financial_bid_opening_date,tnotice.work_identification_code as workId , tnotice.work_sl_no_in_tcn, " +
                " agency.name as agency,license.name as licenseClass,to_char(tnotice.tender_amount,'999G99G99G999D99') as tenderAmountString,to_char(finbid.amount_quoted,'999G99G99G999D99') as bidP," +
                "to_char((finbid.amount_quoted/tnotice.tender_amount)*100-100,'999G99G99G999D99') as rateExcessLess,to_char(finbid.additional_performance_sec_required,'999G99G99G999D99') as bar,award.name as awardType " +
                "from  oiipcra_oltp.tender_m as tender " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id " +
                "left join oiipcra_oltp.bidder_details as bidder on tnotice.id=bidder.work_id and tender.id=bidder.tender_id " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id " +
                "left join oiipcra_oltp.financial_bid_details as finbid on finbid.bidder_id=bidder.id " +
                "left join oiipcra_oltp.award_type as award on award.id=bidder.award_type " +
                "where award.id in(1,2,7) and tender.id=:bidId and tender.financial_bid_opening_date=:tenderOpeningDate " +
//                "order by bid_final_rank";
                "order by tnotice.work_sl_no_in_tcn ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidopeningAbstractDto.class));
    }

    public FinancialBidopeningAbstractDto getSumOfEstdCostandBidprice(Integer bidId, java.sql.Date tenderOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select to_char(sum(tnotice.tender_amount),'999G999G999G999D99') as sumTm,to_char(sum(finbid.amount_quoted),'999G999G999G999D99') as sumBidP " +
                "from  oiipcra_oltp.tender_m as tender " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id " +
                "left join oiipcra_oltp.bidder_details as bidder on tnotice.id=bidder.work_id and tender.id=bidder.tender_id " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id " +
                "left join oiipcra_oltp.financial_bid_details as finbid on finbid.bidder_id=bidder.id " +
                "left join oiipcra_oltp.award_type as award on award.id=bidder.award_type " +
                "where award.id in(1,2,7) and tender.id=:bidId and tender.financial_bid_opening_date=:tenderOpeningDate ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidopeningAbstractDto.class));
    }


    public List<FinancialBidopeningAbstractDto> getTechnicalBidOpeningResult(Integer tenderId, java.sql.Date bidOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select bidder.id,tender.bid_id,tender.technical_bid_opening_date,   " +
//                "tnotice.work_sl_no_in_tcn,tnotice.work_identification_code as workId,tnotice.tender_amount,agency.id,agency.name as agency,license.name as licenseClass,   " +
//                "agency.license_validity,bidder.emd_amount as emd_deposited,to_char(bidder.emd_amount,'999G999G999G999D99')as emdDepositedString,  " +
//                "to_char(agency.license_validity,'dd-Mon-yyyy')as validity,  " +
//                "case when(bidder.turn_over_qualified = true) then 'OK' else 'NO' end || '/'|| case when(bidder.completion_work_value_qualified = true) then 'OK' else 'NO' end as financialValidityWork,   " +
//                "maxturnover.maxTurnOver,to_char(maxturnover.maxTurnOver,'999G999G999G999D99')as turnOver,liquidAsset.credit_amount as creditAvailability,to_char(tnotice.tender_amount,'999G999G999G999D99') as tenderAmountString,   " +
//                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true  " +
//                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as overall_bid_validity  " +
//                "from oiipcra_oltp.tender_m as tender  " +
//                "left join oiipcra_oltp.bidder_details as bidder on tender.id=bidder.tender_id  " +
//                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
//                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
//                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
//                "left join (select value as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id  " +
//                "left join oiipcra_oltp.liquid_asset_availability as liquidAsset on liquidAsset.bidder_id=bidder.id  " +
//                "where tender.id=:tenderId and tender.technical_bid_opening_date=:bidOpeningDate order by work_sl_no_in_tcn   ";

        String qry = "select distinct bidder.id,tender.bid_id,tender.technical_bid_opening_date,   " +
                "tnotice.work_sl_no_in_tcn::text as slNo ,tnotice.work_identification_code as workId,tnotice.tender_amount,agency.id,agency.name as agency,license.name as licenseClass,    " +
                "agency.license_validity,bidder.emd_amount as emd_deposited," +
//                " to_char(bidder.emd_amount,'999G99G99G999D99')as emdDepositedString,  " +
                "case when trim(to_char(bidder.emd_amount,'999G99G99G999D99'))='.00' then '0.00' " +
                "else trim(to_char(bidder.emd_amount,'999G99G99G999D99')) end as emdDepositedString, " +
                "to_char(agency.license_validity,'dd-Mon-yyyy')as validity,  " +
                "case when(bidder.turn_over_qualified = true) then 'OK' else 'NO' end || '/'|| case when(bidder.completion_work_value_qualified = true) then 'OK' else 'NO' end as financialValidityWork,   " +
                "maxturnover.maxTurnOver,case when trim(to_char(maxturnover.maxTurnOver,'999G99G99G999D99'))='.00' then '0.00'  " +
                "else trim(to_char(maxturnover.maxTurnOver,'999G99G99G999D99')) end as turnOver, " +
                "liquidAsset.credit_amount as creditAvailability,to_char(tnotice.tender_amount,'999G99G99G999D99') as tenderAmountString,    " +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true " +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as overall_bid_validity   " +
                "from oiipcra_oltp.tender_m as tender   " +
                "left join oiipcra_oltp.bidder_details as bidder on tender.id=bidder.tender_id  and bidder.is_active=true  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  and tnotice.is_active=true   " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                "left join (select distinct bidder_id, equivalent as maxTurnOver from oiipcra_oltp.finyr_turnover where is_maximum=true and is_active=true) as maxturnover on maxturnover.bidder_id=bidder.id   " +
                "left join oiipcra_oltp.liquid_asset_availability as liquidAsset on liquidAsset.bidder_id=bidder.id and liquidAsset.is_active=true  " +
                "where tender.id=:tenderId  and tender.is_active=true and tender.technical_bid_opening_date=:bidOpeningDate order by tnotice.work_sl_no_in_tcn::text,  agency.name  ";
        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidopeningAbstractDto.class));
    }

    public List<DraftLetterAwardDto> getDraftLetterAward(Integer bidId, java.sql.Date technicalBidOpeningDate, java.sql.Date financialBidOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select bidder.id as bidderId,agency.id as agencyId,tnotice.id,agency.name as agencyName,license.name as licenseClass,agency.address,agency.post,upper(dist.district) as districtName,agency.pincode as pinCode,tnotice.work_identification_code as workId,tender.name_of_work as nameOfWork,project.name as projectName, finbid.balance_aps_required as balance,\n" +
                "tender.bid_id as tenderBidId,to_char(tender.technical_bid_opening_date, 'dd-mm-yyyy') as dateStringForTenderOpen,to_char(tender.financial_bid_opening_date,'dd-mm-yyyy') as dateStringForFinBid,award.name as bidPosition,division.division,to_char(tnotice.tender_amount,'999G99G99G999D99') as charTenderAmount,to_char(finbid.amount_quoted,'999G99G99G999D99') as charAmountQuoted,to_char(finbid.amount_quoted*0.025,'999G99G99G999D99') as charIsdToBeDeposited,\n" +
                "case when trim(to_char(finbid.balance_aps_required,'999G99G99G999D99'))='.00' then '0.00'\n" +
                "else trim(to_char(finbid.balance_aps_required,'999G99G99G999D99')) end as charEmdAmount,tnotice.time_for_completion, \n" +
                "case when(tnotice.tender_amount > finbid.amount_quoted) then to_char(tnotice.tender_amount*0.025,'999G99G99G999D99')\n" +
                "else to_char(finbid.amount_quoted*0.025,'999G99G99G999D99') end as isd, " +
                "tnotice.ee_id as EeId,u.designation_id,des.name as designationName ,tnotice.other_ee as otherEe, u.name as userName, \n" +
                "case when tnotice.ee_id is not null then userM.name else other_ee end as ee, sub.mi_sub_division_name as subDivisionName, sec.mi_section_name as sectionName,\n " +
                "case when tnotice.sub_division_officer is not null then u.name else other_sub_division_officer end as subDivisionOfficerName " +
                "from oiipcra_oltp.bidder_details as bidder\n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id\n" +
                "left join oiipcra_oltp.tender_m as tender  on  bidder.tender_id=tender.id\n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id\n" +
                "left join oiipcra_oltp.financial_bid_details as finbid on finbid.bidder_id=bidder.id and finbid.is_active=true \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id\n" +
                "left join oiipcra_oltp.award_type as award on award.id=bidder.award_type \n" +
                "left join oiipcra_oltp.user_m as u on u.id=tnotice.sub_division_officer \n" +
                "left join oiipcra_oltp.user_m as userM on userM.id=tnotice.ee_id \n " +
                "left join oiipcra_oltp.designation_m as des on des.id = u.designation_id \n" +
                "left join oiipcra_oltp.mi_subdivision_m as sub on sub.mi_sub_division_id = tnotice.sub_division_id\n " +
                "left join oiipcra_oltp.mi_section_m as sec on sec.section_id = tnotice.section_id\n " +
                "left join oiipcra_oltp.project_m as project on project.id=tnotice.project_id\n" +
                "left join(select string_agg(distinct b.district_name,', ') as district,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.district_boundary as b on a.dist_id=b.dist_id group by a.tender_notice_id) as dist\n" +
                "on dist.tender_notice_id=tnotice.id\n" +
                "left join(select string_agg(distinct b.mi_division_name,', ') as division,a.id as notice_id from oiipcra_oltp.tender_notice as a left join oiipcra_oltp.mi_division_m as b on a.division_id=b.mi_division_id group by a.id) as division\n" +
                "on division.notice_id=tnotice.id\n" +
                "where award.id in(1,2,7) and tender.id=:bidId \n" +
                "and tender.technical_bid_opening_date=:technicalBidOpeningDate and tender.financial_bid_opening_date=:financialBidOpeningDate ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("technicalBidOpeningDate", technicalBidOpeningDate);
        sqlParam.addValue("financialBidOpeningDate", financialBidOpeningDate);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DraftLetterAwardDto.class));
    }

    public List<ListOfBidsDto> getListOfBids(Integer bidId, java.sql.Date tenderOpeningDate, Integer agencyId, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tnotice.work_identification_code as packageId,agency.name as agency,license.name as licenseClass,agency.gstin_no as gstInNo,agency.phone::text as contact,to_char(agency.license_validity, 'dd Month yyyy') as charLicenseExpiring,\n" +
                "tender.bid_id as tenderBidId,  \n" +
                "to_char(tender.technical_bid_opening_date, 'dd Month yyyy') as charTenderOpeningDate,round(maxturnover.maxTurnOver,2) as maxturnover, to_char(tnotice.tender_amount,'999G999G999G999D99') as charTenderAmount, " +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true    " +
                "           and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as bidValidity, " +
                "to_char(fbd.amount_quoted,'999G999G999G999D99') as charAmountQuoted, " +
                " round(fbd.amount_percentage,2) as amountPercentage, " +
//              " to_char((fbd.amount_quoted/tnotice.tender_amount)*100-100,'999G999G999G999D99') as amountPercentage," +
                " tnotice.tender_amount,  " +
//                "round((tnotice.tender_amount*stipulation.similar_work_value)/100000,2) as similarWorkValue," +
                " round(similarWork.similar_work_amount,2) as similarWorkValue, " +
                " round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as charAnnualTurnover,  " +
                "similarWork.similar_work_amount,credit.credit_amount as creditFacilityAvailable,credit.liquidity_amount as creditFacilityRequired  " +
                "from oiipcra_oltp.bidder_details as bidder\n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id\n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id\n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id \n" +
                "left join (select equivalent as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover\n" +
                "           where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id \n" +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id\n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id\n" +
                "Left join oiipcra_oltp.completion_of_similar_type_work as similarWork on similarWork.bidder_id=bidder.id\n" +
                "left join oiipcra_oltp.liquid_asset_availability as credit on credit.bidder_id=bidder.id " +
                "where tender.id=:bidId and tender.financial_bid_opening_date=:tenderOpeningDate " +
                "and agency.id=:agencyId " +
                "order by tnotice.tender_amount desc  ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        sqlParam.addValue("agencyId", agencyId);
        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ListOfBidsDto.class));
    }

    public ListOfBidsDto getListOfBidSum(Integer bidId, java.sql.Date tenderOpeningDate, Integer agencyId, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select    to_char(sum(tnotice.tender_amount),'999G999G999G999D99') as charTenderAmount, \n" +
                "to_char(sum(fbd.amount_quoted),'999G999G999G999D99') as charAmountQuoted,\n" +
                "round((sum(tnotice.tender_amount*stipulation.annual_financial_turnover))/100000,2) as annualTurnover,\n" +
                "sum(credit.credit_amount) as creditFacilityAvailable,sum(credit.liquidity_amount) as creditFacilityRequired\n" +
                "from oiipcra_oltp.bidder_details as bidder\n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id\n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id\n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id \n" +
                "left join (select value as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover\n" +
                "           where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id \n" +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id\n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id\n" +
                "Left join oiipcra_oltp.completion_of_similar_type_work as similarWork on similarWork.bidder_id=bidder.id\n" +
                "left join oiipcra_oltp.liquid_asset_availability as credit on credit.bidder_id=bidder.id " +
                "where tender.id=:bidId and tender.tender_opening_date=:tenderOpeningDate " +
                "and agency.id=:agencyId ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        sqlParam.addValue("agencyId", agencyId);
        sqlParam.addValue("userId", userId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ListOfBidsDto.class));
    }

    public List<ListOfBidsDto> getArrayListOfBids(Integer bidId, java.sql.Date tenderOpeningDate, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select DISTINCT agency.id as agencyId, agency.name as agency " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id " +
                "left join (select value as maxTurnOver,agency_id from oiipcra_oltp.finyr_turnover where is_maximum=true) as maxturnover on maxturnover.agency_id=agency.id " +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id " +
                "where tender.id=:bidId and tender.financial_bid_opening_date=:tenderOpeningDate  order by agency.name ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ListOfBidsDto.class));

    }

    public List<FinancialBidResultDto> getFinancialBidResult(Integer bidId, java.sql.Date finBidOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getFinancialBidResultDetails = "select bidder.id,tender.bid_id,tender.financial_bid_opening_date,tnotice.id,tnotice.work_sl_no_in_tcn as workSlNoInTcn,tnotice.work_identification_code as workId,to_char(tnotice.tender_amount,'999G99G99G999D99') as tenderAmountString  " +
                ",agency.id,agency.name as agency,license.name as licenseClass  " +
                ",to_char(finbid.amount_quoted,'999G99G99G999D99')as amountQuoted,tnotice.time_for_completion, " +
                " case when trim(to_char((finbid.amount_quoted/tnotice.tender_amount)*100-100,'999G99G99G999D99'))= '.00' then '0.00' " +
                " else trim (to_char((finbid.amount_quoted/tnotice.tender_amount)*100-100,'999G99G99G999D99')) end as rateExcessLess," +
                " award.name as bidPosition,   " +
                "bidder.max_bid_capacity as capacity " +
                ",case when trim(to_char(bidder.max_bid_capacity,'999G99G99G999D99'))='.00' then '0.00'  " +
                " else trim (to_char(bidder.max_bid_capacity,'999G99G99G999D99')) end  as maxBidCapacityString   " +
                "from  oiipcra_oltp.tender_m as tender  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id   " +
                "left join oiipcra_oltp.bidder_details as bidder on tnotice.id=bidder.work_id and tender.id=bidder.tender_id   " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                "left join oiipcra_oltp.financial_bid_details as finbid on finbid.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.award_type as award on award.id=bidder.award_type  " +
                "where tender.id=:bidId and tender.financial_bid_opening_date=:finBidOpeningDate  AND award.name IS NOT NULL   " +
                "order by  tnotice.work_sl_no_in_tcn,agency.name    ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("finBidOpeningDate", finBidOpeningDate);
        return namedJdbc.query(getFinancialBidResultDetails, sqlParam, new BeanPropertyRowMapper<>(FinancialBidResultDto.class));
    }

    public List<AbstractofFinancialBIDOpening> getAbstractOfFinancialBidOpening(String bidId, Date bidOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getAbstractOfFinancialBidOpening = "select tender.bid_id,tender.financial_bid_opening_date,tnotice.work_identification_code as workId," +
                " agency.name as agency,license.name as licenseClass,tnotice.tender_amount," +
                " finbid.amount_quoted as bid_price,finbid.amount_percentage as quoted," +
                " bidder.emd_amount,award.name as remarks" +
                " from oiipcra_oltp.tender_m as tender" +
                " left join oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id" +
                " left join oiipcra_oltp.bidder_details as bidder on tnotice.id=bidder.work_id" +
                " left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id" +
                " left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id" +
                " left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id" +
                " left join oiipcra_oltp.financial_bid_details as finbid on finbid.bidder_id=bidder.id  left join oiipcra_oltp.award_type as award on bidder.award_type =award.id" +
                " where tender.bid_id=:bidId and tender.tender_opening_date=:bidOpeningDate" +
                " order by bid_final_rank";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        return namedJdbc.query(getAbstractOfFinancialBidOpening, sqlParam, new BeanPropertyRowMapper<>(AbstractofFinancialBIDOpening.class));
    }


    public DraftTenderNoticeDto getDraftTenderNotice(Integer bidId, String bidOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
//        UserInfoDto user = userQueryRepository.getUserById(userId);
        qry += "SELECT DISTINCT " +
                "tender.bid_id as bidId, tender.name_of_work as nameOfWork, " +
                "count(tnotice.id) OVER(PARTITION By tnotice.tender_id) as noOfWorks, " +
                "min(tnotice.tender_amount/100000) OVER(PARTITION By tender_id) as variesFrom," +
                " max(tnotice.tender_amount/100000) OVER(PARTITION By tender_id) as variesTo, " +
                "SUM(tnotice.tender_amount/100000) over(partition by tender_id) as tenderAmount," +
                " tender.tender_publication_date as availabilityDocumentFrom, " +
                " case when tender.bid_submission_date_revised is not null then tender.bid_submission_date_revised else  " +
                " tender.bid_submission_date end as availabilityDocumentTo,  " +
                "tender.tender_publication_date as receiptBidFrom," +
                " tender.bid_submission_date as receiptBidTo, " +
                "tender.pre_bid_meeting_date as preBidMeetingDate, " +
                "tender.technical_bid_opening_date as tenderOpeningDate, " +
                "tender.publication_period_upto as tenderPublicationDate" +
                " FROM oiipcra_oltp.tender_m as tender" +
                " LEFT JOIN oiipcra_oltp.tender_notice as tnotice  on tnotice.tender_id=tender.id " +
                " where tender.id =:bidId";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(DraftTenderNoticeDto.class));
    }

    public List<DraftNoticeAnnexureADto> getDraftNoticeAnnexureA(int bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select distinct work.id as workId, work.work_identification_code as workIdentificationCode,work.emd_to_be_deposited as emd, work.work_sl_no_in_tcn as workSlNOTcn, dist.district_name as distName, block.block_name as blockName," +
////                " round(work.tender_amount/100000,2) as tenderAmount," +
//                "case when (round(work.tender_amount/100000,2)) is null then 0.00  " +
//                "else (round(work.tender_amount/100000,2)) end as tenderAmount, " +
//                " work.tender_amount as tenderAmount2 ,floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100 as bidSecurity,tank.name_of_the_m_i_p as nameOfTheMip,type.name as biddingType,  " +
//                " round((work.tender_amount*stipulation.similar_work_value)/100000,2) as similarWorkValue,workType.name as workType,  " +
//                " round((work.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnover," +
//                " round((work.tender_amount*stipulation.liquid_asset_target)/100000,2) as liquidAssetTarget," +
//                " (work.time_for_completion ::integer) as timeForCompletion, work.name_of_work as nameOfWork " +
//                " FROM oiipcra_oltp.tender_notice as work" +
//                " LEFT JOIN oiipcra_oltp.tender_notice_level_mapping AS workLevel on workLevel.tender_notice_id=work.id" +
//                " left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=work.tender_id" +
//                " left join oiipcra_oltp.district_boundary as dist on dist.dist_id=workLevel.dist_id  " +
//                " left join oiipcra_oltp.tank_m_id as tank on tank.id = workLevel.tank_id  " +
//                " left join oiipcra_oltp.works_bidding_type as type on  type.id = work.bidding_type  "+
//                " left join oiipcra_oltp.work_type_m as workType on workType.id = work.type_of_work  " +
//                " left join oiipcra_oltp.block_boundary as block on block.block_id=workLevel.block_id where work.tender_id=:bidId";
        String qry = "select distinct work.id as workId, work.work_identification_code as workIdentificationCode,work.emd_to_be_deposited as emd, work.work_sl_no_in_tcn as workSlNOTcn, dist.district as distName, block.block as blockName,\n" +
                "case when (round(work.tender_amount/100000,2)) is null then 0.00  \n" +
                "else (round(work.tender_amount/100000,2)) end as tenderAmount,\n" +
                "work.tender_amount as tenderAmount2 ,floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100 as bidSecurity,tank.name_of_the_m_i_p as nameOfTheMip,type.name as biddingType,  \n" +
                "round((work.tender_amount*stipulation.similar_work_value)/100000,2) as similarWorkValue,workType.name as workType, \n" +
                "round((work.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnover,\n" +
                "round((work.tender_amount*stipulation.liquid_asset_target)/100000,2) as liquidAssetTarget,\n" +
                "(work.time_for_completion ::integer) as timeForCompletion, work.name_of_work as nameOfWork \n" +
                "FROM oiipcra_oltp.tender_notice as work\n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=work.tender_id\n" +
                "left join oiipcra_oltp.works_bidding_type as type on  type.id = work.bidding_type \n" +
                "left join oiipcra_oltp.work_type_m as workType on workType.id = work.type_of_work \n" +
                "left join(select string_agg(distinct b.district_name,', ') as district,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.district_boundary as b on a.dist_id=b.dist_id group by a.tender_notice_id) as dist\n" +
                "on dist.tender_notice_id=work.id\n" +
                "left join(select string_agg(distinct b.block_name,', ') as block,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.block_boundary as b on a.block_id=b.block_id group by a.tender_notice_id) as block\n" +
                "on block.tender_notice_id=work.id\n" +
                "left join(select string_agg(b.name_of_the_m_i_p,', ') as name_of_the_m_i_p,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.tank_m_id as b on a.tank_id=b.id group by a.tender_notice_id) as tank\n" +
                "on tank.tender_notice_id=work.id\n" +
                "where work.tender_id=:bidId ";
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DraftNoticeAnnexureADto.class));
    }


    public ContractInfo getContractDetailsByTenderId(Integer id, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getContractDetailsByTenderId = "SELECT cm.id, cm.contract_code, cm.contract_name,cm.contract_date, cm.contract_status_id, cs.name as contractStatus,   " +
                "cmp.tender_id,cm.contract_type_id, type.name as contractType, cm.contract_level_id, cmaster.level_name as contractLevel,    " +
                "cm.procurement_made_for, cm.zone, cm.approval_order_date, cm.work_description, cm.eoi_id, cm.rfp_issued_on,   " +
                "cm.rfp_received_on, cm.area_id, cm.correspondence_file_no, cm.contract_number, cm.agency_id, agency.name as agency,    " +
                "cm.contract_amount,cm.gst, cm.stipulated_date_of_comencement as stipulatedDateOfComencement,cm.stipulated_date_of_completion,   " +
                "cm.approval_order,cm.tachnical_sanction_no, cmp.tender_notice_id, cmp.estimate_id,cm.awarded_as,cm.agreement_number,   " +
                "cm.loa_issued_no, cm.loa_issued_date,cm.rate_of_premium,cm.actual_date_of_commencement,cm.actual_date_of_completion,    " +
                "cm.is_active as isActive, cm.created_by,cm.created_on,cm.updated_by,cm.updated_on,cm.finyr_id,year.name as financialYear,   " +
                "cm.date_eoi,cm.estimate_id from oiipcra_oltp.contract_m as cm   " +
                "left join oiipcra_oltp.contract_status as cs on cs.id = cm.contract_status_id   " +
                "left join oiipcra_oltp.contract_type as type on type.id = cm.contract_type_id   " +
                "left join oiipcra_oltp.contract_level_master as cmaster on cmaster.id = cm.contract_level_id   " +
                "left join oiipcra_oltp.agency_m as agency on agency.id =cm.agency_id  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id = cm.finyr_id   " +
                "left join oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id   " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = cmp.tender_notice_id  " +
                "WHERE cmp.tender_id=:id and cmp.tender_notice_id=:workId";

        sqlParam.addValue("id", id);
        sqlParam.addValue("workId", workId);
        return namedJdbc.queryForObject(getContractDetailsByTenderId, sqlParam, new BeanPropertyRowMapper<>(ContractInfo.class));
    }


    public TenderDetailsInfo getTenderDetailsByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderDetailsByTenderId = "Select tm.id,tm.bid_id,tm.approval_for_procurement_date,tm.technical_bid_opening_date,tm.technical_bid_opening_date_revised,  " +
                "tm.bid_submission_date,tm.bid_submission_date_revised,tm.financial_bid_opening_date,tm.name_of_work,tm.pre_bid_meeting_type,meeting.name as meetingType,  " +
                "tm.pre_bid_meeting_date,tm.tender_publication_date,tm.publication_period_upto, tm.tender_type as tenderTypeId,type.name as tenderType,   " +
                "tm.tender_level_id,level.level_name as tenderLevel,tm.finyr_id,year.name as financialYear,   " +
                "tm.tender_opening_date,tm.date_of_tender_notice,tm.estimate_id,tm.tender_status as tenderStatusId,ts.name as tenderStatus,tm.meeting_location as meetingLocation,  " +
                "ml.name as location,tn.id as workId,tn.bidding_type as biddingTypeId,bidding.name as biddingType, tn.type_of_work,work.name as workType,tn.work_sl_no_in_tcn,tn.work_identification_code,   " +
                "tn.name_of_work as workName, tn.tender_amount,tn.paper_cost,tn.emd_to_be_deposited,tn.time_for_completion,tn.contact_no,tn.circle_id,tn.division_id,div.mi_division_name as divisionName,   " +
                "tn.ee_id,tn.sub_division_id,sub.mi_sub_division_name as subDivisionName,   " +
                "tn.sub_division_officer,tn.project_id,pm.name as projectName,tn.tender_not_awarded_reason   " +
                "FROM oiipcra_oltp.tender_m as tm   " +
                "left join oiipcra_oltp.tender_type as type on type.id = tm.tender_type   " +
                "left join oiipcra_oltp.tender_level_master as level on level.id = tm.tender_level_id   " +
                "left join oiipcra_oltp.meeting_type as meeting on meeting.id = tm.pre_bid_meeting_type   " +
                "left join oiipcra_oltp.fin_year_m as year on year.id = tm.finyr_id   " +
                "left join oiipcra_oltp.tender_status as ts on ts.id = tm.tender_status  " +
                "left join oiipcra_oltp.meeting_location as ml on ml.id = tm.meeting_location   " +
                "left join oiipcra_oltp.tender_notice as tn on tn.tender_id =tm.id  " +
                "left join oiipcra_oltp.work_type_m as work on work.id = tn.type_of_work  " +
                "left join oiipcra_oltp.works_bidding_type as bidding on bidding.id = tn.bidding_type   " +
                "left join oiipcra_oltp.mi_division_m as div on div.id =tn.division_id  " +
                "left join oiipcra_oltp.mi_subdivision_m as sub on sub.id = tn.sub_division_id  " +
                "left join oiipcra_oltp.project_m as pm on pm.id = tn.project_id  " +
                "WHERE tm.id =:tenderId and tn.id =:workId";


    /*    sqlParam.addValue("tenderId",tenderId);
        sqlParam.addValue("workId",workId);*/
        return namedJdbc.queryForObject(getTenderDetailsByTenderId, sqlParam, new BeanPropertyRowMapper<>(TenderDetailsInfo.class));
    }

    public List<ExpenditureDataInfo> getExpenditureDetailsBy(Integer id, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetExpenditureDetailsBy = "select ed.id, ed.finyr_id, year.name as finYear, ed.month_id, month.name as monthName,  " +
                "ed.value,ed.device_id,ed.payment_date,ed.invoice_id,ed.payment_type as paymentTypeId, " +
                "invoice.name as paymentType,em.contract_id,em.activity_id,em.estimate_id,em.tank_id,em.tender_id,em.tender_notice_id  " +
                "from oiipcra_oltp.expenditure_data as ed  " +
                "left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id = ed.id  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id =  ed.finyr_id   " +
                "left join oiipcra_oltp.month_m as month on month.id= ed.month_id  " +
                "left join oiipcra_oltp.contract_m as contract on contract.id = em.contract_id  " +
                "left join oiipcra_oltp.contract_mapping as cm on cm.contract_id =contract.id " +
                "left join oiipcra_oltp.invoice_payment_type as invoice on ed.payment_type = invoice.id  " +
                "left join oiipcra_oltp.tender_m as tm on tm.id = em.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = em.tender_notice_id  " +
                "where em.tender_id=:id and em.tender_notice_id =:workId";

        sqlParam.addValue("id", id);
        sqlParam.addValue("workId", workId);
        return namedJdbc.query(GetExpenditureDetailsBy, sqlParam, new BeanPropertyRowMapper<>(ExpenditureDataInfo.class));
    }

    public Integer updateCompletionOfSimilarTypeOfWork(CompletionOfSimilarTypeOfWork work) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String updateQry = "UPDATE oiipcra_oltp.completion_of_similar_type_work " +
                "SET bidder_id=:bidderId,finyr_id=:finYrId,value=:value,is_maximum=:isMaximum,similar_work_amount=:similarWorkAmount," +
                "completed_amount=:completedAmount,percentage_completed=:percentageCompleted,is_active=:isActive,updated_by=:updatedBy,executed_year=:executedYear " +
                "WHERE id=:id";

        sqlParam.addValue("id", work.getId());
        sqlParam.addValue("bidderId", work.getBidderId());
        sqlParam.addValue("finYrId", work.getFinyrId());
        sqlParam.addValue("value", work.getValue());
        sqlParam.addValue("isMaximum", work.getIsMaximum());
        sqlParam.addValue("similarWorkAmount", work.getSimilarWorkAmount());
        sqlParam.addValue("completedAmount", work.getCompletedAmount());
        sqlParam.addValue("percentageCompleted", work.getPercentageCompleted());
        sqlParam.addValue("isActive", work.getActive());
        sqlParam.addValue("updatedBy", work.getUpdatedBy());
        sqlParam.addValue("executedYear", work.getExecutedYear());

        Integer update = namedJdbc.update(updateQry, sqlParam);
        return update;
    }

    public Integer updateAnnualFinancialTurnOver(AnnualFinancialTurnoverMaster annualTurnover) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String updateQry = "UPDATE oiipcra_oltp.finyr_turnover " +
                "SET  agency_id=:agencyId,bidder_id=:bidderId ,finyr_id=:finYrId, value=:value, is_maximum=:isMaximum, is_active=:isActive,updated_by=:updatedBy,equivalent=:equivalent, turn_over_required=:turnOverRequired, turn_over_maximum_year=:turnOverMaximumYear, turn_over_maximum_value=:turnOverMaximumValue  " +
                "WHERE id=:id";

        sqlParam.addValue("id", annualTurnover.getId());
        sqlParam.addValue("agencyId", annualTurnover.getAgencyId());
        sqlParam.addValue("finYrId", annualTurnover.getFinyrId());
        sqlParam.addValue("value", annualTurnover.getValue());
        sqlParam.addValue("isMaximum", annualTurnover.getMaximum());
        sqlParam.addValue("isActive", annualTurnover.getActive());
        sqlParam.addValue("updatedBy", annualTurnover.getUpdatedBy());
        sqlParam.addValue("bidderId", annualTurnover.getBidderId());
        sqlParam.addValue("equivalent", annualTurnover.getEquivalent());
        sqlParam.addValue("turnOverRequired", annualTurnover.getTurnOverRequired());
        sqlParam.addValue("turnOverMaximumYear", annualTurnover.getTurnOverMaximumYear());
        sqlParam.addValue("turnOverMaximumValue", annualTurnover.getTurnOverMaximumValue());
        Integer update = namedJdbc.update(updateQry, sqlParam);
        return update;
    }

    public Integer updateLiquidAssetAvailability(LiquidAssetAvailability liquidAssetAvailability) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String updateQry = "UPDATE oiipcra_oltp.liquid_asset_availability " +
                "SET  agency_id=:agencyId, bank_name=:bankName, credit_amount=:creditAmount, liquidity_amount=:liquidityAmount, total_liquid_asset=:totalLiquidAsset, is_active=:isActive, updated_by=:updatedBy " +
                "WHERE id=:id";

        sqlParam.addValue("id", liquidAssetAvailability.getId());
        sqlParam.addValue("agencyId", liquidAssetAvailability.getAgencyId());
        sqlParam.addValue("bankName", liquidAssetAvailability.getBankName());
        sqlParam.addValue("creditAmount", liquidAssetAvailability.getCreditAmount());
        sqlParam.addValue("liquidityAmount", liquidAssetAvailability.getLiquidityAmount());
        sqlParam.addValue("totalLiquidAsset", liquidAssetAvailability.getTotalLiquidAsset());
        sqlParam.addValue("isActive", liquidAssetAvailability.getActive());
        sqlParam.addValue("updatedBy", liquidAssetAvailability.getUpdatedBy());

        Integer update = namedJdbc.update(updateQry, sqlParam);
        return update;
    }

    public List<AgencyDto> getAllBidders() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllBidder = "SELECT bidder.id as id,agency.name as name from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = bidder.agency_id order By bidder.id desc ";

        return namedJdbc.query(GetAllBidder, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public List<AwardTypeDto> getAllAwardType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllAwardType = "SELECT award.id, award.name from oiipcra_oltp.award_type as award ";

        return namedJdbc.query(GetAllAwardType, sqlParam, new BeanPropertyRowMapper<>(AwardTypeDto.class));

    }

    public FinancialBidDto getTenderBidderDetailsByBidId(Integer bidderId, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tm.id as tenderId ,tn.id as workId,bidder.id as bidderId,bidder.is_bid_awarded,tm.bid_id,tm.financial_bid_opening_date,date(tm.technical_bid_opening_date) as technicalBidOpeningDate,tn.tender_amount,tn.tender_not_awarded_reason,tn.time_for_completion ,bidder.max_bid_capacity from    " +
                "oiipcra_oltp.tender_m as tm  " +
                "left join oiipcra_oltp.tender_notice as tn on tn.tender_id = tm.id   " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.tender_id = tn.tender_id   " +
                "where bidder.id=:bidderId and tn.id=:workId";

        sqlParam.addValue("bidderId", bidderId);
        sqlParam.addValue("workId", workId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidDto.class));
        } catch (Exception e) {
            return null;
        }

    }

    public List<FinancialBidDto> getAllAgencyNameByWorkId(Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        qry += "SELECT tn.id as workId,bidder.id as bidderId,agency.id as agencyId, agency.name as agencyName from oiipcra_oltp.tender_notice as tn  " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.work_id= tn.id   " +
                "left join oiipcra_oltp.agency_m as agency on agency.id= bidder.agency_id  where bidder.is_bid_qualified=true";
        if (workId > 0) {
            qry += " and tn.id=:workId ";
            sqlParam.addValue("workId", workId);
        }

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidDto.class));
    }

    public List<FinancialBidDto> getAgencyNameByWorkId(Integer workId, Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        qry += "SELECT distinct tn.id as workId,bidder.id as bidderId,agency.id as agencyId, agency.name as agencyName,agency.pan_no as panNo from oiipcra_oltp.tender_notice as tn " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.work_id= tn.id   " +
                "left join oiipcra_oltp.agency_m as agency on agency.id= bidder.agency_id  where tn.id=:workId and bidder.tender_id=:tenderId ";
        sqlParam.addValue("workId", workId);
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidDto.class));
    }

    public Integer getCount(Integer workId, Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        qry += "SELECT count(distinct agency.id) from oiipcra_oltp.tender_notice as tn " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.work_id= tn.id   " +
                "left join oiipcra_oltp.agency_m as agency on agency.id= bidder.agency_id  where tn.id=:workId and bidder.tender_id=:tenderId ";
        sqlParam.addValue("workId", workId);
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public FinancialBidInfo getFinancialBidDetailsByBidderId(Integer bidderId) {
        FinancialBidInfo finBid = new FinancialBidInfo();
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetFinancialDetails = "SELECT fin.id, fin.tender_id,fin.bidder_id,tm.financial_bid_opening_date,fin.amount_quoted,tn.tender_amount,fin.amount_percentage,fin.additional_performance_sec_required,  " +
                "fin.additional_submitted,fin.balance_aps_required,bidder.max_bid_capacity,fin.work_in_hand,fin.balance_bid_capacity,  " +
                "fin.review_tech_bid_date,fin.review_fin_bid_date,bidder.is_bid_awarded,tn.tender_not_awarded_reason,result.date_of_lottery,   " +
                "result.award_type as awardTypeId, award.name as awardType, result.acceptance_letter_no, result.completion_period, tn.time_for_completion, result.agreement_no,result.legal_case," +
                "date(tm.technical_bid_opening_date) as technicalBidOpeningDate,date(fin.created_on) as  financialBidOpeningDate, " +
                "result.remarks from oiipcra_oltp.financial_bid_details as fin  " +
                "left join oiipcra_oltp.tender_m as tm on tm.id = fin.tender_id   " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id = fin.work_id   " +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.id = fin.bidder_id    " +
                "left join oiipcra_oltp.tender_result as result on result.bidder_id = bidder.id   " +
                "left join oiipcra_oltp.award_type as award on award.id = result.award_type " +
                "Where fin.bidder_id=:bidderId and fin.is_active=true ";
        sqlParam.addValue("bidderId", bidderId);
        try {
            finBid = namedJdbc.queryForObject(GetFinancialDetails, sqlParam, new BeanPropertyRowMapper<>(FinancialBidInfo.class));
            return finBid;
        } catch (Exception e) {
            return null;
        }
    }

    public List<AgencyDto> getAllAgencyByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllAgencyByContractId = "SELECT agency.id,agency.name from oiipcra_oltp.agency_m as agency " +
                "left join oiipcra_oltp.contract_m as contract on contract.agency_id = agency.id " +
                "where contract.id =:contractId";

        sqlParam.addValue("contractId", contractId);
        return namedJdbc.query(GetAllAgencyByContractId, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));

    }

    public List<Integer> getTenderIds() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllTenderIds = "SELECT DISTINCT tender_id from oiipcra_oltp.contract_mapping";

        return namedJdbc.queryForList(GetAllTenderIds, sqlParam, Integer.class);
    }

    public List<TenderDto> getAllBidIdForContractDone(List<Integer> tenderIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllBidId = "SELECT id,bid_id as bidId from oiipcra_oltp.tender_m WHERE id in(:tenderIds)";
        sqlParam.addValue("tenderIds", tenderIds);
        return namedJdbc.query(GetAllBidId, sqlParam, new BeanPropertyRowMapper<>(TenderDto.class));
    }

    public List<TenderNoticeDto> getAllWorkIdForContractDone(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllBidId = "SELECT DISTINCT tender_notice_id as id,notice.name_of_work as nameOfWork from oiipcra_oltp.contract_mapping  as cm " +
                "left join oiipcra_oltp.tender_notice as notice on notice.id=cm.tender_notice_id where cm.tender_id =:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(GetAllBidId, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class));
    }

    public boolean updateTenderApproval(Integer tenderId, ApproveStatusDto updateTenderApproval) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "update approved_status=:approvedStatus";

        sqlParam.addValue("approvedStatus", updateTenderApproval.getApprovalStatus());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result = false;
        if (update > 0) {
            result = true;
        }
        return result;
    }

    public List<PaperDto> getDistinctPaperNameFromPublication() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT distinct name as paperName from oiipcra_oltp.tender_published ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PaperDto.class));
    }

    public List<TenderDto> getTenderByEstimateId(Integer estimateId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,bid_id from oiipcra_oltp.tender_m where estimate_id=:estimateId";
        sqlParam.addValue("estimateId", estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderDto.class));
    }

    public Boolean updateTenderStatus(String bidId, Integer statusId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.tender_m" +
                " SET tender_status =5" +
                " WHERE bid_id=:bidId";
        sqlParam.addValue("bidId", bidId);
        // sqlParam.addValue("statusId",statusId);
        int update = namedJdbc.update(qry, sqlParam);
        boolean result = false;
        if (update > 0) {
            result = true;
        }
        return result;
    }

    public Integer getBidderIdByAgencyIdAndWorkId(Integer agencyId, Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select id from oiipcra_oltp.bidder_details where work_id=:workId and agency_id=:agencyId";
        sqlParam.addValue("workId", workId);
        sqlParam.addValue("agencyId", agencyId);
        try {
            Integer i = namedJdbc.queryForObject(qry, sqlParam, Integer.class);
            return i;
        } catch (Exception e) {
            return null;
        }
    }

    public Object disbursementAndProjection() {
        return null;
    }

    public Boolean validationUpdate(Boolean isEmdValid, Boolean completionWorkValueQualified, Boolean turnOver, Boolean liquidAsset, Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.bidder_details   " +
                "SET is_emd_valid=:isEmdValid,completion_work_value_qualified=:completionWorkValueQualified,  " +
                "turn_over_qualified=:turnOver,liquid_asset_qualified=:liquidAsset ";
        sqlParam.addValue("isEmdValid", isEmdValid);
        sqlParam.addValue("completionWorkValueQualified", completionWorkValueQualified);
        sqlParam.addValue("turnOver", turnOver);
        sqlParam.addValue("liquidAsset", liquidAsset);
        if (isEmdValid == true && turnOver == true && liquidAsset == true) {
            qry += " is_bid_qualified=true ";
        }
        qry += " where id=:bidderId ";
        sqlParam.addValue("bidderId", bidderId);
        int update = namedJdbc.update(qry, sqlParam);
        boolean result = false;
        if (update > 0) {
            result = true;
        }
        return result;
    }

    public Integer updateCompletionOfSimilarTypeOfWorkValidation(Integer bidderId, Boolean completionValid, BidderDetails bidder) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.bidder_details   " +
                "SET completion_work_value_qualified=:completionWorkValueQualified  ";
        sqlParam.addValue("completionWorkValueQualified", completionValid);
        sqlParam.addValue("bidderId", bidderId);
        if (bidder.getIsEmdValid() == true && bidder.getLiquidAssetQualified() == true && bidder.getTurnOverQualified() == true && completionValid == true) {
            qry += ",is_bid_qualified=true ";
        }
        qry += " where id=:bidderId ";
        int update = namedJdbc.update(qry, sqlParam);
        return update;
    }

    public Integer updateAnnualTurnOverValidation(Integer bidderId, Boolean annualTurnOverValid, BidderDetails bidder, Double maxBidCapacity) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.bidder_details   " +
                "SET turn_over_qualified=:turnOver ,max_bid_capacity=:maxBidCapacity ";
        sqlParam.addValue("turnOver", annualTurnOverValid);
        sqlParam.addValue("bidderId", bidderId);
        sqlParam.addValue("maxBidCapacity", maxBidCapacity);
        if (bidder.getIsEmdValid() == true && bidder.getLiquidAssetQualified() == true && bidder.getCompletionWorkValueQualified() == true && annualTurnOverValid == true) {
            qry += ",is_bid_qualified=true ";
        }
        qry += " where id=:bidderId ";
        int update = namedJdbc.update(qry, sqlParam);
        return update;
    }

    public Integer updateLiquidAssetValidation(Integer bidderId, Boolean liquidAssetValid, BidderDetails bidder) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.bidder_details   " +
                "SET liquid_asset_qualified=:liquidAsset   ";
        sqlParam.addValue("liquidAsset", liquidAssetValid);
        sqlParam.addValue("bidderId", bidderId);
        if (bidder.getIsEmdValid() == true && bidder.getCompletionWorkValueQualified() == true && bidder.getTurnOverQualified() == true && liquidAssetValid == true) {
            qry += ",is_bid_qualified=true ";
        } else {
            qry += ",is_bid_qualified=false ";
        }
        qry += " where id=:bidderId ";
        int update = namedJdbc.update(qry, sqlParam);
        return update;
    }

    public Boolean updateTenderNoticeLevelStatus(int tenderNoticeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.tender_notice_level_mapping SET is_active = false WHERE tender_notice_id=:tenderNoticeId";
        sqlParam.addValue("tenderNoticeId", tenderNoticeId);
        int update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }

    //Get contractIds against invoiceIds
    public List<Integer> getContractIdsByInvoiceId(int invoiceId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract_id FROM oiipcra_oltp.invoice_m WHERE id =:invoiceId";
        sqlParam.addValue("invoiceId", invoiceId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public TenderCorrigendumDto getPreBidMeetingDateFromTenderCorrigendum(int tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select max(pre_bid_meeting_date) as preBidMeetingDate from oiipcra_oltp.tender_corrigendum where tender_id=:tenderId and is_active=true ";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TenderCorrigendumDto.class));
    }

    public Integer getFinancialyearId(String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select id from oiipcra_oltp.fin_year_m  where name =:year";
        sqlParam.addValue("year", year);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Page<Tender> getDraftTenderList(TenderListDto tenderListDto) {
      /*  UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(tenderListDto.getUserId());
        tenderListDto.setSortOrder("DESC");
        tenderListDto.setSortBy("id");*/

        PageRequest pageable = PageRequest.of(tenderListDto.getPage(), tenderListDto.getSize(), Sort.Direction.fromString(tenderListDto.getSortOrder()), tenderListDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        UserInfoDto userInfoById = userService.getUserById(tenderListDto.getUserId());
        String GetDraftTenderList = "";

      /*  if (tenderListDto.getTenderId() > 0) {
            GetTender += " AND tm.id= :tenderId ";
            sqlParam.addValue("tenderId", tenderListDto.getTenderId());
        }*/
       /* SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (tenderListDto.getStartDate() != null && !tenderListDto.getStartDate().isEmpty()) {
            GetDraftTenderList += " AND date(contract.contract_date) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(tenderListDto.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (tenderListDto.getEndDate() != null && !tenderListDto.getEndDate().isEmpty()) {
            GetDraftTenderList += " AND date(contract.contract_date) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(tenderListDto.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }
*/
        GetDraftTenderList += " ORDER BY " + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(GetDraftTenderList, sqlParam);
        GetDraftTenderList += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<Tender> tenderList = namedJdbc.query(GetDraftTenderList, sqlParam, new BeanPropertyRowMapper<>(Tender.class));
        return new PageImpl<>(tenderList, pageable, resultCount);
    }

    public String getBidId(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT bid_id from oiipcra_oltp.tender_m WHERE id =:bidId ";
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public List<FormGDto> getAgencyDetails(Integer finYr, String issueNo, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetFormGDetails = "";
        GetFormGDetails = "select finyr.name as finyr,agency.name as agencyName,license.name as licenseClass,agency.gstin_no,tender.bid_id||' / '||tnotice.work_identification_code as bid_workId,   " +
                "tnotice.id,dist.district,scheme.name as scheme,tender.tender_opening_date,tnotice.tender_amount, bidder.agency_id,  " +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true   " +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as validity_of_tender,  " +
                "awarded.name as awarded   " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id   " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id   " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id   " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                "left join oiipcra_oltp.fin_year_m as finyr on tender.finyr_id=finyr.id   " +
                "left join(select string_agg(b.district_name,', ') as district,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.district_boundary as b on a.dist_id=b.dist_id group by a.tender_notice_id) as dist on dist.tender_notice_id=tnotice.id   " +
                "left join oiipcra_oltp.project_m as scheme on scheme.id=tnotice.project_id  " +
                "left join oiipcra_oltp.award_type as awarded on awarded.id=bidder.award_type  " +
                "where finyr.id=:finYr and agency_id=:agencyId  ";

        sqlParam.addValue("finYr", finYr);
        sqlParam.addValue("agencyId", agencyId);
        sqlParam.addValue("issueNo", issueNo);
        return namedJdbc.query(GetFormGDetails, sqlParam, new BeanPropertyRowMapper<>(FormGDto.class));
    }

    public List<AgencyDto> getAgencyData(Integer finYr, String issueNo, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetFormGDetails = "";
        GetFormGDetails = "select distinct agency.id as id,agency.name as name,license.name as classOfLicense,agency.gstin_no,finyr.name as finyr  " +
                " from oiipcra_oltp.bidder_details as bidder   " +
                " left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                " left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                " left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id   " +
                " left join oiipcra_oltp.fin_year_m as finyr on tender.finyr_id=finyr.id  " +
                " where finyr.id=:finYr  ";
        if (agencyId != null && agencyId > 0) {
            GetFormGDetails += " and agency_id=:agencyId ";
            sqlParam.addValue("agencyId", agencyId);
        }

        sqlParam.addValue("finYr", finYr);

        sqlParam.addValue("issueNo", issueNo);
        return namedJdbc.query(GetFormGDetails, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public FormGDto getSum(Integer finYr, String issueNo, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetFormGDetails = "";
        GetFormGDetails = "select to_char(sum(tnotice.tender_amount),'999G999G999G999D99') as sumString  " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id   " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id   " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id   " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                "left join oiipcra_oltp.fin_year_m as finyr on tender.finyr_id=finyr.id   " +
                "left join(select string_agg(b.district_name,', ') as district,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.district_boundary as b on a.dist_id=b.dist_id group by a.tender_notice_id) as dist on dist.tender_notice_id=tnotice.id   " +
                " left join oiipcra_oltp.project_m as scheme on scheme.id=tnotice.project_id  " +
                "left join oiipcra_oltp.award_type as awarded on awarded.id=bidder.award_type  " +
                "where finyr.id=:finYr and agency_id=:agencyId  ";

        sqlParam.addValue("finYr", finYr);
        sqlParam.addValue("agencyId", agencyId);
        sqlParam.addValue("issueNo", issueNo);
        return namedJdbc.queryForObject(GetFormGDetails, sqlParam, new BeanPropertyRowMapper<>(FormGDto.class));
    }

    public List<AgencyDto> getAllAgency() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT *  FROM oiipcra_oltp.agency_m ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public List<AgencyDto> getAgencyDetailsByFinYr(Integer finYrId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct agency.id as id,agency.name as name,license.name as classOfLicense,agency.gstin_no,finyr.name as finyr   " +
                "from oiipcra_oltp.bidder_details as bidder    " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id   " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id   " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id   " +
                "left join oiipcra_oltp.fin_year_m as finyr on tender.finyr_id=finyr.id  " +
                "where finyr.id=:finYrId ORDER BY agency.id ";
        sqlParam.addValue("finYrId", finYrId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public TenderInfo getBidIdAndFinancialDate(Integer bidId) {
        TenderInfo tender = new TenderInfo();
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTenderByTenderId = " select bid_id as bidId,to_char(financial_bid_opening_date,'DD-MM-YYYY') as techBidDate from oiipcra_oltp.tender_m where id=:tenderId ";

        sqlParam.addValue("tenderId", bidId);
        try {
            tender = namedJdbc.queryForObject(GetTenderByTenderId, sqlParam, new BeanPropertyRowMapper<>(TenderInfo.class));
        } catch (EmptyResultDataAccessException e) {
            return tender;
        }
        return tender;
    }

    public TechnicalBidAbstractDto getCurrentDate() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT to_char(date(now()),'dd Month YYYY' ) as date ";
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAbstractDto.class));
    }

    public List<EngineerDatabaseDto> getEngineerDatabase(/*Integer designationId,*/ Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select c.circle_name,div.mi_division_name,um.name as ee,um.mobile_number,um.email,um.designation_id " +
                "from oiipcra_oltp.oiipcra_circle_m as c " +
                "left join oiipcra_oltp.mi_division_m as div on div.circle_id=c.circle_id " +
                "left join oiipcra_oltp.user_area_mapping as uam on uam.division_id=div.mi_division_id " +
                "left join oiipcra_oltp.user_m as um on um.id=uam.user_id ";
//                "where um.designation_id=:designationId  ";
//        sqlParam.addValue("designationId", designationId);
        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public List<EngineerDatabaseDto> getCircleKBK1(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct ee.id,ee.name as ee,ee.contact_no as mobileNo,ee.email as email,c.circle_name as circleName,div.mi_division_name as miDivisionName\n" +
                "from oiipcra_oltp.engineers_incharge as ee " +
                "left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=ee.division_id " +
                "left join oiipcra_oltp.oiipcra_circle_m as c on c.id=ee.circle_id " +
                "where ee.circle_id=1 ";
        //  ee.designation_id=:designationId
        sqlParam.addValue("designationId", designationId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public List<EngineerDatabaseDto> getCircleEsternCircle(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct ee.id,ee.name as ee,ee.contact_no as mobileNo,ee.email as email,c.circle_name as circleName,div.mi_division_name as miDivisionName\n" +
                "from oiipcra_oltp.engineers_incharge as ee \n" +
                "left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=ee.division_id \n" +
                "left join oiipcra_oltp.oiipcra_circle_m as c on c.id=ee.circle_id \n" +
                "where ee.circle_id=3  ";
        sqlParam.addValue("designationId", designationId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public Object getCircleKBK2(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct ee.id,ee.name as ee,ee.contact_no as mobileNo,ee.email as email,c.circle_name as circleName,div.mi_division_name as miDivisionName\n" +
                "from oiipcra_oltp.engineers_incharge as ee \n" +
                "left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=ee.division_id \n" +
                "left join oiipcra_oltp.oiipcra_circle_m as c on c.id=ee.circle_id \n" +
                "where ee.circle_id=2 ";
        sqlParam.addValue("designationId", designationId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public Object getNorthernCircle(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct ee.id,ee.name as ee,ee.contact_no as mobileNo,ee.email as email,c.circle_name as circleName,div.mi_division_name as miDivisionName\n" +
                "from oiipcra_oltp.engineers_incharge as ee \n" +
                "left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=ee.division_id \n" +
                "left join oiipcra_oltp.oiipcra_circle_m as c on c.id=ee.circle_id \n" +
                "where ee.circle_id=4  ";
        sqlParam.addValue("designationId", designationId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public Object getCentralCircle(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct ee.id,ee.name as ee,ee.contact_no as mobileNo,ee.email as email,c.circle_name as circleName,div.mi_division_name as miDivisionName\n" +
                "from oiipcra_oltp.engineers_incharge as ee \n" +
                "left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=ee.division_id \n" +
                "left join oiipcra_oltp.oiipcra_circle_m as c on c.id=ee.circle_id \n" +
                "where ee.circle_id=5 ";
        sqlParam.addValue("designationId", designationId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public Object getSouthernCircle(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct ee.id,ee.name as ee,ee.contact_no as mobileNo,ee.email as email,c.circle_name as circleName,div.mi_division_name as miDivisionName\n" +
                "from oiipcra_oltp.engineers_incharge as ee \n" +
                "left join oiipcra_oltp.mi_division_m as div on div.mi_division_id=ee.division_id \n" +
                "left join oiipcra_oltp.oiipcra_circle_m as c on c.id=ee.circle_id \n" +
                "where  ee.circle_id=6 ";
        sqlParam.addValue("designationId", designationId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(EngineerDatabaseDto.class));
    }

    public Integer getDivisionCount() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select count(id) from oiipcra_oltp.engineers_incharge where division_id is not null ";
        // sqlParam.addValue("designationId", designationId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }


    public List<BidderDetailsDto> getBidderDataSheet(Integer bidId, java.sql.Date tenderOpeningDate, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct bidder.id,tender.bid_id as bidId,biddingType.name as biddingType,to_char(tender.technical_bid_opening_date,'dd-mm-yyyy') as tenderOpeningdateChar," +
                " tnotice.name_of_work as nameOfWork,tnotice.work_identification_code as workId,project.name as projectName,tnotice.work_sl_no_in_tcn as workSlNoInTcn," +
                " trim(to_char(tnotice.tender_amount,'999G999G999G999D99')) as tenderAmountChar, \n" +
                " tnotice.tender_amount as tenderAmount, tnotice.paper_cost as paperCost,tnotice.emd_to_be_deposited as emdToDeposit,tnotice.time_for_completion as timeOfCompletion, \n" +
                "agency.name as agencyName,license.name as licenseClass,agency.gstin_no as gstIn,agency.phone as contactNo,agency.license_validity as licenseExpiring,  \n" +
                "bidder.license_validity,case when(bidder.affidavit_validity=true) then 'Yes' else 'No' end as affidavitValid,bidderCategory.name as bidderCategory,bidder.paper_cost_amount as paperCostSubmit,case when(bidder.is_paper_cost_valid=true) then 'Yes' else 'No' end as paperCostValidity, \n" +
                "bidder.emd_amount,emdDepositedType.name as emdDepositType,case when(bidder.is_emd_valid=true) then 'Yes' else 'No' end as emdValidity, \n" +
                "fbd.amount_quoted,fbd.amount_percentage,stipulation.similar_work_value as similarWorkValue,tnotice.tender_amount*stipulation.annual_financial_turnover as annualTurnover, \n" +
                "tnotice.tender_amount*stipulation.credit_lines_amount as creditFacilityequired, \n" +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true \n" +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as bidValidity \n" +
                ", dbd.district_name as distName, mdm.mi_division_name as divName, block.block as blockName,aem.name as licenseName \n" +
                "from oiipcra_oltp.bidder_details as bidder \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id \n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id \n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id \n" +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id \n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id \n" +
                "left join oiipcra_oltp.works_bidding_type as biddingType on biddingType.id=tnotice.bidding_type \n" +
                "left join oiipcra_oltp.project_m as project on project.id=tnotice.project_id \n" +
                "left join oiipcra_oltp.bidder_category_m as bidderCategory on bidderCategory.id=bidder.bidder_category_id \n" +
                "left join oiipcra_oltp.emd_deposit_type as emdDepositedType on emdDepositedType.id=bidder.emd_deposit_type \n" +
                "left join oiipcra_oltp.tender_notice_level_mapping as tnlm on tnlm.tender_notice_id=tnotice.id \n" +
                "left join oiipcra_oltp.district_boundary as dbd on dbd.dist_id=tnlm.dist_id \n" +
                "left join oiipcra_oltp.mi_division_m as mdm on mdm.mi_division_id=tnlm.division_id\n" +
                "left join (select string_agg(b.block_name,', ') as block,a.tender_notice_id\n" +
                "           from oiipcra_oltp.tender_notice_level_mapping as a\n" +
                "           left join oiipcra_oltp.block_boundary as b on a.block_id=b.block_id group by a.tender_notice_id) as block on block.tender_notice_id=tnotice.id\n" +
                "left join oiipcra_oltp.agency_exempt_m as aem on aem.id=agency.exempt_id\n" +
                "where tender.technical_bid_opening_date=:tenderOpeningDate\n";
//        String qry = " select distinct bidder.id,tender.bid_id as bidId,biddingType.name as biddingType,to_char(tender.tender_opening_date,'dd-mm-yyyy') as tenderOpeningdateChar,tnotice.name_of_work as nameOfWork,tnotice.work_identification_code as workId,project.name as projectName,tnotice.work_sl_no_in_tcn as workSlNoInTcn,to_char(tnotice.tender_amount,'999G999G999G999D99') as tenderAmountChar,\n" +
//                "tnotice.paper_cost as paperCost,tnotice.emd_to_be_deposited as emdToDeposit,tnotice.time_for_completion as timeOfCompletion,\n" +
//                "agency.name as agencyName,license.name as licenseClass,agency.gstin_no as gstIn,agency.phone as contactNo,agency.license_validity as licenseExpiring,\n" +
//                "bidder.license_validity,bidder.affidavit_validity as affidavitValidity,bidderCategory.name as bidderCategory,bidder.paper_cost_amount as paperCostSubmit,bidder.is_paper_cost_valid as isPaperCostValid,\n" +
//                "bidder.emd_amount,emdDepositedType.name as emdDepositType,bidder.is_emd_valid as isEmdValid,\n" +
//                "fbd.amount_quoted,fbd.amount_percentage,stipulation.similar_work_value as similarWorkValue,tnotice.tender_amount*stipulation.annual_financial_turnover as annualTurnover,\n" +
//                "tnotice.tender_amount*stipulation.credit_lines_amount as creditFacilityequired,\n" +
//                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true\n" +
//                " and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as bidValidity\n" +
//                ", dbd.district_name as distName, mdm.mi_division_name as divName, bb.block_name as blockName,aem.name as licenseName\n"+
//                "from oiipcra_oltp.bidder_details as bidder\n" +
//                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id\n" +
//                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id\n" +
//                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id\n" +
//                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id\n" +
//                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id\n" +
//                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id\n" +
//                "left join oiipcra_oltp.works_bidding_type as biddingType on biddingType.id=tnotice.bidding_type\n" +
//                "left join oiipcra_oltp.project_m as project on project.id=tnotice.project_id\n" +
//                "left join oiipcra_oltp.bidder_category_m as bidderCategory on bidderCategory.id=bidder.bidder_category_id\n" +
//                "left join oiipcra_oltp.emd_deposit_type as emdDepositedType on emdDepositedType.id=bidder.emd_deposit_type\n" +
//                "left join oiipcra_oltp.tender_notice_level_mapping as tnlm on tnlm.tender_notice_id=tnotice.id\n" +
//                "left join oiipcra_oltp.district_boundary as dbd on dbd.dist_id=tnlm.dist_id\n" +
//                "left join oiipcra_oltp.mi_division_m as mdm on mdm.mi_division_id=tnlm.division_id\n" +
//                "left join oiipcra_oltp.block_boundary as bb on bb.block_id=tnlm.block_id "+
//                "left join oiipcra_oltp.agency_exempt_m as aem on aem.id=agency.exempt_id\n"+
//                " where tender.technical_bid_opening_date=:tenderOpeningDate \n" ;
        if (bidId != null && bidId > 0) {
            qry += " and tender.id=:bidId ";
            sqlParam.addValue("bidId", bidId);

        }
        if (agencyId != null && agencyId > 0) {
            qry += " and agency.id=:agencyId ";
            sqlParam.addValue("agencyId", agencyId);
        }
//        if(tenderOpeningDate.toString() > 0 && bidId != null){
//            qry += " tender.technical_bid_opening_date=:tenderOpeningDate ";
//            sqlParam.addValue("agencyId", agencyId);
//        }
        qry += " order by bidder.id,tnotice.work_identification_code ";
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BidderDetailsDto.class));
    }

    public List<BidderDetailsDto> getBidderByBidIdDD(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct bidder.tender_id as bidId, bidder.agency_id as agencyId, agency.name as agencyName, tender.technical_bid_opening_date as technicalBidOpeningDate " +
                "from oiipcra_oltp.bidder_details as bidder " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=bidder.agency_id " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                "   ";
        if (bidId > 0 && bidId != null) {
            qry += " where bidder.tender_id=:bidId ";
        }
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BidderDetailsDto.class));
    }

    public List<PackageWiseBiddersDto> getPackageWiseBidders(Integer bidId, java.sql.Date bidOpeningDate, Integer packageId, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select distinct bidder.id,tender.bid_id as bidId,biddingType.name as biddingType,to_char(tender.tender_opening_date,'dd-mm-yyyy') as tenderOpeningdateChar,tnotice.name_of_work as nameOfWork,tnotice.work_identification_code as workId,project.name as projectName,tnotice.work_sl_no_in_tcn as workSlNoInTcn,to_char(tnotice.tender_amount,'999G999G999G999D99') as tenderAmountChar,\n" +
                "tnotice.paper_cost as paperCost,tnotice.emd_to_be_deposited as emdToDeposit,tnotice.time_for_completion as timeOfCompletion,\n" +
                "agency.name as agencyName,license.name as licenseClass,agency.gstin_no as gstIn,agency.phone as contactNo,agency.license_validity as licenseExpiring,\n" +
                "bidder.license_validity,bidder.affidavit_validity as affidavitValidity,bidderCategory.name as bidderCategory,bidder.paper_cost_amount as paperCostSubmit,bidder.is_paper_cost_valid as isPaperCostValid,\n" +
                "bidder.emd_amount,emdDepositedType.name as emdDepositType,bidder.is_emd_valid as isEmdValid,\n" +
                "fbd.amount_quoted,fbd.amount_percentage,stipulation.similar_work_value as similarWorkValue,tnotice.tender_amount*stipulation.annual_financial_turnover as annualTurnover,\n" +
                "tnotice.tender_amount*stipulation.credit_lines_amount as creditFacilityequired,\n" +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true\n" +
                " and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as bidValidity\n" +
                ", dbd.district_name as distName, mdm.mi_division_name as divName, bb.block_name as blockName,aem.name as licenseName,\n" +
                "tnotice.work_identification_code as packageId, agency.gstin_no as gstInNo,  maxturnover.maxTurnOver, credit.credit_amount as creditFacilityAvailable,credit.liquidity_amount as creditFacilityRequied\n" +
                "from oiipcra_oltp.bidder_details as bidder\n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id\n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id\n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id\n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id\n" +
                "left join (select value as maxTurnOver,bidder_id,finyr_id from oiipcra_oltp.finyr_turnover\n" +
                "   where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id \n" +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id\n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id\n" +
                "left join oiipcra_oltp.liquid_asset_availability as credit on credit.bidder_id=bidder.id\n" +
                "left join oiipcra_oltp.works_bidding_type as biddingType on biddingType.id=tnotice.bidding_type\n" +
                "left join oiipcra_oltp.project_m as project on project.id=tnotice.project_id\n" +
                "left join oiipcra_oltp.bidder_category_m as bidderCategory on bidderCategory.id=bidder.bidder_category_id\n" +
                "left join oiipcra_oltp.emd_deposit_type as emdDepositedType on emdDepositedType.id=bidder.emd_deposit_type\n" +
                "left join oiipcra_oltp.tender_notice_level_mapping as tnlm on tnlm.tender_notice_id=tnotice.id\n" +
                "left join oiipcra_oltp.district_boundary as dbd on dbd.dist_id=tnlm.dist_id\n" +
                "left join oiipcra_oltp.mi_division_m as mdm on mdm.mi_division_id=tnlm.division_id\n" +
                "left join oiipcra_oltp.block_boundary as bb on bb.block_id=tnlm.block_id left join oiipcra_oltp.agency_exempt_m as aem on aem.id=agency.exempt_id\n" +

                " where tender.technical_bid_opening_date=:techBidDate \n" +
                " and tender.id=:bidId and agency.id=:agencyId order by bidder.id,tnotice.work_identification_code  ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        sqlParam.addValue("packageId", packageId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PackageWiseBiddersDto.class));

    }

    public List<Integer> getEstimateIdsByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT estimate_id from oiipcra_oltp.activity_estimate_tank_mapping where tank_id=:tankId and is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getContractIdsByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract_id from oiipcra_oltp.contract_mapping as contract  " +
                "where tank_id=:tankId and is_active = true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTenderIdsByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tender_id from oiipcra_oltp.contract_mapping as contract  " +
                "where contract_id=:contract_id and is_active = true";
        sqlParam.addValue("contract_id", contractId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getTenderIdsByExpenditureId(Integer expenditureId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tender_id from oiipcra_oltp.contract_mapping as contract  " +
                "where contract.contract_id in (select em.contract_id from oiipcra_oltp.expenditure_mapping as em where em.expenditure_id=:expenditureId) and contract.is_active = true";
        sqlParam.addValue("expenditureId", expenditureId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public String getFYearByTenderOpeningDate(java.sql.Date tenderOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select name from oiipcra_oltp.fin_year_m where :tenderOpeningDate between start_date and end_date \n ";
        sqlParam.addValue("tenderOpeningDate", tenderOpeningDate);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public Double getValueForTornOverAvail(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select round(max(ft.value), 2) as value from oiipcra_oltp.fin_year_m as fm " +
                "left join oiipcra_oltp.finyr_turnover as ft on ft.finyr_id=fm.id " +
                "where bidder_id=:bidderId  ";
//        String qry = " select ft.value from oiipcra_oltp.finyr_turnover as ft where bidder_id=:bidderId  and finyr_id in(select id from oiipcra_oltp.fin_year_m as fm where name=:valFin4) ";
        sqlParam.addValue("bidderId", bidderId);
//        sqlParam.addValue("valFin4", valFin4);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Integer deactivateTenderDocumentData(Integer draftDocumentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " UPDATE oiipcra_oltp.tender_notice_published " +
                "SET  is_active=false " +
                "WHERE id=:draftDocumentId ";
        sqlParam.addValue("draftDocumentId", draftDocumentId);
        return namedJdbc.update(qry, sqlParam);
    }

    public Integer updateFinancialBidOPeningDate(Integer tenderId, Date financialBidOpeningDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " UPDATE oiipcra_oltp.tender_m " +
                "SET  financial_bid_opening_date=:financialBidOpeningDate " +
                " WHERE id=:tenderId";
        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("financialBidOpeningDate", financialBidOpeningDate);
        return namedJdbc.update(qry, sqlParam);
    }

    public List<Integer> getExpenditureIdsByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select expenditure_id from oiipcra_oltp.expenditure_mapping where contract_id in (:contractId) ";
        sqlParam.addValue("contractId", contractId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<FinancialBidDetails> getFinancialValueByWorkId(Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT bidder_id,amount_quoted from oiipcra_oltp.financial_bid_details where is_active=true and work_id=:workId ";
        sqlParam.addValue("workId", workId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidDetails.class));
    }

    public List<TenderNotice> workIdetificationCodeByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT work_identification_code from oiipcra_oltp.tender_notice where tender_Id =:tenderId ";
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderNotice.class));
    }


    public List<BidderDetails> getBidderDataDetails(Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT * from oiipcra_oltp.bidder_details where is_active= true and work_id=:workId ";
        sqlParam.addValue("workId", workId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BidderDetails.class));
    }

    public List<TenderDto> getBidIdListForTender(Integer userId) {
        UserInfoDto userInfoById = userService.getUserById(userId);
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        qry += "Select distinct tender.id, tender.bid_id,technical_bid_opening_date,pre_bid_meeting_date from oiipcra_oltp.tender_m as tender\n" +
                "left join oiipcra_oltp.tender_notice as notice on tender.id=notice.tender_id where tender.is_active=true and tender_status != 5";
        if (userInfoById.getRoleId() > 4) {
            if (userInfoById.getRoleId() == 5 || userInfoById.getRoleId() == 6) {
                List<Integer> divisionId = getDivisionByUserId(userId);
                if (divisionId.size() > 0) {
                    qry += " and (notice.division_id in(:divisionId) or tender.created_by =:userId) ";
                    sqlParam.addValue("divisionId", divisionId);
                    sqlParam.addValue("userId", userInfoById.getUserId());
                }
            } else {
                qry += " and tender.created_by =:userId ";
                sqlParam.addValue("userId", userInfoById.getUserId());
            }
        }
        qry += "  ORDER BY tender.id desc  ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderDto.class));
    }

    public List<AgencyDto> getQualifiedAgencyByBidId(Integer tenderId, Integer workId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select agency.id,agency.name as name,agency.pan_no as panNo,bidder.id as bidderId " +
                "from oiipcra_oltp.bidder_details as bidder " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=bidder.agency_id " +
                "where  bidder.is_bid_qualified=true ";
        if (tenderId != null && tenderId > 0) {
            qry += " and bidder.tender_id=:tenderId ";
            sqlParam.addValue("tenderId", tenderId);
        }
        if (workId != null && workId > 0) {
            qry += " and bidder.work_id=:workId ";
            sqlParam.addValue("workId", workId);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
    }

    public Integer updateBidderDetails(Integer bidderId, Integer awardType) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.bidder_details " +
                "SET   award_type=:awardType " +
                "WHERE id=:bidderId;";
        sqlParam.addValue("bidderId", bidderId);
        sqlParam.addValue("awardType", awardType);
        return namedJdbc.update(qry, sqlParam);
    }

    public List<BidderDetails> cheeckTechnicalQualified(Integer bidderId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select * from oiipcra_oltp.bidder_details where is_bid_qualified=true and id=:bidderId";
        sqlParam.addValue("bidderId", bidderId);
        //sqlParam.addValue("workId", workId);


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BidderDetails.class));
    }

    public List<BidderDetails> cheeckTechnicalBidPresent(Integer tenderId, Integer workId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select * from oiipcra_oltp.bidder_details where is_bid_qualified=true and tender_id=:tenderId and work_id=:workId ";
        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("workId", workId);


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BidderDetails.class));
    }

    public List<DraftNoticeAnnexureADto> getDraftNoticeAnnexureATableData(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
      /*  String qry = "select distinct( work.id) as workId, work.work_identification_code as workIdentificationCode,work.emd_to_be_deposited as emd, work.work_sl_no_in_tcn as workSlNOTcn,  " +
//                "round(work.tender_amount/100000,2) as tenderAmount," +
                "case when (round(work.tender_amount/100000,2)) is null then 0.00  " +
                "else (round(work.tender_amount/100000,2)) end as tenderAmount, "+
                "work.tender_amount as tenderAmount2 ," +
//                "floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100 as bidSecurity," +
                "case when (floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100) is null then 0.00  " +
                "else floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100 end as bidSecurity, " +
                "type.name as biddingType,  " +
                "round((work.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnover, " +
                "round((work.tender_amount*stipulation.liquid_asset_target)/100000,2) as liquidAssetTarget,  " +
                "work.time_for_completion timeForCompletion, work.name_of_work as nameOfWork  " +
                "FROM oiipcra_oltp.tender_notice as work " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=work.tender_id  " +
                "left join oiipcra_oltp.works_bidding_type as type on  type.id = work.bidding_type  " +
                "left join oiipcra_oltp.work_type_m as workType on workType.id = work.type_of_work  " +
                "where work.tender_id=:bidId ";*/
        String qry = "select distinct( work.id) as workId\n" +
                ",work.work_identification_code as workIdentificationCode,\n" +
                "work.emd_to_be_deposited as emd, work.work_sl_no_in_tcn as workSlNOTcn,  \n" +
                "case when (round(work.tender_amount/100000,2)) is null then 0.00 \n" +
                "else (round(work.tender_amount/100000,2)) end as tenderAmount, \n" +
                "work.tender_amount as tenderAmount2 ,\n" +
                "case when (floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100) is null then 0.00 \n" +
                "else floor(((round(work.tender_amount*1/100)) + 99) / 100) * 100 end as bidSecurity, \n" +
                "type.name as biddingType,  \n" +
                "round((work.tender_amount*stipulation.annual_financial_turnover)/100000,2) as turnover,\n" +
                "round((work.tender_amount*3)/((work.time_for_completion/30)*100000),2) as liquidAssetTarget,  \n" +
                "work.time_for_completion timeForCompletion, work.name_of_work as nameOfWork ,dept_dist_name as distName,dept_block_name as blockName,name_of_the_m_i_p as nameOfTheMip\n" +
                "FROM oiipcra_oltp.tender_notice as work \n" +
                "left join oiipcra_oltp.tender_notice_level_mapping as noticeMapping on noticeMapping.tender_notice_id=work.id\n" +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=noticeMapping.id\n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=work.tender_id \n" +
                "left join oiipcra_oltp.works_bidding_type as type on  type.id = work.bidding_type\n" +
                "left join oiipcra_oltp.work_type_m as workType on workType.id = work.type_of_work \n" +
                "where noticeMapping.is_active=true and work.tender_id=:bidId";
        sqlParam.addValue("bidId", bidId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DraftNoticeAnnexureADto.class));

    }

    public String getValue(Double value) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select to_char(:value,'999G999G999G999D99') ";
        sqlParam.addValue("value", value);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public Integer getFinyrIdByPublicationDate(Date tenderPublicationDate) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select id from oiipcra_oltp.fin_year_m where ((:tenderPublicationDate) BETWEEN start_date AND end_date)  ";
        sqlParam.addValue("tenderPublicationDate", tenderPublicationDate);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public List<TenderNotice> getWorkIdentificationExistOrNot(String workIdentificationCode, Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select * from oiipcra_oltp.tender_notice where tender_id=:tenderId and work_identification_code=:workIdentificationCode ";
        sqlParam.addValue("workIdentificationCode", workIdentificationCode);
        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderNotice.class));
    }


    public String getRoundValue(Double value) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select to_char(round(:value,2),'999G999G999G999D99') ";
        sqlParam.addValue("value", value);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);

    }

    public String getStringValue(Double value) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_CONTRACT_BY_ID = " select to_char(:value,'9G99G99G999D99') ";
        sqlParam.addValue("value", value);
        return namedJdbc.queryForObject(GET_CONTRACT_BY_ID, sqlParam, String.class);
    }

    public List<TenderNoticeDto> getSubDivisionOfficerNameBySubDivisionId(Integer subDivisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
        qry += "SELECT distinct um.id as subDivisionOfficer,um.name as subDivOfficerName from oiipcra_oltp.user_m as um  " +
                "left join oiipcra_oltp.user_area_mapping as map on map.user_id= um.id  " +
                "where um.is_active=true  ";

        if (subDivisionId != null && subDivisionId > 0) {
            qry += " and map.subdivision_id=:subDivisionId  ";
            sqlParam.addValue("subDivisionId", subDivisionId);
        }

        qry += "ORDER BY um.id  ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderNoticeDto.class));
    }

    public List<Integer> getDivisionByUserId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GET_USER_MAPPING_BY_ID = " Select division_id from  oiipcra_oltp.user_area_mapping where user_id=:userId ";
        sqlParam.addValue("userId", userId);
        return namedJdbc.queryForList(GET_USER_MAPPING_BY_ID, sqlParam, Integer.class);
    }

    public List<ListOfBidsDto> getListOfBidsForAgencyDetails(Integer bidId, java.sql.Date bidOpeningDate, Integer packageId, Integer userId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "select DISTINCT agency.id as agencyId, agency.name as agency,tnotice.id  " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id " +
                "left join (select value as maxTurnOver,agency_id from oiipcra_oltp.finyr_turnover where is_maximum=true) as maxturnover on maxturnover.agency_id=agency.id " +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id " +
                "where tender.id=:bidId and tender.financial_bid_opening_date=:tenderOpeningDate and tnotice.id=:packageId  order by agency.name ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("tenderOpeningDate", bidOpeningDate);
        sqlParam.addValue("packageId", packageId);
        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ListOfBidsDto.class));

    }

    public List<PackageWiseDto> getPackageWiseDetails(Integer bidId, java.sql.Date bidOpeningDate, Integer packageId, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
        qry += "select distinct tnotice.id,project.name as projectName,tnotice.name_of_work as nameOfWork,dist.district_name,  " +
                "tnotice.work_identification_code as packageId,to_char(tender.technical_bid_opening_date, 'dd Month yyyy') as charTenderOpeningDate, case when(bidder.is_emd_valid=true)then 'Valid' else 'Not Valid'end as bidSecurity ,  " +
                "tender.bid_id as bidId,round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as charFinancialTurnover,  " +
                "agency.name as agency,license.name as licenseClass,agency.gstin_no as gstInNo,agency.phone::text as contact,to_char(agency.license_validity, 'dd Month yyyy') as charLicenseExpiring, " +
                "tender.bid_id as tenderBidId,case when (agency.license_validity>tender.technical_bid_opening_date) then 'License Ok' else 'License Expired' end as licenseValidity,  " +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true   " +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Qualified' else 'Not Eligible' end as bidValidity,  " +
//                "round(maxturnover.maxTurnOver,2) as maxturnover, " +
                "to_char(maxturnover.maxTurnOver,'9G99G99G999D99') as maxTurnOver," +
//                " round(similarWork.value,2) as similarWorkValueReq," +
                "round(similarWork.percentage_completed,2) as percentageCompleted,  " +
                "to_char(tnotice.tender_amount,'9G99G99G999D99') as charTenderAmount,  " +
                "to_char(fbd.amount_quoted,'999G999G999G999D99') as charAmountQuoted,fbd.amount_quoted,  " +
                "round(fbd.amount_percentage,2) as amountPercentage,  " +
                "tnotice.tender_amount,to_char(similarWork.similar_work_amount,'9G99G99G999D99') as similarWorkValue,  " +
//                "--round(similarWork.similar_work_amount,2) as similarWorkValue,  " +
                "round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as charAnnualTurnover,   " +
                "similarWork.similar_work_amount,credit.credit_amount as creditFacilityAvailable,credit.liquidity_amount as creditFacilityRequired  " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id   " +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  " +
                "left join oiipcra_oltp.project_m as project on project.id=tnotice.project_id  " +
                "left join (select equivalent as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover  " +
                "where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  " +
                "Left join oiipcra_oltp.completion_of_similar_type_work as similarWork on similarWork.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.liquid_asset_availability as credit on credit.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.tender_notice_level_mapping as mapping on mapping.tender_notice_id=tnotice.id  " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id=mapping.dist_id   " +
//                "left join oiipcra_oltp.block_boundary as block on mapping.block_id=block.block_id  " +
                "where tender.id=:bidId and tender.financial_bid_opening_date=:bidOpeningDate  " +
                "and tnotice.id =:packageId  " +
                "order by fbd.amount_quoted asc ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        sqlParam.addValue("packageId", packageId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PackageWiseDto.class));
    }

    public List<TechnicalBidAttachmentDto> getTechnicalBidAttachmentDetails(Integer bidId, java.sql.Date techBidDate, Integer packageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        qry += "select distinct tnotice.id as workId,bidder.id as bidderId,project.name as schemeOfFunding,tnotice.name_of_work,dist.district_name," +
//                "block.block_name, \n" +
                "tnotice.work_identification_code as workId,to_char(tender.technical_bid_opening_date, 'dd-Mon-yyyy') as charTenderOpeningDate, \n" +
                "tnotice.work_sl_no_in_tcn as workSlNoInTcn,bidder.emd_bank_name as bidSecuritySubmitted,CASE WHEN (bidder.is_emd_valid)=true then 'Yes' else 'NO' end as eSignedBid, \n" +
                "tender.bid_id as bidIdentification,to_char(tnotice.emd_to_be_deposited,'9G99G99G999D99') as emdToBeDeposited,tnotice.time_for_completion::text as timeForCompletion, \n" +
                "round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as charFinancialTurnover,tnotice.paper_cost as paperCostSpecified, \n" +
                "agency.name as agency,license.name as licenseClass,agency.gstin_no as gstInNo,agency.phone::text as contact,to_char(agency.license_validity, 'dd Mon yyyy') as charLicenseExpiring,\n" +
                "tender.bid_id as tenderBidId,case when (agency.license_validity>tender.technical_bid_opening_date) then 'License Ok' else 'License Expired' end as licenseValidity,  \n" +
                "to_char(maxturnover.maxTurnOver,'9G99G99G999D99') as maxturnover, \n" +
                "round(percentage_completed,2) as workCompleted,case when (bidder.affidavit_validity)=true then 'Yes' else 'NR' end as affidavitValidity,    " +
                "year.name as maxTurnOverYear,to_char(similarWork.value,'9G99G99G999D99') as similarTypeWorkWorkValue,\n" +
                "to_char(tnotice.tender_amount,'9G99G99G999D99') as charTenderAmount,   \n" +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true   \n" +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Yes' else 'No' end as agreedBidValidity,  " +
                "to_char(fbd.amount_quoted,'999G999G999G999D99') as charAmountQuoted,fbd.amount_quoted,  \n" +
                "round(fbd.amount_percentage,2) as amountPercentage,similarWork.finyr_id,year.name as finyrName,\n" +
                "tnotice.tender_amount,to_char(similarWork.similar_work_amount,'9G99G99G999D99') as similarWorkValue, \n" +
                "to_char(similarWork.completed_amount,'9G99G99G999D99') as valueOfWorkCompleted,\n" +
                "round((tnotice.tender_amount*stipulation.annual_financial_turnover)/100000,2) as charAnnualTurnover,  \n" +
                "similarWork.similar_work_amount as amount,credit.credit_amount as creditFacilityAvailable,credit.liquidity_amount as creditFacilityRequired,\n" +
                "(credit.credit_amount - credit.liquidity_amount) as amountOfLiquidAssets,\n" +
                "CASE WHEN (bidder.liquid_asset_qualified)=true then 'Yes' else 'NR' end as liquidAssetQualified " +
                "from oiipcra_oltp.bidder_details as bidder \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  \n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  \n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id  \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id  \n" +
                "left join oiipcra_oltp.project_m as project on project.id=tnotice.project_id  \n" +
                "left join (select equivalent as maxTurnOver,bidder_id from oiipcra_oltp.finyr_turnover  \n" +
                "where is_maximum=true) as maxturnover on maxturnover.bidder_id=bidder.id  \n" +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id \n" +
                "left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  \n" +
                "Left join oiipcra_oltp.completion_of_similar_type_work as similarWork on similarWork.bidder_id=bidder.id  \n" +
                "left join oiipcra_oltp.liquid_asset_availability as credit on credit.bidder_id=bidder.id  \n" +
                "left join oiipcra_oltp.tender_notice_level_mapping as mapping on mapping.tender_notice_id=tnotice.id  \n" +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id=mapping.dist_id  \n" +
//                "left join oiipcra_oltp.block_boundary as block on mapping.block_id=block.block_id \n" +
                "left join oiipcra_oltp.finyr_turnover as turnOver on turnOver.bidder_id=bidder.id \n" +
                "left join oiipcra_oltp.fin_year_m as year on year.id=similarWork.finyr_id " +
//                "and year.id=turnOver.finyr_id\n" +
                "where tender.id=:bidId  and tender.technical_bid_opening_date=:techBidDate\n" +
                "and tnotice.id=:packageId and similarWork.finyr_id=similarWork.executed_year and turnOver.is_maximum=true\n" +
                "order by fbd.amount_quoted desc  ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("techBidDate", techBidDate);
        sqlParam.addValue("packageId", packageId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAttachmentDto.class));
    }

    public List<CompletionOfSimilarTypeOfWorkDto> getSimilarTypeWorkValue(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
        qry += "select value from oiipcra_oltp.completion_of_similar_type_work  " +
                "where bidder_id in (:bidderId) order by id desc  ";
        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CompletionOfSimilarTypeOfWorkDto.class));
    }

    public PreBidMeetingDto getDetailsOfPreBidMeeting(Integer bidId, java.sql.Date bidOpeningDate, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select tender.bid_id as bidId,to_char(tender.technical_bid_opening_date,'DD-MM-YYYY') as technicalBidOpeningDate,   " +
                "to_char(tender.pre_bid_meeting_date,'DD-MM-YYYY') as preBidMeetingDate,  " +
                "CASE WHEN(meeting.no_of_participants)is null THEN '0'  " +
                "ELSE meeting.no_of_participants end as noOfParticipants,  " +
                "CASE WHEN(meeting.no_of_online_clarification)is null THEN '0'  " +
                "ELSE meeting.no_of_online_clarification end as noOfOnlineClarification,  " +
                "CASE WHEN(meeting.no_of_clarification_sought)is null THEN '0'  " +
                "ELSE meeting.no_of_clarification_sought end as noOfClarificationSought,  " +
                "CASE WHEN(meeting.clarification_disposed_online)is null THEN '0'  " +
                "ELSE meeting.clarification_disposed_online end as clarificationDisposedOnline,  " +
                "CASE WHEN(meeting.clarification_disposed_in_pre_bid)is null THEN '0'  " +
                "ELSE meeting.clarification_disposed_in_pre_bid end as clarificationDisposedInPreBid  " +
                "from oiipcra_oltp.tender_m as tender   " +
                "left join oiipcra_oltp.meeting_proceeding as meeting on tender.id=meeting.tender_id  " +
                "where tender.is_active=true and tender.id=:bidId and tender.technical_bid_opening_date=:bidOpeningDate  ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(PreBidMeetingDto.class));
    }

    public String getBidOpeningDate(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT to_char((tender_opening_date + 90), 'dd-MM-yyyy') from oiipcra_oltp.tender_m where id=:bidId ";
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public List<TechnicalBidAttachmentDto> getSimilarWorkValue(Integer bidId, Integer packageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT round(similarWork.value,2) as value,similarWork.finyr_id,year.name  " +
                "from oiipcra_oltp.completion_of_similar_type_work as similarWork  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id= similarWork.finyr_id  " +
                "where similarWork.is_active=true and similarWork.bidder_id in(select bidder.id from oiipcra_oltp.bidder_details as bidder  " +
                "where tender_id=:bidId and work_id=:packageId)  " +
                "ORDER by similarWork.value desc  ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("packageId", packageId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAttachmentDto.class));
    }

    public List<FinancialBidEvaluationDto> getFinancialBidDetails1(Integer bidId, java.sql.Date technicalBidDate, Integer packageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tnotice.id as tNoticeId,bidder.id as bidderId,agency.id as agencyId, tnotice.name_of_work as nameOfWork ,Upper(dist.district_name) as distName, " +
                "'Mi Division, '||div.mi_division_name as miDivisionInCharge, " +
                "tnotice.work_identification_code as workId,  " +
                "to_char(tnotice.tender_amount,'9G99G99G99G999D99') as estimatedCost,  " +
                "to_char(fbd.review_tech_bid_date,'dd-mm-yyyy') as reviewTechBidDate,  " +
                "to_char(tender.technical_bid_opening_date,'dd-mm-yyyy') as techBidDate,  " +
                "to_char(tender.financial_bid_opening_date,'dd-mm-yyyy') as finBidOpeningDate, " +
                "to_char(fbd.review_fin_bid_date,'dd-mm-yyyy') as reviewFinBidDate,  " +
                "to_char(fbd.amount_quoted,'9G99G99G99G999D99') as bidPrice, round(fbd.amount_percentage,2) as percentageExcessLess,  " +
                "tender.bid_id as bidIdentification,agency.name||', '||lclass.name||' ' as bidderNameClass,agency.name as agencyName, " +
                "depositedType.name,bidder.is_bid_awarded,bidder.award_type, award.name as awardTypeName,  " +
                "to_char(tnotice.tender_amount*0.05,'999G9G999G999D99') as mobilisationAdvance, \n" +
                "to_char(tnotice.tender_amount*0.1,'999G9G999G999D99') as equipmentAdvance, \n" +
                "to_char((tnotice.tender_amount*2.5)/100,'999G9G999G999D99') as performanceSecurity,\n" +
                "to_char(fbd.additional_performance_sec_required,'999G9G999G999D99') as addPerfSecurity,  " +
                "to_char(tender.technical_bid_opening_date+90,'dd-mm-yyyy') as bidValidityUpto " +
                "from oiipcra_oltp.financial_bid_details as fbd  " +
                "left join oiipcra_oltp.bidder_details as bidder on fbd.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.tender_m as tender on bidder.tender_id=tender.id  " +
                "left join oiipcra_oltp.tender_notice as tnotice on bidder.work_id=tnotice.id  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=bidder.agency_id  " +
                "left join oiipcra_oltp.license_class as lclass on lclass.id=agency.license_class_id " +
                "left join oiipcra_oltp.emd_deposit_type as depositedType on depositedType.id=bidder.emd_deposit_type  " +
                "left join oiipcra_oltp.tender_result as result on result.bidder_id=bidder.id  " +
                "left join oiipcra_oltp.tender_notice_level_mapping as tnlm on tnlm.tender_notice_id=tnotice.id  " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id=tnlm.dist_id  " +
                "left join oiipcra_oltp.mi_division_m as div on div.id=tnlm.division_id  " +
                "left join oiipcra_oltp.award_type as award on award.id=bidder.award_type " +
                "where bidder.is_bid_awarded=true and  " +
                "tender.id=:bidId and tender.technical_bid_opening_date=:technicalBidDate and tnotice.id=:packageId   " +
                "order by agency.name ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("technicalBidDate", technicalBidDate);
        sqlParam.addValue("packageId", packageId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidEvaluationDto.class));

    }

    public FinYrDto getFinyr(Integer bidId, java.sql.Date techBidDate) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select tender.finyr_id as id,year.name as name from oiipcra_oltp.tender_m as tender  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id= tender.finyr_id  " +
                "where tender.is_active=true and tender.id=:bidId and tender.technical_bid_opening_date=:techBidDate ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("techBidDate", techBidDate);

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(FinYrDto.class));
    }

    public String getValueByBidderId(Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT to_char(similarWork.value,'9G99G99G999D99') as value  " +
                "from oiipcra_oltp.completion_of_similar_type_work as similarWork  " +
                "left join oiipcra_oltp.fin_year_m as year on year.id= similarWork.finyr_id  " +
                "where similarWork.is_active=true and similarWork.bidder_id=:bidderId " +
                "ORDER by similarWork.value desc LIMIT 1  ";

        sqlParam.addValue("bidderId", bidderId);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public Integer getDesignationId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT designation_id from oiipcra_oltp.user_m  where id=:id   ";

        sqlParam.addValue("id", userId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public MeetingProceedingDto getMeetingProceedingsByTenderId(Integer tenderId, Integer meetingTypeId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select  meeting.id,tender_id as bidId,meeting_type,no_of_participants,\n" +
                "no_of_online_clarification,no_of_clarification_sought,\n" +
                "clarification_disposed_online,clarification_disposed_in_pre_bid,tender.bid_id as tender,type.name as meetingTypeName,meeting.created_by\n" +
                "from oiipcra_oltp.meeting_proceeding as meeting \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=meeting.tender_id\n" +
                "left join oiipcra_oltp.meeting_type as type on type.id=meeting.meeting_type\n" +
                "where tender_id=:tenderId and meeting_type=:meetingType";
        sqlParam.addValue("tenderId", tenderId);
        sqlParam.addValue("meetingType", meetingTypeId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(MeetingProceedingDto.class));
        } catch (Exception e) {
            return null;
        }
    }

    public ShlcMeetingDto getShlcMeeting(java.sql.Date date) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, meeting_serial_no, meeting_sequence_no, date_of_meeting, proceedings_issued_lt_nc, committee_formed_date, vote_of_thanks, is_active, created_by, created_on, updated_by, updated_on, proceedings_issue_letter_date as proceedingsIssuedDate\n" +
                "\tFROM oiipcra_oltp.shlc_meeting where date_of_meeting=:date";
        sqlParam.addValue("date", date);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ShlcMeetingDto.class));
        } catch (Exception e) {
            return null;
        }
    }

    public List<CommitteeMembersDto> getCommitteeMemberByMeetingProceedingId(Integer meetingProceedingId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select   committe.id,meeting_proceeding_id,committe.designation_id,member_id,userM.name as memberName,designation.name as designation\n" +
                "from oiipcra_oltp.committee_members as committe\n" +
                "left join oiipcra_oltp.user_m as userM on userM.id=committe.member_id\n" +
                "left join oiipcra_oltp.designation_m as designation on designation.id=committe.designation_id\n" +
                "where committe.is_active=true and meeting_proceeding_id=:meetingProceedingId";
        sqlParam.addValue("meetingProceedingId", meetingProceedingId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CommitteeMembersDto.class));
    }

    public List<CommitteeMembersDto> getCommitteeMemberByShlcMeetingId(Integer shlcId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select   committe.id,shlc_meeting_id,committe.designation_id,user_id as memberId,\n" +
                "userM.name as memberName,designation.name as designation\n" +
                "from  oiipcra_oltp.shlc_meeting_members as committe\n" +
                "left join oiipcra_oltp.user_m as userM on userM.id=committe.user_id\n" +
                "left join oiipcra_oltp.designation_m as designation on designation.id=committe.designation_id \n" +
                "where committe.is_active=true and shlc_meeting_id=:shlcId";
        sqlParam.addValue("shlcId", shlcId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CommitteeMembersDto.class));
    }

    public List<PreBidClarificationsDto> getPreBidClarificationByMeetingProceedingId(Integer meetingProceedingId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select id,pre_bid_clarifications_sought_by_bidders,pre_bid_clarifications_disposed_with_reply,meeting_proceeding_id\n" +
                "from oiipcra_oltp.pre_bid_clarifications " +
                "where is_active=true and meeting_proceeding_id=:meetingProceedingId ";
        sqlParam.addValue("meetingProceedingId", meetingProceedingId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PreBidClarificationsDto.class));
    }

    public List<ShlcMeetingProceedingsEntity> getShlcMeetingProceedings(Integer shlcId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT * FROM oiipcra_oltp.shlc_meeting_proceedings where shlc_meeting_id=:shlcId ";
        sqlParam.addValue("shlcId", shlcId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ShlcMeetingProceedingsEntity.class));
    }

    public List<TechnicalBidAttachmentDto> getTechnicalBids(Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct tnotice.id as tNoticeId, \n" +
                " tender.bid_id as bidId, \n" +
                " to_char(tender.technical_bid_opening_date,'dd-mm-yyyy') as techBidDate, \n" +
                " tnotice.work_sl_no_in_tcn as workSlNoTcn, \n" +
                " tnotice.work_identification_code as workId, \n" +
                " dist.district_name as distName, \n" +
                " case when tnotice.time_for_completion >=30 then tnotice.time_for_completion||' Days' else tnotice.time_for_completion||' Months' end as periodOfCompletion, \n" +
                " to_char(tnotice.tender_amount,'9G99G99G99G999D99') as tenderAmount, \n" +
                " to_char(tnotice.emd_to_be_deposited,'9G99G99G99G999D99') as emdDeposited, \n" +
                " to_char(tender.approval_for_procurement_date,'dd-mm-yyyy') as procurementDate, \n" +
                " to_char(tender.tender_publication_date,'dd-mm-yyyy') as tenderPublishDate,to_char(tender.bid_submission_date,'dd-mm-yyyy') as bidSubmissionDate,to_char(tender.bid_submission_date_revised,'dd-mm-yyyy') as bidSubmissionDateRevised, \n" +
                " to_char(tender.pre_bid_meeting_date,'dd-mm-yyyy') as preBidMeetingDate \n" +
                "  ,to_char(tender.bid_submission_date+90 ,'dd-mm-yyyy') as bidValidUpto, \n" +
                " stipulation.similar_work_completion*100 as clause3a1,stipulation.similar_work_value*100 as clause3a2,round((stipulation.similar_work_value*tnotice.tender_amount/100000),2) as clause3a3, \n" +
                " stipulation.annual_financial_turnover as clause3b1,round((stipulation.annual_financial_turnover-1)*10) as clause3b2, \n" +
                " round((stipulation.liquid_asset_target*tnotice.tender_amount/100000),2) as clause3g," +
                " to_char(tender.financial_bid_opening_date,'dd-mm-yyyy') as financialBidOpeingDate,  \n" +
                " to_char(tender.technical_bid_approval_date,'dd-mm-yyyy') as technicalBidApprovalDate " +
                " from oiipcra_oltp.tender_m as tender \n" +
                " left join oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id \n" +
                " left join oiipcra_oltp.tender_notice_level_mapping as noticeMapping on noticeMapping.tender_notice_id=tnotice.id \n" +
                " left join oiipcra_oltp.district_boundary as dist on dist.dist_id=noticeMapping.dist_id \n" +
                "  left join oiipcra_oltp.tender_stipulation as stipulation on stipulation.tender_id=tender.id  \n" +
                " where tender.id=:bidId and tender.technical_bid_opening_date=:techBidDate and tnotice.id=:packageId  ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("packageId", packageId);
        sqlParam.addValue("techBidDate", techBidDate);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAttachmentDto.class));
    }

    public List<TechnicalBidAttachmentDto> eProcurementNotice1(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select tp.id,tp.name as newsPaperName,b.name as levelName,to_char(tp.published_date,'dd-mm-yyyy') as publishedDate from oiipcra_oltp.tender_published as tp\n" +
                "left join oiipcra_oltp.newspaper_type as b on b.id=tp.newspaper_type\n" +
                "left join oiipcra_oltp.tender_m as tm on tm.id=tp.tender_id\n" +
                "where tp.tender_published_type=1 and tm.id=:bidId  ";

        sqlParam.addValue("bidId", bidId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAttachmentDto.class));
    }

    public List<TechnicalBidAttachmentDto> eProcurementNotice2(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select tp.id,tp.name as newsPaperName,b.name as levelName,to_char(tp.published_date,'dd-mm-yyyy') as publishedDate from oiipcra_oltp.tender_published as tp\n" +
                "left join oiipcra_oltp.newspaper_type as b on b.id=tp.newspaper_type\n" +
                "left join oiipcra_oltp.tender_m as tm on tm.id=tp.tender_id\n" +
                "where tp.tender_published_type=2 and tm.id=:bidId ";

        sqlParam.addValue("bidId", bidId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAttachmentDto.class));
    }

    public Object getTechBidBidderDetails(Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct tnotice.id as tNoticeId,bidder.id,\n" +
                "tender.bid_id as bidId,agency.name||', '||lclass.name||' Contractor.' as biderNameClass,agency.name as agencyName,\n" +
                "to_char(bidder.emd_amount,'9G99G99G99G999D99') as emdAmount,depositedType.name as transactionType,is_bid_awarded as isBidderAwarded,bidder.award_type,\n" +
                "case when completion_work_value_qualified is false then 'Clause 3a: No Similar Work Experience.' end as tenderNotAwardedReason1,\n" +
                "case when bidder.turn_over_qualified is false then 'Claus 3b: Financial Turover Insufficient.' end as tenderNotAwardedReason2,\n" +
                "case when bidder.liquid_asset_qualified is false then 'Clause 3g: Credit facility not uploaded.' end as tenderNotAwardedReason3\n" +
                "from oiipcra_oltp.tender_m as tender\n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.tender_id=tender.id\n" +
                "left join oiipcra_oltp.bidder_details as bidder on bidder.work_id=tnotice.id\n" +
                "left join oiipcra_oltp.agency_m as agency on agency.id=bidder.agency_id\n" +
                "left join oiipcra_oltp.license_class as lclass on lclass.id=agency.license_class_id\n" +
                "left join oiipcra_oltp.emd_deposit_type as depositedType on depositedType.id=bidder.emd_deposit_type\n" +
                "left join oiipcra_oltp.tender_result as result on result.bidder_id=bidder.id\n" +
                "where tender.id=:bidId and tender.technical_bid_opening_date=:techBidDate and tnotice.id=:packageId ";
        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("packageId", packageId);
        sqlParam.addValue("techBidDate", techBidDate);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TechnicalBidAttachmentDto.class));
    }

    public Double getPreviousYearWeightage(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT previous_yr_weightage from oiipcra_oltp.tender_stipulation  " +
                "where tender_id=:bidId ";

        sqlParam.addValue("bidId", bidId);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public String getRoundValue2(Double amount) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select to_char(:amount,'999G999G999G999D99') ";
        sqlParam.addValue("amount", amount);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public List<BlockBoundaryDto> getBlockNameDetails(Integer bidId, java.sql.Date bidOpeningDate, Integer packageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select STRING_AGG(DISTINCT block.block_name, ',')as block_name " +
                "from oiipcra_oltp.tender_notice as tnotice  " +
                "left join oiipcra_oltp.tender_notice_level_mapping as mapping on mapping.tender_notice_id=tnotice.id  " +
                "left join oiipcra_oltp.block_boundary as block on block.block_id=mapping.block_id  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id= tnotice.tender_id  " +
                "where tnotice.is_active=true and   " +
                "tender.id=:bidId and tender.financial_bid_opening_date=:bidOpeningDate   " +
                "and tnotice.id=:packageId  ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        sqlParam.addValue("packageId", packageId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public String getSimilarWorkValueRequired(Integer bidId, java.sql.Date bidOpeningDate, Integer packageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select max(round(similarWork.value,2)) as similarWorkValueReq  " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id  " +
                "Left join oiipcra_oltp.completion_of_similar_type_work as similarWork on similarWork.bidder_id=bidder.id  " +
                "where tender.id=:bidId and tender.financial_bid_opening_date=:bidOpeningDate  " +
                "and tnotice.id =:packageId ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        sqlParam.addValue("packageId", packageId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Double getMaximumBidByTenderId(Integer bidId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select bid_capacity_turnover from oiipcra_oltp.tender_stipulation " +
                "where is_active= true and tender_id=:bidId ";
        sqlParam.addValue("bidId", bidId);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public List<FinancialBidEvaluationDto> getBidderWiseFinancialBidDetails(Integer bidId, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct bidder.id,tnotice.work_identification_code as packageId,  " +
                "round(tnotice.tender_amount/100000,2) as estimated,   " +
                "round(fbd.amount_quoted/100000,2) as amountOfBid,  " +
                "to_char(turnover.equivalent,'9G99G99G99G999D99')as maxAnnualTurnOver,round(bidder.max_bid_capacity,2) as maxBidCapacity,  " +
                "to_char((bidder.max_bid_capacity-(2*round(tnotice.tender_amount/100000,2))),'9G99G99G99G999D99') as balanceBidCapacityString , " +
                "(bidder.max_bid_capacity-(2*round(tnotice.tender_amount/100000,2))) as balanceBidCapacity " +
                //"to_char((1.5*round(turnover.equivalent,2)*(time_for_completion/365)),'9G99G99G99G999D99') as maxBidCapacity   " +
                "from oiipcra_oltp.bidder_details as bidder  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id  " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id and tnotice.tender_id=bidder.tender_id " +
                "left join oiipcra_oltp.financial_bid_details as fbd on fbd.bidder_id=bidder.id " +
                "left join oiipcra_oltp.finyr_turnover as turnover on turnover.bidder_id=bidder.id  " +
                "where bidder.tender_id=:bidId and bidder.agency_id=:agencyId and turnover.is_maximum=true and bidder.award_type in(1,2,7)  " +
                "order by tnotice.work_identification_code ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("agencyId", agencyId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(FinancialBidEvaluationDto.class));
    }


    public List<PackageWiseDto> getProjectIds(Integer bidId, java.sql.Date bidOpeningDate, Integer packageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tlevel.tank_id as tankId ,tank.project_id as projectId " +
                "from oiipcra_oltp.tender_notice_level_mapping as tlevel " +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=tlevel.tender_notice_id  " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=tnotice.tender_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id=tlevel.tank_id  " +
                "where tnotice.is_active=true and tnotice.tender_id=:bidId and   " +
                "tender.financial_bid_opening_date=:bidOpeningDate  " +
                "and tnotice.id =:packageId ";

        sqlParam.addValue("bidId", bidId);
        sqlParam.addValue("bidOpeningDate", bidOpeningDate);
        sqlParam.addValue("packageId", packageId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PackageWiseDto.class));
    }

    public List<ActivityEstimateTankMappingDto> getTankNameByActivityId(Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "Select activity.id as estimateId,activity.name_of_work as nameOfWork,activity.t_project_id as tProjectId,activity.tank_id as tankId,tank.name_of_the_m_i_p as tankName " +
                "from oiipcra_oltp.activity_estimate_mapping as activity  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id=activity.tank_id " +
                "where activity.is_active =true  " +
                "and activity.activity_id=:activityId ORDER BY activity.tank_id asc ";

        sqlParam.addValue("activityId", activityId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimateTankMappingDto.class));

    }

    public List<NoticeListingDto> getTenderNoticeDataByTenderId(Integer tenderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String getTenderNoticeByTenderId = "SELECT tn.id,tn.bidding_type as biddingTypeId,tenm.bid_id as bidId, ty.name as biddingType,tn.type_of_work, work.name as workType, tn.work_sl_no_in_tcn, tn.work_identification_code, tn.name_of_work,\n" +
                "        tn.tender_amount, tn.paper_cost, tn.emd_to_be_deposited, tn.time_for_completion, \n" +
                "                tn.contact_no, tn.tender_level_id,level.level_name as tenderLevel,tn.circle_id,tn.division_id, tn.ee_id, tn.sub_division_id, tn.sub_division_officer, \n" +
                "                tn.project_id,p.name as projectName,tn.tender_not_awarded_reason,tn.ee_id,userm.name as eeName,tn.ee_type as eeType,eeType.name as eeTypeName,tn.other_ee  as otherEe,\n" +
                "                tn.ee_contact_no as eeContactNo from oiipcra_oltp.tender_notice as tn  \n" +
                "                left join oiipcra_oltp.tender_level_master as level on level.id =tn.tender_level_id  \n" +
                "                left join oiipcra_oltp.works_bidding_type as ty on ty.id = tn.bidding_type  \n" +
                "                left join oiipcra_oltp.tender_work_type as work on work.id = tn.type_of_work\n" +
                "                left join oiipcra_oltp.project_m as p on p.id=tn.project_id \n" +
                "                left join oiipcra_oltp.user_m as userm on userm.id=tn.ee_id \n" +
                "                left join oiipcra_oltp.ee_type as eeType on eeType.id=tn.ee_type \n" +
                "\t\t\t\tleft join oiipcra_oltp.tender_m as tenm on tenm.id=tn.tender_id" +
                " WHERE tn.tender_id =:tenderId ";

        sqlParam.addValue("tenderId", tenderId);
        return namedJdbc.query(getTenderNoticeByTenderId, sqlParam, new BeanPropertyRowMapper<>(NoticeListingDto.class));
    }


    public FormGDto getFormGDetails2(Integer finYr, String issueNo, Integer bidderId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        List<FormGDto> formG = new ArrayList<>();
        String qry = "";
        qry = "select finyr.name as finyr,agency.name as agencyName,license.name as licenseClass,agency.gstin_no,tender.bid_id||' / '||tnotice.work_identification_code as bid_workId,   \n" +
                "tnotice.id,dist.district,scheme.name as scheme,to_char(tender.tender_opening_date,'DD-MM-YYYY'),to_char(tender.technical_bid_opening_date,'DD-MM-YYYY'),tnotice.tender_amount,to_char(tnotice.tender_amount,'999G999G999G999D99') as tenderAmountString,bidder.agency_id,\n" +
                "to_char(sum(tnotice.tender_amount)over (partition by agency.id,tender.finyr_id) ,'999G999G999G999D99') as sumString," +
                " to_char(technical_bid_opening_date,'DD-MM-YYYY')  dateString ,  \n" +
                "case when (bidder.completion_work_value_qualified=true and bidder.turn_over_qualified=true   \n" +
                "and bidder.liquid_asset_qualified=true and bidder.is_bid_qualified=true and bidder.is_credit_qualified) then 'Eligible' else 'Not Eligible' end as validity_of_tender  \n" +
//                "awarded.name as awarded  \n" +
                "from oiipcra_oltp.bidder_details as bidder \n" +
                "left join oiipcra_oltp.tender_m as tender on tender.id=bidder.tender_id   \n" +
                "left join oiipcra_oltp.tender_notice as tnotice on tnotice.id=bidder.work_id   \n" +
                "left join oiipcra_oltp.agency_m as agency on bidder.agency_id=agency.id   \n" +
                "left join oiipcra_oltp.license_class as license on license.id=agency.license_class_id \n" +
                "left join oiipcra_oltp.fin_year_m as finyr on tender.finyr_id=finyr.id  \n" +
                "left join(select string_agg(distinct b.district_name,', ') as district,a.tender_notice_id from oiipcra_oltp.tender_notice_level_mapping as a left join oiipcra_oltp.district_boundary as b on a.dist_id=b.dist_id group by a.tender_notice_id) as dist on dist.tender_notice_id=tnotice.id   \n" +
                "left join oiipcra_oltp.project_m as scheme on scheme.id=tnotice.project_id \n" +
                "left join oiipcra_oltp.award_type as awarded on awarded.id=bidder.award_type  ";


        if (bidderId > 0) {
            qry += " where finyr.id=:finYr and agency_id=:bidderId  order by bidder.id desc  ";
        }

        sqlParam.addValue("finYr", finYr);
        sqlParam.addValue("bidderId", bidderId);
        sqlParam.addValue("issueNo", issueNo);

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(FormGDto.class));

    }
}





