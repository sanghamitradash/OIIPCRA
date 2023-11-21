package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.PhysicalProgressExecutedLog;
import com.orsac.oiipcra.entities.PhysicalProgressPlannedLog;
import com.orsac.oiipcra.repository.GraphRepository;
import com.orsac.oiipcra.repository.PhysicalProgressExecutedLogRepository;
import com.orsac.oiipcra.repository.PhysicalProgressPlannedLogRepository;
import com.orsac.oiipcra.repositoryImpl.GraphRepositoryImpl;
import com.orsac.oiipcra.service.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class GraphServiceImpl implements GraphService {
    @Autowired
    private GraphRepositoryImpl graphRepositoryImpl;

    @Autowired
    private PhysicalProgressExecutedLogRepository physicalProgressExecutedLogRepository;
    @Autowired
    private PhysicalProgressPlannedLogRepository physicalProgressPlannedLogRepository;
    @Autowired
    private GraphRepository graphRepository;
    @Override
    public List<CropCycleAyacutDto> getCropCycleIntensity(Integer projectId,String year) {

        List<CropCycleAyacutDto> civilStatus = graphRepositoryImpl.getCropCycleIntensity(projectId,year);

        return civilStatus;
    }

    @Override
    public List<String> getDistinctYearBYProjectId(Integer projectId) {
        return graphRepositoryImpl.getDistinctYearBYProjectId(projectId);
    }

    @Override
    public List<CropNonCropDto> getCropCycleIntensityMonthWise(Integer projectId, String year) {
        return graphRepositoryImpl.getCropCycleIntensityMonthWise(projectId,year);
    }

    @Override
    public List<WaterSpreadDto> getWaterSpreadByProjectId(String projectId, String yearId) {
        return graphRepositoryImpl.getWaterSpreadByProjectId(projectId, yearId);
    }

    @Override
    public List<WaterSpreadDto> getWaterSpreadMonth(String year,String projectId) {
        return graphRepository.getWaterSpreadMonth(year,projectId);
    }



    @Override
    public Integer getIssueCount(Integer tankId,String year) {
        return graphRepository.getIssueCount(tankId,year);
    }

    @Override
    public Integer getGrievanceCount(Integer tankId,String year) {
        return graphRepository.getGrievanceCount(tankId,year);
    }



    @Override
    public List<DepthDto> getDepth(Integer projectId,String year) {
        return graphRepositoryImpl.getDepth(projectId,year);
    }

    @Override
    public Integer getResolvedCount(Integer tankId,String year) {
        return graphRepositoryImpl.getResolvedCount(tankId,year);
    }

    @Override
    public Integer getRejectedCount(Integer tankId, String year) {
        return graphRepositoryImpl.getRejectedCount(tankId,year);
    }

    @Override
    public Integer getResolvedGrievanceCount(Integer tankId, String year) {
        return graphRepositoryImpl.getResolvedGrievanceCount(tankId, year);
    }

    @Override
    public Integer getRejectedGrievanceCount(Integer tankId, String year) {
        return graphRepositoryImpl.getRejectedGrievanceCount(tankId, year);
    }

    @Override
    public List<PhysicalProgressExecutedDto> getPhysicalProgressDetails(Integer tankId) {
        return graphRepositoryImpl.getPhysicalProgressDetails(tankId);
    }

    @Override
    public List<PhysicalProgressPlannedDto> getPhysicalProgressPlannedDetails(Integer tankId) {
        return graphRepositoryImpl.getPhysicalProgressPlannedDetails(tankId);
    }

    @Override
    public List<CropCycleAyacutDto> getCropCycleIntensityList(Integer projectId, String year) {
        List<CropCycleAyacutDto> civilStatus = graphRepositoryImpl.getCropCycleIntensityList(projectId,year);
        return civilStatus;
    }

    @Override
    public PhysicalProgressPlannedDto getPhysicalProgressPlannedDetailsByTankIdAndContractId(Integer tankId, Integer contractId) {
        return graphRepositoryImpl.getPhysicalProgressPlannedDetailsByTankIdAndContractId(tankId,contractId);
    }

    @Override
    public PhysicalProgressExecutedDto getPhysicalProgressExecutedDetailsByTankIdAndContractId(Integer tankId, Integer contractId) {
        return graphRepositoryImpl.getPhysicalProgressExecutedDetailsByTankIdAndContractId(tankId,contractId);
    }

    @Override
    public PhysicalProgressPlannedLog savePhysicalProgressPlannedLog(PhysicalProgressPlannedDto planned, PhysicalProgressUpdateDto update) {
        PhysicalProgressPlannedLog pp=new PhysicalProgressPlannedLog();
        BeanUtils.copyProperties(pp,planned);
        pp.setCreatedBy(update.getUserId());

        return physicalProgressPlannedLogRepository.save(pp);
    }

    @Override
    public PhysicalProgressExecutedLog savePhysicalProgressExecutedLog(PhysicalProgressExecutedDto executed, PhysicalProgressUpdateDto update) {
        PhysicalProgressExecutedLog pp=new PhysicalProgressExecutedLog();
        BeanUtils.copyProperties(pp,executed);
        pp.setCreatedBy(update.getUserId());
        return  physicalProgressExecutedLogRepository.save(pp);
    }

    @Override
    public Integer updatePhysicalData(PhysicalProgressUpdateDto physicalProgress) {
        Integer planned=graphRepositoryImpl.updatePhysicalPlannedData(physicalProgress);
        Integer executed=graphRepositoryImpl.updatePhysicalExecutedData(physicalProgress);
        if(planned==1 && executed==1){
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public WaterSpreadDto getLastYearMonthWaterSpread() {
        return graphRepositoryImpl.getLastYearMonthWaterSpread();
    }

    @Override
    public CropNonCropDto getLastYearMonthCropNonCrop() {
        return graphRepositoryImpl.getLastYearMonthCropNonCrop();
    }


}
