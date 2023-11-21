package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.ContractMappingModel;
import com.orsac.oiipcra.entities.DepartmentMaster;
import com.orsac.oiipcra.entities.UserLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MasterQryRepository {

    @Value("${dbschema}")
    private String DBSCHEMA;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;


    //Getting the User Level Id from user_master table based on the userId
    public Integer getUserLevelIdByUserId(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT user_level_id FROM user_m WHERE id =:userId";
        sqlParam.addValue("userId", userId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public int count(String qryStr, MapSqlParameterSource sqlParam) {
        String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
        Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
        if (null != intRes) {
            return intRes;
        }
        return 0;
    }

    //Getting the User Dept Id From user_master table based on UserId
    public Integer getDeptByUserId(int userId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dept_id FROM user_m WHERE id =:userId";
        sqlParam.addValue("userId",userId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    //Getting the User Role Id From user_master table based on UserId
    public Integer getRoleByUserId(int userId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT role_id FROM user_m WHERE id =:userId";
        sqlParam.addValue("userId",userId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }


    public List<DepartmentMaster> getAllDepartment() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllDepartmentQuery = "SELECT id, name,is_active, created_by,created_on, updated_by, updated_on FROM dept_m " +
                "WHERE is_active = true AND id != -1 ORDER BY id";
        return namedJdbc.query(GetAllDepartmentQuery, sqlParam, new BeanPropertyRowMapper<>(DepartmentMaster.class));
    }
    public List<DepartmentMaster> getAllAdaptDepartment() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllDepartmentQuery = "SELECT dept_id as id, name FROM adapt_dept_m " ;
        return namedJdbc.query(GetAllDepartmentQuery, sqlParam, new BeanPropertyRowMapper<>(DepartmentMaster.class));
    }

    public List<SubDepartmentDto> getSubDepartmentByDeptId(int deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetSubDepartmentByDeptIdQuery = "SELECT id, name, is_active, created_by, updated_by" +
                " FROM sub_dept_m WHERE dept_id=:deptId ORDER BY id ";
        sqlParam.addValue("deptId", deptId);
        return namedJdbc.query(GetSubDepartmentByDeptIdQuery, sqlParam, new BeanPropertyRowMapper<>(SubDepartmentDto.class));
    }

    public List<SubDepartmentDto> getSubDepartmentById(int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name,dept_id, is_active as active, created_by, updated_by" +
                " FROM sub_dept_m ";
        if (id == -1) {
            qry += " ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SubDepartmentDto.class));
        } else {
            qry += " WHERE id=:id";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SubDepartmentDto.class));
        }
    }


    public List<UserLevel> getAllUserLevel(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String GetAllUserLevelQuery = String.format("WITH RECURSIVE user_level_org AS ( SELECT id," +
                " name, parent_id, is_active, created_by, updated_by FROM  oiipcra_oltp.user_level_m  WHERE id = %s" +
                " UNION ALL" +
                " SELECT e.id, e.name, e.parent_id, e.is_active, e.created_by, e.updated_by" +
                " FROM oiipcra_oltp.user_level_m e INNER JOIN user_level_org u ON u.id = e.parent_id )" +
                " SELECT * FROM user_level_org;", userId);
        return namedJdbc.query(GetAllUserLevelQuery, sqlParam, new BeanPropertyRowMapper<>(UserLevel.class));
    }

    public List<UserLevel> getUserLevelById(int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, parent_id, is_active as active, created_by, created_on, updated_by, updated_on " +
                "FROM oiipcra_oltp.user_level_m ";
        if (id == -1) {
            qry += " ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UserLevel.class));
        } else {
            qry += " WHERE id=:id";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UserLevel.class));
        }
    }

    //This function is get all district
    public List<DistrictBoundaryDto> getAllDistrict() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllDistrictQuery = "SELECT dist_id, district_name FROM district_boundary where in_oiipcra=1 ORDER BY district_name asc";
        return namedJdbc.query(GetAllDistrictQuery, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }
    public List<CircleDto> getAllCircle() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllCircleQuery = "SELECT circle_id,circle_name as circleName from oiipcra_oltp.oiipcra_circle_m";
        return namedJdbc.query(GetAllCircleQuery, sqlParam, new BeanPropertyRowMapper<>(CircleDto.class));
    }

    //This function is get all district GeoJson
    public List<DistrictBoundaryDto> getAllDistrictGeoJson() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllDistrictQuery = "SELECT public.st_asgeojson(district_boundary.geom) as geojson, dist_id, district_name FROM district_boundary where in_oiipcra=1 ORDER BY dist_id";
        return namedJdbc.query(GetAllDistrictQuery, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }

    //This function is to get the geoJson for a particular District Id
    public List<DistrictBoundaryDto> getAllDistrictGeoJsonByDistId(int districtId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllDistrictQuery = "SELECT public.st_asgeojson(district_boundary.geom) as geojson, dist_id, district_name FROM district_boundary where dist_id=:districtId" +
                " AND in_oiipcra=1 ORDER BY dist_id";
        sqlParam.addValue("districtId",districtId);
        return namedJdbc.query(GetAllDistrictQuery, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }

    //This Function is to get the list of distId present in the user_area_mapping table
    public List<Integer> getListOfDistrictIdsByUserId(int userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT dist_id FROM user_area_mapping WHERE user_id=:userId and is_active=true";
        sqlParam.addValue("userId", userId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    //Getting the Dist Info by passing the list of distId
    public List<DistrictBoundaryDto> getAllDistInfoById(List<Integer> listOfDistrictIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id, district_name FROM district_boundary WHERE dist_id in (:listOfDistrictIdsByUserId) and in_oiipcra=1 ORDER BY district_name asc";
        sqlParam.addValue("listOfDistrictIdsByUserId", listOfDistrictIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }

    public List<DistrictBoundaryDto> getAllDistInfoGeoJsonById(List<Integer> listOfDistrictIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(district_boundary.geom) as geojson, dist_id, district_name FROM district_boundary WHERE dist_id in (:listOfDistrictIdsByUserId) and in_oiipcra=1";
        sqlParam.addValue("listOfDistrictIdsByUserId", listOfDistrictIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }


    public List<BlockBoundaryDto> getBlocksByDistId(int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetBlockByDistIdQuery = "SELECT block_id, block_name, dist_id, district_name" +
                " FROM block_boundary WHERE dist_id =:distId and in_oiipcra=1 ORDER BY block_name asc";
        sqlParam.addValue("distId", distId);
        return namedJdbc.query(GetBlockByDistIdQuery, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<BlockBoundaryDto> getBlocksByDistIdGeoJson(int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetBlockByDistIdQuery = "SELECT public.st_asgeojson(block_boundary.geom) as geojson, block_id, block_name, dist_id, district_name" +
                " FROM block_boundary WHERE dist_id =:distId  and in_oiipcra=1";
        sqlParam.addValue("distId", distId);
        return namedJdbc.query(GetBlockByDistIdQuery, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<BlockBoundaryDto> getBlocksGeoJsonByBlockId(Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetBlockByDistIdQuery = "SELECT public.st_asgeojson(block_boundary.geom) as geojson, block_id, block_name, dist_id, district_name" +
                " FROM block_boundary WHERE block_id =:blockId  and in_oiipcra=1";
        sqlParam.addValue("blockId", blockId);
        return namedJdbc.query(GetBlockByDistIdQuery, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    //This Function is to get the list of blockId present in the user_area_mapping table
    public List<Integer> getListOfBlockIdsByUserId(int userId, int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT block_id FROM user_area_mapping WHERE user_id=:userId AND dist_id=:distId";
        sqlParam.addValue("userId", userId);
        sqlParam.addValue("distId", distId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    //Getting the Dist Info by passing the list of blockId
    public List<BlockBoundaryDto> getAllBlcInfoById(List<Integer> listOfBlockIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id, district_name, block_id, block_name FROM block_boundary WHERE block_id in (:listOfBlockIdsByUserId) and in_oiipcra=1 ORDER BY block_name asc";
        sqlParam.addValue("listOfBlockIdsByUserId", listOfBlockIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<BlockBoundaryDto> getAllBlcInfoByIdGeoJson(List<Integer> listOfBlockIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(block_boundary.geom) as geojson, dist_id, district_name, block_id, block_name FROM block_boundary WHERE block_id in (:listOfBlockIdsByUserId)  and in_oiipcra=1";
        sqlParam.addValue("listOfBlockIdsByUserId", listOfBlockIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }


    public List<GpBoundaryDto> getGpByBlockId(int blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetGpByBlockId = "SELECT block_id, block_name, gp_id, grampanchayat_name" +
                " FROM gp_boundary WHERE block_id =:blockId  and in_oiipcra=1 ORDER BY grampanchayat_name asc";
        sqlParam.addValue("blockId", blockId);
        return namedJdbc.query(GetGpByBlockId, sqlParam, new BeanPropertyRowMapper<>(GpBoundaryDto.class));
    }

    public List<GpBoundaryDto> getGpByBlockIdGeoJson(int blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetGpByBlockId = "SELECT public.st_asgeojson(gp_boundary.geom) as geojson, block_id, block_name, gp_id, grampanchayat_name" +
                " FROM gp_boundary WHERE block_id =:blockId  and in_oiipcra=1";
        sqlParam.addValue("blockId", blockId);
        return namedJdbc.query(GetGpByBlockId, sqlParam, new BeanPropertyRowMapper<>(GpBoundaryDto.class));
    }

    public List<GpBoundaryDto> getGpByGeoJsonGpId(int gpId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetGpByBlockId = "SELECT public.st_asgeojson(gp_boundary.geom) as geojson, block_id, block_name, gp_id, grampanchayat_name" +
                " FROM gp_boundary WHERE gp_id=:gpId  and in_oiipcra=1";
        sqlParam.addValue("gpId", gpId);
        return namedJdbc.query(GetGpByBlockId, sqlParam, new BeanPropertyRowMapper<>(GpBoundaryDto.class));
    }

    //This Function is to get the list of gpId present in the user_area_mapping table
    public List<Integer> getListOfGpIdsByUserId(int userId, int blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT gp_id FROM user_area_mapping WHERE user_id=:userId AND block_id=:blockId";
        sqlParam.addValue("userId", userId);
        sqlParam.addValue("blockId", blockId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    //Getting the Gp Info by passing the list of gpId
    public List<GpBoundaryDto> getAllGpInfoById(List<Integer> listOfGpIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id, district_name, block_id, block_name, gp_id, grampanchayat_name FROM gp_boundary WHERE gp_id in (:listOfGpIdsByUserId) and in_oiipcra=1 ORDER BY grampanchayat_name asc";
        sqlParam.addValue("listOfGpIdsByUserId", listOfGpIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(GpBoundaryDto.class));
    }

    public List<GpBoundaryDto> getAllGpInfoByIdGeoJson(List<Integer> listOfGpIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(gp_boundary.geom) as geojson, dist_id, district_name, block_id, block_name, gp_id, grampanchayat_name FROM gp_boundary WHERE gp_id in (:listOfGpIdsByUserId)  and in_oiipcra=1";
        sqlParam.addValue("listOfGpIdsByUserId", listOfGpIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(GpBoundaryDto.class));
    }


    //Get Village info by GpId
    public List<VillageBoundaryDto> getVillagesByGpId(int gpId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT dist_id, district_name, block_id, block_name, gp_id, grampanchayat_name, village_id, revenue_village_name" +
                " FROM oiipcra_oltp.odisha_revenue_village_boundary_2021 WHERE gp_id =:gpId and in_oiipcra=1 ORDER BY revenue_village_name ASC";
        sqlParam.addValue("gpId", gpId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(VillageBoundaryDto.class));
    }

    public List<VillageBoundaryDto> getVillagesByGpIdGeoJson(int gpId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT public.st_asgeojson(odisha_revenue_village_boundary_2021.geom) as geojson, dist_id, district_name, block_id, block_name, gp_id, grampanchayat_name, village_id, revenue_village_name" +
                " FROM odisha_revenue_village_boundary_2021 WHERE gp_id =:gpId  and in_oiipcra=1";
        sqlParam.addValue("gpId", gpId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(VillageBoundaryDto.class));
    }

    public List<VillageBoundaryDto> getVillagesGeoJsonByVillageId(int villageId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT public.st_asgeojson(odisha_revenue_village_boundary_2021.geom) as geojson, dist_id, district_name, block_id, block_name, gp_id, grampanchayat_name, village_id, revenue_village_name" +
                " FROM odisha_revenue_village_boundary_2021 WHERE village_id =:villageId  and in_oiipcra=1";
        sqlParam.addValue("villageId", villageId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(VillageBoundaryDto.class));
    }

    public List<DivisionBoundaryDto> getDivisionByDistId(int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
       /* String GetVillageByGpIdQuery = "SELECT mi_division_id as divisionId, mi_division_name as divisionName " +
                " FROM mi_division_m WHERE dist_id =:distId ORDER BY mi_division_name asc ";*/
        String GetVillageByGpIdQuery="select  mapping.division_id as divisionId,mi_division_name as divisionName from oiipcra_oltp.division_mapping as mapping \n" +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=mapping.division_id\n" +
                "where mapping.is_active=true and mapping.dist_id=:distId";
        sqlParam.addValue("distId", distId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(DivisionBoundaryDto.class));
    }

    public List<DivisionBoundaryDto> getDivisionByDistIdGeoJson(int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT public.st_asgeojson(mi_division_m.geom) as geojson, mi_division_id as divisionId, mi_division_name as divisionName " +
                " FROM mi_division_m WHERE dist_id =:distId";
        sqlParam.addValue("distId", distId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(DivisionBoundaryDto.class));
    }

    public List<SubDivisionBoundaryDto> getSubDivisionByDivisionId(int divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT mi_division_id as divisionId,mi_sub_division_name as subdivisionName,mi_sub_division_id as subdivisionId" +
                " FROM oiipcra_oltp.mi_subdivision_m WHERE mi_division_id =:divisionId ORDER BY mi_sub_division_name asc";
        sqlParam.addValue("divisionId", divisionId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(SubDivisionBoundaryDto.class));
    }

    public List<SubDivisionBoundaryDto> getSubDivisionByDivisionIdGeoJson(int divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT public.st_asgeojson(mi_subdivision_m.geom) as geojson, mi_division_id as divisionId, mi_sub_division_name as subdivisionName, mi_sub_division_id as subdivisionId" +
                " FROM oiipcra_oltp.mi_subdivision_m WHERE mi_division_id =:divisionId";
        sqlParam.addValue("divisionId", divisionId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(SubDivisionBoundaryDto.class));
    }

    public List<SectionBoundaryDto> getSectionBySubDivisionId(int subDivisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT mi_division_id as divisionId, mi_sub_division_id as subDivisionId, section_id as sectionId, mi_section_name as sectionName" +
                " FROM oiipcra_oltp.mi_section_m WHERE mi_sub_division_id =:subDivisionId ORDER BY mi_section_name asc ";
        sqlParam.addValue("subDivisionId", subDivisionId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(SectionBoundaryDto.class));
    }

    public List<SectionBoundaryDto> getSectionBySubDivisionIdGeoJson(int subDivisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetVillageByGpIdQuery = "SELECT public.st_asgeojson(mi_section_m.geom) as geojson, mi_division_id as divisionId, mi_sub_division_id as subDivisionId, section_id as sectionId, mi_section_name as sectionName" +
                " FROM oiipcra_oltp.mi_section_m WHERE mi_sub_division_id =:subDivisionId";
        sqlParam.addValue("subDivisionId", subDivisionId);
        return namedJdbc.query(GetVillageByGpIdQuery, sqlParam, new BeanPropertyRowMapper<>(SectionBoundaryDto.class));
    }

    //This Function is to get the list of blockId present in the user_area_mapping table
    public List<Integer> getListOfVillageIdsByUserId(int userId, int gpId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT village_id FROM user_area_mapping WHERE user_id=:userId AND gp_id=:gpId";
        sqlParam.addValue("userId", userId);
        sqlParam.addValue("gpId", gpId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getListOfDivisionByUserId(int userId, int distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT division_id FROM user_area_mapping WHERE user_id=:userId AND dist_id=:distId";
        sqlParam.addValue("userId", userId);
        sqlParam.addValue("distId", distId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getListOfSubDivisionByUserId(int userId, int divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT subdivision_id FROM user_area_mapping WHERE user_id=:userId AND division_id=:divisionId";
        sqlParam.addValue("userId", userId);
        sqlParam.addValue("divisionId", divisionId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getListOfSectionByUserId(int userId, int subDivisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT section_id FROM user_area_mapping WHERE user_id=:userId AND subdivision_id=:subDivisionId";
        sqlParam.addValue("userId", userId);
        sqlParam.addValue("subDivisionId", subDivisionId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    //This Function is to get the list of gpId present in the user_area_mapping table
    public List<VillageBoundaryDto> getAllVillageInfoById(List<Integer> listOfVillageIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id, district_name, block_id, block_name,gp_id,grampanchayat_name,village_id,revenue_village_name FROM oiipcra_oltp.odisha_revenue_village_boundary_2021 WHERE village_id in (:listOfVillageIdsByUserId) and in_oiipcra=1 ORDER BY revenue_village_name ASC";
        sqlParam.addValue("listOfVillageIdsByUserId", listOfVillageIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(VillageBoundaryDto.class));
    }

    public List<VillageBoundaryDto> getAllVillageInfoByIdGeoJson(List<Integer> listOfVillageIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(odisha_revenue_village_boundary_2021.geom) as geojson, dist_id, district_name, block_id, block_name FROM oiipcra_oltp.odisha_revenue_village_boundary_2021 WHERE village_id in (:listOfVillageIdsByUserId)  and in_oiipcra=1";
        sqlParam.addValue("listOfVillageIdsByUserId", listOfVillageIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(VillageBoundaryDto.class));
    }

    public List<DivisionBoundaryDto> getAllDivisionInfoById(List<Integer> listOfDivisionIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        //String qry = "SELECT mi_division_id as divisionId, mi_division_name as divisionName FROM mi_division_m WHERE mi_division_id in (:listOfDivisionIdsByUserId) ORDER BY mi_division_name asc ";
        String qry="select  mapping.division_id as divisionId,division.mi_division_name as divisionName from oiipcra_oltp.division_mapping as mapping " +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=mapping.division_id " +
                "where mapping.is_active=true and  mapping.division_id in (:listOfDivisionIdsByUserId) ORDER BY mi_division_name asc ";
        sqlParam.addValue("listOfDivisionIdsByUserId", listOfDivisionIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DivisionBoundaryDto.class));
    }

    public List<DivisionBoundaryDto> getAllDivisionInfoByIdGeoJson(List<Integer> listOfDivisionIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(mi_division_m.geom) as geojson, mi_division_id as divisionId, mi_division_name as divisionName FROM mi_division_m WHERE mi_division_id in (:listOfDivisionIdsByUserId)";
        sqlParam.addValue("listOfDivisionIdsByUserId", listOfDivisionIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DivisionBoundaryDto.class));
    }

    public List<SubDivisionBoundaryDto> getAllSubDivisionInfoById(List<Integer> listOfSubDivisionIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mi_sub_division_id as subDivisionId, mi_sub_division_name as subdivisionName, mi_division_id as divisionId FROM mi_subdivision_m WHERE mi_sub_division_id in (:listOfSubDivisionIdsByUserId) ORDER BY mi_sub_division_name asc";
        sqlParam.addValue("listOfSubDivisionIdsByUserId", listOfSubDivisionIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SubDivisionBoundaryDto.class));
    }

    public List<SubDivisionBoundaryDto> getAllSubDivisionInfoByIdGeoJson(List<Integer> listOfSubDivisionIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(mi_subdivision_m.geom) as geojson, mi_sub_division_id as subDivisionId, mi_sub_division_name as subdivisionName, mi_division_id as divisionId FROM mi_subdivision_m WHERE mi_sub_division_id in (:listOfSubDivisionIdsByUserId)";
        sqlParam.addValue("listOfSubDivisionIdsByUserId", listOfSubDivisionIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SubDivisionBoundaryDto.class));
    }

    public List<SectionBoundaryDto> getAllSectionInfoById(List<Integer> listOfSectionIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mi_division_id as divisionId,mi_sub_division_id as subDivisionId,section_id as sectionId,mi_section_name as sectionName FROM mi_section_m WHERE section_id in (:listOfSectionIdsByUserId) ORDER BY mi_section_name asc ";
        sqlParam.addValue("listOfSectionIdsByUserId", listOfSectionIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SectionBoundaryDto.class));
    }

    public List<SectionBoundaryDto> getAllSectionInfoByIdGeoJson(List<Integer> listOfSectionIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT public.st_asgeojson(mi_section_m.geom) as geojson, mi_division_id as divisionId, mi_sub_division_id as subDivisionId, section_id as sectionId, mi_section_name as sectionName FROM mi_section_m WHERE section_id in (:listOfSectionIdsByUserId)";
        sqlParam.addValue("listOfSectionIdsByUserId", listOfSectionIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SectionBoundaryDto.class));
    }


    public List<DesignationDto>getDesignationByUserLevelId(int userLevelId, int deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        if (userLevelId == -1) {
            qry += "SELECT id, name, description FROM designation_m WHERE true AND id >1 ORDER BY name ";
        } else {
            qry += "SELECT id, name, description FROM designation_m WHERE true AND user_level_id =:userLevelId AND dept_id =:deptId AND id>1 ORDER BY name";
            sqlParam.addValue("userLevelId", userLevelId);
            sqlParam.addValue("deptId", deptId);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DesignationDto.class));
    }

    public List<DesignationDto> getAllDesignationByUserLevelId(Integer userLevelId, int deptId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry="";
        if(deptId==0) {
            qry += "SELECT id, name, description FROM designation_m WHERE is_active=true AND user_level_id >=:userLevelId AND id >1 ORDER BY name ";
            sqlParam.addValue("userLevelId",userLevelId);
        }
        else
        {
            qry += "SELECT id, name, description FROM designation_m WHERE is_active=true AND user_level_id >=:userLevelId AND dept_id=:deptId AND id >1 ORDER BY name ";
            sqlParam.addValue("userLevelId",userLevelId);
            sqlParam.addValue("deptId",deptId);
        }

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DesignationDto.class));
    }


    public List<DesignationDto> getDesignationInfoBylId(int id,boolean flag) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dm.id, dm.name, dm.description, dm.parent_id, dm.sub_dept_id," +
                "dm.dept_id,dept.name as departmentName, dm.is_active as active,dm.created_by," +
                "dm.created_on, dm.updated_by, dm.updated_on, dm.user_level_id," +
                "ulm.name as userLevelName  FROM oiipcra_oltp.designation_m as " +
                "dm  left join oiipcra_oltp.dept_m as dept on dm.dept_id=dept.id " +
                "left join oiipcra_oltp.user_level_m as ulm on dm.user_level_id=ulm.id ";
        if (id == -1 && flag==false) {
            qry += " where dm.id>1 ORDER BY dm.name ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DesignationDto.class));
        }
       else if (id == -1 && flag==true) {
            qry += "  where dm.is_active=true and dm.id>1 ORDER BY dm.name ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DesignationDto.class));
        }
        else {
            qry += " WHERE dm.id=:id";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DesignationDto.class));
        }

    }


    public List<RoleDto> getRoleByUserLevelId(int userLevelId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        if (userLevelId == -1) {
            qry += "SELECT id, name, description, can_edit, can_add, can_delete, can_approve, is_deletion_request_access, is_deletion_approval_access" +
                    " is_addition_request_access, is_addition_approval_access, is_survey_access, is_issue_resolution_access, is_permission_access FROM role_m WHERE true AND id>1 Order BY id";
        } else {
            qry += "SELECT id, name, description, can_edit, can_add, can_delete, can_approve, is_deletion_request_access, is_deletion_approval_access" +
                    " is_addition_request_access, is_addition_approval_access, is_survey_access, is_issue_resolution_access, is_permission_access FROM role_m " +
                    " WHERE true AND user_level_id =:userLevelId  AND id>1 ORDER BY id";
            sqlParam.addValue("userLevelId", userLevelId);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleDto.class));
    }

    public List<RoleDto> getRoleByRoleId(int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, description, parent_role_id,user_level_id, can_edit, can_add,can_view, can_delete, can_approve," +
                " is_deletion_request_access as deletionRequestAccess, is_deletion_approval_access as deletionApprovalAccess," +
                " is_addition_request_access as additionRequestAccess, is_addition_approval_access as additionApprovalAccess, is_survey_access as surveyAccess, " +
                "is_issue_resolution_access as issueResolutionAccess,is_permission_access as permissionAccess,is_active as active FROM role_m ";
        if (id == -1) {
            qry += " WHERE id>1 ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleDto.class));
        } else {
            qry += " WHERE id=:id ";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleDto.class));
        }

    }


    public List<DepartmentDto> getDepartment(int userId, int id,boolean flag) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, is_active as active, created_by, created_on, updated_by, updated_on " +
                " FROM oiipcra_oltp.dept_m as dm ";
        if (id == -1 && flag ==false) {
            qry += " ORDER BY name ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DepartmentDto.class));
        }
        if (id == -1 && flag ==true) {
            qry += " where dm.is_active=true ORDER BY name ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DepartmentDto.class));
        }
        else {
            qry += " WHERE id=:id ";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DepartmentDto.class));
        }
    }

    public List<MenuDto> getMenu(int userId, int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as menuId, name as menuName, parent_id as parentId, module as targetUrl, is_active as active, created_by, created_on, updated_by, updated_on " +
                "FROM oiipcra_oltp.menu_m ";
        if (id == -1) {
            qry += " ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(MenuDto.class));
        } else {
            qry += " WHERE id=:id ";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(MenuDto.class));
        }
    }

    public List<UnitDto> getUnit(int userId, int id,boolean flag) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, is_active as active, created_by, created_on, updated_by, updated_on " +
                " FROM oiipcra_oltp.unit_m ";
        if (id == -1 && flag== false) {
            qry += " ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UnitDto.class));
        }
        else if (id == -1 && flag== true) {
            qry += " where is_active=true ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UnitDto.class));
        }else {
            qry += " WHERE id=:id ";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UnitDto.class));
        }
    }

    public List<LicenseDto> getAllLicense() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, is_active as active, created_by, created_on, updated_by, updated_on, tender_limit " +
                "FROM oiipcra_oltp.license_class where is_active=true ORDER BY id";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(LicenseDto.class));
    }

    public List<ContractStatusDto> getAllContractStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as statusId, name as statusName FROM oiipcra_oltp.contract_status";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractStatusDto.class));
    }

    public List<ContractStatusDto> getAllContractNumber() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT  id as contractId, contract_number as contractNumber FROM oiipcra_oltp.contract_m";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractStatusDto.class));
    }

    public List<ContractStatusDto> getAllContractType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT  id as contractTypeId,name as contractType from oiipcra_oltp.contract_type";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractStatusDto.class));
    }

    public List<TenderCodeResponse> getAllTenderCode(int userLevelId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as tenderId,code as tenderCode,tender_name as tenderName from oiipcra_oltp.tender_m where tender_level_id=:userLevelId";
        sqlParam.addValue("userLevelId", userLevelId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderCodeResponse.class));
    }


    public Page<AgencyInfo> getAllAgencyList(AgencyListDto agencyListDto) {

        PageRequest pageable = PageRequest.of(agencyListDto.getPage(), agencyListDto.getSize(), Sort.Direction.fromString(agencyListDto.getSortOrder()), agencyListDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) :
                new Sort.Order(Sort.Direction.DESC, "created_by");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        Integer  resultCount = 0;

        String qry = "SELECT agency.id as agencyId,agency.name as agencyName,agency.license_class_id as licenseClassId,licence.name as licenseClassName,agency.exempt_id as exemptId,exempt.name as exemptName,agency.pan_no as panNo,agency.address as address," +
                "agency.pincode as pincode,agency.post as postOffice,agency.license_validity as licenseValidity,agency.gstin_no as gstinNo,agency.phone as phoneNo, agency.is_active as active,dist.district_name as distName,agency.dist_id as distId from oiipcra_oltp.agency_m  as agency " +
                "left join oiipcra_oltp.agency_exempt_m as exempt on exempt.id=agency.exempt_id " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id=agency.dist_id " +
                "left join oiipcra_oltp.license_class as licence on licence.id=agency.license_class_id where agency.is_active=true  ";
        if(agencyListDto.getAgencyId()!=null && agencyListDto.getAgencyId() >0){
            qry+=" and agency.id=:agencyId ";
            sqlParam.addValue("agencyId",agencyListDto.getAgencyId());
        }
        if(agencyListDto.getPanNo()!=null ){
            qry+=" and agency.pan_no=:panNo ";
            sqlParam.addValue("panNo",agencyListDto.getPanNo());
        }
        if(agencyListDto.getDistId()!=null && agencyListDto.getDistId() >0){
            qry+=" and agency.dist_id=:distId ";
            sqlParam.addValue("distId",agencyListDto.getDistId());
        }
        if(agencyListDto.getLicenseClassId()!=null && agencyListDto.getLicenseClassId() >0){
            qry+=" and agency.license_class_id=:licenseClassId ";
            sqlParam.addValue("licenseClassId",agencyListDto.getLicenseClassId());
        }

        qry += " ORDER BY agency. " + order.getProperty() + "  " + order.getDirection().name();
        resultCount = count(qry, sqlParam);
        qry += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        //qry+=" ORDER BY agency.created_by desc ";

        List<AgencyInfo>agencyInfos = namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyInfo.class));
        return new PageImpl<>(agencyInfos,pageable,resultCount);
        //return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyInfo.class));

    }

    public List<AgencyInfo> getAgency(Integer userId,Integer agencyId,String panNo) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT agency.id as agencyId,agency.name as agencyName,agency.license_class_id as licenseClassId,licence.name as licenseClassName,agency.exempt_id as exemptId,exempt.name as exemptName,agency.pan_no as panNo,agency.address as address," +
                "agency.pincode as pincode,agency.post as postOffice,agency.license_validity as licenseValidity,agency.gstin_no as gstinNo,agency.phone as phoneNo, agency.is_active as active,dist.district_name as distName,agency.dist_id as distId from oiipcra_oltp.agency_m  as agency " +
                "left join oiipcra_oltp.agency_exempt_m as exempt on exempt.id=agency.exempt_id " +
                "left join oiipcra_oltp.district_boundary as dist on dist.dist_id=agency.dist_id " +
                "left join oiipcra_oltp.license_class as licence on licence.id=agency.license_class_id where agency.is_active=true  ";
        if(agencyId!=null && agencyId >0){
            qry+=" and agency.id=:agencyId ";
            sqlParam.addValue("agencyId",agencyId);
        }
        if(panNo!=null ){
            qry+=" and agency.pan_no=:panNo ";
            sqlParam.addValue("panNo",panNo);
        }
        qry+=" ORDER BY agency.created_by desc ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyInfo.class));

    }



    public List<WorkTypeDto> getWorkType(int userId, int id,boolean flag) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, is_active as active, created_by, created_on, updated_by, updated_on " +
                " FROM oiipcra_oltp.work_type_m ";

        if (id == -1 && flag==false) {
            qry += " ORDER BY id ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WorkTypeDto.class));
        }
       else if (id == -1 && flag==true) {
            qry += " where is_active=true ORDER BY id  ";
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WorkTypeDto.class));
        }else {
            qry += " WHERE id=:id ";
            sqlParam.addValue("id", id);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WorkTypeDto.class));
        }
    }

    //This repository is used to get the all the role menus with passing the parameter is_active = true And used in a part to update the role menu
    public List<RoleMenuDto> getRoleMenu(int userId, int roleId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT m.name, m.parent_id, m,module, rm.role_id, rm.menu_id, rm.is_active as active, rm.created_by, rm.updated_by, rm.is_default" +
                " FROM oiipcra_oltp.role_menu as rm" +
                " LEFT JOIN menu_m as m ON rm.menu_id = m.id where rm.is_active=true ";
        if (roleId == -1) {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleMenuDto.class));
        } else {
            qry += " AND role_id=:roleId ORDER BY rm.id";
            sqlParam.addValue("roleId", roleId);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleMenuDto.class));
        }
    }

    //This repository is used to get the all the role menus without passing the parameter is_active = true And used in a part to update the role menu
    public List<RoleMenuDto> getRoleMenus(int userId, int roleId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT m.name, m.parent_id, m,module, rm.role_id, rm.menu_id, rm.is_active as active, rm.created_by, rm.updated_by, rm.is_default" +
                " FROM oiipcra_oltp.role_menu as rm" +
                " LEFT JOIN menu_m as m ON rm.menu_id = m.id where true ";
        if (roleId == -1) {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleMenuDto.class));
        } else {
            qry += " AND role_id=:roleId ORDER BY rm.id";
            sqlParam.addValue("roleId", roleId);
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(RoleMenuDto.class));
        }
    }

    //This Repository is used when we want to get all the parent menu by passing the roleid Parameter
    public List<ParentMenuInfo> getAllParentMenu(Integer roleId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT menu.id AS value, menu.name AS label,menu.parent_id, menu.module,roleMenu.is_default as isDefault " +
                " FROM  oiipcra_oltp.role_menu as roleMenu " +
                " left join  oiipcra_oltp.menu_m as menu on menu.id=roleMenu.menu_id " +
                " WHERE parent_id = 0 AND menu.is_active = true";
        if(roleId > 0){
            qry+= " AND roleMenu.role_id=:roleId";
            sqlParam.addValue("roleId", roleId);
        }
        qry+= "  order by menu.order ASC";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ParentMenuInfo.class));
    }

    //This Repository is used when we want to get all the parent menu without passing the roleid Parameter
    public List<ParentMenuInfo> getAllParentMenuWithoutRoleId() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT menu.id AS value, menu.name AS label,menu.parent_id, menu.module " +
                " FROM  oiipcra_oltp.menu_m as menu " +
//                " left join  oiipcra_oltp.menu_m as menu on menu.id=roleMenu.menu_id " +
                " WHERE parent_id = 0 AND menu.is_active = true";
        qry+= "  order by menu.id ASC";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ParentMenuInfo.class));
    }

    public List<HierarchyMenuInfo> getHierarchyMenuListById(Integer parentId, Integer roleId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT menu.id AS value, menu.name AS label,menu.parent_id, menu.module,roleMenu.is_default as isDefault" +
                " FROM oiipcra_oltp.menu_m as menu " +
                " left join oiipcra_oltp.role_menu as roleMenu on menu.id=roleMenu.menu_id " +
                " WHERE parent_id =:parentId  AND menu.is_active = true  And role_id=:roleId ORDER BY menu.order ASC";
        sqlParam.addValue("parentId", parentId);
        sqlParam.addValue("roleId", roleId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(HierarchyMenuInfo.class));
    }


    //This repository is used to get all the menu list as the roleId is not passed
    public List<HierarchyMenuInfo> getHierarchyMenuListByIdWithoutRoleId(Integer parentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT menu.id AS value, menu.name AS label,menu.parent_id, menu.module" +
                //" ,roleMenu.is_default as isDefault" +
                " FROM oiipcra_oltp.menu_m as menu " +
                //" left join oiipcra_oltp.role_menu as roleMenu on menu.id=roleMenu.menu_id " +
                " WHERE parent_id =:parentId  AND menu.is_active = true ORDER BY menu.id ASC";
        sqlParam.addValue("parentId", parentId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(HierarchyMenuInfo.class));
    }

    //Deactivate existing menu before updating
    public Boolean deactivateMenu(int roleId, int menuId, boolean isActive){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE role_menu SET is_active = :isActive WHERE role_id =:roleId AND menu_id=:menuId";
        sqlParam.addValue("roleId",roleId);
        sqlParam.addValue("menuId",menuId);
        sqlParam.addValue("isActive",isActive);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update > 0;
    }


    public ContractInfo getContract(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract.id,contract.contract_name,contract.tender_id,contract.area_id,contract.contract_code,finyr.name as financialYear,contract.contract_date,status.name as contractStatus," +
                "type.name as contractType,contract.procurement_made_for,contract.estimate_id as estimateId," +
                "contract.zone,contract.approval_order_date,contract.work_description,contract.eoi_id as eoiId ,contract.date_eoi," +
                "contract.rfp_issued_on,contract.rfp_received_on,contract.correspondence_file_no," +
                "contract.contract_number,agency.name as agency,contract.contract_amount,contract.gst,(contract.contract_amount*contract.gst/100) as gstAmount," +
                "contract.contract_amount+(contract.contract_amount*contract.gst/100) as totalAmount," +
                "contract.stipulated_date_of_comencement,contract.stipulated_date_of_completion,contract.approval_order," +
                "contract.tachnical_sanction_no,contract.work_id,contract.estimated_cost,contract.awarded_as," +
                "contract.agreement_number,contract.loa_issued_no,contract.loa_issued_date,contract.rate_of_premium," +
                "contract.actual_date_of_commencement,contract.actual_date_of_completion,contract.created_by,contract.created_on ," +
                "date_part('month',age(contract.stipulated_date_of_completion,contract.stipulated_date_of_comencement)) as timePeriod," +
                "date_part('month',age(contract.actual_date_of_completion,contract.actual_date_of_commencement)) as actualTimePeriod,contract.type_id as typeId,"+
                "contract.contract_level_id as contractLevelId,agency.id as agencyId,status.id as contractStatusId,contract.work_type_id as workTypeId,workType.name as workTypeName,contract.procurement_type_id as procurementTypeId,procurementTypeMaster.name as procurementTypeName "+
                "FROM oiipcra_oltp.contract_m as contract " +
                "left join oiipcra_oltp.fin_year_m as finyr on contract.finyr_id=finyr.id " +
                "left join oiipcra_oltp.procurement_type_m as procurementTypeMaster on procurementTypeMaster.id=contract.procurement_type_id "+
                "left join oiipcra_oltp.contract_status as status on contract.contract_status_id=status.id " +
                "left join oiipcra_oltp.type_m as type on contract.contract_type_id=type.id " +
                "left join oiipcra_oltp.work_type_m as workType on contract.work_type_id=workType.id "+
//                "left join oiipcra_oltp.contract_level_master as level on contract.contract_level_id=level.id " +
                "left join oiipcra_oltp.agency_m as agency on contract.agency_id=agency.id where contract.is_active=true ";
        if (id > 0) {
            qry += " AND contract.id=:contractId ";
            sqlParam.addValue("contractId", id);
        }

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ContractInfo.class));
    }

    public AgencyInfo getAgencyById(Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT agency.id as agencyId,agency.name as agencyName,licence.name as licenseClassName,licence.id as licenseClassId,exempt.id as exemptId,exempt.name as exemptName,agency.pan_no as panNo,agency.gstin_no as gstinNo," +
                "agency.phone as phoneNo,agency.is_active as active,agency.license_validity as licenseValidity,agency.address,agency.post as postOffice,agency.pincode as pincode," +
                "district.dist_id as distId,district.district_name as distName,agency.image_name as imageName " +
                "from oiipcra_oltp.agency_m  as agency " +
                "left join oiipcra_oltp.agency_exempt_m as exempt on exempt.id=agency.exempt_id " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=agency.dist_id " +
                "left join oiipcra_oltp.license_class as licence on licence.id=agency.license_class_id where agency.is_active=true  ";
        if (agencyId > 0) {
            qry += " AND agency.id=:agencyId ";
            sqlParam.addValue("agencyId", agencyId);
        }

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyInfo.class));
    }

    public ActivityInfo getActivityById(Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract.contract_number as contractNumber,agency.name as agencyName,contract.contract_date as contractDate," +
                "tank.name_of_the_m_i_p as tankName,activity.no_of_unit as unit,unit.name as unitValue,activity.longitude as longitude,activity.latitude as latitude," +
                "activity.delay_days as delayDays,approvalStatus.name as approvalStatus,status.name as workStatus,activity.target_value as unitTarget," +
                "type.name as workType,tender.bid_id as bid,activity.surveyor_image as surveyorImage,activity.image_path as imagePath " +
                "from oiipcra_oltp.activity_survey as activity " +
                "left join oiipcra_oltp.tank_m as tank on tank.id=activity.tank_id " +
                "left join oiipcra_oltp.master_work_status as status on status.id=activity.work_status_id " +
                "left join oiipcra_oltp.unit_m as unit on unit.id=activity.unit_id " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=activity.contract_id " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=contract.tender_id " +
                "left join oiipcra_oltp.contract_type as type on type.id=contract.contract_type_id " +
                "left join oiipcra_oltp.agency_m as agency on agency.id=contract.agency_id " +
                "left join oiipcra_oltp.master_work_status as masterStatus on masterStatus.id=activity.work_status_id " +
                " left join oiipcra_oltp.approval_status_m as approvalStatus on approvalStatus.id=activity.approval_status where activity.is_active=true ";
        if (activityId > 0) {
            qry += " AND activity.id=:activityId ";
            sqlParam.addValue("activityId", activityId);
        }

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityInfo.class));
    }

    public List<ActivityImageInfo> getActivityImagesById(Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as imageId, survey_id as activityId, image_name as imageName, image_location as imageLocation," +
                "is_active as active, savetime as saveTime " +
                "FROM oiipcra_oltp.activity_survey_image as activity where is_active=true";
        if (activityId > 0) {
            qry += "  AND activity.survey_id=:activityId  ";
            sqlParam.addValue("activityId", activityId);
        }

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityImageInfo.class));
    }

    public List<ContractMappingDto> getContractMapping(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select mapping.id,mapping.contract_id,mapping.activity_id,mapping.dist_id as distId,district.district_name as distName,mapping.block_id as blockId,block.block_name as blockName," +
                "mapping.gp_id as gpId,gp.grampanchayat_name as gpName,mapping.village_id as villageId,village.revenue_village_name as villageName,mapping.tank_id as tankId, " +
                "tank.name_of_the_m_i_p as tankName,mapping.created_by,mapping.created_on,mapping.tender_notice_id as tenderNoticeId, " +
                "mapping.tender_id as tenderId,mapping.estimate_id as estimateId,tender.bid_id as bidId,notice.name_of_work as noticeName,tank.project_id as projectId,division.mi_division_name as divisionName,mapping.division_id as divisionId,mapping.tank_wise_contract_amount as tankWiseContractAmount " +
                "from oiipcra_oltp.contract_mapping as mapping " +
                "left join oiipcra_oltp.district_boundary as district on mapping.dist_id=district.dist_id " +
                "left join oiipcra_oltp.block_boundary as block on mapping.block_id=block.block_id  " +
                "left join oiipcra_oltp.gp_boundary as gp on mapping.gp_id=gp.gp_id  " +
                "left join oiipcra_oltp.village_boundary as village on mapping.village_id=village.village_id " +
                "left join oiipcra_oltp.tender_m as tender on tender.id=mapping.tender_id " +
                "left join oiipcra_oltp.tender_notice as notice on notice.id=mapping.tender_notice_id " +
                "left join oiipcra_oltp.mi_division_m as division on division.mi_division_id=mapping.division_id "+
                "left join oiipcra_oltp.tank_m_id as tank on mapping.tank_id=tank.id where mapping.is_active=true  ";
        if (id > 0) {
            qry += " AND mapping.contract_id=:contractId ";
            sqlParam.addValue("contractId", id);
        }
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractMappingDto.class));
    }

    public List<ContractDocumentDto> getContractDocument(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, contract_id, doc_name, doc_path, is_active, created_by, created_on, updated_by " +
                "FROM oiipcra_oltp.contract_document where is_active=true ";
        if (id > 0) {
            qry += " AND contract_id=:contractId ";
            sqlParam.addValue("contractId", id);
        }

        return  namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractDocumentDto.class));

    }


    /**
     * get district details by division id
     */
    public List<DistrictInfo> getDistrictListByDivisionId(int divId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "SELECT id, mi_division_name as divisionName, mi_division_id as divisionId,div.dist_id, dist.district_name FROM oiipcra_oltp.mi_division_m as div" +
                " left join oiipcra_oltp.district_boundary as dist on div.dist_id = dist.dist_id  where mi_division_id=:divId";
        sqlParam.addValue("divId", divId);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(DistrictInfo.class));
    }

    public boolean activateAndDeactivateMasterDataById(int masterId, int id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String query = "";
        String query_data = "UPDATE %s SET is_active = CASE WHEN is_active = false THEN true WHEN is_active = true THEN false END WHERE id=:id ";
        switch (masterId) {
            case 1:
                query = "unit_m";
                break;
            case 2:
                query = "work_type_m";
                break;
            case 3:
                query = "agency_m";
                break;
            case 4:
                query = "sub_dept_m";
                break;
            case 5:
                query = "dept_m";
                break;
            case 6:
                query = "designation_m";
                break;
            case 7:
                query = "user_level_m";
                break;
            case 8:
                query = "menu_m";
                break;
            case 9:
                query = "role_m";
                break;
            case 10:
                query = "license_class";
                break;
            default:
                break;
        }
        query_data = String.format(query_data, query, query);
        sqlParam.addValue("id", id);
        int update = namedJdbc.update(query_data, sqlParam);
        return update > 0;
    }

    public List<TankInfo> getTankInfoJson(int distId, int blockId, int gpId, int villageId, int divisionId, int subDivisionId, int sectionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "SELECT public.st_asgeojson(geom) as geojson, id, project_id, dept_dist_name, dist_id, dept_block_name, block_id, dept_gp_name, gp_id, name_of_the_m_i_p as tankName, m_i_p_id, latitude, longitude, category," +
                " category_id, type, type_id, catchment_area_sqkm, designed_cca_kharif_ha, designed_cca_rabi_ha, certified_ayacut_kharif_ha, certified_ayacut_rabi_ha, river_basin, river_basin_id, mi_division_name, mi_division_id," +
                " water_surface_area_ha, height_of_dam_weir_in_m, length_of_dam_weir_in_m, type_of_dam_weir, type_of_dam_weir_id, remarks, is_active, created_by, created_on, updated_by, updated_on, tank_id, revised_start_date, revised_end_date, no_of_benificiaries," +
                " village_id, sub_division_id, section_id FROM oiipcra_oltp.tank_m_id where true";
        if (distId > 0) {
            queryString += " AND dist_id=:distId";
            sqlParam.addValue("distId", distId);
        }
        if (blockId > 0) {
            queryString += " AND block_id=:blockId";
            sqlParam.addValue("blockId", blockId);
        }
        if (gpId > 0) {
            queryString += " AND gp_id=:gpId";
            sqlParam.addValue("gpId", gpId);
        }
        if (villageId > 0) {
            queryString += " AND village_id=:villageId";
            sqlParam.addValue("villageId", villageId);
        }
        if (divisionId > 0) {
            queryString += " AND mi_division_id=:divisionId";
            sqlParam.addValue("divisionId", divisionId);
        }
        if (subDivisionId > 0) {
            queryString += " AND sub_division_id=:subDivisionId";
            sqlParam.addValue("subDivisionId", subDivisionId);
        }
        if (sectionId > 0) {
            queryString += " AND section_id=:sectionId";
            sqlParam.addValue("sectionId", sectionId);
        }
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }

    public List<AgencyExemptDto> getAgencyExempt() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as exemptId, name as exemptName, is_active as active, created_by as createdBy, created_on as createdOn, updated_by as updatedBy, updated_on as updatedOn" +
                " FROM  oiipcra_oltp.agency_exempt_m ORDER BY id";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyExemptDto.class));

    }
    public List<ActivityStatusDto> getAllActivityStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name FROM oiipcra_oltp.activity_status where is_active=true ORDER BY id";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityStatusDto.class));

    }
    public List<ActivityStatusDto> getAllApprovedStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name FROM oiipcra_oltp.approval_status_m where is_active=true ORDER BY id";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityStatusDto.class));
    }
    public List<ActivityStatusDto> getAllActivityEstimateLevel() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id,level_name as name FROM oiipcra_oltp.approval_level where is_active=true ORDER BY id";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityStatusDto.class));

    }
    public Activity getDetailsByParentId(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, name, code, parent_id as parentId, master_head_id as masterHeadId, is_terminal as isTerminal, is_active as isActive, description " +
                " FROM oiipcra_oltp.master_head_details where id=:id";
        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));
    }
    //This Function Is Used to get estimate,activityId,tenderId,tenderNoticeId
    public List<Integer> getEstimateIdsByTankId(Integer tankId,Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT distinct estimate_id from oiipcra_oltp.activity_estimate_tank_mapping as  estimateMapping " +
                "left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=estimateMapping.estimate_id where estimateMapping.tank_id=:tankId and estimateMapping.is_active=true " ;

        sqlParam.addValue("tankId", tankId);
        if(activityId != null && activityId>0){
            qry+=" and estimate.activity_id=:activityId ";
            sqlParam.addValue("activityId",activityId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }


    public List<Integer> getActivityIdsByTankId(ContractListRequestDto contractInfo) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select activity_id from oiipcra_oltp.activity_estimate_mapping as estimate " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tank on estimate.id=tank.estimate_id " +
                "where tank.tank_id=:tankId and tank.is_active=true";
        sqlParam.addValue("tankId", contractInfo.getTankId());
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getTenderNoticeIdsByTankId(ContractListRequestDto contractInfo) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tenderNotice.id  from oiipcra_oltp.tender_notice_level_mapping as noticeLevel " +
                "left join oiipcra_oltp.tender_notice as tenderNotice on tenderNotice.id=noticeLevel.tender_notice_id " +
                "left join oiipcra_oltp.tender_level_mapping tender on tender.tender_id=tenderNotice.tender_id where tenderNotice.is_active=true and noticeLevel.tank_id=:tankId ";
        if(contractInfo.getActivityId()>0){
            qry+=" and tender.activity_id=:activityId ";
            sqlParam.addValue("activityId",contractInfo.getActivityId());
        }
        sqlParam.addValue("tankId", contractInfo.getTankId());
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getTenderIdsByTankId(ContractListRequestDto contractInfo) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tender_id from oiipcra_oltp.tender_level_mapping  where tank_id=:tankId ";
        if(contractInfo.getActivityId()>0){
            qry+=" and activity_id=:activityId ";
            sqlParam.addValue("activityId",contractInfo.getActivityId());
        }
        sqlParam.addValue("tankId", contractInfo.getTankId());
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getContractIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT contract_id from oiipcra_oltp.contract_mapping where tank_id=5 and is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }
    public List<Integer> getInvoiceIdsByTankId(int tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT estimate_id from oiipcra_oltp.activity_estimate_tank_mapping where tank_id=:tankId and is_active=true";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }


    public List<MonthDto> getAllMonth() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllMonth = "SELECT month.id, month.name from oiipcra_oltp.month_m as month ";
        return namedJdbc.query(GetAllMonth,sqlParam,new BeanPropertyRowMapper<>(MonthDto.class));
    }
    public Integer getDistinctActivityId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAllMonth = "SELECT Distinct activity_id from oiipcra_oltp.contract_mapping where contract_id=:contractId";
        sqlParam.addValue("contractId",contractId);
        return namedJdbc.queryForObject(GetAllMonth,sqlParam,Integer.class);
    }

    //Logical Changes Repository
    public List<DistrictBoundaryDto> getDistrictListByEstimateId(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.dist_id, dist.district_name, dist.district_code FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.district_boundary AS dist ON aetm.dist_id = dist.dist_id" +
                " WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }

    public List<Integer> districtListByEstimateIdAndDistId(int estimateId, int distId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        if(distId>0){
            qry+= " AND dist_id =:distId";
            sqlParam.addValue("distId",distId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public Integer getLevelId(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT level_id FROM oiipcra_oltp.activity_estimate_mapping as aem WHERE id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public List<Integer> getBlockIdsByEstimateId(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT block_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<DistrictBoundaryDto> getDistrictListByBlockIds(List<Integer> blockIdsByEstimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dist_id, district_name, district_code FROM oiipcra_oltp.block_boundary WHERE block_id IN(:blockIdsByEstimateId) AND ";
        sqlParam.addValue("blockIdsByEstimateId",blockIdsByEstimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }

    public List<Integer> getTankIdsByEstimateId(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank_id FROM oiipcra_oltp.activity_estimate_tank_mapping WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<DistrictBoundaryDto> getDistrictByTankIds(List<Integer> tankIds){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT distBound.dist_id, distBound.district_name, distBound.district_code FROM oiipcra_oltp.district_boundary as distBound" +
                " LEFT JOIN oiipcra_oltp.tank_m_id as tank ON tank.dist_id = distBound.dist_id" +
                " WHERE tank.tank_id IN(:tankIds)";
        sqlParam.addValue("tankIds",tankIds);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }

    //Block List By Estimate Id And Dist when level is block
    public List<BlockBoundaryDto> getBlockIdsByEstimateIdAndDistId(int estimateId, int distId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.dist_id, block.district_name, aetm.block_id, block.block_name FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.block_boundary AS block ON aetm.block_id = block.block_id" +
                " WHERE estimate_id =:estimateId AND aetm.dist_id =:distId";
        sqlParam.addValue("estimateId",estimateId);
        sqlParam.addValue("distId",distId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<BlockBoundaryDto> getBlockListByDistId(List<Integer> distIds){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.dist_id, block.district_name, aetm.block_id, block.block_name FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.block_boundary AS block ON aetm.block_id = block.block_id WHERE aetm.dist_id IN(:distIds)";
        sqlParam.addValue("distIds",distIds);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<BlockBoundaryDto> getBlockListByBlockIdsAndEstimateIds(List<Integer> blockIds, int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.dist_id, block.district_name, aetm.block_id, block.block_name FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.block_boundary AS block ON aetm.block_id = block.block_id WHERE aetm.block_id IN(:blockIds) AND aetm.estimate_id=:estimateId";
        sqlParam.addValue("blockIds",blockIds);
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<Integer> getTankIdsByEstimateIdAndTankId(int estimateId, int distId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank_id FROM oiipcra_oltp.activity_estimate_tank_mapping where estimate_id =:estimateId";
        if(distId > 0){
            qry+= "and dist_id =:distId";
            sqlParam.addValue("distId",distId);
        }
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<BlockBoundaryDto> getBlockIdsByEstimateIdAndTankId(List<Integer> tankIdsByEstimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.dist_id, block.district_name, aetm.block_id, block.block_name FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.block_boundary AS block ON aetm.block_id = block.block_id" +
                " WHERE tank_id IN(:tankIdsByEstimateId)";
        sqlParam.addValue("tankIdsByEstimateId",tankIdsByEstimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(BlockBoundaryDto.class));
    }

    public List<TankInfo> getTankIdsByEstimateIdAndDistAndBlock(int estimateId, int distId, int blockId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.id, dept_dist_name, tank.dist_id, dept_block_name, tank.block_id, name_of_the_m_i_p as tankName FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.tank_m_id AS tank ON tank.id = aetm.tank_id" +
                " WHERE aetm.estimate_id =:estimateId";
        if(distId>0){
            qry+= " AND aetm.dist_id=:distId";
            sqlParam.addValue("distId",distId);
        }
        if(blockId>0){
            qry+= " AND aetm.block_id=:blockId";
            sqlParam.addValue("blockId",blockId);
        }
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }

    public List<TankInfo> getTankIdsListByDistId(List<Integer> districtListByEstimateIdAndDistId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.id, dept_dist_name, tank.dist_id, dept_block_name, tank.block_id, name_of_the_m_i_p as tankName FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.tank_m_id AS tank ON tank.id = aetm.tank_id" +
                " WHERE aetm.dist_id IN(:districtListByEstimateIdAndDistId)";
        sqlParam.addValue("districtListByEstimateIdAndDistId",districtListByEstimateIdAndDistId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public List<TankContractDto> getTankIdsListByDistIdForContract(List<Integer> districtList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as tankId,name_of_the_m_i_p as tankName,dist_id as distId,dept_dist_name districtName,block_id as blockId,dept_block_name as blockName from oiipcra_oltp.tank_m_id where dist_id in(:distIdList)";
        sqlParam.addValue("distIdList",districtList);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankContractDto.class));
    }
    public List<TankContractDto> getTankIdsListByBlockIdForContract(List<Integer> blockList){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as tankId,name_of_the_m_i_p as tankName,dist_id as distId,dept_dist_name districtName,block_id as blockId,dept_block_name as blockName from oiipcra_oltp.tank_m_id where block_id in(:blockList)";
        sqlParam.addValue("blockList",blockList);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankContractDto.class));
    }
    public List<TankContractDto> getTankIdsListByEstimateIdForContract(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank.id as tankId,tank.name_of_the_m_i_p as tankName,tank.dist_id as distId,tank.dept_dist_name as districtName,tank.block_id as blockId,tank.dept_block_name as blockName " +
                "from oiipcra_oltp.activity_estimate_tank_mapping as estimateTank " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=estimateTank.tank_id where estimateTank.estimate_id=:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankContractDto.class));
    }




    public List<TankInfo> getTankIdsListByBlockId(List<Integer> blockIdsByEstimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT aetm.id, dept_dist_name, tank.dist_id, dept_block_name, tank.block_id, name_of_the_m_i_p as tankName FROM oiipcra_oltp.activity_estimate_tank_mapping as aetm" +
                " LEFT JOIN oiipcra_oltp.tank_m_id AS tank ON tank.id = aetm.tank_id" +
                " WHERE aetm.block_id IN(:blockIdsByEstimateId)";
        sqlParam.addValue("blockIdsByEstimateId",blockIdsByEstimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public List<DistrictBoundaryDto> getDistrictByEstimateId(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct tankMapping.dist_id as dist_id ,district.district_name as district_name from  oiipcra_oltp.activity_estimate_tank_mapping as tankMapping " +
                "left join oiipcra_oltp.district_boundary as district on district.dist_id=tankMapping.dist_id " +
                "where tankMapping.estimate_id=:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DistrictBoundaryDto.class));
    }
    public List<TenderDto> getTenderByEstimateId(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        //for result declared and closed
        String qry = "SELECT id,bid_id from oiipcra_oltp.tender_m where estimate_id=:estimateId and tender_status=4";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TenderDto.class));
    }
    public List<TankContractDto> getTankByBlockId(Integer blockId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        //for result declared and closed
        String qry = "select id as tankId ,block_id as blockId,name_of_the_m_i_p as tankName,dept_block_name as blockName,project_id as projectId from " +
                "oiipcra_oltp.tank_m_id where block_id=:blockId";
        sqlParam.addValue("blockId",blockId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankContractDto.class));
    }
    public List<TankContractDto> getTankByEstimateIdForContract(Integer estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        //for tank which are under the give estimateId
        String qry = "select  et.tank_id as tankId,tank.name_of_the_m_i_p as tankName,tank.block_id as blockId,tank.dept_block_name as blockName from  oiipcra_oltp.activity_estimate_tank_mapping as et " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=et.tank_id " +
                "where et.estimate_id=:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankContractDto.class));
    }

    public  Integer getPanExistOrNot(String panNo){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(id) from oiipcra_oltp.agency_m where pan_no=:panNo ";
        sqlParam.addValue("panNo",panNo);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);

    }
    public  Integer workIdentificationCodeExistOrNot(Integer tenderId,String workIdentificationCode){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(*) from oiipcra_oltp.tender_notice where tender_id=:tenderId and work_identification_code=:workIdentificationCode ";
        sqlParam.addValue("workIdentificationCode",workIdentificationCode);
        sqlParam.addValue("tenderId",tenderId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);

    }
    public AgencyDto getPanNoAndAgencyName(String panNo, Integer agencyId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "select name as name,pan_no as panNo from oiipcra_oltp.agency_m as agency where ";
        if (panNo != null) {
            qry += "agency.pan_no=:panNo ";
            sqlParam.addValue("panNo", panNo);
        }
        if (agencyId !=null && agencyId > 0) {
            qry += "agency.id=:agencyId ";
            sqlParam.addValue("agencyId", agencyId);
        }

        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));
        }
        catch(Exception e){
            return null;
        }
    }

    public DivisionBoundaryDto getDivisionById(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mi_division_id as divisionId, mi_division_name as divisionName " +
                " FROM mi_division_m WHERE mi_division_id =:divisionId";
        sqlParam.addValue("divisionId", divisionId);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(DivisionBoundaryDto.class));
    }

    public List<AgencyInfo> getAllPanNo() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select pan_no FROM oiipcra_oltp.agency_m where is_active= true  ";
        return  namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(AgencyInfo.class));
    }

    public List<CircleDto> getCircleByDistrictIds(List<Integer> listOfDistrictIdsByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT DISTINCT div.circle_id,cm.circle_name from oiipcra_oltp.mi_division_m as div  " +
                "left join oiipcra_oltp.oiipcra_circle_m as cm on cm.circle_id=div.circle_id  " +
                "where div.is_active=true and div.dist_id in(:listOfDistrictIdsByUserId) ";
        sqlParam.addValue("listOfDistrictIdsByUserId", listOfDistrictIdsByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CircleDto.class));

    }

    public List<PhysicalProgressConsultancyDto> getPhysicalProgressDetailsForConsultancyById(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = " SELECT consultancy.id, progress_unit, target_unit, target_time,actual_time, target_completion, target_payment," +
                "actual_completion, actual_payment, contract_id,unit.name as targetUnitName,contract.contract_name as contractName," +
                "consultancy.progress_status progressStatusId,status.name as progressStatusName " +
                "FROM oiipcra_oltp.physical_progress_consultancy as consultancy " +
                "left join oiipcra_oltp.physical_progress_unit as unit on unit.id=consultancy.target_unit  " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=consultancy.contract_id " +
                "left join oiipcra_oltp.progress_status_m as status on status.id=consultancy.progress_status ";
        if(contractId!=null &&  contractId >0){
            qry+=" where  consultancy.contract_id=:contractId "  ;
            sqlParam.addValue("contractId",contractId);
        }

            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PhysicalProgressConsultancyDto.class));

    }
    public List<UnitDto> getPhysicalProgressUnit() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT id,name FROM oiipcra_oltp.physical_progress_unit ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UnitDto.class));

    }
    public List<UnitDto> getPhysicalProgressStatus() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT id,name FROM oiipcra_oltp.progress_status_m where is_active=true ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(UnitDto.class));

    }
    public List<ContractMappingDto> getTankByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "SELECT mapping.tank_id,tankM.name_of_the_m_i_p as tankName,tankM.project_id as projectId FROM oiipcra_oltp.contract_mapping as mapping " +
                "left join oiipcra_oltp.tank_m_id as tankM on tankM.id=mapping.tank_id where mapping.is_active=true and mapping.contract_id=:contractId ";
        sqlParam.addValue("contractId",contractId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractMappingDto.class));
    }
    public List<TankInfo> getTankByWorkId(Integer workId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = " select tankM.id as tankId,tankM.project_id,tankM.name_of_the_m_i_p as nameOfTheMip " +
                "from oiipcra_oltp.tank_m_id as tankM " +
                "left  join oiipcra_oltp.tender_notice_level_mapping as notice on notice.tank_id=tankM.id " +
                " where tender_notice_id=:workId";
        sqlParam.addValue("workId",workId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }

    public boolean updateAgricultureEstimate(Integer estimateId, Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.agriculture_estimate  " +
                "SET activity_id=:activityId WHERE id=:estimateId ";
        sqlParam.addValue("activityId",activityId);
        sqlParam.addValue("estimateId",estimateId);
        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }

    public Integer noOfBeneficiariesByComponentId(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select sum(no_of_beneficiaries) from oiipcra_oltp.denormalized_achievement where is_active=true  ";

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        try {
            Integer b = namedJdbc.queryForObject(qry, sqlParam, Integer.class);
            if (b == null) {
                b = 0;
            }
            return b;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean updateDenormalizedAchievement(Integer id, Integer activityId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.denormalized_achievement  " +
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
    public Integer getTankDataByid(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select project_id from  oiipcra_oltp.tank_m_id where id=:tankId ";
        sqlParam.addValue("tankId",tankId);

          return  namedJdbc.queryForObject(qry, sqlParam, Integer.class);

    }
    public List<ContractMappingModel> getContractMappingCountByContractId(Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "select * from  oiipcra_oltp.contract_mapping where contract_id=:contractId and is_active=true";
        sqlParam.addValue("contractId",contractId);

        return  namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ContractMappingModel.class));

    }
    public List<ActivityInformationDto> getAllActivityByDepartment(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "";
        if(id==1){
            qry+="select * from oiipcra_oltp.master_head_details where id in(SELECT  distinct(activity_id )\n" +
                    "FROM oiipcra_oltp.adapt_financial_scheme) order by id asc";
        }
        if(id==2){
            qry+="select * from oiipcra_oltp.master_head_details where id in(SELECT  distinct(activity_id )\n" +
                    "FROM oiipcra_oltp.adapt_physical_scheme) order by id asc";
        }
        if(id==3){
            qry+="select * from oiipcra_oltp.master_head_details where id in(SELECT  distinct(activity_id )\n" +
                    "FROM oiipcra_oltp.fard_scheme_master) order by id asc";
        }

        return  namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityInformationDto.class));

    }


    public List<AgencyDto> getAgencyByContractType(Integer typeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry  = "";
        if(typeId==1){
            qry+="select id as agencyId,name as agencyName,address,phone,pan_no,created_by,created_on,updated_by,updated_on,license_class_id," +
                    "exempt_id,gstin_no,license_validity,post,dist_id,pincode,image_name,image_path from oiipcra_oltp.agency_m where is_active=true";
        }
        if(typeId==2){
            qry+="select id as agencyId,name as agencyName from oiipcra_oltp.agency_m where id in(SELECT  distinct(agency_id )\n" +
                    "                   FROM oiipcra_oltp.contract_m where work_type_id=2) order by id asc";
        }

        return  namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(AgencyDto.class));

    }
}
