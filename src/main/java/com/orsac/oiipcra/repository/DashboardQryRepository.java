package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardQryRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;


    public Double getEstimateAmount(List<Integer> activityId, List<Integer> estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(estimated_amount) as estimatedAmount FROM oiipcra_oltp.activity_estimate_mapping" +
                " WHERE activity_id IN(:activityId)";
        if(estimateId.size()>0){
           qry+= " AND id IN(:estimateId)";
           sqlParam.addValue("estimateId",estimateId);
        }
        sqlParam.addValue("activityId",activityId);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public Double getExpenditureAmount(List<Integer> activityId, List<Integer> estimateId, List<Integer> contractId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT sum(value) FROM oiipcra_oltp.expenditure_data as ed" +
                " LEFT JOIN oiipcra_oltp.expenditure_mapping as em ON ed.id = em.expenditure_id" +
                " WHERE em.activity_id IN(:activityId)";
        if(estimateId.size()>0){
            qry+= " AND em.estimate_id IN(:estimateId)";
            sqlParam.addValue("estimateId",estimateId);
        }
        if(contractId.size()>0){
            qry+= " AND em.contract_id IN(:contractId)";
            sqlParam.addValue("contractId",contractId);
        }
        sqlParam.addValue("activityId",activityId);
        return namedJdbc.queryForObject(qry, sqlParam, Double.class);
    }

    public String getComponentNameDesc(int componentId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT description FROM oiipcra_oltp.master_head_details WHERE id =:componentId";
        sqlParam.addValue("componentId",componentId);
        return namedJdbc.queryForObject(qry, sqlParam, String.class);
    }

    //Query For Dashboard MIP status District Wise
    public List<DashboardStatusOfMIPByDist>getMIPStatusDistEstimateApproved(int districtId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT dbound.dist_id , dbound.district_name AS distName, aetm1.tankCount as estimateApproved FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.activity_estimate_tank_mapping as aetm ON dbound.dist_id = aetm.dist_id" +
                " LEFT JOIN (select id,count(tank_id) OVER (partition by dist_id) as tankCount from oiipcra_oltp.activity_estimate_tank_mapping)aetm1 ON aetm1.id = aetm.id" +
                " LEFT JOIN (select id from oiipcra_oltp.activity_estimate_mapping where approved_status = 2) AS aem ON aem.id = aetm.estimate_id" +
                " WHERE dbound.in_oiipcra = 1";
        if(districtId>0){
            qry+= " AND dbound.dist_id =:districtId";
            sqlParam.addValue("districtId",districtId);
        }
        qry+= " ORDER BY dbound.district_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardStatusOfMIPByDist.class));
    }

    //public List<> getEstimateApproved();

    public List<DashboardStatusOfMIPByDivision>getMIPStatusDivision(int districtId, int divisionId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT mdm.id as divisionId, mdm.mi_division_name as divisionName, aetm1.tankCount as takenUp FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.mi_division_m AS mdm ON mdm.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.activity_estimate_tank_mapping as aetm ON dbound.dist_id = aetm.dist_id" +
                " LEFT JOIN (select id,count(tank_id) OVER (partition by dist_id) as tankCount from oiipcra_oltp.activity_estimate_tank_mapping)aetm1 ON aetm1.id = aetm.id" +
                " LEFT JOIN (select id from oiipcra_oltp.activity_estimate_mapping where approved_status = 2) AS aem ON aem.id = aetm.estimate_id" +
                " WHERE dbound.in_oiipcra = 1 AND dbound.dist_id =:districtId";
        if(divisionId > 0){
            qry+= " AND mdm.mi_division_id =:divisionId";
            sqlParam.addValue("divisionId",divisionId);
        }
        qry+=" ORDER BY mdm.mi_division_name";
        sqlParam.addValue("districtId",districtId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardStatusOfMIPByDivision.class));
    }

    public List<DashboardExpenditureOfMIPByDistrict>getMIPExpenditureDistrict(int districtId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT dbound.dist_id as distId, dbound.district_name as distName, sum(ed.value) as estimatedCost FROM oiipcra_oltp.district_boundary AS dbound \n" +
                " LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.expenditure_mapping as emd on emd.tank_id = tmid.tank_id" +
                " LEFT JOIN oiipcra_oltp.expenditure_data AS ed ON ed.id=emd.expenditure_id" +
                " WHERE dbound.in_oiipcra = 1 ";
        if(districtId > 0){
            qry+= " AND tmid.dist_id =:districtId";
            sqlParam.addValue("districtId",districtId);
        }
        qry+= " GROUP BY dbound.dist_id, dbound.district_name ORDER BY dbound.district_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardExpenditureOfMIPByDistrict.class));
    }

    public List<DashboardExpenditureOfMIPByDivision> getMIPExpenditureDivision(int districtId, int divisionId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT mdm.id as divisionId, mdm.mi_division_name as divisionName, sum(ed.value) as estimatedCost FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.mi_division_m AS mdm ON mdm.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.expenditure_mapping as emd on emd.tank_id = tmid.tank_id" +
                " LEFT JOIN oiipcra_oltp.expenditure_data AS ed ON ed.id=emd.expenditure_id" +
                " WHERE dbound.in_oiipcra = 1 AND dbound.dist_id =:districtId";
        if(divisionId > 0){
            qry+= " AND mdm.mi_division_id =:divisionId";
            sqlParam.addValue("divisionId",divisionId);
        }
        qry+= " GROUP BY mdm.id, mdm.mi_division_name ORDER BY mdm.mi_division_name";
        sqlParam.addValue("districtId",districtId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardExpenditureOfMIPByDivision.class));
    }

    public List<Integer> getTenderIdsStatusCompleted(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id FROM oiipcra_oltp.tender_m WHERE tender_status = 4";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<Integer> getAgreementAwaited(List<Integer> tenderIdsStatusCompleted, Integer districtId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cm1.tenderCount as agrmtAwaited  FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.contract_mapping as cm ON dbound.dist_id = cm.dist_id" +
                " LEFT JOIN (SELECT id, count(tender_id) OVER (partition by dist_id) as tenderCount from oiipcra_oltp.contract_mapping)cm1 ON cm1.id = cm.id" +
                " LEFT JOIN (SELECT tender_id FROM oiipcra_oltp.contract_mapping cm2 WHERE tender_id NOT IN(:tenderIdsStatusCompleted))as cm2 on cm2.tender_id = cm.id" +
                " WHERE dbound.in_oiipcra = 1";
        sqlParam.addValue("tenderIdsStatusCompleted",tenderIdsStatusCompleted);
        if(districtId > 0){
            qry += " AND dbound.dist_id =:districtId";
            sqlParam.addValue("districtId",districtId);
        }
            qry+=" ORDER BY dbound.district_name";
        return namedJdbc.queryForList(qry, sqlParam, Integer.class);
    }

    public List<DashboardStatusToBeEstimatedByDist> getToBeEstimated(int districtId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT dbound.dist_id, dbound.district_name AS distName, aetm1.tankCount as toBeEstimated FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.activity_estimate_tank_mapping as aetm ON dbound.dist_id = aetm.dist_id" +
                " LEFT JOIN (select id,count(tank_id) OVER (partition by dist_id) as tankCount from oiipcra_oltp.activity_estimate_tank_mapping)aetm1 ON aetm1.id = aetm.id" +
                " LEFT JOIN (select id from oiipcra_oltp.activity_estimate_mapping where approved_status NOT IN(2,3)) AS aem ON aem.id = aetm.estimate_id" +
                " WHERE dbound.in_oiipcra = 1";
        if(districtId > 0){
            qry += " AND dbound.dist_id =:districtId";
            sqlParam.addValue("districtId",districtId);
        }
        qry+=" ORDER BY dbound.district_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardStatusToBeEstimatedByDist.class));
    }
    
    public List<DashboardStatusEstimateIdsByDist>getEstimateIdsByDist(int districtId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT dbound.dist_id AS distId, dbound.district_name AS distName, aetm1.estimate_id as estimateIds FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.activity_estimate_tank_mapping as aetm ON dbound.dist_id = aetm.dist_id" +
                " LEFT JOIN oiipcra_oltp.activity_estimate_tank_mapping as aetm1 ON aetm1.id = aetm.id" +
                " LEFT JOIN (select id from oiipcra_oltp.activity_estimate_mapping where approved_status = 2) AS aem ON aem.id = aetm.estimate_id" +
                " WHERE dbound.in_oiipcra = 1";
        if(districtId > 0){
            qry += " AND dbound.dist_id =:districtId";
            sqlParam.addValue("districtId",districtId);
        }
        qry+=" ORDER BY dbound.district_name";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardStatusEstimateIdsByDist.class));
    }

    public Integer getTankIdCountFromContract(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(tank_id) as tankCount FROM oiipcra_oltp.contract_mapping WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Integer getTankIdCountFromExpenditure(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(tank_id) as tankCount FROM oiipcra_oltp.expenditure_mapping WHERE estimate_id =:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Integer getTankIdCountFromTender(int estimateId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT count(tank_id) FROM oiipcra_oltp.tender_level_mapping as tlm" +
                " LEFT JOIN oiipcra_oltp.tender_estimate_mapping as tem ON tem.tender_id = tlm.tender_id" +
                " WHERE tem.estimate_id=:estimateId";
        sqlParam.addValue("estimateId",estimateId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Object getMIPStatusByDivision(Integer districtId, Integer divisionId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT mdm.id as divisionId, mdm.mi_division_name as divisionName, aetm1.tankCount as takenUp FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.mi_division_m AS mdm ON mdm.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.activity_estimate_tank_mapping as aetm ON dbound.dist_id = aetm.dist_id" +
                " LEFT JOIN (select id,count(tank_id) OVER (partition by dist_id) as tankCount from oiipcra_oltp.activity_estimate_tank_mapping)aetm1 ON aetm1.id = aetm.id" +
                " LEFT JOIN (select id from oiipcra_oltp.activity_estimate_mapping where approved_status = 2) AS aem ON aem.id = aetm.estimate_id" +
                " WHERE dbound.in_oiipcra = 1 ";

        qry+=" ORDER BY mdm.mi_division_name";
        sqlParam.addValue("districtId",districtId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardStatusOfMIPByDivision.class));
    }


    public List<DashboardExpenditureOfMIPByDivision> getDashboardExpenditureInMIPByDiv(int districtId, int divisionId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT mdm.id as divisionId, mdm.mi_division_name as divisionName, sum(ed.value) as estimatedCost FROM oiipcra_oltp.district_boundary AS dbound" +
                " LEFT JOIN oiipcra_oltp.mi_division_m AS mdm ON mdm.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.dist_id = dbound.dist_id" +
                " LEFT JOIN oiipcra_oltp.expenditure_mapping as emd on emd.tank_id = tmid.tank_id" +
                " LEFT JOIN oiipcra_oltp.expenditure_data AS ed ON ed.id=emd.expenditure_id" +
                " WHERE dbound.in_oiipcra = 1 ";

        qry+= " GROUP BY mdm.id, mdm.mi_division_name ORDER BY mdm.mi_division_name";
        sqlParam.addValue("districtId",districtId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(DashboardExpenditureOfMIPByDivision.class));
    }
    public List<CivilWorkStatusDivisionDto> getCivilWorkStatusDivisionWise(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select count(distinct tank.id) as noOfTanks,mi_division_name as divisionName,sum(catchment_area_sqkm) as ccaInHa ," +
                "count( tn.tender_notice_id) as worksTakenInTanks," +
                "coalesce(sum(contract.contract_amount), 0.00) as contractAmount," +
                "coalesce(sum(value),0.00)as upToDateExpenditure, " +
                "count(CASE WHEN (contract.contract_status_id=5) THEN 1 end)  AS worksCompleted, " +
                "count(CASE WHEN (contract.contract_status_id=6) THEN 1 end)  AS noOfTanksDropped ," +
                "to_char(sum(catchment_area_sqkm) ,'99G99G99G999D99') as ccaInHaString," +
                "to_char(coalesce(sum(contract_amount),0.00),'99G99G99G999D99') as contractAmountString," +
                "to_char(coalesce(sum(value),0.00),'99G99G99G999D99') as upToDateExpenditureString," +
                "coalesce(sum(contract_amount),0.00)-coalesce(sum(value),0.00) balanceContractValue," +
                "coalesce(sum(estimate.estimated_amount),0.00)as totalEstimateCost,coalesce(sum(estimate.estimated_amount),0.00)-coalesce(sum(contract.contract_amount),0.00) as balanceWorkContract " +
                "from oiipcra_oltp.tank_m_id as tank " +
                "left join oiipcra_oltp.contract_mapping as cm on cm.tank_id=tank.id " +
                "left join oiipcra_oltp.contract_m as contract on contract.id=cm.contract_id " +
                "left join oiipcra_oltp.tender_notice_level_mapping as tn on tn.tank_id=tank.id " +
                "left join oiipcra_oltp.expenditure_mapping as exm on exm.contract_id=contract.id " +
                "left join oiipcra_oltp.expenditure_data as exd on exd.id=exm.expenditure_id " +
                "left join oiipcra_oltp.activity_estimate_tank_mapping as tankestimate on tankestimate.tank_id=tank.id " +
                "left join oiipcra_oltp.activity_estimate_mapping as estimate on estimate.id=tankestimate.estimate_id " +
                "group by mi_division_name order by mi_division_name asc";
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CivilWorkStatusDivisionDto.class));
    }
    public List<CropCycleAyacutDto> getCropCycleAyacut(Integer projectId,String year){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project=String.valueOf(projectId);
        year=year+"_%";

        String qry = "select distinct a.gridcode as gridCode,coalesce(b.area,0.00) as area ,(case when a.gridcode=10 Then 'SINGLE CROP' when a.gridcode=20 Then 'DOUBLE CROP'  when a.gridcode=30 Then 'TRIPLE CROP' else 'No Croping' end) as gridType, a.crop_cycle as year from oiipcra_oltp.crop_cycle_ayacut as a " +
                "left join (select distinct gridcode,round(sum(area),3) as area from oiipcra_oltp.crop_cycle_ayacut " +
                " where project_id=:projectId and crop_cycle like(:year) group by gridcode) as " +
                "  b on a.gridcode=b.gridcode where crop_cycle like(:year)  order by gridcode";
        sqlParam.addValue("year",year);
        sqlParam.addValue("projectId",project);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }
    public List<CropCycleAyacutDto> getCropCycleAyacut1(Integer projectId,List<String> year){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String project=String.valueOf(projectId);
       // year=year+"_%";

        String qry = "select distinct a.gridcode as gridCode,coalesce(b.area,0.00) as area ,"+
                "(case when a.gridcode=10 Then 'SINGLE CROP' when a.gridcode=20 Then 'DOUBLE CROP'  when a.gridcode=30 Then 'TRIPLE CROP' else 'No Croping' end) as gridType "+
                "from oiipcra_oltp.crop_cycle_ayacut as a " +
                "left join (select distinct gridcode,sum(area) as area from oiipcra_oltp.crop_cycle_ayacut where project_id=:projectId ";
        for(int i=0;i<year.size();i++){
            String year1="";
            if(i==0){
                year1=year.get(i)+"_%" ;
                qry+="and crop_cycle like(:year1)";
                sqlParam.addValue("year1",year1);
            }
            else{
                year1=year.get(i)+"_%" ;
                qry+="or crop_cycle like(:year1)";
                sqlParam.addValue("year1",year1);
            }
        }
        qry+=" group by gridcode) as b on a.gridcode=b.gridcode order by gridcode";
        sqlParam.addValue("projectId",project);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CropCycleAyacutDto.class));
    }


}

