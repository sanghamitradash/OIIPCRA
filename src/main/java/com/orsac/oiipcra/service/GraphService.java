package com.orsac.oiipcra.service;

import com.orsac.oiipcra.dto.CropCycleAyacutDto;
import com.orsac.oiipcra.dto.CropNonCropDto;
import com.orsac.oiipcra.dto.DepthDto;
import com.orsac.oiipcra.dto.WaterSpreadDto;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.PhysicalProgressExecutedLog;
import com.orsac.oiipcra.entities.PhysicalProgressPlannedLog;
import org.springframework.stereotype.Service;

import java.util.List;


public interface GraphService {
    List<WaterSpreadDto> getWaterSpreadMonth(String year,String projectId);
    List<CropCycleAyacutDto> getCropCycleIntensity(Integer projectId,String year);
    List<String> getDistinctYearBYProjectId(Integer projectId);
    List<CropNonCropDto> getCropCycleIntensityMonthWise(Integer projectId, String year);


    List<WaterSpreadDto> getWaterSpreadByProjectId(String projectId,String yearId);

    Integer getIssueCount(Integer tankId,String year);

    Integer getGrievanceCount(Integer tankId,String year);
//    List<WaterSpreadDto> getWaterSpreadYear(String projectId, Integer yearId);
   // List<WaterSpreadDto> getWaterSpreadYear(String projectId, Integer yearId);
    List<DepthDto> getDepth(Integer projectId,String year);


    Integer getResolvedCount(Integer tankId,String year);

    Integer getRejectedCount(Integer tankId, String year);

    Integer getResolvedGrievanceCount(Integer tankId, String year);

    Integer getRejectedGrievanceCount(Integer tankId, String year);

    List<PhysicalProgressExecutedDto> getPhysicalProgressDetails(Integer tankId);

    List<PhysicalProgressPlannedDto> getPhysicalProgressPlannedDetails(Integer tankId);


    List<CropCycleAyacutDto> getCropCycleIntensityList(Integer projectId, String year);

    PhysicalProgressPlannedDto  getPhysicalProgressPlannedDetailsByTankIdAndContractId(Integer tankId,Integer contractId);

    PhysicalProgressExecutedDto  getPhysicalProgressExecutedDetailsByTankIdAndContractId(Integer tankId,Integer contractId);
    PhysicalProgressPlannedLog savePhysicalProgressPlannedLog(PhysicalProgressPlannedDto planned,PhysicalProgressUpdateDto update);
    PhysicalProgressExecutedLog savePhysicalProgressExecutedLog(PhysicalProgressExecutedDto executed, PhysicalProgressUpdateDto update);
    Integer updatePhysicalData(PhysicalProgressUpdateDto physicalProgress);

    WaterSpreadDto getLastYearMonthWaterSpread();

    CropNonCropDto getLastYearMonthCropNonCrop();
}
