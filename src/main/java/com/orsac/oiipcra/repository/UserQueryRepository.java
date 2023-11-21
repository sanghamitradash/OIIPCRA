package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.bindings.UserListRequest;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.DepartmentMaster;
import com.orsac.oiipcra.entities.UserAreaMapping;
import com.orsac.oiipcra.entities.UserLevel;
import com.orsac.oiipcra.entities.Role;
import com.orsac.oiipcra.dto.HierarchyMenuDto;
import com.orsac.oiipcra.dto.MenuDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserQueryRepository {

    @Value("${dbschema}")
    private String DBSCHEMA;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;
    public int count(String qryStr, MapSqlParameterSource sqlParam) {
        String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
        Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        if (null != intRes) {
            return intRes;
        }
        return 0;
    }


    public void updateUserPassword(String password, Long mobileNo) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String updateQry = "UPDATE user_m set password=:password WHERE mobile_number=:mobileNo";
        sqlParam.addValue("password", password);
        sqlParam.addValue("mobileNo", mobileNo);
        namedJdbc.update(updateQry, sqlParam);
    }

    public void updateForgotPwdOtp(Integer otp,Long mobile) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String updateQry = "UPDATE user_m set forgot_pwd_otp=:pwdOtp WHERE mobile_number=:mobile";
        sqlParam.addValue("pwdOtp",otp);

        sqlParam.addValue("mobile",mobile);
        namedJdbc.update(updateQry,sqlParam);
    }

    public List<Integer> getUserRoleIdList(int uid){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String role_List = "SELECT DISTINCT(role_id) FROM oiipcra_oltp.user_role where user_id=:uid";
        sqlParam.addValue("uid",uid);
       return namedJdbc.queryForList(role_List,sqlParam,Integer.class);
    }

    public List<Integer> getUserMenuIdList(List<Integer> roleId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String MENU_ID_LIST = "select distinct(menu_id)from oiipcra_oltp.role_menu where role_id in (:roleId)";
        sqlParam.addValue("roleId",roleId);
        return namedJdbc.queryForList(MENU_ID_LIST,sqlParam,Integer.class);
    }

    public List<MenuDto> getParentMenuListByMenuId(List<Integer> menuId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String PARENT_MENU_LIST = "SELECT id as menuId, name as menuName, parent_id as parentId,module as target_url " +
                " FROM oiipcra_oltp.menu_m where id in(:menuId) and parent_id=0";
        sqlParam.addValue("menuId",menuId);

        return namedJdbc.query(PARENT_MENU_LIST,sqlParam, new BeanPropertyRowMapper<>(MenuDto.class));
    }

    public List<HierarchyMenuDto> getHierarchyMenuListById(int id,List<Integer> menuId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String CHILD_MENU_LIST = "select id ,name as menuName,parent_id as parentId,module as targetUrl "+
                "from oiipcra_oltp.menu_m where parent_id=:id and id in(:menuId)";
        sqlParam.addValue("id",id);
        sqlParam.addValue("menuId",menuId);

        return namedJdbc.query(CHILD_MENU_LIST,sqlParam, new BeanPropertyRowMapper<>(HierarchyMenuDto.class));
    }

    public Page<UserInfo> getUserList(UserListRequest userListRequest) {
        MapSqlParameterSource sqlParam=new MapSqlParameterSource();
        PageRequest pageable = null;
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"id");
        if (userListRequest!=null){
            pageable = PageRequest.of(userListRequest.getPage(), userListRequest.getSize(), Sort.Direction.fromString(userListRequest.getSortOrder()), userListRequest.getSortBy());
            order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC,"id");
        }
        //For Default Page Load
        Integer userLevelId=getUserLevelIdByUserId(userListRequest.getId());
        int resultCount=0;
        String query_field=" ";
        String qry = " select um.id,um.name as userName,um.mobile_number,um.email,um.is_active " +
                "as active,um.is_surveyor as surveyor,ulm.name as userLevel,dm.name as department, " +
                "dem.name as designation,rm.name as role from oiipcra_oltp.user_m as um  " +
                "left join oiipcra_oltp.user_level_m as ulm on um.user_level_id=ulm.id  " +
                "left join oiipcra_oltp.dept_m as dm on um.dept_id=dm.id  " +
                "left join oiipcra_oltp.role_m as rm on um.role_id=rm.id  " +
//              "left join oiipcra_oltp.user_area_mapping as uam on um.id=uam.user_id " +
                "left join oiipcra_oltp.designation_m as dem on um.designation_id=dem.id WHERE TRUE";

       if(userLevelId==1) {
           if(userListRequest.getDeptId()>0) {
               qry+= " AND um.dept_id=:dept ";
               sqlParam.addValue("dept",userListRequest.getDeptId());
           }
           if(userListRequest.getDesignation()>0) {
               qry+= " AND um.designation_id=:designation ";
               sqlParam.addValue("designation",userListRequest.getDesignation());
           }
           if(userListRequest.getSearchId()>0) {
               qry+= " AND um.id=:id ";
               sqlParam.addValue("id",userListRequest.getSearchId());
           }
           if(userListRequest.getMobileNumber()!=null && userListRequest.getMobileNumber()>0) {
               qry+= " and  um.mobile_number=:mobileNumber ";
               sqlParam.addValue("mobileNumber",userListRequest.getMobileNumber());
           }

           if(userListRequest.getUserLevelId()>0) {
               qry+= " and um.user_level_id=:userLevelId ";
               sqlParam.addValue("userLevelId",userListRequest.getUserLevelId());
           }

           qry += " AND um.designation_id >1 and um.role_id >1 ORDER BY um.name asc";
       }
       else {
           String query_data = "Select distinct(user_id) from oiipcra_oltp.user_area_mapping where %s in " +
                   "(Select %s from oiipcra_oltp.user_area_mapping where user_id=" + userListRequest.getId() + ")";

           switch (userLevelId) {
               case 2:
                   query_field = "dist_id";
                   break;
               case 3:
                   query_field = "block_id";
                   break;
               case 4:
                   query_field = "gp_id";
                   break;
               case 5:
                   query_field = "village_id";
                   break;
               case 6:
                   query_field = "division_id";
                   break;
               case 7:
                   query_field = "subdivision_id";
                   break;
               case 8:
                   query_field = "section_id";
                   break;
               default:
                   break;

           }
           query_data = String.format(query_data,query_field,query_field);

           List<Integer> userId = getUserId(query_data);
           if(userListRequest.getDeptId()>0) {
               qry+= " AND um.dept_id=:dept ";
               sqlParam.addValue("dept",userListRequest.getDeptId());
           }
           if(userListRequest.getDesignation()>0) {
               qry+= " AND um.designation_id=:designation ";
               sqlParam.addValue("designation",userListRequest.getDesignation());
           }
           if(userListRequest.getSearchId()>0) {
               qry+= " AND um.id=:id ";
               sqlParam.addValue("id",userListRequest.getSearchId());
           }
           if(userListRequest.getMobileNumber()!=null && userListRequest.getMobileNumber()>0 ) {
               qry+= " AND um.mobile_number=:mobileNo ";
               sqlParam.addValue("mobileNo",userListRequest.getMobileNumber());
           }

           if(userListRequest.getUserLevelId()>0) {
               qry+= " and um.user_level_id=:userLevelId ";
               sqlParam.addValue("userLevelId",userListRequest.getUserLevelId());
           }

           qry += " AND um.id in(:userId) and  um.designation_id >1 and um.role_id >1 ORDER BY um.name asc ";
           sqlParam.addValue("userId", userId);


       }
        resultCount = count(qry, sqlParam);
        if (userListRequest.getSize() > 0){
            qry += " LIMIT "+pageable.getPageSize() + " OFFSET "+ pageable.getOffset();
        }
        // return new PageImpl<>(namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UserInfo.class)));
        List<UserInfo> userInfo=namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UserInfo.class));
        return new PageImpl<>(userInfo, pageable, resultCount);
    }
    public List<UserListDropDownDto> getUserListForDropDown(Integer userId) {
        MapSqlParameterSource sqlParam=new MapSqlParameterSource();

        //For Default Page Load
        Integer userLevelId=getUserLevelIdByUserId(userId);
        int resultCount=0;
        String query_field=" ";
        String qry = " select um.id as userId,um.name as userName,dm.name as department, " +
                "dem.name as designation,rm.name as role from oiipcra_oltp.user_m as um  " +
                "left join oiipcra_oltp.dept_m as dm on um.dept_id=dm.id  " +
                "left join oiipcra_oltp.role_m as rm on um.role_id=rm.id  " +
                "left join oiipcra_oltp.designation_m as dem on um.designation_id=dem.id WHERE TRUE";

        if(userLevelId==1) {

            qry += " AND um.designation_id >1 and um.role_id >1 ORDER BY um.name asc";
            resultCount = count(qry, sqlParam);
        } else {
            String query_data = "Select distinct(user_id) from user_area_mapping where %s in (Select %s from user_area_mapping where user_id=" +userId + ")";
            switch (userLevelId) {
                case 2:
                    query_field = "dist_id";
                    break;
                case 3:
                    query_field = "block_id";
                    break;
                case 4:
                    query_field = "gp_id";
                    break;
                case 5:
                    query_field = "village_id";
                    break;
                case 6:
                    query_field = "division_id";
                    break;
                case 7:
                    query_field = "subdivision_id";
                    break;
                case 8:
                    query_field = "section_id";
                    break;
                default:
                    break;

            }
            query_data = String.format(query_data,query_field,query_field);

            List<Integer> userIdList = getUserId(query_data);

            qry += " AND um.id in(:userId) and  um.designation_id >1 and um.role_id >1 ORDER BY um.name asc ";
            sqlParam.addValue("userId", userIdList);
            resultCount = count(qry, sqlParam);


        }
        List<UserListDropDownDto> userInfo=namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UserListDropDownDto.class));
        return  userInfo;
    }


    public UserInfoDto getUserById(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String DATA_BY_USERID = "select um.id as userId,um.name,um.mobile_number,um.email,ulm.id as userLevelId,ulm.name as userLevel, " +
                "dm.id as deptId,dm.name as department,desm.id as desgId,desm.name as designation,am.id as agencyId,am.name as agency, " +
                "sdm.id as subDepartmentId,sdm.name as subDepartmentName,rm.id as roleId,rm.name as roleName,um.is_surveyor as surveyor,um.is_active as active " +
                "from oiipcra_oltp.user_m as um " +
                "left join oiipcra_oltp.user_level_m as ulm on um.user_level_id=ulm.id " +
                "left join oiipcra_oltp.dept_m as dm on um.dept_id=dm.id " +
                "left join oiipcra_oltp.designation_m as desm on um.designation_id=desm.id " +
                "left join oiipcra_oltp.sub_dept_m as sdm on um.sub_dept_id=sdm.id " +
                "left join oiipcra_oltp.role_m as rm on um.role_id=rm.id " +
                "left join oiipcra_oltp.agency_m as am on um.agency_id=am.id where um.id=:id";
        sqlParam.addValue("id",id);

        return namedJdbc.queryForObject(DATA_BY_USERID,sqlParam, new BeanPropertyRowMapper<>(UserInfoDto.class));
    }

    public Integer getUserLevelIdByUserId(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String USER_LEVEL_ID = "select user_level_id from oiipcra_oltp.user_m where id=:id";
        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(USER_LEVEL_ID,sqlParam, Integer.class);

    }
    public List<Integer> getUserId(String query) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        return namedJdbc.queryForList(query, sqlParam, Integer.class);

    }

    public boolean deactivateAreaByUserId(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE user_area_mapping SET is_active = false WHERE user_id=:id";
        sqlParam.addValue("id",id);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update>0;
    }

    public List<UserAreraInfo> getAreaInfo(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String query="select  district.dist_id as distId,district.district_name as districtName,block.block_id as blockId,block.block_name as blockName," +
                "gp.gp_id as gpId,gp.grampanchayat_name as gpName,village.village_id as villageId,village.revenue_village_name as villageName," +
                "division.mi_division_id as divisionId,division.mi_division_name as miDivisionName," +
                "subdivision.mi_sub_division_id as subdivisionId,subdivision.mi_sub_division_name as miSubDivisionName," +
                "section.section_id as sectionId,section.mi_section_name as miSectionName " +
                "from  oiipcra_oltp.user_area_mapping as uam " +
                "left join oiipcra_oltp.district_boundary as district on uam.dist_id=district.dist_id " +
                "left join oiipcra_oltp.block_boundary as block on uam.block_id=block.block_id " +
                "left join oiipcra_oltp.gp_boundary as gp on uam.gp_id=gp.gp_id " +
                "left join oiipcra_oltp.village_boundary as village on uam.village_id=village.village_id " +
                "left join oiipcra_oltp.mi_division_m as division on uam.division_id=division.mi_division_id " +
                "left join oiipcra_oltp.mi_subdivision_m as subdivision on uam.subdivision_id=subdivision.mi_sub_division_id " +
                "left join oiipcra_oltp.mi_section_m as section on uam.section_id=section.section_id where uam.user_id=:uid and uam.is_active=true ";
               sqlParam.addValue("uid",id);
        return namedJdbc.query(query,sqlParam,new BeanPropertyRowMapper<>(UserAreraInfo.class));

    }

    public boolean activateAndDeactivateUser(int userId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE user_m SET is_active = CASE WHEN is_active = false THEN true WHEN is_active = true THEN false END WHERE id=:userId";
        sqlParam.addValue("userId",userId);
        int update = namedJdbc.update(qry, sqlParam);
        return update>0;
    }

    public boolean activateAndDeactivateUserAreaMapping(int userId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE user_area_mapping SET is_active = CASE WHEN is_active = false THEN true WHEN is_active = true THEN false END WHERE user_id=:userId";
        sqlParam.addValue("userId",userId);
        int update = namedJdbc.update(qry, sqlParam);
        return update>0;
    }

    /** Api required for get user level ,it is uses for tank master listing  */
  public List<UserLevelDto> getUserLevelByUserId(int userLevelId){
      MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select id,name,parent_id from oiipcra_oltp.user_level_m where id=:userLevelId";

      sqlParam.addValue("userLevelId",userLevelId);
      return namedJdbc.query(queryString,sqlParam,new BeanPropertyRowMapper<>(UserLevelDto.class));
  }

    public Integer getUserLevelById(int userId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select user_level_id from oiipcra_oltp.user_m where id=:userId";

        sqlParam.addValue("userId",userId);
        return namedJdbc.queryForObject(queryString,sqlParam,Integer.class);
    }

    /** Api required for get user Authority ,it is uses for tank master listing  */
  public List<UserAreaMappingDto> getUserAuthority(int userId) {
      MapSqlParameterSource sqlParam = new MapSqlParameterSource();
      String queryString = "select distinct dist_id as district_id,block_id,gp_id,village_id, division_id, subdivision_id as sub_division_id,section_id FROM oiipcra_oltp.user_area_mapping where user_id=:userId";
      sqlParam.addValue("userId", userId);return namedJdbc.query(queryString,sqlParam,new BeanPropertyRowMapper<>(UserAreaMappingDto.class));
  }

    public List<Integer> getSubUsers(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "select distinct b.id from oiipcra_oltp.user_m as b where user_level_id in " +
                "(WITH RECURSIVE lowerlevel AS (select id, parent_id, name FROM oiipcra_oltp.user_level_m where id in(select user_level_id " +
                "from oiipcra_oltp.user_m where id in (:userId)) " +
                "UNION ALL " +
                "SELECT t1.id, t1.parent_id, t1.name FROM oiipcra_oltp.user_level_m as t1 " +
                "INNER JOIN lowerlevel t2 ON t2.id = t1.parent_id) " +
                "SELECT id FROM lowerlevel ORDER BY id) and id in (select user_id from oiipcra_oltp.user_area_mapping " +
                "where dist_id in(select dist_id from oiipcra_oltp.user_area_mapping where user_id in (:userId))) " +
                "order by b.id";
        sqlParam.addValue("userId", userId);

        return namedJdbc.queryForList(queryString,sqlParam,Integer.class);
    }

}

