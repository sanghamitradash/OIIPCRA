package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.Activity;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.DenormalizedAchievement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class DashboardRepositoryImpl {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    public Integer getNoOfPaniPanchayatByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(distinct(pani_panchayat_id))  from oiipcra_oltp.pani_panchayat_details where tank_id=:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public List<String> getPaniPanchayatNameByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct(pani_panchayat_name) from oiipcra_oltp.pani_panchayat_m as ppm " +
                "left join oiipcra_oltp.pani_panchayat_details as ppd on ppd.pani_panchayat_id=ppm.id " +
                "where ppd.tank_id=:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, String.class);
    }

    public DprInformationDto getAyacutClassification(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT dpr.tank_id,dpr.observerd_high_flood_level, dpr.met_station_name, dpr.high_land_ha, dpr.medium_land_ha, dpr.low_land_ha " +
                " FROM oiipcra_oltp.dpr_information as dpr where dpr.tank_id=:tankId ";

        sqlParam.addValue("tankId", tankId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(DprInformationDto.class));
        } catch (Exception e) {
            return null;
        }

    }

    public Double getEstimateCost(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select round(sum(estimated_amount)/10000000,2) from oiipcra_oltp.activity_estimate_mapping as estimate " +
                " left join oiipcra_oltp.activity_estimate_tank_mapping as tank on tank.estimate_id=estimate.id where tank_id=:tankId";

        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Integer getFinancialYear(String date) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select id from oiipcra_oltp.fin_year_m where  date(now()) between start_date and end_date";

        // sqlParam.addValue("date", date);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Double getExpenditureByPreviousMonth(Integer year, Integer month, Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select round(sum(value)/10000000,2) from oiipcra_oltp.expenditure_data as ed " +
                " left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id " +
                " where tank_id=:tankId and finyr_id <=:year and month_id<=:month ";

        sqlParam.addValue("year", year);
        sqlParam.addValue("month", month);
        sqlParam.addValue("tankId", tankId);

        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Double getExpenditureThisMonth(Integer year, Integer month, Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select round(sum(value)/10000000,2) from oiipcra_oltp.expenditure_data as ed " +
                " left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id " +
                " where tank_id=:tankId and finyr_id =:year and month_id=:month ";

        sqlParam.addValue("year", year);
        sqlParam.addValue("month", month);
        sqlParam.addValue("tankId", tankId);

        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public List<String> getVillageNameByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select distinct village_name from oiipcra_oltp.tank_m_id where tank_id=:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForList(qry, sqlParam, String.class);
    }

    public String getTankName(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select name_of_the_m_i_p from oiipcra_oltp.tank_m_id where id=:tankId ";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    public List<ValueDto> getCountType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT type as typeName ,count(*) as value from oiipcra_oltp.master_data_538 GROUP by type ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ValueDto.class));
    }

    public List<CivilWorkDto> getDivisionWiseTypeCount(Integer typeId, String typeName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = " select  " + typeId + " as typeId, md.type as typeName, tm.mi_division_name as divisionName, tm.mi_division_id as divisionId, count(md.type) as value from oiipcra_oltp.master_data_538 as md " +
//                " join oiipcra_oltp.tank_m_id as tm ON md.project_id = tm.project_id " +
//                "WHERE md.type =\'" + typeName + "\' " +
//                "GROUP by tm.mi_division_id ,md.type, tm.mi_division_name ORDER by tm.mi_division_id ";


        String qry = "select mi_division_name as divisionName, coalesce(ls.value, 0 ) as value, mi_division_id as divisionId, " + typeId + " as typeId,\'" + typeName + "\' as typeName " +
                "from oiipcra_oltp.mi_division_m mdm " +
                "left join " +
                "( " +
                "SELECT tm.mi_division_name as divisionName, tm.mi_division_id as divisionId, count(md.type) as value  " +
                "FROM  oiipcra_oltp.tank_m_id as tm " +
                "left join oiipcra_oltp.master_data_538 as md ON md.project_id = tm.project_id WHERE md.type =\'" + typeName + "\' " +
                "group by tm.mi_division_name,divisionId " +
                ") as ls on ls.divisionId = mdm.mi_division_id order by mi_division_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));

    }
//    public List<CivilWorkDto> getDivisionWiseTypeCountDW(int typeId, String typeName) {
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = " select  "+typeId+" as typeId, md.type as typeName, tm.mi_division_name as divisionName, tm.mi_division_id as divisionId, count(md.type) as value from oiipcra_oltp.master_data_538 as md  " +
//                " join oiipcra_oltp.tank_m_id as tm ON md.project_id = tm.project_id " +
//                " WHERE md.type =\'" + typeName + "\' " +
//                " GROUP by tm.mi_division_id ,md.type, tm.mi_division_name ORDER by tm.mi_division_id ";
//        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
//    }

    public List<CivilWorkDto> getCivilWorkStatus(Integer typeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT count(*) as value, mi_division_name as divisionName, mi_division_id as divisionId FROM oiipcra_oltp.tank_m_id group by  mi_division_name, mi_division_id  ";
        sqlParam.addValue("typeId", typeId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
    }

    public List<CivilWorkDto> getCountValue(Integer typeId, String typeName, Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        select mi_division_id,count(*) as value  FROM oiipcra_oltp.tank_m_id GROUP by mi_division_id
        // String qry = "select mi_division_id as divisionId,mi_division_name as divisionName, count(*) as value, 2 as type_id,'TOTAL NO OF TANKS' as  type_name FROM oiipcra_oltp.tank_m_id GROUP by mi_division_id,mi_division_name ";
//        sqlParam.addValue("divisionId",divisionId);
        String yearFilter = "";
        if (yearId > 0) {
            yearFilter += "WHERE finyr_id=" + yearId + "";
        }
        String qry = "";

        if (typeId == 2) {
            //TOTAL NO OF TANKS
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";

            qry += "select distinct mi_division_id as divisionId,mi_division_name as divisionName, count(*) as value " +
                    " FROM oiipcra_oltp.tank_m_id GROUP by mi_division_id,mi_division_name ";

        }
        if (typeId == 3) {
            //CCA IN HA
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += "select  tm.mi_division_id as divisionId,tm.mi_division_name as divisionName , cast(sum(cab.area_ha/10) as decimal(10,2)) as value from oiipcra_oltp.catchment_area_boundary as cab  \n" +
                    "left join oiipcra_oltp.tank_m_id as tm on tm.project_id=cab.projectid GROUP by tm.mi_division_id,tm.mi_division_name  ";
        }
        if (typeId == 4) {
            //WORKS TAKEN UP
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += "select distinct count(tm.id) as value ,tm.mi_division_id as divisionId " +
                    " from oiipcra_oltp.tank_progress_status as tps " +
                    " left join oiipcra_oltp.tank_progress as tp on tp.progress_status=tps.id " +
                    " left join oiipcra_oltp.tank_m_id as tm on tm.project_id=tp.tank_id GROUP by mi_division_id ";
        }
        if (typeId == 5) {
            //CONTRACT AMOUNT

            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += " SELECT md.mi_division_id as divisionId, md.mi_division_name as divisionName,  " +
                    " coalesce(sum(ls2.valus),0) as value  FROM oiipcra_oltp.mi_division_m md  " +
                    " LEFT JOIN ( " +
                    " SELECT cmp.contract_id, cmp.division_id   " +
                    " FROM oiipcra_oltp.contract_m  as cm   " +
                    " JOIN oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id " +
                    " group by cmp.contract_id,cmp.division_id )ls1 ON md.mi_division_id = ls1.division_id " +
                    " LEFT JOIN " +
                    " (  " +
                    " select id, round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as valus from oiipcra_oltp.contract_m " + yearFilter + " group by id " +
                    "    )ls2 ON ls1.contract_id = ls2.id   GROUP BY md.mi_division_id, md.mi_division_name order by mi_division_name";
        }
        if (typeId == 6) {
            //WORKS IN TANK COMPLETED
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += " SELECT tm.mi_division_id as divisionId, count(*) as value  " +
                    " FROM oiipcra_oltp.tank_progress AS tp  " +
                    " left join oiipcra_oltp.tank_m_id  as tm on tp.tank_id=tm.project_id  " +
                    " WHERE tp.progress_status= 2  GROUP by mi_division_id,mi_division_name";
        }
        if (typeId == 7) {
            //Expenditure
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += " select  tm.mi_division_id as divisionId,tm.mi_division_name as divisionName ,round(sum(value)/10000000,2) as value from oiipcra_oltp.expenditure_data  as ed " +
                    " left join oiipcra_oltp.expenditure_mapping as em on ed.id=em.expenditure_id " +
                    " left join oiipcra_oltp.tank_m_id as tm on tm.id=em.tank_id " + yearFilter + " GROUP by tm.mi_division_id,tm.mi_division_name";
        }
        if (typeId == 8) {
            //BALANCE NO OF TANK ONGOING
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += " select  tm.mi_division_id as divisionId,tm.mi_division_name as divisionName ,round(sum(value)/10000000,2) as value from oiipcra_oltp.expenditure_data  as ed " +
                    " left join oiipcra_oltp.expenditure_mapping as em on ed.id=em.expenditure_id " +
                    " left join oiipcra_oltp.tank_m_id as tm on tm.id=em.tank_id GROUP by tm.mi_division_id,tm.mi_division_name";
        }

        if (typeId == 10) {
            //Dropped
            qry += "select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                    " from oiipcra_oltp.mi_division_m md " +
                    "  left join " +
                    "  ( ";
            qry += "SELECT mi_division_id as divisionId,mi_division_name as divisionName, count(*) as value FROM oiipcra_oltp.tank_m_id WHERE tank_m_id.tank_progress_status =4\n" +
                    "GROUP by mi_division_id,mi_division_name ";

        }
        qry += " ) ls " +
                " on ls.divisionId = md.mi_division_id ORDER by mi_division_name ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
//        return namedJdbc.queryForObject(qry,sqlParam,CivilWorkDto.class);
    }

    public List<CivilWorkDto> getAllDivision() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT distinct mi_division_id as divisionId,mi_division_name as divisionName  FROM oiipcra_oltp.tank_m_id group by mi_division_id,mi_division_name order by mi_division_name;";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
    }

    public Integer getCountValueOfWorksTakenUp(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct count(tm.id) from oiipcra_oltp.tank_progress_status as tps " +
                "left join oiipcra_oltp.tank_progress as tp on tp.progress_status=tps.id " +
                "left join oiipcra_oltp.tank_m_id as tm on tm.project_id=tp.tank_id where tm.mi_division_id=:divisionId ";
        sqlParam.addValue("divisionId", divisionId);

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Double getCountValueOfContractAmount(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) from oiipcra_oltp.contract_m " +
                " where id in(select distinct contract_id from oiipcra_oltp.contract_mapping where division_id=:divisionId)  ";
        sqlParam.addValue("divisionId", divisionId);
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        } else {
            return namedJdbc.queryForObject(qry, sqlParam, Double.class);
        }


    }

    public Integer getCountValueOfWorksInTankCompleted(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct count(tps.id) from oiipcra_oltp.tank_m_id as tm " +
                "left join oiipcra_oltp.tank_progress as tp on tp.tank_id=tm.id " +
                "left join oiipcra_oltp.tank_progress_status as tps on tps.id=tp.progress_status where tm.mi_division_id=:divisionId ";
        sqlParam.addValue("divisionId", divisionId);

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Double getCountValueOfUptodateExpd(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct sum(ed.id) from oiipcra_oltp.expenditure_data as ed " +
                "     left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id " +
                "        left join oiipcra_oltp.tank_m_id as tm on tm.id=em.tank_id where tm.mi_division_id=:divisionId ";
        sqlParam.addValue("divisionId", divisionId);

        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        } else {
            return namedJdbc.queryForObject(qry, sqlParam, Double.class);
        }

    }

    public Integer getCountValueOfCcaInHa(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select distinct cast(sum(area_ha/10) as decimal(10,2)) from oiipcra_oltp.catchment_area_boundary as cab " +
                "left join oiipcra_oltp.tank_m_id as tm on tm.project_id=cab.projectid  where tm.mi_division_id=:divisionId ";
        sqlParam.addValue("divisionId", divisionId);

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public List<CivilWorkDto> getCountBalTankOngoing(Integer typeId, String typeName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select mi_division_name as divisionName,mi_division_id as divisionId, (coalesce(ls.value, 0 ))-(coalesce(ls1.value, 0 ) ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                "from oiipcra_oltp.mi_division_m md " +
                "left join " +
                "( " +
                " select distinct count(tm.id) as value ,tm.mi_division_id as divisionId " +
                " from oiipcra_oltp.tank_progress_status as tps  " +
                " left join oiipcra_oltp.tank_progress as tp on tp.progress_status=tps.id  " +
                " left join oiipcra_oltp.tank_m_id as tm on tm.project_id=tp.tank_id GROUP by mi_division_id " +

                ") ls " +
                "on ls.divisionId = md.mi_division_id " +
                "left JOIN " +
                "( " +
                " SELECT tm.mi_division_id as divisionId, count(*) as value  " +
                " FROM oiipcra_oltp.tank_progress AS tp  " +
                " left join oiipcra_oltp.tank_m_id  as tm on tp.tank_id=tm.project_id  " +
                " WHERE tp.progress_status= 2  GROUP by mi_division_id,mi_division_name " +
                ") ls1 " +
                "on ls1.divisionId = md.mi_division_id ORDER by mi_division_name";


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
    }

    public Integer getCountValueTankDropped(Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(*) FROM oiipcra_oltp.tank_m_id WHERE tank_m_id.tank_progress_status =4 and  mi_division_id=:divisionId";
        sqlParam.addValue("divisionId", divisionId);

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Double getStateWiseValue(Integer typeId, Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String yearFilter = "";
        if (yearId > 0) {
            yearFilter = " and finyr_id=:yearId";
        }
        String qry = "";
        if (typeId == 2) {
            qry += "select count(*) as value  FROM oiipcra_oltp.tank_m_id where is_active='t'  ";
        }
        if (typeId == 3) {
            qry += " SELECT cast(sum(area_ha/10) as decimal(10,2)) FROM oiipcra_oltp.catchment_area_boundary   ";
        }

        if (typeId == 4) {
            qry += "select distinct count(tm.id) from oiipcra_oltp.tank_progress_status as tps " +
                    " left join oiipcra_oltp.tank_progress as tp on tp.progress_status=tps.id " +
                    " left join oiipcra_oltp.tank_m_id as tm on tm.project_id=tp.tank_id ";
        }
        if (typeId == 5) {
            qry += "select round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) from oiipcra_oltp.contract_m  " +
                    " where id in(select distinct contract_id from oiipcra_oltp.contract_mapping) " + yearFilter + " ";


        }
        if (typeId == 6) {
            qry += "SELECT count(*) FROM oiipcra_oltp.tank_progress WHERE is_active='t' and  progress_status=2; ";
        }
        if (typeId == 7) {
            qry += "SELECT round(sum(value)/10000000,2) FROM oiipcra_oltp.expenditure_data where true " + yearFilter + " ";

        }
        if (typeId == 10) {
            qry += "SELECT count(*) FROM oiipcra_oltp.tank_m_id WHERE tank_m_id.tank_progress_status =4; ";
        }
        if (typeId == 12) {
            qry += "select round(sum(estimated_amount)/10000000,2) from oiipcra_oltp.activity_estimate_mapping where id in( " +
                    " select distinct estimate_id from oiipcra_oltp.activity_estimate_tank_mapping where tank_id in(select id from oiipcra_oltp.tank_m_id )) ";
        }


        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Integer getCountTankDropped(Integer divisionId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(*) FROM oiipcra_oltp.tank_progress WHERE is_active='t' and  progress_status=1 and ; ";
        sqlParam.addValue("divisionId", divisionId);

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public List<CivilWorkDto> getAppEst(Integer typeId, String typeName, Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select mi_division_name as divisionName,mi_division_id as divisionId, coalesce(ls.value, 0 ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name  " +
                "from oiipcra_oltp.mi_division_m md " +
                "left join " +
                "( " +
                "select  mi_division_id as divisionId,mi_division_name as divisionName,round(sum(estimated_amount)/10000000,2) as value from oiipcra_oltp.activity_estimate_mapping as aem " +
                " left join oiipcra_oltp.activity_estimate_tank_mapping AS aet on aet.estimate_id = aem.id " +
                "LEFT join oiipcra_oltp.tank_m_id as tm on tm.id = aet.tank_id GROUP by tm.mi_division_id,tm.mi_division_name " +

                ") ls " +
                "on ls.divisionId = md.mi_division_id order by mi_division_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
    }

    public List<CivilWorkDto> getBalanceContractValue(Integer typeId, String typeName, Integer yearId) {
        String yearFilter = "";
        if (yearId > 0) {
            yearFilter += "WHERE finyr_id=" + yearId + "";
        }
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select mi_division_name as divisionName,mi_division_id as divisionId, (coalesce(ls.value, 0 ))-(coalesce(ls1.value, 0 ) ) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                " from oiipcra_oltp.mi_division_m md " +
                " left join " +
                "( " +
                "SELECT md.mi_division_id as divisionId, md.mi_division_name as divisionName, coalesce(sum(ls2.valus),0) as value  " +
                "FROM oiipcra_oltp.mi_division_m md  " +
                "LEFT JOIN " +
                "( " +
                " SELECT cmp.contract_id, cmp.division_id " +
                " FROM oiipcra_oltp.contract_m  as cm " +
                " JOIN oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id " +
                " group by cmp.contract_id,cmp.division_id " +
                ")ls1 ON md.mi_division_id = ls1.division_id " +
                "LEFT JOIN " +
                "( " +
                " select id, round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as valus from oiipcra_oltp.contract_m " + yearFilter + " group by id\n" +
                ")ls2 ON ls1.contract_id = ls2.id " +
                "  GROUP BY md.mi_division_id, md.mi_division_name" +
                " ) ls " +
                " on ls.divisionId = md.mi_division_id " +
                " left JOIN " +
                " ( " +
                " select  tm.mi_division_id as divisionId,tm.mi_division_name as divisionName ,round(sum(value)/10000000,2) as value from oiipcra_oltp.expenditure_data  as ed " +
                "    left join oiipcra_oltp.expenditure_mapping as em on ed.id=em.expenditure_id " +
                "    left join oiipcra_oltp.tank_m_id as tm on tm.id=em.tank_id " + yearFilter + " GROUP by tm.mi_division_id,tm.mi_division_name " +
                " ) ls1 " +
                " on ls1.divisionId = md.mi_division_id ORDER by mi_division_name";


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
    }

    public List<CivilWorkDto> getNoOfTankTakenUp(Integer typeId, String typeName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select mi_division_name as divisionName,mi_division_id as divisionId, (coalesce(ls.value, 0 ))-(coalesce(ls1.value, 0 ) )-(coalesce(ls2.value, 0 )) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name  " +
                "from oiipcra_oltp.mi_division_m md " +
                "left join " +
                "( " +
                "    select distinct mi_division_id as divisionId,mi_division_name as divisionName, count(*) as value " +
                "    FROM oiipcra_oltp.tank_m_id GROUP by mi_division_id,mi_division_name " +
                ") ls " +
                "on ls.divisionId = md.mi_division_id " +
                "left JOIN " +
                "( " +
                " select distinct count(tm.id) as value ,tm.mi_division_id as divisionId " +
                " from oiipcra_oltp.tank_progress_status as tps  " +
                " left join oiipcra_oltp.tank_progress as tp on tp.progress_status=tps.id  " +
                " left join oiipcra_oltp.tank_m_id as tm on tm.project_id=tp.tank_id GROUP by mi_division_id " +
                ") ls1 " +
                "on ls1.divisionId = md.mi_division_id " +
                "left JOIN " +
                "( " +
                " SELECT mi_division_id as divisionId,mi_division_name as divisionName, count(*) as value FROM oiipcra_oltp.tank_m_id WHERE tank_m_id.tank_progress_status =4 " +
                " GROUP by mi_division_id,mi_division_name " +
                ") ls2 " +
                "on ls2.divisionId = md.mi_division_id " +
                "ORDER by mi_division_name ";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));
    }

    public List<CivilWorkDto> getBalanceWorkForContract(Integer typeId, String typeName, Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String yearFilter = "";
//        if (yearId>0){
//            yearFilter +="WHERE finyr_id=" + yearId + "";
//        }
        String qry = "select mi_division_name as divisionName,mi_division_id as divisionId, (coalesce(ls.value, 0 ))- (coalesce(ls1.value, 0 )) as value," + typeId + " as type_id,\'" + typeName + "\' as  type_name " +
                "from oiipcra_oltp.mi_division_m md " +
                "left join " +
                " (" +

                "  select  mi_division_id as divisionId,mi_division_name as divisionName,round(sum(estimated_amount)/10000000,2) as value from oiipcra_oltp.activity_estimate_mapping as aem " +
                " left join oiipcra_oltp.activity_estimate_tank_mapping AS aet on aet.estimate_id = aem.id " +
                " LEFT join oiipcra_oltp.tank_m_id as tm on tm.id = aet.tank_id GROUP by tm.mi_division_id,tm.mi_division_name " +

                ") ls on ls.divisionId = md.mi_division_id " +
                "LEFT join  " +
                "( " +
                " SELECT md.mi_division_id as divisionId, md.mi_division_name as divisionName, " +
                " coalesce(sum(ls2.valus),0) as value  FROM oiipcra_oltp.mi_division_m md " +
                " LEFT JOIN ( " +
                " SELECT cmp.contract_id, cmp.division_id   " +
                " FROM oiipcra_oltp.contract_m  as cm   " +
                " JOIN oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id  " +
                " group by cmp.contract_id,cmp.division_id )ls1 ON md.mi_division_id = ls1.division_id " +
                " LEFT JOIN  " +
                " (   " +
                " select id, round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as valus from oiipcra_oltp.contract_m " + yearFilter + " group by id " +
                "    )ls2 ON ls1.contract_id = ls2.id   GROUP BY md.mi_division_id, md.mi_division_name " +
                ") as ls1 on ls1.divisionId = md.mi_division_id ORDER by mi_division_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkDto.class));

    }


    public Double getEstimateCostByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select round(sum(estimated_amount)/10000000,2) as estimateAmount from oiipcra_oltp.activity_estimate_mapping\n" +
                "where id in( select distinct estimate_id from oiipcra_oltp.activity_estimate_tank_mapping where tank_id =:tankId)";
        sqlParam.addValue("tankId", tankId);
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Double getExpenditureByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "select round(sum(value)/10000000,2) as expenditureAmount from oiipcra_oltp.expenditure_data\n" +
                "where id in(select distinct expenditure_id \n" +
                "\tfrom oiipcra_oltp.expenditure_mapping where tank_id =:tankId)";
        sqlParam.addValue("tankId", tankId);
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Double getContractAmountByTankId(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "\tselect round(sum(contract_amount+(contract_amount*(gst/10000000))),2) as contractAmount from oiipcra_oltp.contract_m\n" +
                "where id in(select distinct contract_id from oiipcra_oltp.contract_mapping where tank_id =:tankId)";
        sqlParam.addValue("tankId", tankId);
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public BigDecimal getTankWiseCatchmentArea(Integer tankId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "SELECT cast(sum(area_ha/10) as decimal(10,2))" +
                " FROM oiipcra_oltp.catchment_area_boundary WHERE projectid=:tankId;";
        sqlParam.addValue("tankId", tankId);

//        try {
//            if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
//                return 0.0;
//            }
//        } catch (Exception e) {
//            return namedJdbc.queryForObject(qry, sqlParam, Double.class);
//
//        }
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

    public List<CropCycleAyacutDto> getTankWiseAyaCut(Integer projectId) {
        String ProjectId = String.valueOf(projectId);
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT round(sum(area),3) as area,crop_cycle as year " +
                " FROM oiipcra_oltp.crop_cycle_ayacut where project_id=:ProjectId GROUP by crop_cycle order by crop_cycle;";
        sqlParam.addValue("ProjectId", ProjectId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }

    public Double getWSA(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        WaterSpreadDto wp = getLastMonthAndYear();
        String extraSql = "";
        if (projectId > 0) {
            extraSql = "and project_id::integer in (:projectId)";
        }

//        String qry = "select round(sum(area),3)/(select count(*) from oiipcra_oltp.tank_m_id where true " + extraSql + " ) from oiipcra_oltp.oiipcra_water_spread where true  ";
//
//        if (projectId > 0) {
//            qry += "and  month_id in (" + wp.getMonthId() + ") and \"year\" in ('" + wp.getYear() + "') and project_id::integer=:projectId ";
//            sqlParam.addValue("projectId", projectId);
//        }


        String qry = "select round(sum(b.iavg)/(select count(*) from oiipcra_oltp.tank_m_id where true " + extraSql + " ),3) as a from\n" +
                "(select avg(area) as iavg,project_id from oiipcra_oltp.oiipcra_water_spread group by project_id order by project_id)\n" +
                "as b where true  ";

        if (projectId > 0) {
            qry += "and b.project_id::integer=:projectId ";
            sqlParam.addValue("projectId", projectId);
        }

        if (!divisionName.isEmpty()) {
            qry += "and project_id::integer " +
                    "in (select project_id FROM oiipcra_oltp.tank_m_id where mi_division_name=:divisionName)";
            sqlParam.addValue("divisionName", divisionName);
        }
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);

    }

    public WaterSpreadDto getLastMonthAndYear() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select DISTINCT month_id as monthId,year,month from oiipcra_oltp.oiipcra_water_spread order by \"year\" DESC,month_id desc limit 1 ";
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }
    public WaterSpreadDto getAyacutWiseCropNonCropMonthYear() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select month as cropNonCropYearMonth,\n" +
                "(case when month like('crop_noncrop_01%') Then 'JANUARY' when month like('crop_noncrop_02%') Then 'FEBRUARY' \n" +
                " when month like('crop_noncrop_03%') Then 'MARCH' when month like('crop_noncrop_04%') Then 'APRIL' \n" +
                " when month like('crop_noncrop_05%') Then 'MAY' when month like('crop_noncrop_06%') Then 'JUNE' \n" +
                " when month like('crop_noncrop_07%') Then 'JULY' when month like('crop_noncrop_08%') Then 'AUGUST' \n" +
                " when month like('crop_noncrop_09%') Then 'SEPTEMBER' when month like('crop_noncrop_10%') Then 'OCTOBER' \n" +
                " when month like('crop_noncrop_11%') Then 'NOVEMBER' when month like('crop_noncrop_12%') Then 'DECEMBER' else 'No Year' end)\n" +
                " as month ,SPLIT_PART(month,'_',4) as year\n" +
                "from oiipcra_oltp.oiipcra_ayacutwise_crop_noncrop \n" +
                " order  by SPLIT_PART(month,'_',4) desc, SPLIT_PART(month,'_',3) desc limit 1";
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }
    public WaterSpreadDto getCropCycleAyacutMonthYear() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select crop_cycle as year from oiipcra_oltp.crop_cycle_ayacut order by crop_cycle desc limit 1 ";
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }

    public Double getAyaCut(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " SELECT round(sum(area),3) " +
                " FROM oiipcra_oltp.ayacut_area_boundary where is_active=true ";

        if (projectId > 0) {
            qry += " and project_id::integer=:projectId ";

            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and project_id::integer " +
                    "in (select project_id FROM oiipcra_oltp.tank_m_id where mi_division_name=:divisionName)";
            sqlParam.addValue("divisionName", divisionName);
        }
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public BigDecimal getTankWiseAyacutArea(Integer projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String tankId = String.valueOf(projectId);

        String qry = "SELECT area FROM oiipcra_oltp.ayacut_area_boundary WHERE project_id=:projectId";
        sqlParam.addValue("projectId", tankId);

//        if (namedJdbc.queryForObject(qry, sqlParam, Double.class)==null){
//            return 0.0;
//        }
//        return namedJdbc.queryForObject(qry, sqlParam, Double.class);

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


    public Integer workAwarded(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "SELECT count(*)  " +
                " FROM oiipcra_oltp.tank_progress where progress_status in (select DISTINCT progress_status from oiipcra_oltp.tank_progress)  ";

        if (projectId > 0) {
            qry += " and tank_id =:projectId  ";
            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and tank_id in (select project_id from oiipcra_oltp.tank_m_id where mi_division_name =:divisionName) ";
            sqlParam.addValue("divisionName", divisionName);
        }
        if (namedJdbc.queryForObject(qry, sqlParam, Integer.class) == null) {
            return 0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }


    public List<WaterSpreadDto> getWaterSpreadByProjectId(Integer projectId, String yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String tankId = String.valueOf(projectId);
        String qry = " SELECT distinct month_id,month as month,sum(area) over(partition by month_id ) " +
                " from oiipcra_oltp.oiipcra_water_spread where year=:yearId and project_id =:projectId ORDER BY month_id desc  ";
        sqlParam.addValue("projectId", tankId);
        sqlParam.addValue("yearId", yearId);
        //  sqlParam.addValue("size",size);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(WaterSpreadDto.class));
    }

    public Double getWaterSpreadSumOfMonth(Integer projectId, String yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String tankId = String.valueOf(projectId);
        String qry = " SELECT sum(area)  " +
                "from oiipcra_oltp.oiipcra_water_spread where year=:yearId and project_id =:projectId  ";
        sqlParam.addValue("projectId", tankId);
        sqlParam.addValue("yearId", yearId);
        if (namedJdbc.queryForObject(qry, sqlParam, Double.class) == null) {
            return 0.0;
        }
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public List<ValueDto> tanksByCategory(Integer projectId, String divisionName) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select type as typeName ,count(*) as value FROM oiipcra_oltp.tank_m_id  WHERE  true ";
        if (projectId > 0) {
            qry += " and  project_id=:projectId   ";
            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and  mi_division_name=:divisionName   ";
            sqlParam.addValue("divisionName", divisionName);

        }

        qry += "   GROUP by type";
        //
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ValueDto.class));
    }


    public List<String> getDistinctYearBYProjectId(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);

        String qry = "SELECT distinct crop_cycle from oiipcra_oltp.crop_cycle_ayacut where true ";
        if (projectId > 0) {
            qry += "and project_id=:projectId ";
            sqlParam.addValue("projectId", project);
        }
        if (!divisionName.isEmpty()) {
            qry += " and project_id::integer in(SELECT project_id from  oiipcra_oltp.tank_m_id where mi_division_name=:divisionName) ";
            sqlParam.addValue("divisionName", divisionName);


        }


        return namedJdbc.queryForList(qry, sqlParam, String.class);
    }

    public List<CropCycleAyacutDto> getCropCycleIntensity(Integer projectId, String year, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project = String.valueOf(projectId);
        String qry = "select distinct gridcode.type_id as gridCode, a.crop_cycle as year,coalesce(b.area,0.00) as area," +
                "(case when a.gridcode=10 Then 'SINGLE CROP' when a.gridcode=20 Then 'DOUBLE CROP'  when a.gridcode=30 Then 'TRIPLE CROP' else 'No Croping' end) as gridType " +
                "from oiipcra_oltp.crop_cycle_master as gridcode " +
                "left join oiipcra_oltp.crop_cycle_ayacut as a on a.gridcode=gridcode.type_id " +
                "left join ( select distinct crop_cycle,gridcode,sum(area) over(partition by gridcode,crop_cycle) as area " +
                "from oiipcra_oltp.crop_cycle_ayacut where true  ";
        if (projectId > 0) {
            qry += " and project_id=:projectId ";
            sqlParam.addValue("projectId", project);
        }
        if (!divisionName.isEmpty()) {
            qry += " and  project_id::integer in (SELECT project_id from  oiipcra_oltp.tank_m_id where mi_division_name=:divisionName) ";
            sqlParam.addValue("divisionName", divisionName);
        }


        qry += ") as b on a.crop_cycle=b.crop_cycle and gridcode.type_id=b.gridcode " +
                "where a.crop_cycle=:year " +
                "order by a.crop_cycle,gridcode.type_id ";

        sqlParam.addValue("year", year);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }

    public List<DivisionWiseExpenditureDto> commulativeExpenditure(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select tm.mi_division_name as divisionName,tm.mi_division_id as divisionId, round(sum(value)/10000000,2) as value from oiipcra_oltp.expenditure_data as ed  \n" +
                " left join oiipcra_oltp.expenditure_mapping as em on ed.id=em.expenditure_id \n" +
                " left join oiipcra_oltp.tank_m_id as tm on tm.id=em.tank_id where true  ";
        if (projectId > 0) {
            qry += " and  project_id=:projectId   ";
            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and mi_division_name=:divisionName  ";
            sqlParam.addValue("divisionName", divisionName);

        }

        qry += " GROUP by tm.mi_division_name,tm.mi_division_id";
        //
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DivisionWiseExpenditureDto.class));
    }

    public DivisionWiseExpenditureDto totalCommulativeExpenditure(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select  round(sum(value)/10000000,2) as value from oiipcra_oltp.expenditure_data as ed   " +
                " left join oiipcra_oltp.expenditure_mapping as em on ed.id=em.expenditure_id  " +
                " left join oiipcra_oltp.tank_m_id as tm on tm.id=em.tank_id where true ";
        if (projectId > 0) {
            qry += " and   project_id=:projectId   ";
            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and  mi_division_name=:divisionName  ";
            sqlParam.addValue("divisionName", divisionName);

        }


        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(DivisionWiseExpenditureDto.class));
    }

    public List<ComponentDto> getComponents(Integer userId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "SELECT id as componentId,name as componentName,parent_id as parentId" +
                " FROM oiipcra_oltp.master_head; ";


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));


    }


    public List<ComponentDto> getsubActivityDetails(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select mhd.id as componentId ,mhd.parent_id as parentId,mhd.master_head_id as masterHeadId,mhd.name as componentName \n" +
                "from oiipcra_oltp.master_head_details mhd " +
                " where is_terminal = true   ";
        if (componentId != null) {
            qry += "  and parent_id=:componentId ";
        }
        qry += "   order by mhd.id";
        sqlParam.addValue("componentId", componentId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));
    }

    public List<ComponentDto> getSubcomponentDetails(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select mhd.id as componentId ,mhd.name as componentName,mhd.parent_id as parentId,mhd.master_head_id as masterHeadId" +
                " from oiipcra_oltp.master_head_details mhd  " +

                "  where mhd.master_head_id=2  ";
        if (componentId != null) {
            qry += "  and parent_id=:componentId ";
        }
        qry += "   order by mhd.id";
        sqlParam.addValue("componentId", componentId);


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

    }

    public List<ComponentDto> getActivityDetails(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select mhd.id as componentId ,mhd.name as componentName,mhd.parent_id as parentId,mhd.master_head_id as masterHeadId" +
                " from oiipcra_oltp.master_head_details mhd " +
                "  where mhd.master_head_id=3  ";
        if (componentId != null) {
            qry += "  and parent_id=:componentId ";
        }
        qry += "   order by mhd.id";
        sqlParam.addValue("componentId", componentId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));
    }

    public List<ComponentDto> getcompontDetails(List<Integer> activityIds) {


        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  select mhd.id as componentId ,mhd.parent_id as parentId,mhd.master_head_id as masterHeadId,mhd.name as componentName " +
                "from oiipcra_oltp.master_head_details mhd " +
                "  where mhd.master_head_id=1 ";

        if (activityIds != null) {
            qry += "and mhd.id in (:activityIds)";
            sqlParam.addValue("activityIds", activityIds);
        }
        qry += "order by mhd.id";


        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));
    }

    public List<Integer> getActivityByDeptId(Integer deptIdByUserId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT mhd.id FROM oiipcra_oltp.master_head_details AS mhd" +
                " LEFT JOIN oiipcra_oltp.master_head AS mh ON mhd.master_head_id=mh.id" +
                " LEFT JOIN oiipcra_oltp.activity_dept_mapping AS adm ON mhd.id=adm.activity_id" +
                " WHERE true " +
                " AND adm.dept_id =:deptIdByUserId ORDER BY mhd.id";

        sqlParam.addValue("deptIdByUserId", deptIdByUserId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(Integer.class));
    }

    public BigDecimal getValueOfComponents(List<Integer> terminalIds, Integer yearId, Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        if (!terminalIds.isEmpty()) {
            String qry = " SELECT round(sum(value)/10000000,2) FROM oiipcra_oltp.expenditure_data ed " +
                    " left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id where " +
                    "em.activity_id in (:terminalIds) and ed.is_active = true";

            if (yearId != null && yearId > 0) {
                qry += "AND ed.finyr_id=:yearId";
                sqlParam.addValue("yearId", yearId);
            }
            sqlParam.addValue("terminalIds", terminalIds);

            try {
                BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
                if (b == null) {
                    b = BigDecimal.valueOf(0.0);
                }
                return b;
            } catch (Exception e) {
                return BigDecimal.valueOf(0.00);
            }
        } else {
            return new BigDecimal("0.0");
        }
    }

    public PhysicalProgressValueDto getPhysicalProgressValue() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select * from " +
                "    (" +
                "        (  SELECT sum(total_length_of_canal_as_per_estimate) as totalLengthOfCanalAsPerEstimate\n" +
                " FROM oiipcra_oltp.physical_progress_planned  ) A " +
                "\n" +
                "    JOIN (SELECT sum(length_of_canal_improved) as lengthOfCanalImproved\n" +
                "\tFROM oiipcra_oltp.physical_progress_executed) B  ON 1=1\n" +
                "\n" +
                "    JOIN (SELECT sum(no_of_cd_structures_repared) as noOfCdStructuresRepared\n" +
                "\tFROM oiipcra_oltp.physical_progress_executed) C  ON 1=1\n" +
                "\t\tJOIN (SELECT sum(no_of_cd_structures_to_be_repared) as noOfCdStructuresToBeRepared\n" +
                "\tFROM oiipcra_oltp.physical_progress_planned) D  ON 1=1\n" +
                "\t\tJOIN (\tSELECT sum(total_length_of_cad) as totalLengthOfCad\n" +
                "\tFROM oiipcra_oltp.physical_progress_planned) E  ON 1=1\n" +
                "\t\tJOIN (\tSELECT sum(total_length_of_cad) as CadConstructed\n" +
                "\tFROM oiipcra_oltp.physical_progress_executed) F  ON 1=1\n" +
                "\t\tJOIN (\tSELECT sum(no_of_outlet_constructed) as noOfOutletConstructed\n" +
                "\tFROM oiipcra_oltp.physical_progress_executed) G  ON 1=1\n" +
                "\t\t\n" +
                "    ) ";


        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(PhysicalProgressValueDto.class));

    }

    public BigDecimal getValueOfComponentsCA(List<Integer> terminalIds, Integer yearId, Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        if (!terminalIds.isEmpty()) {
            String qry = "  select round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as value from " +
                    "oiipcra_oltp.contract_m" +
                    " where activity_id in (:terminalIds)";

            if (yearId != null && yearId > 0) {
                qry += "AND finyr_id=:yearId";
                sqlParam.addValue("yearId", yearId);
            }

            if (workTypeId != null && workTypeId > 0) {
                qry += "AND work_type_id=:workTypeId";
                sqlParam.addValue("workTypeId", workTypeId);
            }
            sqlParam.addValue("terminalIds", terminalIds);

            try {
                BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
                if (b == null) {
                    b = BigDecimal.valueOf(0.0);
                }
                return b;
            } catch (Exception e) {
                return BigDecimal.valueOf(0.00);
            }

        } else {
            return BigDecimal.valueOf(0.00);
        }


    }

    public BigDecimal getTotalLengthOfCanalAsPerEstimate(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {


        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(pl.total_length_of_canal_as_per_estimate) as totalLengthOfCanalAsPerEstimate " +
                "FROM oiipcra_oltp.physical_progress_planned as pl " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= pl.tank_m_id where pl.is_active=true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }
        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getLengthOfCanalImproved(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(length_of_canal_improved) as lengthOfCanalImproved  " +
                "FROM oiipcra_oltp.physical_progress_executed as lc  " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= lc.tank_m_id " +
                "where lc.is_active = true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }
        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getNoOfCdStructuresRepared(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(no_of_cd_structures_repared) as noOfCdStructuresRepared  " +
                "FROM oiipcra_oltp.physical_progress_executed as cd " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= cd.tank_m_id  " +
                "where cd.is_active = true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }

        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getNoOfCdStructuresToBeRepared(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(no_of_cd_structures_to_be_repared) as noOfCdStructuresToBeRepared  " +
                "FROM oiipcra_oltp.physical_progress_planned as pg  " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= pg.tank_m_id  " +
                "where pg.is_active = true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }

        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getTotalLengthOfCad(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(total_length_of_cad) as totalLengthOfCad  " +
                "FROM oiipcra_oltp.physical_progress_planned as py  " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= py.tank_m_id  " +
                "where py.is_active = true    ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }

        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getNoOfOutletConstructed(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(no_of_outlet_constructed) as noOfOutletConstructed  " +
                "FROM oiipcra_oltp.physical_progress_executed as con  " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= con.tank_m_id  " +
                "where con.is_active = true    ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }

        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getCadConstructed(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(total_length_of_cad) as CadConstructed  " +
                "FROM oiipcra_oltp.physical_progress_executed as cd  " +
                "Left join oiipcra_oltp.tank_m_id as tank on tank.id= cd.tank_m_id  " +
                "where cd.is_active = true    ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }

        if (divisionId > 0) {
            qry += "and tank.mi_division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
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

    public BigDecimal getTotalWaterSpreadArea(Integer projectId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        // String project = String.valueOf(projectId);
        WaterSpreadDto wp = getLastMonthAndYear();
        String qry = "select sum(area) from oiipcra_oltp.oiipcra_water_spread where true and  month_id in(" + wp.getMonthId() + ") and  \"year\" in('" + wp.getYear() + "') ";

        if (projectId > 0) {
            qry += "and project_id::integer=:projectId  ";
            sqlParam.addValue("projectId", projectId);

        }
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

    public BigDecimal getcontractAmount(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as value from oiipcra_oltp.contract_m\n" +
                "where id in(select distinct contract_id  " +
                "from oiipcra_oltp.contract_mapping where true ";

        if (projectId > 0) {
            qry += " and  tank_id in (select tank_id from oiipcra_oltp.tank_m_id where project_id =:projectId )   ";
            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and  division_id in (select distinct mi_division_id from oiipcra_oltp.tank_m_id where mi_division_name=:divisionName)  ";
            sqlParam.addValue("divisionName", divisionName);
        }
        qry += ")";
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

    public BigDecimal getcatchmentArea(Integer projectId, String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cast(sum(area_ha/10) as decimal(10,2)) as value FROM oiipcra_oltp.catchment_area_boundary cab \n" +
                "left join oiipcra_oltp.tank_m_id as tm on tm.project_id=cab.projectid where true   ";

        if (projectId > 0) {
            qry += " and  cab.projectid=:projectId   ";
            sqlParam.addValue("projectId", projectId);
        }
        if (!divisionName.isEmpty()) {
            qry += " and tm.mi_division_name =:divisionName  ";
            sqlParam.addValue("divisionName", divisionName);
        }
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

    public List<Integer> getProjectIdByDivisionName(String divisionName) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT  project_id FROM oiipcra_oltp.tank_m_id where mi_division_name =:divisionName   ";
        sqlParam.addValue("divisionName", divisionName);

        try {
            return namedJdbc.queryForList(qry, sqlParam, Integer.class);

        } catch (Exception e) {
            return null;
        }
    }

    public ComponentDto getDivisionWiseContingencyDetails(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct em.activity_id as parentId,round(((sum(value) over (partition by aem.division_id)/10000000)),2) as expenditureAmount   \n" +
                "from oiipcra_oltp.mi_division_m as div\n" +
                "left join oiipcra_oltp.expenditure_mapping as em on div.mi_division_id=em.division_id\n" +
                "left join oiipcra_oltp.expenditure_data as ed on em.expenditure_id=ed.id\n" +
                "where ed.type=4  and  em.division_id=:componentId ";
        sqlParam.addValue("componentId", componentId);
        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            ComponentDto componentDto = new ComponentDto();
            componentDto.setContingencyAmount(BigDecimal.valueOf(0.0));
            return componentDto;
        }


    }

    public List<ComponentDto> getDivisionWiseExpenditureDetailsOLD(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select distinct em.activity_id as parentId,div.mi_division_name as componentName,div.mi_division_id as divisionId, round(((sum(value) over (partition by aem.division_id)/10000000)),2) as expenditureAmount " +
                " from oiipcra_oltp.mi_division_m as div " +
                " left join oiipcra_oltp.activity_estimate_tank_mapping as aem on div.mi_division_id=aem.division_id " +
                " left join oiipcra_oltp.expenditure_mapping as em on aem.estimate_id=em.estimate_id " +
                " left join oiipcra_oltp.expenditure_data as ed on em.expenditure_id=ed.id where ed.type<>4 and  em.activity_id=:componentId ";
        sqlParam.addValue("componentId", componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }


    }

    public List<ComponentDto> getDivisionWiseExpenditureDetails(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " SELECT md.mi_division_id as divisionId ,ls1.activity_id as parentId , md.mi_division_name  as componentName, \n" +
                "  coalesce(sum(ls2.valus),0) as contractAmount,ls3.expenditureAmount,ls4.estimateAmount as estimatedAmount  FROM oiipcra_oltp.mi_division_m md\n" +
                "  LEFT JOIN  \n" +
                "  (\n" +
                "\t  SELECT cmp.contract_id, cmp.division_id ,cmp.activity_id  \n" +
                "\t  FROM oiipcra_oltp.contract_m  as cm   \n" +
                "\t  JOIN oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id  \n" +
                "\t  group by cmp.contract_id,cmp.division_id ,cmp.activity_id \n" +
                "  )ls1 ON md.mi_division_id = ls1.division_id  \n" +
                "  LEFT JOIN   \n" +
                "  (  \n" +
                "\t  select id, round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as valus from oiipcra_oltp.contract_m group by id     \n" +
                "  )ls2 ON ls1.contract_id = ls2.id\n" +
                "  left join \n" +
                "  (\n" +
                "   select distinct em.activity_id as parentId,div.mi_division_name as componentName,div.mi_division_id as divisionId, round(((sum(value) over (partition by aem.division_id)/10000000)),2) \n" +
                " as expenditureAmount\n" +
                " from oiipcra_oltp.mi_division_m as div \n" +
                " left join oiipcra_oltp.activity_estimate_tank_mapping as aem on div.mi_division_id=aem.division_id \n" +
                " left join oiipcra_oltp.expenditure_mapping as em on aem.estimate_id=em.estimate_id \n" +
                " left join oiipcra_oltp.expenditure_data as ed on em.expenditure_id=ed.id where ed.type<>4 \n" +
                "\t  \n" +
                "  ) as ls3 on md.mi_division_id = ls3.divisionId  \n" +
                "  \n" +
                "    left join \n" +
                "  (\n" +
                " select distinct actem.activity_id as parentId,div.mi_division_name as componentName,div.mi_division_id as divisionId, coalesce(round(((sum(estimated_amount) over (partition by aem.division_id)/10000000)),2),0.0)  \n" +
                " as estimateAmount \n" +
                " from oiipcra_oltp.mi_division_m as div \n" +
                "\t  left join oiipcra_oltp.activity_estimate_tank_mapping as aem on div.mi_division_id=aem.division_id \n" +
                "\t  left join oiipcra_oltp.activity_estimate_mapping as actem on aem.estimate_id=actem.id \n" +
                "   \n" +
                "\t  \n" +
                "  ) as ls4 on md.mi_division_id = ls4.divisionId  \n" +
                "  where ls1.activity_id=:componentId \n" +
                "  GROUP BY md.mi_division_id, md.mi_division_name,ls1.activity_id ,ls3.expenditureAmount,ls4.estimateAmount order by mi_division_name ";
        sqlParam.addValue("componentId", componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }


    }

    public List<ComponentDto> getAllDivisionList(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT distinct mi_division_id as componentId,mi_division_name as componentName  " +
                "FROM oiipcra_oltp.tank_m_id group by mi_division_id,mi_division_name order by mi_division_name;";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));
    }

    public ComponentDto getValueOfComponentsStatus(List<Integer> terminalIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        if (!terminalIds.isEmpty()) {
            String qry = " select  distinct cstatus.name as name,count(cm.id) as value" +
                    "from oiipcra_oltp.contract_status as cstatus " +
                    "left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id " +
                    "left join oiipcra_oltp.contract_mapping as cmap on cmap.contract_id=cm.id" +
                    "where cmap.activity_id in (:terminalIds)" +
                    "group by cstatus.id,cstatus.name";
            sqlParam.addValue("terminalIds", terminalIds);
            try {
                return namedJdbc.queryForObject(qry, sqlParam, ComponentDto.class);
            } catch (Exception e) {
                return new ComponentDto();
            }
        } else {
            return new ComponentDto();
        }
    }

    public List<StatusDto> getcontractStatusComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cs.id as  statusId,cs.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.contract_status as cs \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(cm.id) as value\n" +
                "from oiipcra_oltp.contract_status as cstatus\n" +
                "left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id\n" +
                "where cm.activity_id in (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=1) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "           )\t\t \n" +
                "group by cstatus.id,cstatus.name\n" +
                "\n" +
                "      ) as le on le.id=cs.id order by cs.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public List<StatusDto> getcontractStatusSubComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cs.id as  statusId,cs.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.contract_status as cs \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(cm.id) as value\n" +
                "from oiipcra_oltp.contract_status as cstatus\n" +
                "left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id\n" +
                "where cm.activity_id in (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=2) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "           )\t\t \n" +
                "group by cstatus.id,cstatus.name\n" +
                "\n" +
                "      ) as le on le.id=cs.id order by cs.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public List<StatusDto> getcontractStatusActivityComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cs.id as  statusId,cs.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.contract_status as cs \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(cm.id) as value\n" +
                "from oiipcra_oltp.contract_status as cstatus\n" +
                "left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id\n" +
                "where cm.activity_id in (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=3) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "           )\t\t \n" +
                "group by cstatus.id,cstatus.name\n" +
                "\n" +
                "      ) as le on le.id=cs.id order by cs.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public List<StatusDto> getcontractStatusSubActComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cs.id as  statusId,cs.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.contract_status as cs \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(cm.id) as value\n" +
                "from oiipcra_oltp.contract_status as cstatus\n" +
                "left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id\n" +
                "where cm.activity_id in (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=4) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "           )\t\t \n" +
                "group by cstatus.id,cstatus.name\n" +
                "\n" +
                "      ) as le on le.id=cs.id order by cs.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public Integer estimateStatusComponentWise(Integer statusId, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "     select  distinct count(aem.id) as value\n" +
                "                from oiipcra_oltp.activity_status as cstatus\n" +
                "                left join oiipcra_oltp.activity_estimate_mapping as aem on aem.status_id=cstatus.id\n" +
                "                where aem.activity_id in\n" +
                "                 (\n" +
                "                    (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE \n" +
                "   id=:componentId UNION ALL \n" +
                "                SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "                 SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "                  ) and cstatus.id=:statusId";
        sqlParam.addValue("statusId", statusId);
        sqlParam.addValue("componentId", componentId);
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

    public List<StatusDto> estimateStatusSubComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " SELECT acts.id as  statusId,acts.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.activity_status as acts \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(aem.id) as value\n" +
                "from oiipcra_oltp.activity_status as cstatus\n" +
                "left join oiipcra_oltp.activity_estimate_mapping as aem on aem.status_id=cstatus.id\n" +
                "where aem.activity_id in\n" +
                " (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=2) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "  )\t\t \n" +
                "group by cstatus.id,cstatus.name  \n" +
                "\n" +
                "      ) as le on le.id=acts.id order by acts.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public List<StatusDto> estimateStatusActivityComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT acts.id as  statusId,acts.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.activity_status as acts \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(aem.id) as value\n" +
                "from oiipcra_oltp.activity_status as cstatus\n" +
                "left join oiipcra_oltp.activity_estimate_mapping as aem on aem.status_id=cstatus.id\n" +
                "where aem.activity_id in\n" +
                " (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=3) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "  )\t\t \n" +
                "group by cstatus.id,cstatus.name  \n" +
                "\n" +
                "      ) as le on le.id=acts.id order by acts.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public List<StatusDto> estimateStatusSubActComponentWise() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT acts.id as  statusId,acts.name as statusName ,coalesce(le.value,0) as count from oiipcra_oltp.activity_status as acts \n" +
                "LEFT join \n" +
                "     ( \n" +
                " select  distinct cstatus.id,cstatus.name,count(aem.id) as value\n" +
                "from oiipcra_oltp.activity_status as cstatus\n" +
                "left join oiipcra_oltp.activity_estimate_mapping as aem on aem.status_id=cstatus.id\n" +
                "where aem.activity_id in\n" +
                " (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id in (SELECT id FROM oiipcra_oltp.master_head_details where master_head_id=4) UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "  )\t\t \n" +
                "group by cstatus.id,cstatus.name  \n" +
                "\n" +
                "      ) as le on le.id=acts.id order by acts.id";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public List<StatusDto> getcontractStatusall() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select  distinct cstatus.id as statusId,cstatus.name statusName,count(cm.id) as count " +
                " from oiipcra_oltp.contract_status as cstatus " +
                " left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id " +
                " group by cstatus.id,cstatus.name order by cstatus.id ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public Integer getValueComponentWiseStatus(Integer statusId, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select  count(cm.id) as value\n" +
                "from oiipcra_oltp.contract_status as cstatus\n" +
                "left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id\n" +
                "where cm.activity_id in (\n" +
                "\t      (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE id =:componentId UNION ALL \n" +
                "\t\t  SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)\n" +
                "\t\t  SELECT id FROM terminal_Ids WHERE is_terminal = true) \n" +
                "           )\tand cstatus.id=:statusId\t \n" +
                "group by cstatus.id,cstatus.name \n";
        sqlParam.addValue("statusId", statusId);
        sqlParam.addValue("componentId", componentId);
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

    public BigDecimal getValueByComponentId(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " select  distinct cstatus.id as statusId,cstatus.name statusName,count(cm.id) as count " +
                " from oiipcra_oltp.contract_status as cstatus " +
                " left join oiipcra_oltp.contract_m as cm on cm.contract_status_id=cstatus.id " +
                " group by cstatus.id,cstatus.name order by cstatus.id ";

        return namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
    }

    public List<StatusDto> getestimateStatusall() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select  distinct cstatus.id as statusId,cstatus.name as statusName,count(aem.id) as count " +
                "    from oiipcra_oltp.activity_status as cstatus " +
                "    left join oiipcra_oltp.activity_estimate_mapping as aem on aem.status_id=cstatus.id  " +
                "      group by cstatus.id,cstatus.name ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(StatusDto.class));
    }

    public Integer NoOfEstimateApprovedByComponentId(Integer componentId, Integer distId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "select count(*) from oiipcra_oltp.activity_estimate_mapping where  approved_status=2 and is_active=true  ";

        String qry = "select count(*) from oiipcra_oltp.activity_estimate_mapping as am  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as map on map.estimate_id=am.id  " +
                "where am.approved_status=2 and am.is_active=true ";

        if (componentId > 0) {
            qry += "  and  am.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL " +
                    "                SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "                SELECT id FROM terminal_Ids WHERE is_terminal = true) ";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {

            qry += "and map.dist_id=:distId ";
            sqlParam.addValue("distId",distId);
        }

        if (yearId != null && yearId > 0) {

            qry += " and am.finyr_id=:yearId ";
            sqlParam.addValue("yearId",yearId);
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

    public Integer NoOfContractCompleteByComponentId(Integer componentId, Integer yearId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select count(*)from oiipcra_oltp.contract_m " +
                "where contract_status_id=5 and is_active=true  ";

        if (componentId > 0) {
            qry += " and id in  " +
                    "(select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                    "id=:componentId UNION ALL " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)  ";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {
            qry += " and dist_id=:distId ) ";
            sqlParam.addValue("distId",distId);
        } else {
            qry += ")";
        }

        if (yearId != null && yearId > 0) {
            qry += " and finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
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

    public Integer NoOfContractOnGoingByComponentId(Integer componentId, Integer yearId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select count(*)from oiipcra_oltp.contract_m " +
                "where contract_status_id=2 and is_active=true  ";

        if (componentId > 0) {
            qry += " and id in  " +
                    "(select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                    "id=:componentId UNION ALL " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)  ";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {
            qry += " and dist_id=:distId ) ";
            sqlParam.addValue("distId",distId);
        } else {
            qry += ")";
        }

        if (yearId != null && yearId > 0) {
            qry += " and finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
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


    public BigDecimal TotalApproxEstCostByComponentId(Integer componentId, Integer yearId, Integer workTypeId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "  select round(sum(estimated_amount)/10000000,2) as approxEstCost from oiipcra_oltp.activity_estimate_mapping " +
//                "where is_active = true " +
//                "         ";

        String qry = "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost  " +
                "from oiipcra_oltp.activity_estimate_mapping as em  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active = true and em.approved_status=2 ";

        if (componentId > 0) {
            qry += "and em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id,  " +
                    "is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd  " +
                    "INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)  ";

            sqlParam.addValue("componentId", componentId);

            if (workTypeId != null && workTypeId > 0) {
                qry += "and work_type=:workTypeId  ";
                sqlParam.addValue("workTypeId", workTypeId);
            }
        }
        if (distId != null && distId > 0) {
            qry += " and emp.dist_id=:distId ";
            sqlParam.addValue("distId",distId);
        }

        if (yearId != null && yearId > 0) {
            qry += " and em.finyr_id=:yearId ";
            sqlParam.addValue("yearId",yearId);
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public Double TotalEstCostByComponentId(Integer componentId, Integer yearId, Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select round(sum(estimated_amount)/10000000,2) as approxEstCost from oiipcra_oltp.activity_estimate_mapping " +
                "where is_active = true " +
                "         ";

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, " +
                    "is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd " +
                    "INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);

            if (workTypeId != null && workTypeId > 0) {
                qry += "and work_type=:workTypeId";
                sqlParam.addValue("workTypeId", workTypeId);
            }

            if(yearId != null && yearId >0){

                qry += "and finyr_id=:yearId ";
            }
        }

        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.00;
            }
            return b;
        } catch (Exception e) {
            return 0.00;
        }
    }

    public Double TotalEstCostByComponentId2(Integer componentId, Integer yearId, Integer workTypeId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select round(sum(estimate.estimated_amount)/10000000,2) as approxEstCost  " +
                "from oiipcra_oltp.activity_estimate_mapping as estimate " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tank on tank.estimate_id = estimate.id  " +
                "where estimate.is_active = true and estimate.approved_status=2 " +
                "         ";

        if (componentId > 0) {
            qry += " and estimate.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, " +
                    "is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd " +
                    "INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);

            if (workTypeId != null && workTypeId > 0) {
                qry += "and work_type=:workTypeId";
                sqlParam.addValue("workTypeId", workTypeId);
            }
        }

        if (distId != null && distId > 0) {
            qry += " and tank.dist_id =:distId ";
        }

        if (yearId != null && yearId > 0) {
            qry += " and estimate.finyr_id=:yearId ";
        }

        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.00;
            }
            return b;
        } catch (Exception e) {
            return 0.00;
        }
    }

    public BigDecimal contractAmountByComponentId(Integer componentId, Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "  select   round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as contractAmount from " +
//                "oiipcra_oltp.contract_m as cm  " +
////                "left join oiipcra_oltp.contract_mapping as map on map.contract_id = cm.id  " +
//                " where cm.is_active = true  ";
        String qry = "select trunc(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as contractAmount from  " +
                "oiipcra_oltp.contract_m as cm where cm.is_active = true ";

        if (yearId != null && yearId > 0) {
            qry += "AND cm.finyr_id=:yearId";
            sqlParam.addValue("yearId", yearId);
        }

        if (componentId > 0) {
            qry += "  and id in " +
                    "(select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public BigDecimal BalanceWorkForContractByComponentId(Integer componentId, Integer yearId) {
        try {

            BigDecimal ab = TotalApproxEstCostByComponentId(componentId, yearId, null, null);
            BigDecimal bc = contractAmountByComponentId(componentId, yearId);

            BigDecimal b = ab.subtract(bc);
            if (b == null || b.compareTo(BigDecimal.ZERO) < 0) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public BigDecimal UPtoDateExpenditureByComponentId(Integer componentId, Integer yearId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  SELECT round(sum(value)/10000000,2) as upToDateExpenditure FROM oiipcra_oltp.expenditure_data ed " +
                " left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id where ed.is_active=true  ";

        if (yearId != null && yearId > 0) {
            qry += "AND ed.finyr_id=:yearId";
            sqlParam.addValue("yearId", yearId);
        }

        if (componentId > 0) {
            qry += "  and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE " +
                    "       id=:componentId UNION ALL " +
                    "          SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "      SELECT id FROM terminal_Ids WHERE is_terminal = true) ";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {
            qry += "and emp.district_id=:distId ";
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public Double UPtoDateExpenditureAmountByComponentId(Integer componentId, Integer yearId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  SELECT round(sum(value)/10000000,2) as upToDateExpenditure FROM oiipcra_oltp.expenditure_data ed " +
                " left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id where ed.is_active=true  ";

        if (yearId != null && yearId > 0) {
            qry += "AND ed.finyr_id=:yearId";
            sqlParam.addValue("yearId", yearId);
        }

        if (componentId > 0) {
            qry += "  and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE " +
                    "       id=:componentId UNION ALL " +
                    "          SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "      SELECT id FROM terminal_Ids WHERE is_terminal = true) ";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {
            qry += "emp.district_id=:distId ";
        }

        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.0;
            }
            return b;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public BigDecimal BalanceContractValueByComponentId(Integer componentId, Integer yearId) {

        try {
            BigDecimal bc = contractAmountByComponentId(componentId, yearId);
            BigDecimal ab = UPtoDateExpenditureByComponentId(componentId, yearId, null);
            BigDecimal b = bc.subtract(ab);
            if (b == null || b.compareTo(BigDecimal.ZERO) < 0) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public Double TotalCanalLengthByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = " SELECT sum(total_length_of_canal_as_per_estimate) as totalLengthOfCanalAsPerEstimate\n" +
//                " FROM oiipcra_oltp.physical_progress_planned  \n" +
//                "  where true  ";

        String qry = "SELECT sum(planned.total_length_of_canal_as_per_estimate) as totalLengthOfCanalAsPerEstimate " +
                "FROM oiipcra_oltp.physical_progress_planned as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= planned.tank_m_id  " +
                "where planned.is_active=true ";

        if (componentId > 0) {
            qry += "and planned.estimate_id in ( SELECT id  from oiipcra_oltp.activity_estimate_mapping where activity_id in   (" +
                    "    WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "   SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "     SELECT id FROM terminal_Ids WHERE is_terminal = true   " +
                    "    )" +
                    "    )  ";

            sqlParam.addValue("componentId", componentId);
        }
        if (distId != null && distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }


        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.0;
            }
            return b;
        } catch (Exception e) {
            return 0.0;
        }
        // return 0.0;
    }

    public Double CanalImprovedByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "  SELECT sum(length_of_canal_improved) as lengthOfCanalImproved " +
//                "        FROM oiipcra_oltp.physical_progress_executed where true ";

        String qry = "SELECT sum(executed.length_of_canal_improved) as totalCanalLength  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += "and executed.contract_id in  (" +
                    "    SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in " +
                    "     ( " +
                    "   WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL " +
                    "   SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "   SELECT id FROM terminal_Ids WHERE is_terminal = true " +
                    "   )" +
                    "   ) ";

            sqlParam.addValue("componentId", componentId);
        }
        if (distId != null && distId > 0) {
            qry += "tank.dist_id=:distId ";
        }

        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.0;
            }
            return b;
        } catch (Exception e) {
            return 0.0;
        }

    }

    public Integer CdStructurePreparedByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = " SELECT sum(no_of_cd_structures_repared) as noOfCdStructuresRepared " +
//                "\tFROM oiipcra_oltp.physical_progress_executed " +
//                " where true   ";

        String qry = "SELECT sum(executed.no_of_cd_structures_repared) as noOfCdStructuresRepared  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += " and executed.contract_id in  ( " +
                    " SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in " +
                    " (  " +
                    " WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                    " SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "  SELECT id FROM terminal_Ids WHERE is_terminal = true " +
                    " )   " +
                    " )";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {
            qry += "and tank.dist_id=:distId ";
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

    public Integer CdStructureToBePreparedByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = " SELECT sum(no_of_cd_structures_to_be_repared) as noOfCdStructuresToBeRepared " +
//                "  FROM oiipcra_oltp.physical_progress_planned   " +
//                " where true  ";

        String qry = "SELECT sum(planned.no_of_cd_structures_to_be_repared) as noOfCdStructuresToBeRepared  " +
                "FROM oiipcra_oltp.physical_progress_planned  as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= planned.tank_m_id   " +
                "where planned.is_active=true  ";

        if (componentId > 0) {
            qry += " and planned.estimate_id in  " +
                    "    ( " +
                    "    SELECT id  from oiipcra_oltp.activity_estimate_mapping where activity_id in " +
                    "   ( " +
                    "    WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "    SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "    SELECT id FROM terminal_Ids WHERE is_terminal = true " +
                    "     ) " +
                    "     ) ";

            sqlParam.addValue("componentId", componentId);
        }

        if (distId != null && distId > 0) {
            qry += " and tank.dist_id=:distId ";
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

    public Double TotalCadLengthByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = " SELECT sum(total_length_of_cad) as totalLengthOfCad " +
//                " FROM oiipcra_oltp.physical_progress_planned   where true   ";

        String qry = "SELECT sum(planned.total_length_of_cad) as totalLengthOfCad  " +
                "FROM oiipcra_oltp.physical_progress_planned as planned   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= planned.tank_m_id  " +
                "where planned.is_active=true ";

        if (componentId > 0) {
            qry += " and planned.estimate_id in " +
                    "               (" +
                    "                 SELECT id  from oiipcra_oltp.activity_estimate_mapping where activity_id in " +
                    "                (" +
                    "               WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "               SELECT id FROM terminal_Ids WHERE is_terminal = true " +
                    "             ) " +
                    "             ) ";

            sqlParam.addValue("componentId", componentId);
        }
        if (distId != null && distId > 0) {
            qry += "and tank.dist_id=:distId ";
        }

        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.0;
            }
            return b;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Double CadConstructedByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT sum(total_length_of_cad) as total_length_of_cad\n" +
//                "\tFROM oiipcra_oltp.physical_progress_executed where true  ";

        String qry = "SELECT sum(executed.total_length_of_cad) as cadConstructed  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += "and executed.contract_id in  (" +
                    "     SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in " +
                    "  ( " +
                    "    WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "     SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "       SELECT id FROM terminal_Ids WHERE is_terminal = true" +
                    "     )  " +
                    "     )  ";

            sqlParam.addValue("componentId", componentId);
        }
        if (distId != null && distId > 0) {
            qry += "and tank.dist_id=:distId  ";
        }

        try {
            Double b = namedJdbc.queryForObject(qry, sqlParam, Double.class);
            if (b == null) {
                b = 0.0;
            }
            return b;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Integer NoOfOutletConstructedByComponentId(Integer componentId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT sum(no_of_outlet_constructed) as noOfOutletConstructed " +
//                "\tFROM oiipcra_oltp.physical_progress_executed where true   ";

        String qry = "SELECT sum(executed.no_of_outlet_constructed) as noOfOutletConstructed  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += "and executed.contract_id in  (  " +
                    "   SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in " +
                    "  ( " +
                    "  WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                    "   SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "    SELECT id FROM terminal_Ids WHERE is_terminal = true " +
                    "    ) " +
                    "    )  ";

            sqlParam.addValue("componentId", componentId);
        }
        if (distId != null && distId > 0) {
            qry += "and tank.dist_id=:distId  ";
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

    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public String getWSAtillMonth(Integer projectId, String divisionName) {

        ///MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        WaterSpreadDto wp = getLastMonthAndYear();
//        return wp;
        String month = toCamelCase(wp.getMonth());

        if (wp.getMonth() != null && wp.getYear() != null) {
            return "" + month + "" + "," + wp.getYear() + "";
        } else {
            return "NA";
        }


//        String qry = "select month from oiipcra_oltp.oiipcra_water_spread where true  ";
//
//        if (projectId > 0) {
//            qry += "and  month_id in (" + wp.getMonthId() + ") and \"year\" in ('" + wp.getYear() + "') and project_id::integer=:projectId ";
//            sqlParam.addValue("projectId", projectId);
//        }
//
//        if (!divisionName.isEmpty()) {
//            qry += "and project_id::integer " +
//                    "in (select project_id FROM oiipcra_oltp.tank_m_id where mi_division_name=:divisionName)";
//
//            sqlParam.addValue("divisionName", divisionName);
//        }
//        qry += "order by \"year\" DESC,month_id desc limit 1";
//        if (namedJdbc.queryForObject(qry, sqlParam, String.class) == null) {
//            return "NA";
//        }
//        return namedJdbc.queryForObject(qry, sqlParam, String.class);


    }

    public List<ComponentDto> getDivisionWiseContractAmountDetails(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  SELECT md.mi_division_id as divisionId ,ls1.activity_id as parentId , md.mi_division_name  as componentName,  " +
                "    coalesce(sum(ls2.valus),0) as contractAmount  FROM oiipcra_oltp.mi_division_m md " +
                " LEFT JOIN  " +
                " (" +
                " SELECT cmp.contract_id, cmp.division_id ,cmp.activity_id  " +
                " FROM oiipcra_oltp.contract_m  as cm   " +
                " JOIN oiipcra_oltp.contract_mapping as cmp on cmp.contract_id = cm.id   " +
                " group by cmp.contract_id,cmp.division_id ,cmp.activity_id  " +
                " )ls1 ON md.mi_division_id = ls1.division_id   " +
                " LEFT JOIN   " +
                "  (   " +
                " select id, round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as valus from oiipcra_oltp.contract_m group by id     " +
                "  )ls2 ON ls1.contract_id = ls2.id  where ls1.activity_id=:componentId  " +
                "    GROUP BY md.mi_division_id, md.mi_division_name,ls1.activity_id  order by mi_division_name  ";
        sqlParam.addValue("componentId", componentId);

        try {

            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();

            return componentDto;
        }
    }

    public BigDecimal getContractAmountByYear(Integer id, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "\tselect  round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) from oiipcra_oltp.contract_m " +
                "\t where finyr_id=:id ";
        if (componentId > 0) {
            qry += " and id in(select distinct contract_id from oiipcra_oltp.contract_mapping where activity_id in  " +
                    "\t (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL " +
                    "\t SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "\t SELECT id FROM terminal_Ids WHERE is_terminal = true))  ";
            sqlParam.addValue("componentId", componentId);
        }

        sqlParam.addValue("id", id);

        if (namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class) == null) {
            return BigDecimal.valueOf(0.0);
        }
        return namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
    }

    public BigDecimal getExpenditureByYear(int id, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "select  round(sum(value)/10000000,2) as expenditureAmount from oiipcra_oltp.expenditure_data ed\n" +
                " LEFT join oiipcra_oltp.expenditure_mapping em on  em.expenditure_id=ed.id\n" +
                " where finyr_id=:id ";
        if (componentId > 0) {
            qry += "and em.activity_id in " +
                    " (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL\n" +
                    " SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) \n" +
                    " SELECT id FROM terminal_Ids WHERE is_terminal = true)";
            sqlParam.addValue("componentId", componentId);
        }

        sqlParam.addValue("id", id);

        if (namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class) == null) {
            return BigDecimal.valueOf(0.0);
        }
        return namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
    }


    public List<Integer> getTenderIdsByWorkTypeId(Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "Select id from oiipcra_oltp.tender_m where tender_type =:workTypeId ";
        sqlParam.addValue("workTypeId", workTypeId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }


    public BigDecimal getExpenditureByTenderId(List<Integer> terminalIds, Integer yearId, List<Integer> tenderIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        if (!terminalIds.isEmpty()) {
            String qry = " SELECT round(sum(value)/10000000,2) FROM oiipcra_oltp.expenditure_data ed  " +
                    "left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id where em.activity_id " +
                    "in (:terminalIds) and  ed.is_active = true " +
                    "and tender_id in (:tenderIds ) ";

            if (yearId != null && yearId > 0) {
                qry += "AND ed.finyr_id=:yearId";
                sqlParam.addValue("yearId", yearId);
            }
            sqlParam.addValue("terminalIds", terminalIds);
            sqlParam.addValue("tenderIds", tenderIds);

            try {
                BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
                if (b == null) {
                    b = BigDecimal.valueOf(0.0);
                }
                return b;
            } catch (Exception e) {
                return BigDecimal.valueOf(0.00);
            }
        } else {
            return new BigDecimal("0.0");
        }


    }

    public BigDecimal getExpenditureByContractId(List<Integer> terminalIds, Integer yearId, List<Integer> contractIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        if (!terminalIds.isEmpty()) {
            String qry = " SELECT round(sum(value)/10000000,2) FROM oiipcra_oltp.expenditure_data ed  " +
                    "left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id where em.activity_id in (:terminalIds) and " +
                    "ed.is_active= true " +
                    "and contract_id in (:contractIds ) ";

            if (yearId != null && yearId > 0) {
                qry += "AND ed.finyr_id=:yearId";
                sqlParam.addValue("yearId", yearId);
            }
            sqlParam.addValue("terminalIds", terminalIds);
            sqlParam.addValue("contractIds", contractIds);

            try {
                BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
                if (b == null) {
                    b = BigDecimal.valueOf(0.0);
                }
                return b;
            } catch (Exception e) {
                return BigDecimal.valueOf(0.00);
            }
        } else {
            return new BigDecimal("0.0");
        }
    }

    public BigDecimal getExpenditureByEstimateIds(List<Integer> terminalIds, Integer yearId, List<Integer> estimateIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        if (!terminalIds.isEmpty()) {
            String qry = " SELECT round(sum(value)/10000000,2) FROM oiipcra_oltp.expenditure_data ed  " +
                    "left join oiipcra_oltp.expenditure_mapping as em on em.expenditure_id=ed.id where " +
                    "em.activity_id in (:terminalIds) " +
                    "and estimate_id in (:estimateIds ) ";

            if (yearId != null && yearId > 0) {
                qry += "AND ed.finyr_id=:yearId";
                sqlParam.addValue("yearId", yearId);
            }
            sqlParam.addValue("terminalIds", terminalIds);
            sqlParam.addValue("estimateIds", estimateIds);

            try {
                BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
                if (b == null) {
                    b = BigDecimal.valueOf(0.0);
                }
                return b;
            } catch (Exception e) {
                return BigDecimal.valueOf(0.00);
            }
        } else {
            return new BigDecimal("0.0");
        }
    }

    public BigDecimal contractAmountByComponentIdForWork(Integer componentId, Integer yearId, Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select trunc(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as contractAmount from \n" +
                "oiipcra_oltp.contract_m as cm where cm.is_active = true   ";

        if (yearId != null && yearId > 0) {
            qry += "AND cm.finyr_id=:yearId";
            sqlParam.addValue("yearId", yearId);
        }

        if (componentId > 0) {
            qry += " and id in " +
                    "(select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }
        if (workTypeId > 0) {
            qry += "AND cm.work_type_id=:workTypeId";
            sqlParam.addValue("workTypeId", workTypeId);
        }
        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public BigDecimal contractAmountByComponentIdForConsultancy(Integer componentId, Integer yearId, Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "  select   round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as contractAmount from oiipcra_oltp.contract_m as cm  " +
//                " where cm.is_active = true  ";

        String qry = "select trunc(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2) as contractAmount from  " +
                "oiipcra_oltp.contract_m as cm where cm.is_active = true ";

        if (yearId != null && yearId > 0) {
            qry += "AND cm.finyr_id=:yearId";
            sqlParam.addValue("yearId", yearId);
        }

        if (componentId > 0) {
            qry += " and id in " +
                    "(select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }
        if (workTypeId > 0) {
            qry += "AND cm.work_type_id=:workTypeId";
            sqlParam.addValue("workTypeId", workTypeId);
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public BigDecimal estimateAmountByComponentIdForWork(Integer componentId, Integer yearId, Integer workTypeId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "  select round(sum(estimated_amount)/10000000,2) as approxEstCost from oiipcra_oltp.activity_estimate_mapping " +
//                "where is_active=true " +
//                "         ";

        String qry = "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost  " +
                "from oiipcra_oltp.activity_estimate_mapping as em  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active=true and em.approved_status=2 ";

        if (componentId > 0) {
            qry += "and em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        if (workTypeId > 0) {
            qry += "AND work_type=:workTypeId  ";
            sqlParam.addValue("workTypeId", workTypeId);
        }
        if (distId != null && distId > 0) {
            qry += "  and emp.dist_id=:distId ";
            sqlParam.addValue("distId", distId);
        }

        if (yearId != null && yearId > 0) {
            qry += "and em.finyr_id=:yearId ";
            sqlParam.addValue("yearId", yearId);
        }



        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public AdaptFinancialDto getAdaptEstimateAmount(Integer componentId, Integer distId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum( financial_allocation_in_app)/100 as financialAllocationInApp, " +
                "sum(actual_fund_allocated)/100 as actualFundAllocated, " +
                "sum(expenditure)/100 as expenditure " +
                "\tFROM oiipcra_oltp.denormalized_financial_achievement where  is_active=true  ";
        if (componentId > 0) {
            qry += " and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        if (distId != null && distId > 0) {
            qry += " and adapt_dist_id=:distId ";
        }
        sqlParam.addValue("distId", distId);

        if (yearId != null && yearId > 0) {
            qry += " and year_id=:yearId ";
        }
        sqlParam.addValue("yearId", yearId);


        try {
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(AdaptFinancialDto.class));
        } catch (Exception e) {
            return null;
        }
    }

    public BigDecimal estimateAmountByComponentIdForConsultancy(Integer componentId, Integer yearId, Integer workTypeId, Integer distId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

//        String qry = "  select round(sum(estimated_amount)/10000000,2) as approxEstCost from oiipcra_oltp.activity_estimate_mapping " +
//                "where is_active = true " +
//                "         ";

        String qry = "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost  " +
                "from oiipcra_oltp.activity_estimate_mapping as em  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active=true and em.approved_status=2 ";

        if (componentId > 0) {
            qry += "and em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        if (workTypeId > 0) {
            qry += " AND work_type=:workTypeId ";
            sqlParam.addValue("workTypeId", workTypeId);
        }

        if (distId != null && distId > 0) {
            qry += " and emp.dist_id=:distId ";
            sqlParam.addValue("distId", distId);
        }

        if (yearId != null && yearId > 0) {
            qry += " and em.finyr_id=:yearId ";
            sqlParam.addValue("yearId", yearId);
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public BigDecimal estimateAmountByComponentIdForAgriculture(Integer componentId, Integer yearId, Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "  select round(sum(estimated_amount)/10000000,2) as approxEstCost from oiipcra_oltp.activity_estimate_mapping where is_active=true " +
                "         ";

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        if (workTypeId > 0) {
            qry += "AND work_type=:workTypeId";
            sqlParam.addValue("workTypeId", workTypeId);
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }


    public List<Integer> getContractIds(Integer workTypeId, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.contract_m where work_type_id=:workTypeId and is_active=true ";

        sqlParam.addValue("workTypeId", workTypeId);

        if (componentId > 0) {
            qry += "and id in (select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getContractIdsGoodWise(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.contract_m where work_type_id=3 and is_active=true ";

        if (componentId > 0) {
            qry += "and id in (select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getEstimateIds(Integer workTypeId, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.activity_estimate_mapping  where work_type=:workTypeId and is_active=true ";
        sqlParam.addValue("workTypeId", workTypeId);



        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }

        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getActivityIds(Integer workTypeId, Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct activity_id from oiipcra_oltp.activity_estimate_mapping  where work_type=:workTypeId ";
        sqlParam.addValue("workTypeId", workTypeId);

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public BigDecimal expenditureAmountByComponentIdForWork(Integer yearId, List<Integer> contractIds, List<Integer> estimateIds) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  SELECT round(sum(value)/10000000,2) as upToDateExpenditure FROM oiipcra_oltp.expenditure_data ed " +
                " left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id   where ed.is_active=true  ";

//        if (componentId > 0) {
//            qry += "  and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE " +
//                    "       id=:componentId UNION ALL " +
//                    "          SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
//                    "      SELECT id FROM terminal_Ids WHERE is_terminal = true) ";
//
//            sqlParam.addValue("componentId", componentId);
//        }

        if(yearId != null && yearId > 0)
        {
            qry += "AND ed.finyr_id=:yearId ";
            sqlParam.addValue("yearId", yearId);
        }

        if (contractIds != null && contractIds.size() > 0 && estimateIds != null && estimateIds.size() > 0 && yearId != null && yearId > 0) {
            qry += " AND( emp.contract_id in (:contractIds) or emp.estimate_id in (:estimateIds)) ";
            sqlParam.addValue("contractIds", contractIds);
            sqlParam.addValue("estimateIds", estimateIds);

        } else {
            if (contractIds != null && contractIds.size() > 0) {
                qry += " AND( emp.contract_id in (:contractIds) )";
                sqlParam.addValue("contractIds", contractIds);
            } else {
                qry += " AND( emp.estimate_id in (:estimateIds)) ";
                sqlParam.addValue("estimateIds", estimateIds);
            }

        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }

    public List<Integer> getContractIdsByWorkTypeId(Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id from oiipcra_oltp.contract_m where contract_type_id=:workTypeId ";
        sqlParam.addValue("workTypeId", workTypeId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getEstimateIdsByWorkTypeId(Integer workTypeId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id from oiipcra_oltp.activity_estimate_mapping where work_type=:workTypeId ";
        sqlParam.addValue("workTypeId", workTypeId);
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public Integer getBeneficiariesSum(Integer componentId, Integer distId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
        qry += "SELECT sum( no_of_beneficiaries) " +
                "\tFROM oiipcra_oltp.denormalized_achievement where is_active= true ";

        if (distId != null && distId > 0) {
            qry += "and adapt_dist_id =:distId ";

            sqlParam.addValue("distId", distId);
        }

        if (componentId > 0) {
            qry += " and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }

        if (yearId != null && yearId > 0) {
            qry += "and year_id =:yearId ";

            sqlParam.addValue("yearId", yearId);
        }

        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public BigDecimal expenditureAmountByComponentIdForAgriculture(Integer componentId, Integer yearId, List<Integer> activityIds, List<Integer> estimateIds) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  SELECT round(sum(value)/10000000,2) as upToDateExpenditure FROM oiipcra_oltp.expenditure_data ed " +
                " left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id   where ed.is_active=true  ";

        if (yearId != null && yearId > 0) {
            qry += "AND ed.finyr_id=:yearId";
            sqlParam.addValue("yearId", yearId);
        }

        if (componentId > 0) {
            qry += "  and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE " +
                    "       id=:componentId UNION ALL " +
                    "          SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "      SELECT id FROM terminal_Ids WHERE is_terminal = true) ";

            sqlParam.addValue("componentId", componentId);
        }
        if (activityIds != null && activityIds.size() > 0 && estimateIds != null && estimateIds.size() > 0) {
            qry += "AND emp.activity_id in (:activityIds) or emp.estimate_id in (:estimateIds)";
            sqlParam.addValue("activityIds", activityIds);
            sqlParam.addValue("estimateIds", estimateIds);
        } else {
            if (activityIds != null && activityIds.size() > 0) {
                qry += "AND emp.activity_id in (:activityIds) ";
                sqlParam.addValue("activityIds", activityIds);
            } else {
                qry += "AND emp.estimate_id in (:estimateIds) ";
                sqlParam.addValue("estimateIds", estimateIds);
            }
        }

        try {
            BigDecimal b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = BigDecimal.valueOf(0.0);
            }
            return b;
        } catch (Exception e) {
            return BigDecimal.valueOf(0.0);
        }
    }


    public Object getTankSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(distinct tankSurvey.tank_id) from oiipcra_oltp.tank_survey_data as tankSurvey  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = tankSurvey.tank_id " +
                "where tankSurvey.is_active=true  ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tankSurvey.district_id=:distId ";
        }
        if (divisionId > 0) {
            qry += "and tankSurvey.division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tankSurvey.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
        try {
            Object b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = 0;
            }
            return b;
        } catch (Exception e) {
            return 0;
        }
    }


    public Object getCadSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(distinct cad.tank_id) from oiipcra_oltp.cad_m as cad  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = cad.tank_id  " +
                "where cad.is_active=true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.district_id=:distId ";
        }
        if (divisionId > 0) {
            qry += "and tank.division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
        try {
            Object b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = 0;
            }
            return b;
        } catch (Exception e) {
            return 0;
        }
    }

    public Object getFeederSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(distinct feeder.tank_id) from oiipcra_oltp.feeder_m as feeder \n" +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = feeder.tank_id\n" +
                "where feeder.is_active=true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.district_id=:distId ";
        }
        if (divisionId > 0) {
            qry += "and tank.division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
        try {
            Object b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = 0;
            }
            return b;
        } catch (Exception e) {
            return 0;
        }
    }

    public Object getDepthCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(distinct depth.tank_id) from oiipcra_oltp.depth_m as depth  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id = depth.tank_id " +
                "where depth.is_active=true ";

        if (circleId > 0) {
            qry += "and tank.circle_id =:circleId ";
        }
        if (distId > 0) {
            qry += "and tank.district_id=:distId ";
        }
        if (divisionId > 0) {
            qry += "and tank.division_id =:divisionId ";
        }
        if (blockId > 0) {
            qry += "and tank.block_id=:blockId ";
        }
        sqlParam.addValue("divisionId", divisionId);
        sqlParam.addValue("blockId", blockId);
        sqlParam.addValue("circleId", circleId);
        sqlParam.addValue("distId", distId);
        try {
            Object b = namedJdbc.queryForObject(qry, sqlParam, BigDecimal.class);
            if (b == null) {
                b = 0;
            }
            return b;
        } catch (Exception e) {
            return 0;
        }
    }


    public List<DenormalizedAchievement> getAdaptDistId(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  ";
        qry += "Select distinct adapt_dist_id from oiipcra_oltp.denormalized_achievement " +
                " where is_active= true  ";

        if (componentId > 0) {
            qry += " and activity_id=:componentId  ";
        }

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DenormalizedAchievement.class));
    }

    public List<ActivityEstimateTankMappingDto> getEstimateDistId(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "  ";
        qry += "Select distinct tank.dist_id from oiipcra_oltp.activity_estimate_mapping as map  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tank on tank.estimate_id = map.id " +
                "where map.is_active=true ";

        if (componentId > 0) {
            qry += " and map.activity_id=:componentId ";
        }

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ActivityEstimateTankMappingDto.class));
    }


    public List<ComponentDto> getDistWiseExpenditureDetails(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select  dist_id as distId,district_name as componentName,coalesce(ls4.estimateAmount,0.00) as estimate,\n" +
                "coalesce(ls3.expenditureAmount,0.00) as expenditure FROM oiipcra_oltp.district_boundary as db\n" +
                "\n" +
                "left join(\n" +
                "\tselect distinct actem.activity_id as parentId,div.district_name as distName,div.dist_id as distId,\n" +
                "\tcoalesce(round(((sum(estimated_amount) over (partition by aem.dist_id)/10000000)),2),0.0) \n" +
                "    as estimateAmount \n" +
                "    from oiipcra_oltp.district_boundary as div \n" +
                "    left join oiipcra_oltp.activity_estimate_tank_mapping as aem on div.dist_id=aem.dist_id \n" +
                "    left join oiipcra_oltp.activity_estimate_mapping as actem on aem.estimate_id=actem.id \n" +
                "\twhere actem.is_active=true and actem.activity_id=:componentId \n" ;

        if (yearId != null && yearId >0)
        {
            qry += "and actem.finyr_id=:yearId " ;
            sqlParam.addValue("yearId",yearId);
        }

        qry  +=  ") as ls4 on db.dist_id = ls4.distId \n" +
                "\n" +
                "left join (\n" +
                "\t select distinct em.activity_id as parentId,div.district_name  as distName,div.dist_id  as distId,round(((sum(value) over (partition by aem.dist_id)/10000000)),2)\n" +
                "     as expenditureAmount\n" +
                "     from oiipcra_oltp.district_boundary as div \n" +
                "     left join oiipcra_oltp.activity_estimate_tank_mapping as aem on div.dist_id=aem.dist_id \n" +
                "     left join oiipcra_oltp.expenditure_mapping as em on aem.estimate_id=em.estimate_id\n" +
                "     left join oiipcra_oltp.expenditure_data as ed on em.expenditure_id=ed.id where ed.type<>4 \n" +
                "\t\tand em.activity_id=:componentId \n" ;

        if (yearId != null && yearId >0)
        {
            qry += "and ed.finyr_id=:yearId " ;
            sqlParam.addValue("yearId",yearId);
        }

        qry   +=  ") as ls3 on db.dist_id = ls3.distId where db.in_oiipcra = 1 \n ";


        sqlParam.addValue("componentId", componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }

    }


    public List<ComponentDto> getDistWiseAdaptData(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

//         qry  += " select adapt_dist_id as distId ,district_name as districtName ,sum(expenditure) as adaptExpenditure,sum(financial_allocation_in_app) as adaptFinancialAllocationInApp,  " +
//                 " sum(actual_fund_allocated) as adaptActualFundAllocated from oiipcra_oltp.denormalized_financial_achievement where is_active=true   ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.adaptExpenditure,0.00) as adaptExpenditure, " +
                "coalesce(ls4.adaptFinancialAllocationInApp,0.00) as adaptFinancialAllocationInApp, " +
                "coalesce(ls4.adaptActualFundAllocated,0.00) as adaptActualFundAllocated " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join (  " +
                "select df.adapt_dist_id,df.district_name, " +
                "sum(df.expenditure) as adaptExpenditure,  " +
                "sum(df.financial_allocation_in_app) as adaptFinancialAllocationInApp,   " +
                "sum(df.actual_fund_allocated) as adaptActualFundAllocated  " +
                "from oiipcra_oltp.denormalized_financial_achievement as df " +
                "where df.is_active=true ";

        if (componentId > 0) {
            qry += " and df.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL    " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)   " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true) " ;


            sqlParam.addValue("componentId", componentId);
        }

        if(yearId!= null && yearId >0)
        {
            qry += "and df.year_id =:yearId ";
            sqlParam.addValue("yearId",yearId);
        }


        qry += " group by df.adapt_dist_id,df.district_name) as ls4 on db.dist_id = ls4.adapt_dist_id  where db.in_oiipcra=1 ORDER BY db.dist_id  ";

        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getDistWiseBenifi(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
//        qry += "SELECT adapt_dist_id as distId,district_name as districtName,sum( no_of_beneficiaries) as adaptBeneficiaries " +
//                "\tFROM oiipcra_oltp.denormalized_achievement where is_active= true ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.adaptBeneficiaries,0) as adaptBeneficiaries  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join (  " +
                "SELECT da.adapt_dist_id,da.district_name, " +
                "sum(da.no_of_beneficiaries) as adaptBeneficiaries  " +
                "FROM oiipcra_oltp.denormalized_achievement as da  " +
                "where da.is_active= true ";

        if (componentId > 0) {
            qry += " and da.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId  UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true) " ;

            sqlParam.addValue("componentId", componentId);
        }

        if(yearId !=null && yearId >0)
        {
            qry += "and da.year_id=:yearId ";
            sqlParam.addValue("yearId",yearId);
        }

        qry += " group by da.adapt_dist_id,da.district_name) as ls4 on db.dist_id = ls4.adapt_dist_id where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public Activity getParentData(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
        qry += " select * from oiipcra_oltp.master_head_details where id=\n" +
                " (select parent_id from oiipcra_oltp.master_head_details where id=:componentId) ";
        sqlParam.addValue("componentId", componentId);

        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(Activity.class));

    }

    public List<ComponentDto> getDistWiseContractComplete(Integer componentId) {
        return null;
    }

    public List<ComponentDto> getNoOfEstimateApproved(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "select distinct dist.dist_id,dist.district_name,coalesce(ls4.count,0) as noOfEstimateApproved  " +
                "from oiipcra_oltp.district_boundary as dist " +
                "left join(  " +
                "select count(*) as count,map.dist_id from oiipcra_oltp.activity_estimate_mapping as am  " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as map on map.estimate_id=am.id   " +
                "where am.approved_status=2 and am.is_active=true  " ;

        if(yearId != null && yearId >0 )
        {
            qry += "and am.finyr_id=:yearId ";
            sqlParam.addValue("yearId",yearId);
        }

        qry  += "and am.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "group By map.dist_id) as ls4 on ls4.dist_id=dist.dist_id  " +
                "where dist.in_oiipcra=1   ";

        sqlParam.addValue("componentId",componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> getTotalCanalLength(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
//        qry += "SELECT adapt_dist_id as distId,district_name as districtName,sum( no_of_beneficiaries) as adaptBeneficiaries " +
//                "\tFROM oiipcra_oltp.denormalized_achievement where is_active= true ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.totalLengthOfCanalAsPerEstimate,0) as totalCanalLength  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(\n" +
                "SELECT sum(planned.total_length_of_canal_as_per_estimate) as totalLengthOfCanalAsPerEstimate,tank.dist_id  " +
                "FROM oiipcra_oltp.physical_progress_planned as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= planned.tank_m_id   " +
                "where planned.is_active=true    ";

        if (componentId > 0) {
            qry += " and planned.estimate_id in ( SELECT id  from oiipcra_oltp.activity_estimate_mapping where activity_id in   (  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true   " +
                    ")\n" +
                    ")\t\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> getTotalCanalImproved(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";
//        qry += "SELECT adapt_dist_id as distId,district_name as districtName,sum( no_of_beneficiaries) as adaptBeneficiaries " +
//                "\tFROM oiipcra_oltp.denormalized_achievement where is_active= true ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.lengthOfCanalImproved,0) as canalImproved  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(  " +
                "SELECT sum(executed.length_of_canal_improved) as lengthOfCanalImproved,tank.dist_Id  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed    " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += " and executed.contract_id in  (  " +
                    "SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in  " +
                    "(  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)   " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true  " +
                    ")\n" +
                    ")\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> getCdRestored(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.cdStructurePrepared,0) as cdStructurePrepared  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(  " +
                "SELECT sum(executed.no_of_cd_structures_repared) as cdStructurePrepared,tank.dist_Id  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed    " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += " and executed.contract_id in  (  " +
                    "SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in  " +
                    "(  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)   " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true  " +
                    ")\n" +
                    ")\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getCdToBeRestored(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";


        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.cdStructureToBePrepared,0) as cdStructureToBePrepared  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(\n" +
                "SELECT sum(planned.no_of_cd_structures_to_be_repared) as cdStructureToBePrepared,tank.dist_id  " +
                "FROM oiipcra_oltp.physical_progress_planned as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= planned.tank_m_id   " +
                "where planned.is_active=true    ";

        if (componentId > 0) {
            qry += " and planned.estimate_id in ( SELECT id  from oiipcra_oltp.activity_estimate_mapping where activity_id in   (  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true   " +
                    ")\n" +
                    ")\t\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getCadLength(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";


        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.totalLengthOfCad,0) as totalCadLength  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(\n" +
                "SELECT sum(planned.total_length_of_cad) as totalLengthOfCad,tank.dist_id  " +
                "FROM oiipcra_oltp.physical_progress_planned as planned  " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= planned.tank_m_id   " +
                "where planned.is_active=true    ";

        if (componentId > 0) {
            qry += " and planned.estimate_id in ( SELECT id  from oiipcra_oltp.activity_estimate_mapping where activity_id in   (  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true   " +
                    ")\n" +
                    ")\t\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> getCadConstructedDistWise(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.cadConstructed,0) as cadConstructed  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(  " +
                "SELECT sum(executed.total_length_of_cad) as cadConstructed,tank.dist_Id  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed    " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += " and executed.contract_id in  (  " +
                    "SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in  " +
                    "(  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)   " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true  " +
                    ")\n" +
                    ")\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getOutlet(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "select db.dist_id as distId,db.district_name as componentName, coalesce(ls4.noOfOutletConstructed,0) as noOfOutletConstructed  " +
                "FROM oiipcra_oltp.district_boundary as db  " +
                "left join  " +
                "(  " +
                "SELECT sum(executed.no_of_outlet_constructed) as noOfOutletConstructed,tank.dist_Id  " +
                "FROM oiipcra_oltp.physical_progress_executed as executed    " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.tank_id= executed.tank_m_id  " +
                "where executed.is_active=true  ";

        if (componentId > 0) {
            qry += " and executed.contract_id in  (  " +
                    "SELECT contract_id  from oiipcra_oltp.contract_mapping where activity_id in  " +
                    "(  " +
                    "WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)   " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true  " +
                    ")\n" +
                    ")\n" +
                    "GROUP BY tank.dist_id) as ls4 on db.dist_id = ls4.dist_id   ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " where db.in_oiipcra=1 ORDER BY db.dist_id  ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> NoOfContractComplete(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "select distinct dist.dist_id,dist.district_name as componentName ,coalesce(count(contract_id),0)as noOfContractComplete  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join (select distinct contract_id,dist_id from oiipcra_oltp.contract_mapping where is_active=true and contract_id in(select id from oiipcra_oltp.contract_m  " +
                "where contract_status_id=5 and is_active=true  ";

        if(yearId!= null && yearId >0)
        {
            qry += "and finyr_id=:yearId " ;

            sqlParam.addValue("yearId",yearId);
        }

        if (componentId > 0) {
            qry += "and id in (select distinct contract_id from oiipcra_oltp.contract_mapping where is_active=true and activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true))  ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " )) as cmap on dist.dist_id=cmap.dist_id " +
                "where dist.in_oiipcra=1  " +
                "group by dist.dist_id,dist.district_name " +
                "order by dist.dist_id   ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> NoOfContractOngoing(Integer componentId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "select distinct dist.dist_id,dist.district_name as componentName,coalesce(count(contract_id),0)as noOfContractOnGoing  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join (select distinct contract_id,dist_id from oiipcra_oltp.contract_mapping where is_active=true and contract_id in(select id from oiipcra_oltp.contract_m  " +
                "where contract_status_id=2 and is_active=true  ";

        if(yearId!= null && yearId >0)
        {
            qry += "and finyr_id=:yearId " ;
            sqlParam.addValue("yearId",yearId);
        }

        if (componentId > 0) {
            qry += "and id in (select distinct contract_id from oiipcra_oltp.contract_mapping where is_active=true and activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true))  ";

            sqlParam.addValue("componentId", componentId);
        }

        qry += " )) as cmap on dist.dist_id=cmap.dist_id " +
                "where dist.in_oiipcra=1  " +
                "group by dist.dist_id,dist.district_name " +
                "order by dist.dist_id   ";
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getContractAmount(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name as componentName,coalesce(round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2),0.0) as contractAmount  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join (select distinct contract_id,dist_id from oiipcra_oltp.contract_mapping where is_active=true  " +
                "and activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)) as cmap on dist.dist_id=cmap.dist_id  " +
                "left join oiipcra_oltp.contract_m as cm on cmap.contract_id=cm.id and cm.is_active=true  " ;

        if (yearId!= null && yearId > 0 )
        {
            qry += "and cm.finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
        }

         qry += " where dist.in_oiipcra=1 " +
                " group by dist.dist_id,dist.district_name " +
                " order by dist.dist_id  ";

        sqlParam.addValue("componentId", componentId);

        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getConsultancyWieContractAmount(Integer componentId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = "select distinct dist.dist_id,dist.district_name as componentName, coalesce(round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2),0.0) as consultancyWiseContractAmount " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join (select distinct contract_id,dist_id from oiipcra_oltp.contract_mapping where is_active=true  " +
                "and activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)) as cmap on dist.dist_id=cmap.dist_id  " +
                "left join oiipcra_oltp.contract_m as cm on cmap.contract_id=cm.id and cm.is_active=true  " +
                "and cm.work_type_id=2  " ;

        if (yearId!= null && yearId > 0 )
        {
            qry += "and cm.finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
        }

        qry +=  "where dist.in_oiipcra=1  " +
                "group by dist.dist_id,dist.district_name  " +
                "order by dist.dist_id  ";

        sqlParam.addValue("componentId", componentId);

        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }

    }

    public List<ComponentDto> getWorkWiseContractAmount(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name as componentName, coalesce(round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2),0.0) as workWiseContractAmount " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join (select distinct contract_id,dist_id from oiipcra_oltp.contract_mapping where is_active=true  " +
                "and activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)) as cmap on dist.dist_id=cmap.dist_id  " +
                "left join oiipcra_oltp.contract_m as cm on cmap.contract_id=cm.id and cm.is_active=true  " +
                "and cm.work_type_id=1  " ;

        if(yearId!= null && yearId >0)
        {
            qry += "and cm.finyr_id=:yearId ";
        }

        qry  +=   " where dist.in_oiipcra=1  " +
                  " group by dist.dist_id,dist.district_name  " +
                  " order by dist.dist_id  ";

        sqlParam.addValue("componentId", componentId);

        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getGoodWiseContractAmount(Integer componentId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name as componentName, coalesce(round(sum(contract_amount+(contract_amount*(gst/100)))/10000000,2),0.0) as goodsWiseContractAmount " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join (select distinct contract_id,dist_id from oiipcra_oltp.contract_mapping where is_active=true  " +
                "and activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)) as cmap on dist.dist_id=cmap.dist_id  " +
                "left join oiipcra_oltp.contract_m as cm on cmap.contract_id=cm.id and cm.is_active=true  " +
                "and cm.work_type_id=3  " ;
        if(yearId!= null && yearId >0)
        {
            qry += "and cm.finyr_id=:yearId";
            sqlParam.addValue("yearId",yearId);
        }

        qry +=  " where dist.in_oiipcra=1  " +
                " group by dist.dist_id,dist.district_name  " +
                " order by dist.dist_id  ";

        sqlParam.addValue("componentId", componentId);

        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getUpToDateExpenditure(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name,coalesce(ls4.upToDateExpenditure,0.0) as upToDateExpenditure  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(  " +
                "SELECT round(sum(value)/10000000,2) as upToDateExpenditure,emp.district_id  " +
                "FROM oiipcra_oltp.expenditure_data ed  " +
                "left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id   where ed.is_active=true  " ;

        if(yearId!= null && yearId >0)
        {
            qry += "and ed.finyr_id=:yearId ";
            sqlParam.addValue("yearId",yearId);

        }
         qry  += " and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                 "id=:componentId UNION ALL  " +
                 "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                 "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                 "GROUP BY emp.district_id) as ls4 on ls4.district_id=dist.dist_id  " +
                 "where dist.in_oiipcra=1   ";

        sqlParam.addValue("componentId", componentId);

        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getGoodWisExpenditureAmount(Integer componentId, List<Integer> contractIds, List<Integer> estimateIds,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name,coalesce(ls4.upToDateExpenditure,0.0) as goodsWiseExpenditureAmount  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(  " +
                "SELECT round(sum(value)/10000000,2) as upToDateExpenditure,emp.district_id,emp.contract_id,emp.estimate_id " +
                "FROM oiipcra_oltp.expenditure_data ed  " +
                "left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id   where ed.is_active=true  " ;

        if(yearId!= null && yearId > 0 )
        {
            qry += "and ed.finyr_id=:yearId ";
            sqlParam.addValue("yearId",yearId);
        }

        qry  += "and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "GROUP BY emp.district_id,emp.contract_id,emp.estimate_id) as ls4 on ls4.district_id=dist.dist_id  " +
                "where dist.in_oiipcra=1  ";

        if (contractIds != null && contractIds.size() > 0 && estimateIds != null && estimateIds.size() > 0) {
            qry += "AND ( ls4.contract_id in (:contractIds) or ls4.estimate_id in (:estimateIds) )";
            sqlParam.addValue("contractIds", contractIds);
            sqlParam.addValue("estimateIds", estimateIds);
        } else {
            if (contractIds != null && contractIds.size() > 0) {
                qry += "AND (ls4.contract_id in (:contractIds)) ";
                sqlParam.addValue("contractIds", contractIds);
            } else {
                qry += "AND (ls4.estimate_id in (:estimateIds) ) ";
                sqlParam.addValue("estimateIds", estimateIds);
            }
        }
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<Integer> getEstimateIdsGoodWise(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.activity_estimate_mapping  where work_type=3 and is_active=true ";

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }

        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getContractIdsWorkWise(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.contract_m where work_type_id=1 and is_active=true ";

        if (componentId > 0) {
            qry += "and id in (select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getEstimateIdsWorkWise(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.activity_estimate_mapping  where work_type=1 and is_active=true ";

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }

        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<ComponentDto> getWorkWisExpenditureAmount(Integer componentId, List<Integer> contractIds, List<Integer> estimateIds,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name,coalesce(ls4.upToDateExpenditure,0.0) as workWiseExpenditureAmount  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(  " +
                "SELECT round(sum(value)/10000000,2) as upToDateExpenditure,emp.district_id,emp.contract_id,emp.estimate_id " +
                "FROM oiipcra_oltp.expenditure_data ed  " +
                "left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id   where ed.is_active=true  " ;

        if(yearId!=null && yearId >0)
        {
            qry += "and ed.finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
        }

        qry +=  "and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "GROUP BY emp.district_id,emp.contract_id,emp.estimate_id ) as ls4 on ls4.district_id=dist.dist_id  " +
                "where dist.in_oiipcra=1  ";

         sqlParam.addValue("componentId",componentId);

        if (contractIds != null && contractIds.size() > 0 && estimateIds != null && estimateIds.size() > 0) {
            qry += "AND (ls4.contract_id in (:contractIds) or ls4.estimate_id in (:estimateIds))";
            sqlParam.addValue("contractIds", contractIds);
            sqlParam.addValue("estimateIds", estimateIds);
        } else {
            if (contractIds != null && contractIds.size() > 0) {
                qry += "AND (ls4.contract_id in (:contractIds)) ";
                sqlParam.addValue("contractIds", contractIds);
            } else {
                qry += "AND (ls4.estimate_id in (:estimateIds)) ";
                sqlParam.addValue("estimateIds", estimateIds);
            }
        }
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<Integer> getContractIdsConWise(Integer componentId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.contract_m where work_type_id=2 and is_active=true ";

        if (componentId > 0) {
            qry += "and id in (select contract_id from oiipcra_oltp.contract_mapping where activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE   " +
                    "id=:componentId UNION ALL  " +
                    "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                    "SELECT id FROM terminal_Ids WHERE is_terminal = true)) ";

            sqlParam.addValue("componentId", componentId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }


    public List<Integer> getEstimateIdsConWise(Integer componentId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();

        String qry = " select distinct id from oiipcra_oltp.activity_estimate_mapping  where work_type=2 and is_active=true ";

        if (componentId > 0) {
            qry += "and activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL  " +
                    "              SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id) " +
                    "          SELECT id FROM terminal_Ids WHERE is_terminal = true)";

            sqlParam.addValue("componentId", componentId);
        }
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<ComponentDto> getConWisExpenditureAmount(Integer componentId, List<Integer> contractIds, List<Integer> estimateIds,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select distinct dist.dist_id,dist.district_name,coalesce(ls4.upToDateExpenditure,0.0) as consultancyWiseExpenditureAmount  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(  " +
                "SELECT round(sum(value)/10000000,2) as upToDateExpenditure,emp.district_id,emp.contract_id,emp.estimate_id " +
                "FROM oiipcra_oltp.expenditure_data ed  " +
                "left join oiipcra_oltp.expenditure_mapping emp on emp.expenditure_id= ed.id   where ed.is_active=true  " ;

        if(yearId!=null && yearId >0)
        {
            qry += "and ed.finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
        }

        qry +=  "and emp.activity_id in (WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  " +
                "id=:componentId UNION ALL  " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "GROUP BY emp.district_id,emp.contract_id,emp.estimate_id) as ls4 on ls4.district_id=dist.dist_id  " +
                "where dist.in_oiipcra=1  ";

        if (contractIds != null && contractIds.size() > 0 && estimateIds != null && estimateIds.size() > 0) {
            qry += "AND (ls4.contract_id in (:contractIds) or ls4.estimate_id in (:estimateIds)) ";
            sqlParam.addValue("contractIds", contractIds);
            sqlParam.addValue("estimateIds", estimateIds);
        } else {
            if (contractIds != null && contractIds.size() > 0) {
                qry += "AND (ls4.contract_id in (:contractIds))  ";
                sqlParam.addValue("contractIds", contractIds);
            } else {
                qry += "AND (ls4.estimate_id in (:estimateIds))  ";
                sqlParam.addValue("estimateIds", estimateIds);
            }
        }
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getEstimateAmountByComponentIdForWork(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "Select distinct dist.dist_id,dist.district_name,coalesce(ls4.approxEstCost,0.0) as approxEstCost  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(   " +
                "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost,emp.dist_id  " +
                "from oiipcra_oltp.activity_estimate_mapping as em   " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active=true  " ;

        if(yearId != null && yearId >0)
        {
            qry += "and em.finyr_id=:yearId   ";
            sqlParam.addValue("yearId",yearId);
        }

        qry += " em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "and em.work_type =1  " +
                "group By emp.dist_id) as ls4 on ls4.dist_id=dist.dist_id   " +
                "where dist.in_oiipcra=1    ";

        sqlParam.addValue("componentId",componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getEstimateAmountByComponentIdForConsultancy(Integer componentId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "Select distinct dist.dist_id,dist.district_name,coalesce(ls4.approxEstCost,0.0) as approxEstCost  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(   " +
                "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost,emp.dist_id  " +
                "from oiipcra_oltp.activity_estimate_mapping as em   " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active=true  " ;

         if(yearId!= null && yearId > 0)
         {
             qry += " and em.finyr_id=:yearId  ";
             sqlParam.addValue("yearId",yearId);
         }

         qry += " and em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "and em.work_type =2  " +
                "group By emp.dist_id) as ls4 on ls4.dist_id=dist.dist_id   " +
                "where dist.in_oiipcra=1    ";

        sqlParam.addValue("componentId",componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }


    public List<ComponentDto> getEstimateAmountByComponentIdForGoods(Integer componentId,Integer yearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "Select distinct dist.dist_id,dist.district_name,coalesce(ls4.approxEstCost,0.0) as approxEstCost  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(   " +
                "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost,emp.dist_id  " +
                "from oiipcra_oltp.activity_estimate_mapping as em   " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active=true  " ;

        if(yearId!=null && yearId >0)
        {
            qry += "and em.finyr_id=:yearId ";
            sqlParam.addValue("yearId",yearId);
        }

         qry += " and em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "and em.work_type =3  " +
                "group By emp.dist_id) as ls4 on ls4.dist_id=dist.dist_id   " +
                "where dist.in_oiipcra=1    ";

        sqlParam.addValue("componentId",componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

    public List<ComponentDto> getTotalApproxEstCostByComponentId(Integer componentId,Integer yearId) {

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = " ";

        qry += "Select distinct dist.dist_id,dist.district_name,coalesce(ls4.approxEstCost,0.0) as totalApproxEstCost  " +
                "from oiipcra_oltp.district_boundary as dist  " +
                "left join(   " +
                "select round(sum(em.estimated_amount)/10000000,2) as approxEstCost,emp.dist_id  " +
                "from oiipcra_oltp.activity_estimate_mapping as em   " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as emp on emp.estimate_id= em.id  " +
                "where em.is_active=true  " ;

        if(yearId!= null && yearId > 0)
        {
            qry += " and em.finyr_id=:yearId  ";
            sqlParam.addValue("yearId",yearId);
        }

         qry  += " and em.activity_id in ( WITH RECURSIVE terminal_Ids AS (SELECT id, parent_id, is_terminal FROM oiipcra_oltp.master_head_details WHERE  id=:componentId UNION ALL   " +
                "SELECT mhd.id, mhd.parent_id, mhd.is_terminal FROM oiipcra_oltp.master_head_details mhd INNER JOIN terminal_Ids t ON t.id = mhd.parent_id)  " +
                "SELECT id FROM terminal_Ids WHERE is_terminal = true)  " +
                "group By emp.dist_id) as ls4 on ls4.dist_id=dist.dist_id   " +
                "where dist.in_oiipcra=1    ";

        sqlParam.addValue("componentId",componentId);
        try {
            return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(ComponentDto.class));

        } catch (Exception e) {
            List<ComponentDto> componentDto = new ArrayList<>();
            return componentDto;
        }
    }

}
