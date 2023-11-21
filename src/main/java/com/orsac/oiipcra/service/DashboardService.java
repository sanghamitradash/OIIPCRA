package com.orsac.oiipcra.service;

import com.orsac.oiipcra.dto.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public interface DashboardService {

    List<DashboardStatusOfMIP> getCountForExecutionOfMip();

    List<DashboardComponentEstExp> getComponentListEstExpList();
    List<CivilWorkStatusDivisionDto> getCivilWorkStatusDivisionWise();
    List<CropCycleAyacutDto>  getCropCycleAyacut(Integer projectId,String year);
    List<CropCycleAyacutDto> getCropCycleAyacut1(Integer projectId,List<String> year);
    List<CivilWorkDto>  getCivilWorkStatus(Integer typeId, Integer yearId);
    List<ValueDto> getValue(Integer yearId);
    Double getExpenditureByPreviousMonth(Integer tankId) throws ParseException;
    Double getExpenditureThisMonth(Integer tankId) throws ParseException;

    List<DashboardStatusOfMIPByDist> getDashboardStatusOfMIPByDist(Integer districtId);

    List<DashboardStatusOfMIPByDivision> getDashboardStatusOfMIPByDivision(int districtId, int divisionId);

    List<DashboardExpenditureOfMIPByDistrict> getDashboardExpenditureInMIPByDistrict(int districtId);

    List<DashboardExpenditureOfMIPByDivision> getDashboardExpenditureInMIPByDivision(int districtId, int divisionId);
    TankWiseCountDto getTankWiseCount(Integer tankId);

    BigDecimal getTankWiseCatchmentArea(Integer tankId);

  List<CropCycleAyacutDto> getTankWiseAyaCut(Integer tankId);


    Double getWSA(Integer tankId, String divisionName);

    Double getAyaCut(Integer tankId, String divisionName);

    Integer workAwarded(Integer tankId, String divisionName);

    Object croppingIntensity(Integer tankId, String divisionName);

    Object tanksByCategory(Integer projectId, String divisionName);
    BigDecimal getTankWiseAyacutArea(Integer projectId);




    Double TotalWaterSpreadArea(Integer projectId);
//    Object tanksByCategory(Integer projectId, Integer divisionId);


    List<String> getDistinctYearBYProjectIdAndDivisionId(Integer projectId, String divisionName);


    List<CropCycleAyacutDto> getCropCycleIntensity(Integer projectId,String year,String divisionName);

    List<DivisionWiseExpenditureDto> commulativeExpenditure(Integer projectId, String divisionName);

    Object getComponents(Integer userId, Integer typeId,Integer workTypeId, Integer componentId, Integer yearId);


    PhysicalProgressValueDto getPhysicalProgressValue();

    Object getComponentWiseContractAmount(Integer userId, Integer typeId, Integer componentId, Integer yearId);



    BigDecimal getTotalLengthOfCanalAsPerEstimate(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getLengthOfCanalImproved(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getNoOfCdStructuresRepared(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getNoOfCdStructuresToBeRepared(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getTotalLengthOfCad(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getNoOfOutletConstructed(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getCadConstructed(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    BigDecimal getTotalWaterSpreadArea(Integer projectId);

    DivisionWiseExpenditureDto totalCommulativeExpenditure(Integer projectId, String divisionName);

    BigDecimal getcontractAmount(Integer projectId, String divisionName);

    BigDecimal getcatchmentArea(Integer projectId, String divisionName);

    List<Integer> getProjectIdByDivisionName(String divisionName);

    Object getComponentWiseContractStatus(Integer userId, Integer typeId, Integer statusId);

    Object getComponentWiseEstimateStatus(Integer userId, Integer typeId, Integer statusId);

    Object getValueByComponentId(Integer componentId, Integer yearId);

    String getWSAtillMonth(Integer projectId, String divisionName );

    Object getExpenditureEstimateContractYearWise(Integer userId, Integer componentId);

    Object getTankSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    Object getCadSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    Object getFeederSurveyedCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    Object getDepthCount(Integer circleId, Integer distId, Integer divisionId, Integer blockId);

    Object getValueByComponentIdAndDistId(Integer componentId, Integer yearId, Integer distId,Integer typeId);


//    Object getAllTank();
}
