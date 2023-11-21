package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.TankInfo;
import com.orsac.oiipcra.dto.*;

import com.orsac.oiipcra.dto.DepthDto;
import com.orsac.oiipcra.dto.WaterSpreadDto;
import com.orsac.oiipcra.dto.DraftTenderNoticeDto;
import com.orsac.oiipcra.dto.DepthDto;
import com.orsac.oiipcra.dto.WaterSpreadDto;
import com.orsac.oiipcra.repository.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public class GraphRepositoryImpl implements GraphRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    public List<CropCycleAyacutDto> getCropCycleIntensity(Integer projectId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);
        String qry = "select distinct gridcode.type_id as gridCode, a.crop_cycle as year,coalesce(b.area,0.00) as area," +
                "(case when a.gridcode=10 Then 'SINGLE CROP' when a.gridcode=20 Then 'DOUBLE CROP'  when a.gridcode=30 Then 'TRIPLE CROP' else 'No Croping' end) as gridType " +
                "from oiipcra_oltp.crop_cycle_master as gridcode " +
                "left join oiipcra_oltp.crop_cycle_ayacut as a on a.gridcode=gridcode.type_id " +
                "left join ( select distinct crop_cycle,gridcode,sum(area) over(partition by gridcode,crop_cycle) as area " +
                "from oiipcra_oltp.crop_cycle_ayacut ";
        if (projectId > 0){
            qry += "where project_id=:projectId " ;
        }


        qry +=  ") as b on a.crop_cycle=b.crop_cycle and gridcode.type_id=b.gridcode " +
                "where a.crop_cycle=:year " +
                "order by a.crop_cycle,gridcode.type_id ";
        sqlParam.addValue("projectId", project);
        sqlParam.addValue("year", year);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }

    public List<String> getDistinctYearBYProjectId(Integer projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);
        String qry = "SELECT distinct crop_cycle from oiipcra_oltp.crop_cycle_ayacut where project_id=:projectId";
        sqlParam.addValue("projectId", project);
        return namedJdbc.queryForList(qry, sqlParam, String.class);
    }

    public List<CropNonCropDto> getCropCycleIntensityMonthWise(Integer projectId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);
        year = "%" + year;
        String qry = "select sum(area_ha) as area ,gridcode as gridCode,month as month,class_type as cropType," +
                "(case when month like('crop_noncrop_01%') Then 'JANUARY' when month like('crop_noncrop_02%') Then 'FEBRUARY' " +
                " when month like('crop_noncrop_03%') Then 'MARCH' when month like('crop_noncrop_04%') Then 'APRIL' " +
                " when month like('crop_noncrop_05%') Then 'MAY' when month like('crop_noncrop_06%') Then 'JUNE' " +
                " when month like('crop_noncrop_07%') Then 'JULY' when month like('crop_noncrop_08%') Then 'AUGUST' " +
                " when month like('crop_noncrop_09%') Then 'SEPTEMBER' when month like('crop_noncrop_10%') Then 'OCTOBER' " +
                " when month like('crop_noncrop_11%') Then 'NOVEMBER' when month like('crop_noncrop_12%') Then 'DECEMBER' else 'No Year' end) as monthName " +
                "from oiipcra_oltp.oiipcra_ayacutwise_crop_noncrop " +
                "where month like(:year) and oiipcra_id=:projectId group by gridcode,month,class_type order  by month asc ";
        sqlParam.addValue("projectId", project);
        sqlParam.addValue("year", year);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropNonCropDto.class));
    }

    @Override
    public List<WaterSpreadDto> getWaterSpreadMonth(String projectId, String yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT distinct month_id,month as month,sum(area) over(partition by month_id )  " +
//                "from oiipcra_oltp.oiipcra_water_spread where year LIKE (:year) ORDER BY month_id asc ";
//        String qry = "SELECT distinct month_id,month as month,sum(area) over(partition by month_id )  \n" +
//                "from oiipcra_oltp.oiipcra_water_spread where year=:yearId and project_id =:projectId ORDER BY month_id asc ";
        String qry ="SELECT distinct month_id,month as month,round(sum(area),3) as sum,year as year\n" +
                "from oiipcra_oltp.oiipcra_water_spread where year=:yearId and project_id =:projectId group by month,month_id,year    \n" +
                "ORDER BY month_id asc\n";
        sqlParam.addValue("projectId", projectId);
        sqlParam.addValue("yearId", yearId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }

    @Override
    public List<WaterSpreadDto> getWaterSpreadByProjectId(String projectId, String yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT year,sum(area)%12 as sum from oiipcra_oltp.oiipcra_water_spread where year=:yearId and project_id =:projectId group by year ";
        sqlParam.addValue("projectId", projectId);
        sqlParam.addValue("yearId", yearId);
        //  sqlParam.addValue("size",size);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }

//    @Override
//    public IssueTrackerDto getIssueCount(Integer tankId) {
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select count(issue.id)as issueCount,issue.tank_id,tank.name_of_the_m_i_p as tankName,  " +
//                "count(issue.id) filter (where issue.status=5) as resolved,count(issue.id) filter (where issue.status=2) as rejected  " +
//                "from oiipcra_oltp.issue_tracker as issue  " +
//                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = issue.tank_id   " +
//                "where issue.tank_id=:tankId  " +
//                "GROUP BY issue.tank_id,tank.name_of_the_m_i_p  ";
//        sqlParam.addValue("tankId",tankId);
//        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(IssueTrackerDto.class));
//    }

    //    @Override
//    public GrievanceDto getGrievanceCount(Integer tankId) {
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select count (gri.id) as grievanceCount,gri.tank_id,tank.name_of_the_m_i_p as tankName,  " +
//                "count(gri.id) filter (where gri.status=3) as resolved,count(gri.id) filter (where gri.status=2) as rejected  " +
//                "from oiipcra_oltp.grievance as gri    " +
//                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = gri.tank_id  " +
//                "where gri.tank_id=:tankId  " +
//                "GROUP BY gri.tank_id,tank.name_of_the_m_i_p ";
//        sqlParam.addValue("tankId",tankId);
//        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(GrievanceDto.class));
//    }
    public List<DepthDto> getDepth(Integer projectId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select tank.project_id as tankId,depth.latitude,depth.longitude,depth.altitude,depth.accuracy,depth.depth_in_m, " +
//                "depth.capture, EXTRACT(YEAR FROM depth.capture) AS year " +
//                ",(case when date_part('month', capture)=1 Then 'JANUARY' " +
//                "   when date_part('month', capture)=2 Then 'FEBRUARY' " +
//                "   when date_part('month', capture)=3 Then 'MARCH' " +
//                "   when date_part('month', capture)=4 Then 'APRIL' " +
//                "   when date_part('month', capture)=5 Then 'MAY' " +
//                "   when date_part('month', capture)=6 Then 'JUNE' " +
//                "   when date_part('month', capture)=7 Then 'JULY' " +
//                "   when date_part('month', capture)=8 Then 'AUGUST' " +
//                "   when date_part('month', capture)=9 Then 'SEPTEMBER' " +
//                "   when date_part('month', capture)=10 Then 'OCTOBER' " +
//                "   when date_part('month', capture)=11 Then 'NOVEMBER' " +
//                "   when date_part('month', capture)=12 Then 'DECEMBER' " +
//                "  else 'No Year' end) as month " +
//                "from oiipcra_oltp.depth_m as depth  " +
//                "left join oiipcra_oltp.tank_m_id as tank on tank.id=depth.tank_id " +
//                "where tank.project_id=:projectId and CAST(date_part('year', capture) as varchar(4))=:year order by capture asc ";
        String qry ="select distinct a.month, round((sum(a.depth) over(partition by a.month)/count(*) over(partition by month)),3) as depthInM,a.tankId,a.year as year " +
                "from(select tank.project_id as tankId,depth.latitude,depth.longitude,depth.altitude,depth.accuracy,depth.depth_in_m as depth, " +
                "depth.capture, EXTRACT(YEAR FROM depth.capture) AS year  " +
                ",(case when date_part('month', capture)=1 Then 'JANUARY'  " +
                "when date_part('month', capture)=2 Then 'FEBRUARY'  " +
                "when date_part('month', capture)=3 Then 'MARCH'  " +
                "when date_part('month', capture)=4 Then 'APRIL'  " +
                "when date_part('month', capture)=5 Then 'MAY'  " +
                "when date_part('month', capture)=6 Then 'JUNE'  " +
                "when date_part('month', capture)=7 Then 'JULY'  " +
                "when date_part('month', capture)=8 Then 'AUGUST'  " +
                "when date_part('month', capture)=9 Then 'SEPTEMBER'  " +
                "when date_part('month', capture)=10 Then 'OCTOBER'  " +
                "when date_part('month', capture)=11 Then 'NOVEMBER'  " +
                "when date_part('month', capture)=12 Then 'DECEMBER'  " +
                "else 'No Year' end) as month " +
                "from oiipcra_oltp.depth_m as depth   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id=depth.tank_id  " +
                "where tank.project_id=:projectId and CAST(date_part('year', capture) as varchar(4))=:year order by capture asc) as a ";
        sqlParam.addValue("year", year);
        sqlParam.addValue("projectId", projectId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DepthDto.class));
    }

    public BigDecimal getContractByProjectId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry =  "select sum(tank_wise_contract_amount) from oiipcra_oltp.contract_mapping where tank_id=:tankId";
//        String qry = "select round(sum(contract_amount+(contract_amount*(gst/100))),2) as contractAmount from oiipcra_oltp.contract_m  " +
//                "where id in(select distinct contract_id from oiipcra_oltp.contract_mapping where tank_id =:tankId) ";
        String qry ="select round(sum(tank_wise_contract_amount),2) as contractAmount from  oiipcra_oltp.contract_mapping  " +
                "where tank_id =:tankId   ";
        sqlParam.addValue("tankId", tankId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal getEstimateByProjectId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select tank_wise_cost as estimate from oiipcra_oltp.tender_notice_level_mapping where tank_id=:tankId";
        String qry = " select sum(estimated_amount) as estimateAmount from oiipcra_oltp.activity_estimate_mapping  " +
                " where id in( select distinct estimate_id from oiipcra_oltp.activity_estimate_tank_mapping where tank_id =:tankId) ";
        sqlParam.addValue("tankId", tankId);
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal getExpenditureByProjectId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "select sum (value) from oiipcra_oltp.expenditure_data where id in " +
//                "(select distinct expenditure_id from oiipcra_oltp.expenditure_mapping where tank_id=:tankId) ";
        String qry = "select sum(value) as expenditureAmount from oiipcra_oltp.expenditure_data " +
                " where id in(select distinct expenditure_id " +
                " from oiipcra_oltp.expenditure_mapping where tank_id =:tankId) ";
        sqlParam.addValue("tankId", tankId);

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.00);
        }

    }

    public List<PhysicalProgressExecutedDto> getPhysicalProgressDetails(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT tank.tank_id as tankId ,executed.id,executed.tank_id as projectId,executed.length_of_canal_improved,\n" +
                "executed.no_of_cd_structures_repared,executed.total_length_of_cad,executed.no_of_outlet_constructed,\n" +
                "executed.progress_status as progressStatusId,status.name as progressStatusName,executed.planned_id as plannedId  \n" +
                "from oiipcra_oltp.physical_progress_executed as executed  \n" +
                "left join oiipcra_oltp.tank_m_id as tank on tank.project_id = executed.tank_id  \n" +
                "left join oiipcra_oltp.progress_status_m as status on status.id=executed.progress_status \n" +
                "where executed.is_active = true and tank.tank_id =:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PhysicalProgressExecutedDto.class));
    }

    public List<PhysicalProgressPlannedDto> getPhysicalProgressPlannedDetails(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT tank.tank_id,planned.id,planned.tank_id as projectId,planned.total_length_of_canal_as_per_estimate,"+
                "planned.no_of_cd_structures_to_be_repared,planned.total_length_of_cad,planned.progress_status as progressStatusId,status.name as progressStatusName  " +
                "from oiipcra_oltp.physical_progress_planned as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.project_id = planned.tank_id  " +
                "left join oiipcra_oltp.progress_status_m as status on status.id=planned.progress_status "+
                "where planned.is_active = true and tank.tank_id =:tankId  ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PhysicalProgressPlannedDto.class));
    }

    @Override
    public Integer getIssueCount(Integer tankId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count (issue.id) as issueCount " +
                "from oiipcra_oltp.issue_tracker as issue " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = issue.tank_id " +
                "where issue.tank_id=:tankId ";
        if (year != null) {
            String yr = year + "%";
            qry += " and issue_date::varchar like :year ";
            sqlParam.addValue("year", yr);

        }

        qry += "GROUP BY tank.tank_id,tank.name_of_the_m_i_p ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Integer getGrievanceCount(Integer tankId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count (gri.id) as grievanceCount from oiipcra_oltp.grievance as gri   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = gri.tank_id " +
                "where gri.tank_id=:tankId ";

        if (year != null) {
            String yr = year + "%";
            qry += " and gri.created_on::varchar like :year ";
            sqlParam.addValue("year", yr);

        }

        qry += " GROUP BY gri.tank_id,tank.name_of_the_m_i_p  ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Integer getResolvedCount(Integer tankId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count (issue.id) as issueCount " +
                "from oiipcra_oltp.issue_tracker as issue " +
                " left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = issue.tank_id " +
                "where issue.status=5 and issue.tank_id=:tankId ";

        if (year != null) {
            String yr = year + "%";
            qry += " and issue_date::varchar like :year ";
            sqlParam.addValue("year", yr);

        }

        qry += " GROUP BY tank.tank_id,tank.name_of_the_m_i_p  ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return 0;
        }


    }

    @Override
    public Integer getRejectedCount(Integer tankId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count (issue.id) as issueCount " +
                "from oiipcra_oltp.issue_tracker as issue " +
                " left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = issue.tank_id " +
                "where issue.status=2 and issue.tank_id=:tankId ";

        if (year != null) {
            String yr = year + "%";
            qry += " and issue_date::varchar like :year ";
            sqlParam.addValue("year", yr);

        }

        qry += " GROUP BY tank.tank_id,tank.name_of_the_m_i_p  ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return 0;
        }


    }

    @Override
    public Integer getResolvedGrievanceCount(Integer tankId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count (gri.id) as grievanceCount from oiipcra_oltp.grievance as gri   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = gri.tank_id " +
                "where gri.status=3 and gri.tank_id=:tankId ";

        if (year != null) {
            String yr = year + "%";
            qry += " and gri.created_on::varchar like :year ";
            sqlParam.addValue("year", yr);

        }

        qry += " GROUP BY gri.tank_id,tank.name_of_the_m_i_p  ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return 0;
        }


    }

    @Override
    public Integer getRejectedGrievanceCount(Integer tankId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count (gri.id) as grievanceCount from oiipcra_oltp.grievance as gri   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = gri.tank_id " +
                "where gri.status=2 and gri.tank_id=:tankId ";

        if (year != null) {
            String yr = year + "%";
            qry += " and gri.created_on::varchar like :year ";
            sqlParam.addValue("year", yr);

        }

        qry += " GROUP BY gri.tank_id,tank.name_of_the_m_i_p  ";
        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }


    public List<CropCycleAyacutDto> getCropCycleIntensityList(Integer projectId, String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);
        String qry = "select distinct gridcode.type_id as gridCode, b.crop_cycle as year,coalesce(b.area,0.00) as cropArea,ayacutBoundary.area as commandArea, " +
                "(case when b.gridcode=10 Then 'SINGLE CROP' when b.gridcode=20 Then 'DOUBLE CROP'  " +
                "when b.gridcode=30 Then 'TRIPLE CROP' else 'No Croping' end) as gridType " +
                "from oiipcra_oltp.crop_cycle_master as gridcode " +
                "left join ( select distinct crop_cycle,gridcode,project_id,sum(area) over(partition by gridcode,crop_cycle) as area  " +
                "from oiipcra_oltp.crop_cycle_ayacut ";
        if (projectId > 0){
            qry += "where project_id=:projectId " ;
        }

        qry +=  ") as b on gridcode.type_id=b.gridcode " +
                "left join oiipcra_oltp.ayacut_area_boundary as ayacutBoundary on ayacutBoundary.project_id=b.project_id  " +
                "where b.crop_cycle=:year  " +
                "order by b.crop_cycle,gridcode.type_id ";
        sqlParam.addValue("projectId", project);
        sqlParam.addValue("year", year);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }

    public PhysicalProgressPlannedDto getPhysicalProgressPlannedDetailsByTankIdAndContractId(Integer tankId,Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select * from   oiipcra_oltp.physical_progress_planned as planned " +
                "where planned.is_active = true  and planned.contract_id=:contractId";
        if(tankId!=null){
            qry+=" and planned.tank_m_id =:tankId";
            sqlParam.addValue("tankId", tankId);
        }

        sqlParam.addValue("contractId",contractId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(PhysicalProgressPlannedDto.class));
        }
        catch(Exception e){
            return null;

        }
    }
    public PhysicalProgressExecutedDto getPhysicalProgressExecutedDetailsByTankIdAndContractId(Integer tankId,Integer contractId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select * from   oiipcra_oltp.physical_progress_executed as executed  " +
                "where executed.is_active = true  and executed.contract_id=:contractId";
        if(tankId!=null){
            qry+=" and executed.tank_m_id=:tankId";
            sqlParam.addValue("tankId", tankId);
        }
        sqlParam.addValue("contractId",contractId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(PhysicalProgressExecutedDto.class));
        }
        catch(Exception e){
            return null;

        }
    }
    public Integer updatePhysicalPlannedData(PhysicalProgressUpdateDto physicalProgress){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " update oiipcra_oltp.physical_progress_planned set total_length_of_canal_as_per_estimate=:totalLengthOfCanalAsPerEstimate ,no_of_cd_structures_to_be_repared=:noOfCdStructuresToBeRepared," +
                " total_length_of_cad=:totalLengthOfCad,progress_status=:progressStatus where  contract_id=:contractId " ;
        if(physicalProgress.getTankId()!=null){
            qry+="and tank_m_id=:tankId";
        }
       // sqlParam.addValue("id", physicalProgress.getId());
        sqlParam.addValue("progressStatus", physicalProgress.getProgressStatus());
        sqlParam.addValue("tankId", physicalProgress.getTankId());
        sqlParam.addValue("contractId",physicalProgress.getContractId());
        sqlParam.addValue("totalLengthOfCanalAsPerEstimate",physicalProgress.getTotalLengthOfCanalAsPerEstimate());
        sqlParam.addValue("noOfCdStructuresToBeRepared",physicalProgress.getNoOfCdStructuresToBeRepared());
        sqlParam.addValue("totalLengthOfCad",physicalProgress.getTotalLengthOfCad());
        return namedJdbc.update(qry,sqlParam);
    }
    public Integer updatePhysicalExecutedData(PhysicalProgressUpdateDto physicalProgress){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " update oiipcra_oltp.physical_progress_executed set length_of_canal_improved=:lengthOfCanalImproved ,no_of_cd_structures_repared=:noOfCdStructuresRepared," +
                " total_length_of_cad=:totalLengthOfCad,no_of_outlet_constructed=:noOfOutletConstructed,progress_status=:progressStatus where  contract_id=:contractId  " ;
        if(physicalProgress.getTankId()!=null){
            qry+="and tank_m_id=:tankId";
            sqlParam.addValue("tankId", physicalProgress.getTankId());
        }
        sqlParam.addValue("id", physicalProgress.getId());
        sqlParam.addValue("progressStatus", physicalProgress.getProgressStatus());
        sqlParam.addValue("contractId",physicalProgress.getContractId());
        sqlParam.addValue("lengthOfCanalImproved",physicalProgress.getLengthOfCanalImproved());
        sqlParam.addValue("noOfCdStructuresRepared",physicalProgress.getNoOfCdStructuresRepared());
        sqlParam.addValue("totalLengthOfCad",physicalProgress.getTotalLengthOfCadExecuted());
        sqlParam.addValue("noOfOutletConstructed",physicalProgress.getNoOfOutletConstructed());
        return namedJdbc.update(qry,sqlParam);
    }

    public WaterSpreadDto getLastYearMonthWaterSpread() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select month,year,month_id from oiipcra_oltp.oiipcra_water_spread " +
                       "ORDER BY gid DESC LIMIT 1 ";
        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }

    public CropNonCropDto getLastYearMonthCropNonCrop() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select gid,month, " +
                "(case when month like('crop_noncrop_01%') Then 'JANUARY' when month like('crop_noncrop_02%') Then 'FEBRUARY'  " +
                "when month like('crop_noncrop_03%') Then 'MARCH' when month like('crop_noncrop_04%') Then 'APRIL' " +
                "when month like('crop_noncrop_05%') Then 'MAY' when month like('crop_noncrop_06%') Then 'JUNE'  " +
                "when month like('crop_noncrop_07%') Then 'JULY' when month like('crop_noncrop_08%') Then 'AUGUST'  " +
                "when month like('crop_noncrop_09%') Then 'SEPTEMBER' when month like('crop_noncrop_10%') Then 'OCTOBER'  " +
                "when month like('crop_noncrop_11%') Then 'NOVEMBER' when month like('crop_noncrop_12%') Then 'DECEMBER' else 'No Year' end) as monthName  " +
                "from oiipcra_oltp.oiipcra_ayacutwise_crop_noncrop " +
                "ORDER BY gid DESC LIMIT 1 ";

        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(CropNonCropDto.class));
    }
    public List<TankInfo> getAllReserviorTank() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select * from oiipcra_oltp.tank_m_id where type_id=3 ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankInfo.class));
    }
    public  List<CropCycleAyacutDto> getCropCycleIntensityProjectWise(Integer projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);
        String qry = "select a.crop_cycle as year,\n" +
                "coalesce(sum(distinct b.area),0.00) as area,(coalesce(sum(distinct b.area),0.00)*100)/(SELECT area FROM oiipcra_oltp.ayacut_area_boundary WHERE project_id=:projectId) as percentage,\n" +
                "b.project_id as projectId,tankM.name_of_the_m_i_p as tankName\n" +
                "from oiipcra_oltp.crop_cycle_master as gridcode left join oiipcra_oltp.crop_cycle_ayacut as a on a.gridcode=gridcode.type_id left join\n" +
                "( select distinct crop_cycle,gridcode,sum(area) over(partition by gridcode,crop_cycle) as area,project_id \n" +
                " from oiipcra_oltp.crop_cycle_ayacut where project_id =:projectId)\n" +
                "as b on a.crop_cycle=b.crop_cycle and gridcode.type_id=b.gridcode\n" +
                "left join oiipcra_oltp.tank_m_id as tankM on tankM.project_id::varchar=b.project_id \n" +
                "group by a.crop_cycle,b.project_id,tankM.name_of_the_m_i_p \n" +
                "order by a.crop_cycle ";
        sqlParam.addValue("projectId", project);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }
}

