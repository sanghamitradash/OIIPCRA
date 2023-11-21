package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.UserListRequest;
import com.orsac.oiipcra.dto.GrievanceListingDto;
import com.orsac.oiipcra.dto.*;
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
public class GrievanceRepositoryImpl {

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

    public GrievanceDto getGrievanceId(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
      /*  String qry ="SELECT gri.id AS griId, gri.name, gri.email, gri.mobile, gri.address, gri.remarks, gri.project_id, gri.tank_id, tank.name_of_the_m_i_p AS nameOfTheMip ,tank.dept_dist_name, tank.dept_block_name, tank.dept_gp_name, gen.name AS genderName, " +
                "gri.status,gs.name as statusName FROM oiipcra_oltp.grievance AS gri  " +*/
        String qry = "SELECT gri.id AS griId, gri.name, gri.email, gri.mobile, gri.address, gri.remarks, gri.project_id, gri.tank_id, gri.document, gri.image, tank.project_id AS projectCode, tank.name_of_the_m_i_p AS nameOfTheMip ,tank.dept_dist_name, tank.dept_block_name, tank.dept_gp_name, gen.name AS genderName,  " +
                "gri.status,gs.name as statusName, gri.resolution_level as resolutionLevel,rs.level_name as resolutionLevelName, "+
                "gri.designation_id as designationId,gri.resolved_user_id as resolvedUserId,designation.name as designationName,userM.name as userName,gri.dept_id as deptId, dept.name as deptName "+
                "FROM oiipcra_oltp.grievance AS gri  " +
                "LEFT JOIN oiipcra_oltp.tank_m_id AS tank ON  gri.tank_id = tank.id  " +
                "LEFT JOIN oiipcra_oltp.block_boundary AS block ON block.block_id = tank.block_id " +
                "LEFT JOIN oiipcra_oltp.district_boundary AS dist ON dist.dist_id = tank.dist_id " +
                "LEFT JOIN oiipcra_oltp.gender_m AS gen ON gen.id = gri.gender " +
                "left join oiipcra_oltp.issue_resolution_level as rs on rs.id = gri.resolution_level    " +
                "LEFT JOIN oiipcra_oltp.grievance_status AS gs on gs.id = gri.status " +
                "left join oiipcra_oltp.designation_m as designation on designation.id=gri.designation_id " +
                "left join oiipcra_oltp.user_m as userM on userM.id=gri.resolved_user_id "+
                "left join oiipcra_oltp.dept_m as dept on dept.id = gri.dept_id    " +
                "WHERE gri.id=:id  ";
        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(GrievanceDto.class));
    }

    public Page<GrievanceDto> getAllGrievance2(GrievanceListingDto grievanceListingDto, List<Integer> tankIds){

        PageRequest pageable = PageRequest.of(grievanceListingDto.getPage(), grievanceListingDto.getSize(), Sort.Direction.fromString(grievanceListingDto.getSortOrder()), grievanceListingDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        UserInfoDto userInfoById = userQryRepo.getUserById(grievanceListingDto.getUserId());
        if (userInfoById != null) {
            userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
        }

        Integer resultCount = 0;
        String queryString = " ";

        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(grievanceListingDto.getUserId());

        queryString = "SELECT distinct gri.id AS griId, gri.name, gri.email, " +
                "gri.mobile, gri.address, gri.remarks, gri.project_id, gri.tank_id, " +
                "tank.dept_dist_name, tank.project_id AS projectCode, tank.dept_block_name,  " +
                "tank.dept_gp_name, tank.name_of_the_m_i_p AS nameOfTheMip, " +
                "gen.name AS genderName, gri.status, grists.name AS statusName   " +
                "FROM oiipcra_oltp.grievance AS gri    " +
                "LEFT JOIN oiipcra_oltp.tank_m_id AS tank ON  gri.tank_id = tank.id   " +
                "LEFT JOIN oiipcra_oltp.block_boundary AS block ON block.block_id = tank.block_id   " +
                "LEFT JOIN oiipcra_oltp.district_boundary AS dist ON dist.dist_id = tank.dist_id   " +
                "LEFT JOIN oiipcra_oltp.gender_m AS gen ON gen.id = gri.gender   " +
                "LEFT JOIN oiipcra_oltp.grievance_status AS grists ON grists.id = gri.status   " +
                "WHERE gri.is_active=true ";

        if (tankIds != null && tankIds.size() > 0) {
            queryString += " AND gri.tank_id IN (:tankIds)";
            sqlParam.addValue("tankIds", tankIds);
        }else if(grievanceListingDto.getGpId()!=null && grievanceListingDto.getGpId()>0){
            queryString += " AND tank.gp_id = :gpId";
            sqlParam.addValue("gpId", grievanceListingDto.getGpId());
        }else if(grievanceListingDto.getBlockId()!=null && grievanceListingDto.getBlockId()>0){
            queryString += " AND tank.block_id IN (:blockId)";
            sqlParam.addValue("blockId", grievanceListingDto.getBlockId());
        }else if(grievanceListingDto.getDistId()!=null && grievanceListingDto.getDistId()>0){
            queryString += " AND tank.dist_id IN (:distId)";
            sqlParam.addValue("distId", grievanceListingDto.getDistId());
        }

        if(grievanceListingDto.getProgressStatus() != null && grievanceListingDto.getProgressStatus()>0){
            queryString += " AND gri.status IN (:progressStatus)";
            sqlParam.addValue("progressStatus", grievanceListingDto.getProgressStatus());
        }


        if(grievanceListingDto.getStatusId() != null && grievanceListingDto.getStatusId()>0){
            queryString += " AND gri.status IN (:StatusId)";
            sqlParam.addValue("StatusId", grievanceListingDto.getStatusId());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (grievanceListingDto.getUploadFromDate() != null && !grievanceListingDto.getUploadFromDate().isEmpty()) {
            queryString += " AND date(gri.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(grievanceListingDto.getUploadFromDate());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (grievanceListingDto.getUploadToDate() != null && !grievanceListingDto.getUploadToDate().isEmpty()) {
            queryString += " AND date(gri.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(grievanceListingDto.getUploadToDate());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }

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

        queryString += " ORDER BY gri." + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<GrievanceDto> grievanceDtoList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(GrievanceDto.class));
        return new PageImpl<>(grievanceDtoList, pageable, resultCount);
    }



    public Page<GrievanceDto> getAllGrievance(GrievanceListingDto grievanceListingDto, List<Integer> tankIdsByExpenditureId, List<Integer> tankIdsByInvoiceId, List<Integer> tankIdsByEstimateId, List<Integer> tankIdsByContractId) {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setSortOrder("DESC");
        userListRequest.setSortBy("id");
        userListRequest.setSize(14);

        List<Integer> authorityIdList = new ArrayList<>();
        List<UserLevelDto> userLevel = null;
        List<UserAreaMappingDto> userAreaMappingList = null;
        UserInfoDto userInfoById = null;

        if (grievanceListingDto.getUserId() != null) {
            userInfoById = userQryRepo.getUserById(grievanceListingDto.getUserId());
            if (userInfoById != null) {
                userLevel = userQryRepo.getUserLevelByUserId(userInfoById.getUserLevelId());
            }
            userQryRepo.getUserAuthority(grievanceListingDto.getUserId());
        }


        PageRequest pageable = PageRequest.of(grievanceListingDto.getPage(), grievanceListingDto.getSize(), Sort.Direction.fromString(grievanceListingDto.getSortOrder()), grievanceListingDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        Integer resultCount = 0;
        String queryString = " ";

        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(grievanceListingDto.getUserId());
//        Page<UserInfo> userList = userQryRepo.getUserList(userListRequest);
//        for (int i = 0; i < userList.getSize(); i++) {
//            userIdList.add(userList.getContent().get(i).getId());
//        }
//        UserInfoDto userInfoById = userQryRepo.getUserById(grievanceListingDto.getUserId());

        queryString = "SELECT distinct gri.id AS griId, gri.name, gri.email, " +
                "gri.mobile, gri.address, gri.remarks, gri.project_id, gri.tank_id, " +
                "tank.dept_dist_name, tank.project_id AS projectCode, tank.dept_block_name,  " +
                "tank.dept_gp_name, tank.name_of_the_m_i_p AS nameOfTheMip, " +
                "gen.name AS genderName, gri.status, grists.name AS statusName   " +
                "FROM oiipcra_oltp.grievance AS gri    " +
                "LEFT JOIN oiipcra_oltp.tank_m_id AS tank ON  gri.tank_id = tank.id   " +
                "LEFT JOIN oiipcra_oltp.block_boundary AS block ON block.block_id = tank.block_id   " +
                "LEFT JOIN oiipcra_oltp.district_boundary AS dist ON dist.dist_id = tank.dist_id   " +
                "LEFT JOIN oiipcra_oltp.gender_m AS gen ON gen.id = gri.gender   " +
                "LEFT JOIN oiipcra_oltp.expenditure_mapping as em on em.tank_id = gri.tank_id  " +
                "LEFT JOIN oiipcra_oltp.invoice_m as im on im.id = em.invoice_id  " +
                "LEFT JOIN oiipcra_oltp.grievance_status AS grists ON grists.id = gri.status   " +
                "LEFT JOIN oiipcra_oltp.issue_tracker as issue on issue.tank_id = gri.tank_id  " +
                "WHERE gri.is_active=true  ";


        if (userAreaMappingList != null) {
            if (userInfoById.getRoleId() <= 4) {
                queryString += " ";
            } else {
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

            if (grievanceListingDto.getDistId() > 0) {
                queryString += " AND tank.dist_id=:distId";
                sqlParam.addValue("distId", grievanceListingDto.getDistId());
            }
            if (grievanceListingDto.getBlockId() > 0) {
                queryString += " AND tank.block_id=:blockId";
                sqlParam.addValue("blockId", grievanceListingDto.getBlockId());
            }
            if (grievanceListingDto.getTankId() != null && grievanceListingDto.getTankId() > 0) {
                queryString += " AND gri.tank_id=:tankId";
                sqlParam.addValue("tankId", grievanceListingDto.getTankId());
            }
            if (grievanceListingDto.getStatusId() != null && grievanceListingDto.getStatusId() > 0) {
                queryString += " AND gri.status=:statusId";
                sqlParam.addValue("statusId", grievanceListingDto.getStatusId());
            }

            if (tankIdsByEstimateId != null && tankIdsByEstimateId.size() > 0) {
                queryString += " AND tank.tank_id IN (:tankIdsByEstimateId)";
                sqlParam.addValue("tankIdsByEstimateId", tankIdsByEstimateId);
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (grievanceListingDto.getUploadFromDate() != null && !grievanceListingDto.getUploadFromDate().isEmpty()) {
                queryString += " AND date(gri.created_on) >= :uploadFromDate";
                Date uploadFromDate = null;
                try {
                    uploadFromDate = format.parse(grievanceListingDto.getUploadFromDate());
                } catch (Exception exception) {
                    log.info("From Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
            }
            if (grievanceListingDto.getUploadToDate() != null && !grievanceListingDto.getUploadToDate().isEmpty()) {
                queryString += " AND date(gri.created_on) <= :uploadToDate";
                Date uploadToDate = null;
                try {
                    uploadToDate = format.parse(grievanceListingDto.getUploadToDate());
                } catch (Exception exception) {
                    log.info("To Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
            }

        }
        if (grievanceListingDto.getTankId() != null && grievanceListingDto.getTankId() > 0) {
            queryString += " AND gri.tank_id=:tankId";
            sqlParam.addValue("tankId", grievanceListingDto.getTankId());
        }

        if (tankIdsByContractId != null && tankIdsByContractId.size() > 0) {
            queryString += " AND gri.tank_id IN (:tankIdsByContractId)";
            sqlParam.addValue("tankIdsByContractId", tankIdsByContractId);
        }

        if (grievanceListingDto.getExpenditureId() != null && grievanceListingDto.getExpenditureId() > 0) {
            queryString += " AND em.expenditure_id =:expenditureId";
            sqlParam.addValue("expenditureId", grievanceListingDto.getExpenditureId());
        }

        if (grievanceListingDto.getInvoiceId() != null && grievanceListingDto.getInvoiceId() > 0) {
            queryString += " AND em.invoice_id =:invoiceId ";
            sqlParam.addValue("invoiceId", grievanceListingDto.getInvoiceId());
        }

        if (grievanceListingDto.getIssueId() != null && grievanceListingDto.getIssueId() > 0) {
            queryString += " AND issue.id=:issueId";
            sqlParam.addValue("issueId", grievanceListingDto.getIssueId());
        }

        if (grievanceListingDto.getDistId() != null && grievanceListingDto.getDistId() > 0) {
            queryString += " AND tank.dist_id=:distId";
            sqlParam.addValue("distId", grievanceListingDto.getDistId());
        }

        if (grievanceListingDto.getBlockId() != null && grievanceListingDto.getBlockId() > 0) {
            queryString += " AND tank.block_id=:blockId";
            sqlParam.addValue("blockId", grievanceListingDto.getBlockId());
        }

        if (grievanceListingDto.getStatusId() != null && grievanceListingDto.getStatusId() > 0) {
            queryString += " AND gri.status=:statusId";
            sqlParam.addValue("statusId", grievanceListingDto.getStatusId());
        }

//        if (id <= 0) {
//            qry += " ORDER BY id";
//            return namedJdbc.query(qry, mapSqlParameterSource, new BeanPropertyRowMapper<>(GrievanceDto.class));
//        } else {
//            qry += " WHERE gri.id=:id";
//            mapSqlParameterSource.addValue("id", id);
//       }
//        if(userInfoById.getRoleId()>4) {
//            if (userInfoById.getSurveyor()) {
//                queryString += " AND ed.created_by IN (:userId)";
//                sqlParam.addValue("userId", grievanceListingDto.getUserId());
//            } else {
//                queryString += " AND ed.created_by IN (:userIdList)";
//                sqlParam.addValue("userIdList", userIdList);
//            }
//        }


        queryString += " ORDER BY gri." + order.getProperty() + " " + order.getDirection().name();
        resultCount = count(queryString, sqlParam);
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<GrievanceDto> grievanceDtoList = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(GrievanceDto.class));
        return new PageImpl<>(grievanceDtoList, pageable, resultCount);
    }

    public List<GrievanceStatusDto> getGrievanceStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT grists.id, grists.name, grists.is_active FROM oiipcra_oltp.grievance_status AS grists ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(GrievanceStatusDto.class));
    }
}
