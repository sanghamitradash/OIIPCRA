package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.dto.OfficeDataDto;
import com.orsac.oiipcra.dto.TenderDto;
import com.orsac.oiipcra.dto.UserInfoDto;
import com.orsac.oiipcra.repository.OfficeDataRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OfficeDataRepositoryImpl implements OfficeDataRepository {
    @Autowired
    UserQueryRepository userQueryRepository;
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    @Autowired
    private UserService userService;
    @Override
    public List<OfficeDataDto> getOfficeData(Integer distId, Integer divisionId,Integer userId) {
        UserInfoDto userInfoById = userService.getUserById(userId);
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
            String qry = " SELECT data.id, office_name, head_of_dept, head_of_office, data.designation,designation.name as designationName, designation.description,spu_address," +
                    "spu_post, spu_email, land_line_no, spu_pin_no, data.dist_id, data.is_active,data.created_by,cir.circle_name as circleName,  " +
                    "data.division_id, copy_to_1 as copyTo1, copy_to_2 as copyTo2, copy_to_3 as copyTo3, copy_to_4  as copyTo4," +
                    "copy_to_5 as copyTo5, copy_to_6  as copyTo6, copy_to_7  as copyTo7, copy_to_8 as copyTo8, copy_to_9 as copyTo9, copy_to_10 as copyTo10," +
                    "copy_to_11 as copyTo11,district.district_name as districtName,division.mi_division_name as divisionName,userM.name as createdByUser " +
                    "FROM oiipcra_oltp.office_data as data " +
                    "left join oiipcra_oltp.designation_m as designation on designation.id=data.designation " +
                    "left join oiipcra_oltp.district_boundary as district on district .dist_id=data.dist_id " +
                    "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=data.division_id " +
                    "left join oiipcra_oltp.oiipcra_circle_m as cir on cir.circle_id=division.circle_id " +
                     "left join oiipcra_oltp.user_m as userM on userM.id=data.created_by where data.is_active=true ";
            if(userId != null && userId>=0 ) {
                if (distId!=null && distId >= 0) {
                    qry += " and data.dist_id=:distId ";
                    sqlParam.addValue("distId", distId);
                }
                if (divisionId!=null && divisionId >=0) {
                    qry += " and data.division_id=:divisionId ";
                    sqlParam.addValue("divisionId", divisionId);
                }
                if (distId==-1 && divisionId ==-1) {
                    qry += " and data.dist_id=-1 and data.division_id=-1  ";
                    sqlParam.addValue("divisionId", divisionId);
                }
                if(distId==0){
                    qry += " and data.dist_id=0";

                }
//                if (userInfoById.getRoleId() < 4) {
//                    qry += " and data.created_by =:userId ";
//                    sqlParam.addValue("userId", userId);
//                }

                qry += " order by id asc ";
            }
    /*        else{
               Integer userLevelId= userQueryRepository.getUserLevelById(userId);
               if(userLevelId==2){
                 //  List<Integer> districtId=userQueryRepository.getDistrictIdByUserId(userId);
                   qry += " and data.dist_id in(:districtId) ";
                 //  sqlParam.addValue("distId", districtId);
               }
//                if(userLevelId==2){
//                  //  List<Integer> districtId=userQueryRepository.getDivisionIdByUserId(userId);
//                    qry += " and data.division_id in(:divisionId) ";
//                  //  sqlParam.addValue("divisionId", divisionId);
//                }
//            }*/
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(OfficeDataDto.class));
    }

    @Override
    public Integer getDivisionId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select division_id from oiipcra_oltp.user_area_mapping where user_id=:userId ";
        sqlParam.addValue("userId",userId);
        return namedJdbc.queryForObject(qry,sqlParam,Integer.class);
    }

    @Override
    public List<Integer> getDistId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="select dist_id from oiipcra_oltp.user_area_mapping where user_id=:userId ";
        sqlParam.addValue("userId",userId);
        return namedJdbc.queryForList(qry,sqlParam,Integer.class);
    }
    public Integer getDivisionIdByUserId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="select division_id from oiipcra_oltp.user_area_mapping where is_active=true and user_id=:userId ";
        sqlParam.addValue("userId",userId);
        return namedJdbc.queryForObject(qry,sqlParam,Integer.class);
    }

    public List<Integer> getDivisionIdsByUserId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="select division_id from oiipcra_oltp.user_area_mapping where is_active=true and user_id in (:userId) ";
        sqlParam.addValue("userId",userId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(Integer.class));
    }

    @Override
    public Integer getDivisionIdByDistId(Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct division_id from oiipcra_oltp.block_mapping where dist_id =:distId ";
        sqlParam.addValue("distId",distId);
        return namedJdbc.queryForObject(qry,sqlParam,Integer.class);
    }

    @Override
    public List<OfficeDataDto> getOfficeDataDetails(Integer distId, Integer divisionId, Integer userId, List<Integer> disIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT data.id, office_name, head_of_dept, head_of_office, data.designation,designation.name as designationName, designation.description,spu_address," +
                "spu_post, spu_email, land_line_no, spu_pin_no, data.dist_id, data.is_active,data.created_by," +
                "data.division_id, copy_to_1 as copyTo1, copy_to_2 as copyTo2, copy_to_3 as copyTo3, copy_to_4  as copyTo4," +
                "copy_to_5 as copyTo5, copy_to_6  as copyTo6, copy_to_7  as copyTo7, copy_to_8 as copyTo8, copy_to_9 as copyTo9, copy_to_10 as copyTo10," +
                "copy_to_11 as copyTo11,district.district_name as districtName,division.mi_division_name as divisionName,userM.name as createdByUser " +
                "FROM oiipcra_oltp.office_data as data " +
                "left join oiipcra_oltp.designation_m as designation on designation.id=data.designation " +
                "left join oiipcra_oltp.district_boundary as district on district .dist_id=data.dist_id " +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=data.division_id " +
                "left join oiipcra_oltp.user_m as userM on userM.id=data.created_by where data.is_active=true ";
        if(userId != null && userId>=0 ) {
            if (disIds!=null && disIds.size() >= 0) {
                qry += " and data.dist_id IN (:disIds)";
                sqlParam.addValue("disIds", disIds);
            }
            if (divisionId!=null && divisionId >=0) {
                qry += " and data.division_id=:divisionId ";
                sqlParam.addValue("divisionId", divisionId);
            }
            if (distId!=null && distId >=0) {
                qry += " and data.dist_id=:distId ";
                sqlParam.addValue("distId", distId);
            }
            qry += " order by id asc ";
        }
    /*        else{
               Integer userLevelId= userQueryRepository.getUserLevelById(userId);
               if(userLevelId==2){
                 //  List<Integer> districtId=userQueryRepository.getDistrictIdByUserId(userId);
                   qry += " and data.dist_id in(:districtId) ";
                 //  sqlParam.addValue("distId", districtId);
               }
//                if(userLevelId==2){
//                  //  List<Integer> districtId=userQueryRepository.getDivisionIdByUserId(userId);
//                    qry += " and data.division_id in(:divisionId) ";
//                  //  sqlParam.addValue("divisionId", divisionId);
//                }
//            }*/
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(OfficeDataDto.class));
    }


    public List<Integer> getdivisionIdsByUserId(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  ";
        sqlParam.addValue("userId", userId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Integer.class));

    }
}
