package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.bindings.UserListRequest;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.HydrologyRepository;

import com.orsac.oiipcra.dto.RiverBasinDto;
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

import java.security.PublicKey;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class HydrologyRepositoryImpl {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

//    @Autowired
//    private NamedParameterJdbcTemplate namedJdbc;
public int count(String qryStr, MapSqlParameterSource sqlParam) {
    String sqlStr = "SELECT COUNT(*) from (" + qryStr + ") as t";
    Integer intRes = namedJdbc.queryForObject(sqlStr, sqlParam, Integer.class);
    if (null != intRes) {
        return intRes;
    }
    return 0;
}


    public List<RiverBasinDto> getRiverBasinId(){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String sqlQry = "SELECT id, name FROM oiipcra_oltp.river_basin";
        return namedJdbc.query(sqlQry,sqlParam,new BeanPropertyRowMapper<>(RiverBasinDto.class));
    }
    public int getFinYearId( String createdOn){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String sqlQry = "SELECT id FROM oiipcra_oltp.fin_year_m WHERE date(:createdOn) between start_date and end_date";
        sqlParam.addValue("createdOn", createdOn);
        return namedJdbc.queryForObject(sqlQry,sqlParam,Integer.class);
    }

   public Page<HydrologyDataDto> getHydrologylist(HydrologyFilterDto hydrologyFilterDto){
       UserListRequest userListRequest = new UserListRequest();
       userListRequest.setSortOrder("DESC");
       userListRequest.setSortBy("id");
       userListRequest.setSize(14);

       MapSqlParameterSource sqlParam = new MapSqlParameterSource();

       PageRequest pageable = PageRequest.of(hydrologyFilterDto.getPage(), hydrologyFilterDto.getSize(), Sort.Direction.fromString(hydrologyFilterDto.getSortOrder()), hydrologyFilterDto.getSortBy());
       Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
       Integer resultCount = 0;
       String queryString = "";
       queryString += "SELECT hd.id as id,hd.tank_id as tankId, hd.name_of_stream as nameOfStream, hd.max_flood as maxFlood, hd.average_yearly_rainfall as averageYearlyRainfall, " +
               "hd.dependable_rainfall as dependableRainfall, hd.average_monsoon_rainfall as averageMonsoonRainfall, " +
               "hd.dep_mansoon_rainfall as depMansoonRainfall, hd.effective_rain_fall as effectiveRainFall, hd.monsoon_rainfall_distbn as monsoonRainfallDistbn, " +
               "hd.shifting_of_monsoon as shiftingOfMonsoon, hd.live_storage as liveStorage, hd.irrigation_requirement as irrigationRequirement, hd.water_spread_area as waterSpreadArea, " +
               "hd.max_spilling_month as maxSpillingMonth, hd.max_5_day_spilling_qnty as max5DaySpillingQnty, hd.min_temp_degree_c as minTempDegreeC, hd.max_temp_degree_c as maxTempDegreeC, " +
               "hd.relhumidity_winter as relhumidityWinter, hd.relhumidity_rainy as relhumidityRainy, hd.wind_speed_km_day as windSpeedKmDay, " +
               "hd.sunshine_hours_day as sunshineHoursDay, hd.radiation_kj_day as radiationKjDay, hd.average_eto_mm as averageEtoMm, hd.irr_water_req_khariff_ha_mm as irrWaterReqKhariffHaMm, " +
               "hd.irr_water_req_rabi_ha_m as irrWaterReqRabiHaM, hd.dependable_yield_mcm as dependableYieldMcm, " +
               "hd.irrigation_waer_supply_mm as irrigationWaerSupplyMm, hd.class_of_irrigation as classOfIrrigation, hd.extension_of_rabi_ayacut_ha as extension_of_rabi_ayacut_ha, " +
               "hd.gwt_data_avl_from_year as gwtDataAvlFromYear, hd.gwt_dat_avl_upto_year as gwtDatAvlUptoYear, " +
               "hd.gw_level_in_year_past_m as gwLevelInYearPastM, hd.gw_level_in_year_present_m as gwLevelInYearPresentM, hd.gw_level_trend as gwLevelTrend, " +
               "hd.annual_gw_potential_mcm as annualGwPotentialMcm, hd.annual_gw_recharge_by_project_comp as annualGwRechargeByProjectComp, " +
               "hd.gw_proposed_to_be_extracted_for_irrn as gwProposedToBeExtractedForIrrn, hd.utilisation_of_gw_recharge as utilisationOfGwRecharge, " +
               "hd.vol_of_gw_supplementation_required as volOfGwSupplementationRequired, hd.max_vol_of_5_day_gw_drawal_required as maxVolOf5DayGwDrawalRequired, " +
               "hd.rate_of_max_drawal_of_gw_day as rateOfMaxDrawalOfGwDay, hd.no_of_pumps_required_hp_rating as noOfPumpsRequiredHpRating, hd.drawl_capacity_of_pumps as drawlCapacityOfPumps, " +
               "hd.is_active as active, hd.created_by as createdBy, hd.created_on as createdOn, hd.updated_by as updatedBy, hd.updated_on as updatedOn, hd.division_id as divisionId, " +
               "hd.district_id as districtId, dist.district_name AS districtName, hd.block_id as blockId, block.block_name as blockName, div.mi_division_name as divisionName, " +
               "tmid.project_id as projectid, tmid.name_of_the_m_i_p as nameOfTheMip " +
               "FROM oiipcra_oltp.hydrology_data as hd  " +
               "LEFT JOIN oiipcra_oltp.district_boundary AS dist ON hd.district_id = dist.dist_id  " +
               "LEFT JOIN oiipcra_oltp.block_boundary AS block ON hd.block_id = block.block_id " +
               "LEFT JOIN oiipcra_oltp.mi_division_m AS div ON hd.division_id = div.id " +
               "LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON hd.tank_id = tmid.id " +
               "WHERE hd.is_active = TRUE ";

       if(hydrologyFilterDto.getTankId() != null && hydrologyFilterDto.getTankId()>0){
           queryString += " AND hd.tank_id =:tankId";
           sqlParam.addValue("tankId", hydrologyFilterDto.getTankId());
       }
       if(hydrologyFilterDto.getBlockId()!= null && hydrologyFilterDto.getBlockId()>0){
           queryString += " AND hd.block_id =:blockId";
           sqlParam.addValue("blockId", hydrologyFilterDto.getBlockId());
       }
       if(hydrologyFilterDto.getDivisionId() != null && hydrologyFilterDto.getDivisionId()>0){
           queryString += " AND hd.division_id =:divisionId";
           sqlParam.addValue("divisionId", hydrologyFilterDto.getDivisionId());
       }
       if(hydrologyFilterDto.getDistId() != null && hydrologyFilterDto.getDistId()>0){
           queryString += " AND hd.district_id =:distId";
           sqlParam.addValue("distId", hydrologyFilterDto.getDistId());
       }

       queryString += " ORDER BY hd."+order.getProperty() + " "+ order.getDirection().name();
       resultCount = count(queryString, sqlParam);
       queryString += " LIMIT "+pageable.getPageSize() + " OFFSET "+ pageable.getOffset();
       List<HydrologyDataDto> hydrologyDataDtoList  = namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(HydrologyDataDto.class));
       return new PageImpl<>(hydrologyDataDtoList,pageable,resultCount);
   }

    public Integer  getHydrologyByProjectId(int id){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "";
        queryString += "SELECT id as id FROM oiipcra_oltp.hydrology_data WHERE tank_id=:id ORDER BY created_on DESC limit 1";
        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(queryString, sqlParam, Integer.class);
    }
    public Integer  getDprByProjectId(int projectId){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString ="SELECT id as id FROM oiipcra_oltp.dpr_information WHERE tank_id=:projectId ORDER BY created_on DESC limit 1";
        sqlParam.addValue("projectId", projectId);
        return namedJdbc.queryForObject(queryString, sqlParam, Integer.class);
    }

    public Integer  getDamByProjectId(int projectId){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString ="SELECT id as id FROM oiipcra_oltp.dam_weir_details WHERE tank_id=:projectId ORDER BY created_on DESC limit 1";
        sqlParam.addValue("projectId", projectId);
        return namedJdbc.queryForObject(queryString, sqlParam, Integer.class);
    }

    public HydrologyDataDto  getHydrologyById(int hydrologyId){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "";
        queryString += "SELECT hd.id as id, hd.tank_id as tankId, hd.name_of_stream as nameOfStream, hd.max_flood as maxFlood, hd.average_yearly_rainfall as averageYearlyRainfall,  " +
                "hd.dependable_rainfall as dependableRainfall, hd.average_monsoon_rainfall as averageMonsoonRainfall, hd.dep_mansoon_rainfall as depMansoonRainfall, " +
                "hd.effective_rain_fall as effectiveRainFall, hd.monsoon_rainfall_distbn as monsoonRainfallDistbn, hd.shifting_of_monsoon as shiftingOfMonsoon, hd.live_storage as liveStorage,  " +
                "hd.irrigation_requirement as irrigationRequirement, hd.water_spread_area as waterSpreadArea, month.name as maxSpillingMonthName,  " +
                "hd.max_5_day_spilling_qnty as max5DaySpillingQnty, hd.min_temp_degree_c as minTempDegreeC, hd.max_temp_degree_c as maxTempDegreeC, hd.relhumidity_winter as relhumidityWinter,  " +
                "hd.relhumidity_rainy as relhumidityRainy, hd.wind_speed_km_day as windSpeedKmDay, hd.sunshine_hours_day as sunshineHoursDay, hd.radiation_kj_day as radiationKjDay,  " +
                "hd.average_eto_mm as averageEtoMm, hd.irr_water_req_khariff_ha_mm as irrWaterReqKhariffHaMm, hd.irr_water_req_rabi_ha_m as irrWaterReqRabiHaM,  " +
                "hd.dependable_yield_mcm as dependableYieldMcm, hd.irrigation_waer_supply_mm as irrigationWaerSupplyMm, hd.class_of_irrigation as classOfIrrigation,  " +
                "hd.extension_of_rabi_ayacut_ha as extension_of_rabi_ayacut_ha, hd.gwt_data_avl_from_year as gwtDataAvlFromYear, hd.gwt_dat_avl_upto_year as gwtDatAvlUptoYear,  " +
                "hd.gw_level_in_year_past_m as gwLevelInYearPastM, hd.gw_level_in_year_present_m as gwLevelInYearPresentM, glt.name as gwLevelTrendName,  " +
                "annual_gw_potential_mcm as annualGwPotentialMcm, annual_gw_recharge_by_project_comp as annualGwRechargeByProjectComp,  " +
                "hd.gw_proposed_to_be_extracted_for_irrn as gwProposedToBeExtractedForIrrn, hd.utilisation_of_gw_recharge as utilisationOfGwRecharge,  " +
                "hd.vol_of_gw_supplementation_required as volOfGwSupplementationRequired, hd.max_vol_of_5_day_gw_drawal_required as maxVolOf5DayGwDrawalRequired, " +
                "hd.rate_of_max_drawal_of_gw_day as rateOfMaxDrawalOfGwDay, hd.no_of_pumps_required_hp_rating as noOfPumpsRequiredHpRating, hd.drawl_capacity_of_pumps as drawlCapacityOfPumps, " +
                "hd.is_active as active, hd.created_by as createdBy, hd.created_on as createdOn, hd.updated_by as updatedBy, hd.updated_on as updatedOn, hd.division_id as divisionId,  " +
                "hd.district_id as districtId, hd.block_id as blockId, tmid.name_of_the_m_i_p as nameOfTheMip, tmid.project_id as projectId, tmid.dept_dist_name as districtName,  " +
                "tmid.dept_block_name as blockName, tmid.mi_division_name as divisionName " +
                "FROM oiipcra_oltp.hydrology_data AS hd " +
                "LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON hd.tank_id = tmid.id " +
                "LEFT JOIN oiipcra_oltp.month_m AS month ON hd.max_spilling_month = month.id " +
                "LEFT JOIN oiipcra_oltp.gw_level_trend glt ON hd.gw_level_trend = glt.id " +
                "WHERE hd.id=:hydrologyId";
        sqlParam.addValue("hydrologyId", hydrologyId);
        return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(HydrologyDataDto.class));
    }
    public List<CropDetailsEntity> getCropDetailsByHydroId(int hydrologyId){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "";
        queryString += "SELECT id id, hydrology_data_id hydrologyDataId, crop_type cropType, crop_area cropArea, water_demand_in_ha waterDemandInHa, " +
                " water_available_in_ha waterAvailableInHa, is_active active, created_by createdBy, created_on createdOn, updated_by updatedBy, updated_on updatedOn " +
                " FROM oiipcra_oltp.crop_details  WHERE hydrology_data_id=:hydrologyId ";
        sqlParam.addValue("hydrologyId", hydrologyId);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(CropDetailsEntity.class));
    }
    public PageIndexEntity  getPageIndexByHydroId(int hydrologyId){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "SELECT id id, introduction introduction, hydrology_design hydrologyDesign, project_features projectFeatures, cost_benefits costBenefits, cost_estimat_of_impt costEstimatOfImpt, post_operation_m postOperationM, " +
                "implementn_schedule implementnSchedule, drawings drawings, is_active active, created_by createdBy, created_on createdOn, updated_by updatedBy, updated_on updatedOn, hydrology_data_id hydrologyDataId " +
                " FROM oiipcra_oltp.page_index  WHERE hydrology_data_id=:hydrologyId";
        sqlParam.addValue("hydrologyId", hydrologyId);
        return namedJdbc.queryForObject(queryString, sqlParam, new BeanPropertyRowMapper<>(PageIndexEntity.class));
    }
    public List<CcaRestoreDto>  getCcaRestoreByHydroId(int hydrologyId){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String queryString = "SELECT cca.id as id, cca.hydrology_data_id as hydrologyDataId, cca.finyr as FinYr, cca.cca_restore as ccaRestore, cca.expenditure as expenditure, cca.irr, " +
                "cca.benifit_cost_ratio as benifitCostRatio,  cca.is_active as active, cca.created_by as createdBy, cca.created_on as createdOn, cca.updated_by as updatedBy, " +
                "cca.updated_on as updatedOn, finyr.name as finYrName " +
                "FROM oiipcra_oltp.cca_restore as cca " +
                "left join oiipcra_oltp.fin_year_m as finyr on cca.finYr = finyr.id " +
                "WHERE hydrology_data_id=:hydrologyId";
        sqlParam.addValue("hydrologyId", hydrologyId);
        return namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(CcaRestoreDto.class));
    }


//    @Override
    public DprInformationDto getDprInfo(Integer id) {
            MapSqlParameterSource sqlParam = new MapSqlParameterSource();
            String qry = "SELECT dpr.id, dpr.tank_id,tank.name_of_the_m_i_p as tankName,tank.designed_cca_kharif_ha as designedCcaKharifHa,tank.designed_cca_rabi_ha as designedCcaRabiHa, dpr.observerd_high_flood_level, dpr.met_station_name, dpr.high_land_ha, dpr.medium_land_ha, dpr.low_land_ha,  " +
                    "dpr.water_availability_period, dpr.gross_command_area_gca_ha, dpr.existing_khariff_paddy_area_ha, dpr.lost_ayacut_ha, dpr.estd_cost_of_imp_rs, dpr.estimate_for_cada_rs,  " +
                    "dpr.cost_ha_improvement_rs, dpr.cost_of_cada_rs,dpr.is_active as active,dpr.created_by,dpr.created_on,dpr.updated_by,dpr.updated_on, tank.project_id as projectId,  " +
                    "tank.dept_dist_name as districtName, tank.mi_division_name as divisionName, tank.sub_division_id, misubdiv.mi_sub_division_name as subDivisionName, " +
                    "tank.section_id, misec.mi_section_name as sectionName, tank.designed_cca_kharif_ha as designedCcaKharifHa, tank.designed_cca_rabi_ha as designedCcaRabiHa,  " +
                    "ppd.pp_members as ppMembers, fy.name as formationYear, ppd.re_election_done as reElectionDone, tank.dept_block_name as blockName  " +
                    "FROM oiipcra_oltp.dpr_information as dpr " +
                    "left join oiipcra_oltp.tank_m_id as tank on dpr.tank_id=tank.id " +
                    "left join oiipcra_oltp.mi_subdivision_m as misubdiv on tank.sub_division_id = misubdiv.mi_sub_division_id  " +
                    "left join oiipcra_oltp.mi_section_m as misec on tank.section_id = misec.section_id  " +
                    "left join oiipcra_oltp.pani_panchayat_details as ppd on dpr.id = ppd.dpr_information_id  " +
                    "left join oiipcra_oltp.fin_year_m as fy on ppd.formation_year = fy.id " +
                    "where dpr.id=:dprId ";
            sqlParam.addValue("dprId",id);
            return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(DprInformationDto.class));
    }

    public List<PaniPanchayatListDto> getAllPanipanchayatDetails(Integer blockId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT ppm.id, ppm.pani_panchayat_name AS paniPanchayatName FROM oiipcra_oltp.pani_panchayat_m AS ppm WHERE ppm.block_id=:blockId";
        sqlParam.addValue("blockId",blockId);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PaniPanchayatListDto.class));
    }
    public Page<DprInformationDto> getAllDpr(TankOtherDetailsListingDto details ) {
        PageRequest pageable = PageRequest.of(details.getPage(), details.getSize(), Sort.Direction.fromString(details.getSortOrder()), details.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        int resultCount=0;
        String queryString = "SELECT dpr.id,tank.name_of_the_m_i_p as tankName,status.name as approvalStatus,dpr.is_active as active,dpr.met_station_name, tank.project_id as projectId, tank.dept_dist_name as districtName,  " +
                "tank.mi_division_name as divisionName, tank.sub_division_id, misubdiv.mi_sub_division_name as subDivisionName, tank.section_id, misec.mi_section_name as sectionName, " +
                "tank.dept_block_name as blockName " +
                "from oiipcra_oltp.dpr_information as dpr " +
                "left join  oiipcra_oltp.tank_m_id as tank on tank.id=dpr.tank_id  " +
                "left join oiipcra_oltp.mi_subdivision_m as misubdiv on tank.sub_division_id = misubdiv.mi_sub_division_id  " +
                "left join oiipcra_oltp.mi_section_m as misec on tank.section_id = misec.section_id  " +
                "left join oiipcra_oltp.approval_status_m as status on status.id=dpr.approved_status where dpr.is_active=true ";
        if(details.getApprovedStatus()>0){
            queryString+="AND dpr.approved_status=:status ";
            sqlParam.addValue("status",details.getApprovedStatus());
        }
        if(details.getTankId()>0){
            queryString+="AND tank.id=:tankId ";
            sqlParam.addValue("tankId",details.getTankId());
        }
        if(details.getDistId()>0){
            queryString+="AND tank.dist_id=:distId ";
            sqlParam.addValue("distId",details.getDistId());
        }
        if(details.getBlockId()>0){
            queryString+="AND tank.block_id=:blockId ";
            sqlParam.addValue("blockId",details.getBlockId());
        }
        if(details.getDivisionId()>0){
            queryString+="AND tank.mi_division_id=:divisionId";
            sqlParam.addValue("divisionId",details.getDivisionId());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (details.getUploadFrom() != null && !details.getUploadFrom().isEmpty()) {
            queryString += " AND date(dpr.created_on) >= :uploadFromDate";
            Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(details.getUploadFrom());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
        }
        if (details.getUploadTo() != null && !details.getUploadTo().isEmpty()) {
            queryString += " AND date(dpr.created_on) <= :uploadToDate";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(details.getUploadTo());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
        }
        resultCount = count(queryString, sqlParam);
        queryString += " ORDER BY dpr." + order.getProperty() + " " + order.getDirection().name();
        queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<DprInformationDto> dprInfo=namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(DprInformationDto.class));
        return new PageImpl<>(dprInfo, pageable, resultCount);
    }

//    @Override
    public List<CatchmentDetailsDto> getCatchmentDetails(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, tank_id, catchment_area_type, catchment_area_details, catchment_area_sqkm, created_by,"+
                     "created_on, updated_by, updated_on, dpr_information_id " +
                     "FROM oiipcra_oltp.catchment_details where dpr_information_id=:dprId";
        sqlParam.addValue("dprId",id);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(CatchmentDetailsDto.class));
    }

//    @Override
    public List<SoilDetailsDto> getSoilDetails(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id, tank_id, soil_type, soil_type_details, soil_type_area_sqkm, created_by, created_on, updated_by,"+
                     "updated_on, dpr_information_id FROM oiipcra_oltp.soil_details where dpr_information_id=:dprId";
        sqlParam.addValue("dprId",id);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(SoilDetailsDto.class));
    }

//    @Override
    public List<PaniPanchayatDetailsDto> getPaniPanchayatDetails(Integer id) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT pp.id, pp.tank_id, pp.pani_panchayat_id as paniPanchayatId, tmid.dept_block_name as blockName, ppm.pani_panchayat_name as paniPanchayatName, pp.formation_year, pp.pp_members, pp.re_election_done,  " +
                "pp.is_active as active, pp.created_by, pp.created_on, pp.updated_by, pp.updated_on, pp.dpr_information_id, finyr.name as finyrName " +
                "FROM oiipcra_oltp.pani_panchayat_details as pp " +
                "left join oiipcra_oltp.tank_m_id as tmid on pp.tank_id = tmid.id  " +
                "left join oiipcra_oltp.pani_panchayat_m as ppm on pp.pani_panchayat_id= ppm.id " +
                "left join oiipcra_oltp.fin_year_m as finyr on pp.pani_panchayat_id = finyr.id " +
                "where pp.dpr_information_id=:dprId";
        sqlParam.addValue("dprId", id);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(PaniPanchayatDetailsDto.class));
    }
        public List<GwLevelTrendDto> getGwLevelTrend() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetGwLevelTrend = "select id,name from oiipcra_oltp.gw_level_trend ";
        return namedJdbc.query(GetGwLevelTrend,sqlParam,new BeanPropertyRowMapper<>(GwLevelTrendDto.class));
    }

    public List<WaterSourceMDto> getWaterSource() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetWaterSource = "SELECT water.id, water.name from oiipcra_oltp.water_source_m  as water ";
        return namedJdbc.query(GetWaterSource,sqlParam,new BeanPropertyRowMapper<>(WaterSourceMDto.class));
    }

    public List<TankWeirCrestDto> getTankWeirCrest() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetTankWeirCrest = "SELECT tank.id, tank.name from oiipcra_oltp.tank_weir_crest as tank";
        return namedJdbc.query(GetTankWeirCrest,sqlParam,new BeanPropertyRowMapper<>(TankWeirCrestDto.class));
    }

    public List<NatureOfWeirCrestDto> getNatureOfWeir() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetNatureOfWeir = "SELECT nature.id, nature.name from oiipcra_oltp.nature_of_weir_crest as nature ";
        return namedJdbc.query(GetNatureOfWeir,sqlParam,new BeanPropertyRowMapper<>(NatureOfWeirCrestDto.class));
    }

    public List<BodyWallTypeDto> getBodyWallType() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetBodyWallType = "SELECT id, name from oiipcra_oltp.body_wall_type";
        return namedJdbc.query(GetBodyWallType,sqlParam,new BeanPropertyRowMapper<>(BodyWallTypeDto.class));

    }

    public List<AbutmentsTypeDto> getAbutmentsType() {
       MapSqlParameterSource sqlParam = new MapSqlParameterSource();
       String GetAbutmentsType = "SELECT id,name from oiipcra_oltp.abutments_type";
       return namedJdbc.query(GetAbutmentsType,sqlParam,new BeanPropertyRowMapper<>(AbutmentsTypeDto.class));
    }

    public TankOtherDetailsDto getTankOtherDetailsById(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
            String qry = "SELECT tankother.id AS tankOtherDetailsId, tankother.present_ayacut_khariff, tankother.present_ayacut_rabi, tankother.agro_climatic_zone, " +
                    "tankother.nearest_village, tankother.population_of_nearest_village, tankother.project_constructed_year, " +
                    "tankother.river_basin, river.name AS riverbasinName, orvb.revenue_village_name AS nearestVillageName, finyr.name as finYrName, " +
                    "tmid.id AS tankid, tmid.project_id, tmid.name_of_the_m_i_p AS nameOfTheMip, tmid.mi_division_name, tmid.mi_division_id, tmid.dept_dist_name, tmid.dept_block_name, tmid.dept_gp_name, " +
                    "tmid.category, tmid.type, tmid.latitude, tmid.longitude, tmid.catchment_area_sqkm, tmid.designed_cca_kharif_ha, tmid.designed_cca_rabi_ha, tmid.project_id as projectId,tankother.topo_sheet_no as topoSheetNo " +
                    "FROM oiipcra_oltp.tank_other_details AS tankother " +
                    "LEFT JOIN oiipcra_oltp.river_basin AS river ON tankother.river_basin = river.id " +
                    "LEFT JOIN oiipcra_oltp.odisha_revenue_village_boundary_2021 AS orvb ON tankother.nearest_village = orvb.village_id " +
                    "LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.id = tankother.tank_id " +
                    "LEFT JOIN oiipcra_oltp.fin_year_m AS finyr ON finyr.id = tankother.project_constructed_year " +
                    "WHERE tankother.id=:id ";
        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TankOtherDetailsDto.class));
    }

    public List<CroppingPatternDto> getCroppingPatternById(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT cropp.id, cropp.tank_id, vil.revenue_village_name AS villageName, cropp.ayacut_in_ha, cropp.kh_paddy_ha, cropp.kh_non_paddy_ha, cropp.rabi_paddy_ha, cropp.rabi_non_paddy_ha " +
                "FROM oiipcra_oltp.cropping_pattern AS cropp " +
                "LEFT JOIN oiipcra_oltp.village_boundary AS vil ON vil.gid = cropp.village_id " +
                "WHERE cropp.tank_other_details_id=:id";
        sqlParam.addValue("id",id);
        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CroppingPatternDto.class));
    }

    public HistoricalDetailsDto getHistoricalDetailsById(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT his.id, his.tank_id, his.previous_major_investments, his.fin_year, his.scheme_funded, his.pwt_held_date, his.reno_work_taken_up, " +
                "his.imp_proposed_now_1 AS impProposedNow1, his.imp_proposed_now_2 AS impProposedNow2, finyr.name as finyrname " +
                "FROM oiipcra_oltp.historical_details AS his " +
                "LEFT JOIN oiipcra_oltp.fin_year_m AS finyr ON his.fin_year = finyr.id " +
                "WHERE his.tank_other_details_id=:id";
        sqlParam.addValue("id",id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(HistoricalDetailsDto.class));
    }

    public DemographicDetails getDemographicDetails(Integer id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT demo.id, demo.tank_id, demo.no_of_villages_in_command_area, demo.total_population_of_command, demo.percentage_of_sc_population, demo.percentage_of_st_population, " +
                "demo.percentage_of_obc_population, demo.total_population_of_the_block, demo.population_of_male_in_the_block, demo.population_of_female_in_the_block, " +
                "demo.male_literacy_in_the_block_perc, demo.female_literacy_in_the_block_perc, demo.general_profession_of_population, demo.perc_agricultural_labour_in_the_block, " +
                "demo.perc_of_cultivators_in_the_block, demo.perc_of_non_working_population, demo.livestock_population, demo.fishermen_population, demo.year_of_census_data, " +
                "demo.present_funding_scheme, demo.present_funding_amount_in_lakh FROM oiipcra_oltp.demographic_details AS demo " +
                "WHERE demo.tank_other_details_id=:id";
        sqlParam.addValue("id", id);
        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(DemographicDetails.class));
    }

    public Integer getTankOtherDetailsByProject(Integer tankId){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT id as tankId FROM oiipcra_oltp.tank_other_details WHERE tank_id=:tankId ORDER BY created_on DESC limit 1";
        sqlParam.addValue("tankId", tankId);
        return namedJdbc.queryForObject(qry, sqlParam, Integer.class);
    }

    public Page<TankOtherDetailsListingDto> getAllTankOtherDetailsList(TankOtherDetailsListingDto tankOtherDetailsListingDto){
        PageRequest pageable = PageRequest.of(tankOtherDetailsListingDto.getPage(), tankOtherDetailsListingDto.getSize(), Sort.Direction.fromString(tankOtherDetailsListingDto.getSortOrder()), tankOtherDetailsListingDto.getSortBy());
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
        Integer resultCount = 0;
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "";
        qry += "SELECT tankother.id as id,tankother.tank_id AS tankid, tmid.name_of_the_m_i_p AS nameOfTheMip, tmid.mi_division_name, tmid.mi_division_id, tmid.dept_dist_name, " +
                "tmid.dept_block_name, tmid.dept_gp_name, tankother.created_on, tmid.project_id FROM oiipcra_oltp.tank_other_details AS tankother " +
                "LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.id = tankother.tank_id " +
                " WHERE tankother.is_active=TRUE ";

//                "SELECT tmid.id AS tankid, tmid.name_of_the_m_i_p, tmid.mi_division_name, tmid.mi_division_id, tmid.dept_dist_name, " +
//                "tmid.dept_block_name, tmid.dept_gp_name, tankother.created_on FROM oiipcra_oltp.tank_other_details AS tankother " +
//                "LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.tank_id = tankother.id WHERE tmid.isActive=TRUE ";

        if(tankOtherDetailsListingDto.getDistId() > 0){
            qry += " AND tmid.dist_id=:distId";
            sqlParam.addValue("distId", tankOtherDetailsListingDto.getDistId());
        }
        if(tankOtherDetailsListingDto.getBlockId() > 0){
            qry += " AND tmid.block_id=:blockId";
            sqlParam.addValue("blockId", tankOtherDetailsListingDto.getBlockId());
        }
        if(tankOtherDetailsListingDto.getDivisionId() > 0){
            qry += " AND tmid.mi_division_id=:divisionId";
            sqlParam.addValue("divisionId", tankOtherDetailsListingDto.getDivisionId());
        }
        if(tankOtherDetailsListingDto.getTankId() > 0) {
            qry += " AND tmid.tank_id=:tankId";
            sqlParam.addValue("tankId", tankOtherDetailsListingDto.getTankId());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (tankOtherDetailsListingDto.getUploadFrom() != null && !tankOtherDetailsListingDto.getUploadFrom().isEmpty()) {
            qry += " AND date(tankother.created_on) >= :uploadFrom";
            java.util.Date uploadFromDate = null;
            try {
                uploadFromDate = format.parse(tankOtherDetailsListingDto.getUploadFrom());
            } catch (Exception exception) {
                log.info("From Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadFrom", uploadFromDate, Types.DATE);
        }
        if (tankOtherDetailsListingDto.getUploadTo() != null && !tankOtherDetailsListingDto.getUploadTo().isEmpty()) {
            qry += " AND date(tankother.created_on) <= :uploadTo";
            Date uploadToDate = null;
            try {
                uploadToDate = format.parse(tankOtherDetailsListingDto.getUploadTo());
            } catch (Exception exception) {
                log.info("To Date Parsing exception : {}", exception.getMessage());
            }
            sqlParam.addValue("uploadTo", uploadToDate, Types.DATE);
        }
        if(tankOtherDetailsListingDto.getApprovedStatus() > 0) {
            qry += " AND tankother.approved_status=:approvedStatus";
            sqlParam.addValue("approvedStatus", tankOtherDetailsListingDto.getApprovedStatus());
        }

        qry += " ORDER BY tankother."+order.getProperty() + " "+ order.getDirection().name();
        resultCount = count(qry, sqlParam);
        qry += " LIMIT "+pageable.getPageSize() + " OFFSET "+ pageable.getOffset();

        List<TankOtherDetailsListingDto> tankOtherDetailsListingDtos = namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(TankOtherDetailsListingDto.class));
        return new PageImpl<>(tankOtherDetailsListingDtos,pageable,resultCount);
    }
    public boolean updateDprApproval(Integer dprId, ApproveStatusDto approvalRequest){

        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="UPDATE oiipcra_oltp.dpr_information " +
                "SET approved_status=:approveStatus,approved_on=:approvalDate,approved_by=:approvedBy,approved_remarks=:approvedRemarks " +
                " WHERE id=:id  ";

        sqlParam.addValue("id",dprId);
        sqlParam.addValue("approveStatus",approvalRequest.getApprovalStatus());
        sqlParam.addValue("approvalDate",approvalRequest.getApprovalDate());
        sqlParam.addValue("approvedBy",approvalRequest.getApprovedBy());
        sqlParam.addValue("approvedRemarks",approvalRequest.getApprovedRemarks());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }

    public boolean updateTankOtherInfoApproval(Integer tankOtherInfoId, ApproveStatusDto approvalRequest){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="UPDATE oiipcra_oltp.tank_other_details " +
                "SET approved_status=:approveStatus,approved_on=:approvalDate,approved_by=:approvedBy,approved_remarks=:approvedRemarks " +
                " WHERE id=:id  ";

        sqlParam.addValue("id",tankOtherInfoId);
        sqlParam.addValue("approveStatus",approvalRequest.getApprovalStatus());
        sqlParam.addValue("approvalDate",approvalRequest.getApprovalDate());
        sqlParam.addValue("approvedBy",approvalRequest.getApprovedBy());
        sqlParam.addValue("approvedRemarks",approvalRequest.getApprovedRemarks());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }
    public boolean updateHydrologyDataApproval(Integer hydrologyDataId, ApproveStatusDto approvalRequest){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="UPDATE oiipcra_oltp.hydrology_data " +
                "SET approved_status=:approveStatus,approved_on=:approvalDate,approved_by=:approvedBy,approved_remarks=:approvedRemarks " +
                " WHERE id=:id  ";

        sqlParam.addValue("id",hydrologyDataId);
        sqlParam.addValue("approveStatus",approvalRequest.getApprovalStatus());
        sqlParam.addValue("approvalDate",approvalRequest.getApprovalDate());
        sqlParam.addValue("approvedBy",approvalRequest.getApprovedBy());
        sqlParam.addValue("approvedRemarks",approvalRequest.getApprovedRemarks());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }
    public boolean updateDamDetailsApproval(Integer damDetailsId, ApproveStatusDto approvalRequest){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry ="UPDATE oiipcra_oltp.dam_weir_details " +
                "SET approved_status=:approveStatus,approved_on=:approvalDate,approved_by=:approvedBy,approved_remarks=:approvedRemarks " +
                " WHERE id=:id  ";

        sqlParam.addValue("id",damDetailsId);
        sqlParam.addValue("approveStatus",approvalRequest.getApprovalStatus());
        sqlParam.addValue("approvalDate",approvalRequest.getApprovalDate());
        sqlParam.addValue("approvedBy",approvalRequest.getApprovedBy());
        sqlParam.addValue("approvedRemarks",approvalRequest.getApprovedRemarks());

        int update = namedJdbc.update(qry, sqlParam);
        boolean result=false;
        if(update>0){
            result=true;
        }
        return result;
    }

    public DamWeirDetailsDto getDamById(Integer damId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetDamById = "SELECT dam.id,dam.tank_id,tank.name_of_the_m_i_p as nameOfTheMip,dam.name_of_stream,dam.water_source,ws.name as waterSourceName,dam.dry_weather_flow_rate_of_water_source,dam.maximum_height_of_dam_m,  " +
                "dam.length_of_dam_m,dam.present_top_width_of,dam.upstream_pitching_riprap_provided,dam.status_of_pitching,dam.down_stream_slope_drains,  " +
                "dam.status_of_down_stream,dam.status_of_rock_toe,dam.rock_toe_if_provided,dam.v_notch_down_stream_of_rocktoe,dam.status_of_v_notch,  " +
                "dam.instrumentation_installed,dam.status_of_instrumentation,dam.tbl_in_m,dam.frl_in_m,dam.mwl_in_m,dam.dsl_in_m,  " +
                "dam.length_of_diversion,dam.length_of_over_flow,dam.type_of_weir_crest ,tw.name as tankWeir,dam.weir_crest_is,nw.name as natureWeir,  " +
                "dam.crest_level_m,dam.max_pond_level,  " +
                "dam.width_of_crest,dam.up_stream_appron_level,dam.down_stream_appron_level,dam.no_of_scour_vents,dam.size_of_scour_vent, " +
                "dam.down_stream_appron_length,dam.type_of_construction_of_bodywall,dam.type_of_construction_abutments,dam.status_of_body_wall,  " +
                "dam.status_of_abutments,dam.status_of_return_walls,dam.status_of_down,dam.status_of_cut_off,dam.status_of_end_sill, " +
                "dam.status_of_ss_gates,dam.status_of_hr_gates, dam.approved_status,ap.name as status,dam.approved_remarks,tank.project_id, tank.dept_dist_name as distName,tank.dept_block_name as blockName,tank.type  " +
                ",tw.name as tankWeirCrestName, nw.name as natureOfWeirCrestName,  bt.name as bodyWallName, abt.name as abutmentsName " +
                "from oiipcra_oltp.dam_weir_details as dam   " +
                "left join oiipcra_oltp.tank_m_id as tank on tank.id = dam.tank_id  " +
                "left join oiipcra_oltp.water_source_m as ws on ws.id =dam.water_source   " +
                "left join oiipcra_oltp.nature_of_weir_crest as nw on nw.id = dam.weir_crest_is  " +
                "left join oiipcra_oltp.tank_weir_crest as tw on tw.id = dam.type_of_weir_crest   " +
                "left join oiipcra_oltp.approval_status_m as ap on ap.id = dam.approved_status  " +
                " left join oiipcra_oltp.body_wall_type as bt on bt.id=dam.type_of_construction_of_bodywall " +
                " left join oiipcra_oltp.abutments_type as abt on abt.id=dam.type_of_construction_abutments " +
                "WHERE dam.is_active =true AND dam.id=:damId ";
        sqlParam.addValue("damId",damId);
        return namedJdbc.queryForObject(GetDamById,sqlParam,new BeanPropertyRowMapper<>(DamWeirDetailsDto.class));
    }

    public List<AreaCapacityCurve> getAreaDetailsByDamId(Integer damId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String GetAreaDetailsByDamId = "SELECT area.id,area.tank_id,area.lowest_res_contour_m,area.contour_interval_in_m,area.contour_level_in_m,  " +
                                       "area.squared_area_in_sqm,area.dam_weir_details from oiipcra_oltp.area_capacity_curve as area  " +
                                       "WHERE area.dam_weir_details=:damId";
        sqlParam.addValue("damId",damId);
        return namedJdbc.query(GetAreaDetailsByDamId,sqlParam,new BeanPropertyRowMapper<>(AreaCapacityCurve.class));
    }

    public Page<DamWeirDetailsDto> getAllDam(TankOtherDetailsListingDto tankOtherDetailsListingDto) {
    PageRequest pageable = PageRequest.of(tankOtherDetailsListingDto.getPage(), tankOtherDetailsListingDto.getSize(), Sort.Direction.fromString(tankOtherDetailsListingDto.getSortOrder()), tankOtherDetailsListingDto.getSortBy());
            Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : new Sort.Order(Sort.Direction.DESC, "id");
            MapSqlParameterSource sqlParam = new MapSqlParameterSource();
            int resultCount=0;
            String queryString = "SELECT dam.id,dam.tank_id,tank.name_of_the_m_i_p as nameOfTheMip ,status.name as approvalStatus,dam.is_active as active,dam.name_of_stream as nameOfStream,  " +
                    "tank.dept_dist_name as distName, tank.mi_division_name as divName,tank.project_id,tank.sub_division_id,mi.mi_sub_division_name as subDivName,tank.dept_block_name as blockName  " +
                    "from oiipcra_oltp.dam_weir_details as dam  " +
                    "left join  oiipcra_oltp.tank_m_id as tank on tank.id=dam.tank_id  " +
                    "left join oiipcra_oltp.mi_subdivision_m as mi on mi.mi_sub_division_id = tank.sub_division_id  " +
                    "left join oiipcra_oltp.approval_status_m as status on status.id=dam.approved_status where dam.is_active =true ";

            if(tankOtherDetailsListingDto.getApprovedStatus()>0){
                queryString+="AND dam.approved_status=:status ";
                sqlParam.addValue("status",tankOtherDetailsListingDto.getApprovedStatus());
            }
            if(tankOtherDetailsListingDto.getTankId()>0){
                queryString+="AND tank.id=:tankId ";
                sqlParam.addValue("tankId",tankOtherDetailsListingDto.getTankId());
            }
            if(tankOtherDetailsListingDto.getDistId()>0){
                queryString+="AND tank.dist_id=:distId ";
                sqlParam.addValue("distId",tankOtherDetailsListingDto.getDistId());
            }
            if(tankOtherDetailsListingDto.getBlockId()>0){
                queryString+="AND tank.block_id=:blockId ";
                sqlParam.addValue("blockId",tankOtherDetailsListingDto.getBlockId());
            }
            if(tankOtherDetailsListingDto.getDivisionId()>0){
                queryString+="AND tank.mi_division_id=:divisionId";
                sqlParam.addValue("divisionId",tankOtherDetailsListingDto.getDivisionId());
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (tankOtherDetailsListingDto.getUploadFrom() != null && !tankOtherDetailsListingDto.getUploadFrom().isEmpty()) {
                queryString += " AND date(dam.created_on) >= :uploadFromDate";
                Date uploadFromDate = null;
                try {
                    uploadFromDate = format.parse(tankOtherDetailsListingDto.getUploadFrom());
                } catch (Exception exception) {
                    log.info("From Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadFromDate", uploadFromDate, Types.DATE);
            }
            if (tankOtherDetailsListingDto.getUploadTo() != null && !tankOtherDetailsListingDto.getUploadTo().isEmpty()) {
                queryString += " AND date(dam.created_on) <= :uploadToDate";
                Date uploadToDate = null;
                try {
                    uploadToDate = format.parse(tankOtherDetailsListingDto.getUploadTo());
                } catch (Exception exception) {
                    log.info("To Date Parsing exception : {}", exception.getMessage());
                }
                sqlParam.addValue("uploadToDate", uploadToDate, Types.DATE);
            }
            resultCount = count(queryString, sqlParam);
            queryString += " ORDER BY dam." + order.getProperty() + " " + order.getDirection().name();
            queryString += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
            List<DamWeirDetailsDto> damInfo=namedJdbc.query(queryString, sqlParam, new BeanPropertyRowMapper<>(DamWeirDetailsDto.class));
            return new PageImpl<>(damInfo, pageable, resultCount);
        }
    }



//    public TankOtherDetailsDto getTankOtherDetailsByTankId(Integer id){
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT tankother.id, tankother.present_ayacut_khariff, tankother.present_ayacut_rabi, tankother.agro_climatic_zone, " +
//                "tankother.nearest_village, tankother.population_of_nearest_village, tankother.project_constructed_year, tankother.river_basin, river.name AS riverbasinName, " +
//                "tmid.id AS tankid, tmid.project_id, tmid.name_of_the_m_i_p, tmid.mi_division_name, tmid.mi_division_id, tmid.dept_dist_name, tmid.dept_block_name, tmid.dept_gp_name, " +
//                "tmid.category, tmid.type, tmid.latitude, tmid.longitude, tmid.catchment_area_sqkm, tmid.designed_cca_kharif_ha, tmid.designed_cca_rabi_ha " +
//                "FROM oiipcra_oltp.tank_other_details AS tankother " +
//                "LEFT JOIN oiipcra_oltp.river_basin AS river ON tankother.river_basin = river.id " +
//                "LEFT JOIN oiipcra_oltp.tank_m_id AS tmid ON tmid.id = tankother.tank_id WHERE tankother.tank_id=:tankId";
//        sqlParam.addValue("tankId", id);
//        return namedJdbc.queryForObject(qry, sqlParam, new BeanPropertyRowMapper<>(TankOtherDetailsDto.class));
//    }
//
//    public List<CroppingPatternDto> getCroppingPatternDetailsByTankId(Integer id){
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT cropp.id, vil.revenue_village_name AS villageName, cropp.cropping_pattern_type, cropp.ayacut_in_ha, cropp.kh_paddy_ha, cropp.kh_non_paddy_ha, cropp.rabi_paddy_ha, cropp.rabi_non_paddy_ha " +
//                "FROM oiipcra_oltp.cropping_pattern AS cropp " +
//                "LEFT JOIN oiipcra_oltp.village_boundary AS vil ON vil.gid = cropp.village_id " +
//                "WHERE cropp.tank_id=:tankId";
//        sqlParam.addValue("tankId", id);
//        return namedJdbc.query(qry,sqlParam,new BeanPropertyRowMapper<>(CroppingPatternDto.class));
//    }
//
//    public HistoricalDetailsDto getHistoricalDetailsByTankId(Integer id){
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT his.id, his.previous_major_investments, his.fin_year, his.scheme_funded, his.pwt_held_date, his.reno_work_taken_up, his.imp_proposed_now_1 AS impProposedNow1, " +
//                "his.imp_proposed_now_2 AS impProposedNow2 " +
//                "FROM oiipcra_oltp.historical_details AS his WHERE his.tank_id=:tankId";
//        sqlParam.addValue("tankId", id);
//        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(HistoricalDetailsDto.class));
//    }
//
//    public DemographicDetails getDemographicDetailsByTankId(Integer id){
//        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
//        String qry = "SELECT demo.id, demo.no_of_villages_in_command_area, demo.total_population_of_command, demo.percentage_of_sc_population, demo.percentage_of_st_population, " +
//                "demo.percentage_of_obc_population, demo.total_population_of_the_block, demo.population_of_male_in_the_block, demo.population_of_female_in_the_block, " +
//                "demo.male_literacy_in_the_block_perc, demo.female_literacy_in_the_block_perc, demo.general_profession_of_population, demo.perc_agricultural_labour_in_the_block, " +
//                "demo.perc_of_cultivators_in_the_block, demo.perc_of_non_working_population, demo.livestock_population, demo.fishermen_population, demo.year_of_census_data, " +
//                "demo.present_funding_scheme, demo.present_funding_amount_in_lakh " +
//                "FROM oiipcra_oltp.demographic_details AS demo " +
//                "WHERE demo.tank_id=:tankId";
//        sqlParam.addValue("tankId", id);
//        return namedJdbc.queryForObject(qry,sqlParam,new BeanPropertyRowMapper<>(DemographicDetails.class));
//    }



