package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.IssueTracker;
import com.orsac.oiipcra.entities.IssueTypeMaster;
import com.orsac.oiipcra.repository.UserQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class IssueRepositoryImpl {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

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

    public Page<IssueInfoListing> getAllIssueList(IssueSearchRequest issueSearchRequest, List<Integer> tankIds) {
        PageRequest pageable = PageRequest.of(issueSearchRequest.getPage(), issueSearchRequest.getSize(), Sort.Direction.fromString(issueSearchRequest.getSortOrder()), issueSearchRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "issue_date");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        Integer resultCount = 0;
        String queryString = " ";

        List<Integer> deptIds;
        deptIds = new ArrayList<>();
        deptIds.add(issueSearchRequest.getDeptId());

        List<Integer> levelIds;
        levelIds = new ArrayList<>();
        levelIds.add(issueSearchRequest.getDeptId());

        UserInfoDto userInfoById = userQryRepo.getUserById(issueSearchRequest.getUserId());


        queryString += "select Distinct issue.id as issueId,type.name as issueType,issue.remarks as issueDescription," +
                "status.status as issueStatus,tank.name_of_the_m_i_p as tankName,issue.created_on as issueDate," +
                "issue.dept_id,dept.name as department, tank.project_id as projectId,  " +
                " issue.issue_date as issueDate, issue.tank_id as tankId,contract.contract_number as contractNumber,tender.bid_id as bidId, " +
                "issue.contract_id as contractId,issue.work_id as workId, md.id as activityId,type.name as issueType from " +
                "oiipcra_oltp.issue_tracker as issue  " +
                "left join oiipcra_oltp.issue_status as status on status.id=issue.status   " +
                "left join oiipcra_oltp.issue_type_m as type on type.id=issue.issue_type_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id= issue.tank_id  " +
                "left join oiipcra_oltp.dept_m as dept on dept.id = issue.dept_id    " +
                "left join oiipcra_oltp.master_head_details as md on md.id = issue.activity_id   " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=issue.tender_id "+
                "left join oiipcra_oltp.contract_m as contract on contract.id=issue.contract_id      " +
                "where issue.is_active=true";


        if (tankIds != null && tankIds.size() > 0) {
            queryString += " AND issue.tank_id IN (:tankIds)";
            sqlParam.addValue("tankIds", tankIds);
        } else if (issueSearchRequest.getContractId() != null && issueSearchRequest.getContractId() > 0) {
            queryString += " AND issue.contract_id = :contractId";
            sqlParam.addValue("contractId", issueSearchRequest.getContractId());
        } else if (issueSearchRequest.getInvoiceId() != null && issueSearchRequest.getInvoiceId() > 0) {
            queryString += " AND invoice.id IN (:invoiceId)";
            sqlParam.addValue("invoiceId", issueSearchRequest.getInvoiceId());
        }
        if (issueSearchRequest.getDeptId() != null && issueSearchRequest.getDeptId() > 0) {
            queryString += " AND issue.dept_id IN (:deptId)";
            sqlParam.addValue("deptId", issueSearchRequest.getDeptId());
        }

        if (issueSearchRequest.getIssueStatus() > 0) {
            queryString += " AND status.id=:statusId";
            sqlParam.addValue("statusId", issueSearchRequest.getIssueStatus());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (issueSearchRequest.getUploadFromDate() != null && !issueSearchRequest.getUploadFromDate().isEmpty()) {
            queryString += " AND date(issue.issue_date) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(issueSearchRequest.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (issueSearchRequest.getUploadToDate() != null && !issueSearchRequest.getUploadToDate().isEmpty()) {
            queryString += " AND date(issue.issue_date) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(issueSearchRequest.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }

        List<Integer> userIdList = new ArrayList<>();

        userIdList.add(issueSearchRequest.getUserId());

        userIdList = userQryRepo.getSubUsers(issueSearchRequest.getUserId());

        if (userInfoById.getRoleId() > 4) {
            queryString += " AND issue.created_by IN (:userIdList)";
            sqlParam.addValue("userIdList", userIdList);
        }


//        if(userInfoById.getRoleId()<=4){
//
////                 queryString += " AND issue.created_by IN (:userIdList)";
////                 sqlParam.addValue("userIdList", userIdList);
//        }
//
//        else if (userInfoById.getDeptId()==4)
//        {
//            queryString +=" AND issue.resolution_level IN (:levelIds)";
//            sqlParam.addValue("levelIds", levelIds);
//        }
//        else
//        {
//            queryString += " AND issue.dept_id IN (:deptIds)";
//            sqlParam.addValue("deptIds", deptIds);
//        }

        queryString += " ORDER BY issue." + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<IssueInfoListing> getIssueList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(IssueInfoListing.class));
        return new PageImpl<>(getIssueList, pageable, resultCount);

    }

    public Page<IssueInfoListing> getIssueList(IssueSearchRequest issueSearchRequest) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setId(issueSearchRequest.getUserId());
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("issue_date");
        userListRequest.setSize(100);

        PageRequest pageable = PageRequest.of(issueSearchRequest.getPage(), issueSearchRequest.getSize(), Sort.Direction.fromString(issueSearchRequest.getSortOrder()), issueSearchRequest.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "issue_date");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        Integer resultCount = 0;
        String queryString = " ";

        List<Integer> deptIds;
        deptIds = new ArrayList<>();
        deptIds.add(issueSearchRequest.getDeptId());


        List<Integer> levelIds;
        levelIds = new ArrayList<>();
        levelIds.add(issueSearchRequest.getDeptId());

     /*   List<Integer> userIdList = new ArrayList<>();
        userIdList.add(issueSearchRequest.getUserId());
        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
       for (int i = 0; i < userList.getTotalElements(); i++) {
           userIdList.add(userList.getContent().get(i).getId());
        }
*/
        UserInfoDto userInfoById = userQryRepo.getUserById(issueSearchRequest.getUserId());
        queryString += "select Distinct issue.id as issueId,type.name as issueType,issue.remarks as issueDescription,contract.contract_number as contractNumber,  " +
                "status.status as issueStatus,tank.name_of_the_m_i_p as tankName,issue.created_on as issueDate,issue.dept_id,dept.name as department,   " +
                "tm.bid_id as tenderId , issue.issue_date as issueDate, issue.tank_id as tankId, issue.contract_id as contractId,issue.work_id as workId, md.id as activityId from oiipcra_oltp.issue_tracker as issue  " +
                "left join oiipcra_oltp.issue_status as status on status.id=issue.status   " +
                "left join oiipcra_oltp.issue_type_m as type on type.id=issue.issue_type_id  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id= issue.tank_id  " +
                "left join oiipcra_oltp.dept_m as dept on dept.id = issue.dept_id    " +
                "left join oiipcra_oltp.master_head_details as md on md.id = issue.activity_id   " +
                "left join oiipcra_oltp.tender_m as tm on tm.id = issue.tender_id  " +
                "left join oiipcra_oltp.contract_mapping as contractMapping on issue.contract_id=contractMapping.contract_id  " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=contractMapping.contract_id " +
                "left join oiipcra_oltp.invoice_m as invoice on invoice.contract_id =contract.id  " +
                "left join oiipcra_oltp.expenditure_mapping as map on map.invoice_id =invoice.id  " +
                "left join oiipcra_oltp.expenditure_data as ed on ed.id = map.expenditure_id  " +
                "where issue.is_active=true";

        if (issueSearchRequest.getDistId() > 0) {
            queryString += " AND contractMapping.dist_id=:distId ";
            sqlParam.addValue("distId", issueSearchRequest.getDistId());
        }
        if (issueSearchRequest.getBlockId() > 0) {
            queryString += " AND contractMapping.block_id=:blockId ";
            sqlParam.addValue("blockId", issueSearchRequest.getBlockId());
        }
        if (issueSearchRequest.getIssueStatus() > 0) {
            queryString += " AND status.id=:statusId";
            sqlParam.addValue("statusId", issueSearchRequest.getIssueStatus());
        }
        if (issueSearchRequest.getIssueType() > 0) {
            queryString += " AND type.id=:typeId";
            sqlParam.addValue("typeId", issueSearchRequest.getIssueType());
        }

        if (issueSearchRequest.getDeptId() > 0) {
            queryString += " AND issue.dept_id=:deptId";
            sqlParam.addValue("deptId", issueSearchRequest.getDeptId());
        }
        if (issueSearchRequest.getContractNumber() != null) {
            queryString += " AND contract.contract_number LIKE (:number)";
            sqlParam.addValue("number", issueSearchRequest.getContractNumber());
        }
        if (issueSearchRequest.getTankId() > 0) {
            queryString += " AND tank.id=:tankId";
            sqlParam.addValue("tankId", issueSearchRequest.getTankId());
        }

        if (issueSearchRequest.getInvoiceId() > 0) {
            queryString += " AND invoice.id=:invoiceId";
            sqlParam.addValue("invoiceId", issueSearchRequest.getInvoiceId());
        }

        if (issueSearchRequest.getExpenditureId() > 0) {
            queryString += " AND ed.id=:expenditureId";
            sqlParam.addValue("expenditureId", issueSearchRequest.getExpenditureId());
        }

        if (issueSearchRequest.getContractId() > 0) {
            queryString += " AND issue.contract_id=:contractId";
            sqlParam.addValue("contractId", issueSearchRequest.getContractId());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (issueSearchRequest.getUploadFromDate() != null && !issueSearchRequest.getUploadFromDate().isEmpty()) {
            queryString += " AND date(issue.issue_date) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(issueSearchRequest.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (issueSearchRequest.getUploadToDate() != null && !issueSearchRequest.getUploadToDate().isEmpty()) {
            queryString += " AND date(issue.issue_date) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(issueSearchRequest.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }

        if (userInfoById.getRoleId() <= 4) {

//                 queryString += " AND issue.created_by IN (:userIdList)";
//                 sqlParam.addValue("userIdList", userIdList);
        } else if (userInfoById.getDeptId() == 4) {
            queryString += " AND issue.resolution_level IN (:levelIds)";
            sqlParam.addValue("levelIds", levelIds);
        } else {
            queryString += " AND issue.dept_id IN (:deptIds)";
            sqlParam.addValue("deptIds", deptIds);
        }

        queryString += " ORDER BY issue." + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<IssueInfoListing> getIssueList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(IssueInfoListing.class));
        return new PageImpl<>(getIssueList, pageable, resultCount);
    }

    public ContractInfo getIssueById(Integer issueId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cm.id, cm.contract_code as contractCode,cm.contract_name as contractName, cm.contract_date as contractDate,   " +
                "cm.contract_status_id as contractStatusId, cs.name as contractStatus, cm.tender_id, cm.contract_type_id as contractTypeId,  " +
                "type.name as contractType, cm.contract_level_id as contractLevelId, level.level_name as contractLevel,cm.procurement_made_for  " +
                "as procurementMadeFor, cm.zone, cm.approval_order_date as approvalOrderDate, cm.work_description as workDescription,  " +
                "cm.eoi_id as eoiId, cm.rfp_issued_on as rfpIssuedOn, cm.rfp_received_on as rfpReceivedOn, cm.area_id as areaId,  " +
                "cm.contract_number as contractNumber,cm.correspondence_file_no as correspondenceFileNo, cm.agency_id as agencyId,  " +
                "agency.name as agency, cm.contract_amount as contractAmount, cm.gst , cm.stipulated_date_of_comencement as stipulatedDateOfComencement  " +
                "cm.stipulated_date_of_completion as stipulatedDateOfCompletion, cm.approval_order as approvalOrder,  " +
                "cm.tachnical_sanction_no as tachnicalSanctionNo, cm.work_id as workId, cm.estimated_cost as estimatedCost,  " +
                "cm.awarded_as as awardedAs, cm.agreement_number as agreementNumber, cm.loa_issued_no as loaIssuedNo, cm.loa_issued_date as loaIssuedDate,  " +
                "cm.rate_of_premium as rateOfPremium, cm.actual_date_of_commencement as actualDateOfCommencement, cm.actual_date_of_completion asactualDateOfCompletion,  " +
                "cm.is_active as isActive, cm.created_by , cm.created_on , cm.updated_by, cm.updated_on,cm.finyr_id as finyrId,  " +
                "fm.name as financialYear, cm.date_eoi, cm.activity_id, cm.estimate_id FROM oiipcra_oltp.contract_m as cm  " +
                "left join oiipcra_oltp.contract_type as type on type.id = cm.contract_type_id  " +
                "left join oiipcra_oltp.contract_status as cs on cs.id = cm.contract_status_id  " +
                "left join oiipcra_oltp.agency_m as agency on agency.id = cm.agency_id  " +
                "left join oiipcra_oltp.contract_level_master as level on level.id = cm.contract_level_id  " +
                "left join oiipcra_oltp.fin_year_m as fm on fm.id = cm.finyr_id  " +
                "where cm.is_active=true ";
        if (issueId > 0) {
            qry += " AND issue.id=:issueId ";
            sqlParam.addValue("issueId", issueId);
        }

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ContractInfo.class));
    }

    public List<IssueDocumentDto> getIssueDocument(Integer issueId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,issue_id , doc_name, doc_path, is_active, created_by, created_on, updated_by  " +
                "FROM oiipcra_oltp.issue_document where is_active=true ";
        if (issueId > 0) {
            qry += " AND issue_id=:issueId ";
            sqlParam.addValue("issueId", issueId);
        }

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(IssueDocumentDto.class));
    }


    public IssuePermissionDto getIssueByIssueId(Integer issueId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT issue.id, issue.issue_date, issue.description, issue.permission_required, issue.dept_id, dept.name as deptName,   " +
                "issue.work_id,issue.created_by,issue.estimate_id,estimate.name_of_work as workName,issue.resolution_level,rs.level_name as levelName, issue.status as statusId, issueStatus.status as status,issue.tank_id,   " +
                "tank.name_of_the_m_i_p as tankName,tank.project_id as projectId,issue.contract_id,contract.contract_number,tm.id as tenderId, tm.bid_id,md.code as code,issue.issue_date as issueDate,   " +
                "tank.dist_id as distId,tank.dept_dist_name as distName,tank.block_id as blockId,tank.dept_block_name as blockName,tn.work_identification_code as identificationCode,tn.name_of_work as workName,   " +
                "issue.activity_id as subActivityId, issue.remarks,issue.resolution_remarks,issue.description, " +
                "issue.designation_id as designationId,issue.resolved_user_id as resolvedUserId,designation.name as designationName,userM.name as userName,type.name as issueType "+
                "from oiipcra_oltp.issue_tracker as issue    " +
                "left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate. id= issue.estimate_id    " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=issue.contract_id      " +
                "left join oiipcra_oltp.dept_m as dept on dept.id = issue.dept_id    " +
                "left join oiipcra_oltp.issue_resolution_level as rs on rs.id = issue.resolution_level    " +
                "left join oiipcra_oltp.issue_status as issueStatus on issueStatus.id=issue.status     " +
                "left join oiipcra_oltp.issue_type_m as type on type.id=issue.issue_type_id      " +
               // "left join oiipcra_oltp.contract_mapping as contractMapping on issue.contract_id=contractMapping.contract_id    " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=issue.tank_id      " +
                "left join oiipcra_oltp.master_head_details as md on md.id = issue.activity_id    " +
                "left join oiipcra_oltp.tender_m as tm on tm.id = issue.tender_id   " +
                "left join oiipcra_oltp.tender_notice as tn on tn.id= issue.work_id " +
                "left join oiipcra_oltp.designation_m as designation on designation.id=issue.designation_id " +
                "left join oiipcra_oltp.user_m as userM on userM.id=issue.resolved_user_id "+
                "WHERE issue.id = :issueId ";
        sqlParam.addValue("issueId", issueId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(IssuePermissionDto.class));
    }

    public List<IssueTypeDto> getIssueType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetIssueType = "SELECT issue.id, issue.name from oiipcra_oltp.issue_type_m as issue where issue.is_active=true ";
        return namedJdbc.query(GetIssueType, sqlParam, new BeanPropertyRowMapper<>(IssueTypeDto.class));
    }

    public List<ContractDto> getAllContract() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllContract = "SELECT id,contract_name,contract_number from oiipcra_oltp.contract_m";
        return namedJdbc.query(GetAllContract, sqlParam, new BeanPropertyRowMapper<>(ContractDto.class));
    }

    public List<IssueStatusDto> getIssueStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetIssueStatus = "SELECT status.id, status.status as statusName from oiipcra_oltp.issue_status as status ";
        return namedJdbc.query(GetIssueStatus, sqlParam, new BeanPropertyRowMapper<>(IssueStatusDto.class));
    }

    public List<IssueResolutionLevelDto> getResolutionLevel() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetResolutionLevel = "Select issue.id , issue.level_name from oiipcra_oltp.issue_resolution_level as issue";
        return namedJdbc.query(GetResolutionLevel, sqlParam, new BeanPropertyRowMapper<>(IssueResolutionLevelDto.class));
    }

    public List<IssueTrackImagesDto> getIssueImage(Integer issueId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetIssueImage = "SELECT id ,issue_id,image_name,image_location,longitude,latitude,accuracy,savetime from oiipcra_oltp.issue_track_images   " +
                "where is_active=true    ";
        if (issueId > 0) {
            GetIssueImage += " AND issue_id=:issueId ";
            sqlParam.addValue("issueId", issueId);
        }
        return namedJdbc.query(GetIssueImage, sqlParam, new BeanPropertyRowMapper<>(IssueTrackImagesDto.class));
    }

    public List<Integer> getDeptId(Integer deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetDeptId = "SELECT issue.dept_id from oiipcra_oltp.issue_tracker as issue     " +
                "WHERE issue.dept_id =:deptId";
        sqlParam.addValue("deptId", deptId);
        return namedJdbc.queryForList(GetDeptId, sqlParam, Integer.class);
    }

    public List<Integer> getLevelIdByDeptId(Integer deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetLevelId = "SELECT resolution_level from oiipcra_oltp.issue_tracker where dept_id = 4";
        sqlParam.addValue("dept_id", deptId);
        return namedJdbc.queryForList(GetLevelId, sqlParam, Integer.class);
    }
    public List<UserDto> getUserByDesignation(Integer designationId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String userList = "Select * from oiipcra_oltp.user_m where is_active=true ";
        if(designationId!=null && designationId>0){
            userList+=" and designation_id=:designationId ";
            sqlParam.addValue("designationId", designationId);
        }
        userList+=" order by id asc ";
        return namedJdbc.query(userList, sqlParam, new BeanPropertyRowMapper<>(UserDto.class));
    }

    public List<DesignationDto> getDesignationByUserLevelId(Integer userLevelId,Integer deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String userList = "Select * from oiipcra_oltp.designation_m where is_active=true ";
        if(userLevelId!=null && userLevelId>0){
            userList+=" and user_level_id=:userLevelId ";
            sqlParam.addValue("userLevelId", userLevelId);
        }
        if(deptId!=null && deptId>0){
            userList+=" and dept_id=:deptId ";
            sqlParam.addValue("deptId", deptId);
        }
        userList+=" order by id asc ";
        return namedJdbc.query(userList, sqlParam, new BeanPropertyRowMapper<>(DesignationDto.class));
    }
}
