package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.UserSaveRequests;
import com.orsac.oiipcra.dto.HydrologyDataDto;
import com.orsac.oiipcra.dto.RiverBasinDto;
import com.orsac.oiipcra.entities.CcaRestoreEntity;
import com.orsac.oiipcra.entities.CropDetailsEntity;
import com.orsac.oiipcra.entities.HydrologyDataEntity;
import com.orsac.oiipcra.entities.PageIndexEntity;
import com.orsac.oiipcra.exception.RecordExistException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.orsac.oiipcra.bindings.ContractInfo;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.CatchmentDetails;
import com.orsac.oiipcra.entities.DprInformationEntity;
import com.orsac.oiipcra.entities.PaniPanchayatDetailsEntity;
import com.orsac.oiipcra.entities.SoilDetailsEntity;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;

import java.util.Date;
import java.util.List;

@Service
public interface HydrologyService {

   // List<RiverBasinDto> getRiverBasinId();
    DprInformationEntity saveDpr(DprInformationDto dprInfo );
    List<CatchmentDetails> saveCatchmentDetails(List<CatchmentDetails> catchmentDetails, Integer dprId);
    List<SoilDetailsEntity> soilDetails(List<SoilDetailsEntity> soilDetails, Integer dprId);
    List<PaniPanchayatDetailsEntity> savepaniPanchayatDetails(List<PaniPanchayatDetailsEntity> paniPanchayatDetails, Integer dprId);
    DprInformationDto getDprInfo(Integer id);
    List<PaniPanchayatListDto> getAllPanipanchayatDetails(Integer blockId);
 Page<DprInformationDto> getAllDpr(TankOtherDetailsListingDto tankOtherDetailsListingDto);

 List<CatchmentDetailsDto> getCatchmentDetails(Integer id);
    List<SoilDetailsDto> getSoilDetails(Integer id);
    List<PaniPanchayatDetailsDto> getPaniPanchayatDetails(Integer id);


    List<GwLevelTrendDto> getGwLevelTrend();

 DamWeirDetails saveEarthDam(DamWeirDetailsDto damInfo);

 List<AreaCapacityCurve> saveAreaCapacityDetails(List<AreaCapacityCurve> areaCapacityCurves, Integer id);

 List<WaterSourceMDto> getWaterSource();

 List<TankWeirCrestDto> getTankWeirCrest();

 List<NatureOfWeirCrestDto> getNatureOfWeir();

 List<BodyWallTypeDto> getBodyWallType();

 List<AbutmentsTypeDto> getAbutmentsType();

 List<RiverBasinDto> getRiverBasinId();

 TankOtherDetails createTankOtherDetails(TankOtherDetailsDto tankOtherDetailsDto);

 List<CroppingPattern> createCroppingPattern(List<CroppingPattern> croppingPatternList, Integer id);

 HistoricalDetails createHistoricalDetails(HistoricalDetails historicalDetails, Integer id);

 DemographicDetails createDemographicDetails(DemographicDetails demographicDetails, Integer id);
    HydrologyDataEntity saveHydrology(HydrologyDataDto hydrologyDataDto) throws RecordExistException;

    Page<HydrologyDataDto> getHydrologylist(HydrologyFilterDto hydrologyFilterDto);
     HydrologyDataDto  getHydrologyById(int hydrologyId);

  Integer  getHydrologyByProjectId(int id);
 Integer  getDprByProjectId(int projectId);
 Integer  getDamByProjectId(int projectId);
//     List<HydrologyDataDto> getLastHydrologyById(int id);

   List<CropDetailsEntity>  saveCropDetails(List<CropDetailsEntity> cropDetailsEntities,int hydroId,int createdBy );
    PageIndexEntity  savePageIndex(PageIndexEntity pageIndexEntities, int hydroId, int createdBy);
    List<CcaRestoreEntity>  saveCcaRestore(List<CcaRestoreEntity> ccaRestoreEntities, int hydroId, int createdBy, Date createdOn );

 List<CropDetailsEntity>  getCropDetailsByHydroId(int hydroId);
 PageIndexEntity  getPageIndexByHydroId(int hydroId);

 List<CcaRestoreDto>  getCcaRestoreByHydroId(int hydroId);



 TankOtherDetailsDto getTankOtherDetailsById(Integer id);

 List<CroppingPatternDto> getCroppingPatternById(Integer id);

    HistoricalDetailsDto getHistoricalDetailsById(Integer id);

 DemographicDetails getDemographicDetails(Integer id);

 Integer getTankOtherDetailsByProject(Integer tankId);

 Page<TankOtherDetailsListingDto> getAllTankOtherDetailsList(TankOtherDetailsListingDto tankOtherDetailsListingDto);

    DamWeirDetailsDto getDamById(Integer damId);
    List<AreaCapacityCurve> getAreaDetailsByDamId(Integer damId);

// TankOtherDetailsDto getTankOtherDetailsByTankId(Integer id);
//
// List<CroppingPatternDto> getCroppingPatternDetailsByTankId(Integer id);
//
// HistoricalDetailsDto getHistoricalDetailsByTankId(Integer id);
//
// DemographicDetails getDemographicDetailsByTankId(Integer id);
 Boolean updateDprApproval(Integer dprId, ApproveStatusDto updateDpr);
 Boolean updateTankOtherInfoApproval(Integer tankOtherInfoId, ApproveStatusDto updateTankOtherInfo);

 Boolean updateHydrologyDataApproval(Integer hydrologyDataId, ApproveStatusDto updateHydrologyData);
 Boolean updateDamDetailsApproval(Integer damDataId, ApproveStatusDto updateDamDetails);

    Page<DamWeirDetailsDto> getAllDam(TankOtherDetailsListingDto tankOtherDetailsListingDto);
}
