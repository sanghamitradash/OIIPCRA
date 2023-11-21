package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.ContractInfo;
import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.bindings.UserSaveRequests;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.*;
import com.orsac.oiipcra.repositoryImpl.HydrologyRepositoryImpl;
import com.orsac.oiipcra.dto.HydrologyDataDto;
import com.orsac.oiipcra.dto.RiverBasinDto;
import com.orsac.oiipcra.entities.CcaRestoreEntity;
import com.orsac.oiipcra.entities.CropDetailsEntity;
import com.orsac.oiipcra.entities.HydrologyDataEntity;
import com.orsac.oiipcra.entities.PageIndexEntity;
import com.orsac.oiipcra.repository.CcaRestoreRepository;
import com.orsac.oiipcra.repository.CropDetailsRepository;
import com.orsac.oiipcra.repository.HydrologyRepository;
import com.orsac.oiipcra.repository.PageIndexRepository;
import com.orsac.oiipcra.service.HydrologyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j

@Service
public class HydrologyServiceImpl implements HydrologyService {


    @Autowired
    HydrologyRepositoryImpl hydrologyRepositoryimpl;

    @Autowired
    HydrologyRepository hydrologyRepository;
    @Autowired
    HydrologyRepositoryImpl hydrologyRepositoryImpl;
    @Autowired
    CropDetailsRepository cropDetailsRepository;
    @Autowired
    PageIndexRepository pageIndexRepository;
    @Autowired
    CcaRestoreRepository ccaRestoreRepository;


    @Autowired
    CroppingPatternRepository croppingPatternRepository;

    @Autowired
    HistoricalDetailsRepository historicalDetailsRepository;

    @Autowired
    DemographicDetailsRepository demographicDetailsRepository;
    //    @Autowired
//    HydrologyRepository hydrologyRepository;
    @Autowired
    DprRepository dprRepository;
    @Autowired
    CatchmentRepository catchmentRepository;
    @Autowired
    SoilRepository soilRepository;

    @Autowired
    DamWeirDetailsRepository damWeirDetailsRepository;
    @Autowired
    PaniPanchayatRepository paniPanchayatRepository;

    @Autowired
    AreaCapacityCurveRepository areaCapacityCurveRepository;
    @Autowired
    HydrologyDataRepository hydrologyDataRepository;

    @Override
    public DprInformationEntity saveDpr(DprInformationDto dprInfo) {
        DprInformationEntity dpr = new DprInformationEntity();
        BeanUtils.copyProperties(dprInfo, dpr);
        dpr.setApprovedStatus(1);
        dpr.setActive(true);
        return dprRepository.save(dpr);
    }

    @Override
    public List<CatchmentDetails> saveCatchmentDetails(List<CatchmentDetails> catchmentDetails, Integer dprId) {
        List<CatchmentDetails> catchment = new ArrayList<>();
        for (CatchmentDetails catchmentDetails1 : catchmentDetails) {
            catchmentDetails1.setDprInformationId(dprId);
            catchment.add(catchmentDetails1);
        }
        return catchmentRepository.saveAll(catchment);
    }

    @Override
    public List<SoilDetailsEntity> soilDetails(List<SoilDetailsEntity> soilDetails, Integer dprId) {
        List<SoilDetailsEntity> soil = new ArrayList<>();
        for (SoilDetailsEntity soilDetails1 : soilDetails) {
            soilDetails1.setDprInformationId(dprId);
            soil.add(soilDetails1);
        }
        return soilRepository.saveAll(soil);
    }

    @Override
    public List<PaniPanchayatDetailsEntity> savepaniPanchayatDetails(List<PaniPanchayatDetailsEntity> paniPanchayatDetails, Integer dprId) {
        List<PaniPanchayatDetailsEntity> paniPanchayat = new ArrayList<>();
        for (PaniPanchayatDetailsEntity paniPanchayatDetails1 : paniPanchayatDetails) {
            paniPanchayatDetails1.setDprInformationId(dprId);
            paniPanchayat.add(paniPanchayatDetails1);
        }
        return paniPanchayatRepository.saveAll(paniPanchayat);
    }

    @Override
    public DprInformationDto getDprInfo(Integer dprId) {
        return hydrologyRepositoryImpl.getDprInfo(dprId);

    }

    @Override
    public List<CatchmentDetailsDto> getCatchmentDetails(Integer dprId) {
        return hydrologyRepositoryImpl.getCatchmentDetails(dprId);
    }

    @Override
    public List<SoilDetailsDto> getSoilDetails(Integer dprId) {
        return hydrologyRepositoryImpl.getSoilDetails(dprId);
    }

    @Override
    public List<PaniPanchayatDetailsDto> getPaniPanchayatDetails(Integer dprId) {
        return hydrologyRepositoryImpl.getPaniPanchayatDetails(dprId);
    }

    @Override
    public List<GwLevelTrendDto> getGwLevelTrend() {
        return hydrologyRepositoryImpl.getGwLevelTrend();
    }

    @Override
    public DamWeirDetails saveEarthDam(DamWeirDetailsDto damInfo) {
        DamWeirDetails eathDam = new DamWeirDetails();
        BeanUtils.copyProperties(damInfo, eathDam);
        eathDam.setIsActive(true);
        return damWeirDetailsRepository.save(eathDam);
    }

    @Override
    public List<AreaCapacityCurve> saveAreaCapacityDetails(List<AreaCapacityCurve> areaCapacityCurves, Integer id) {
        List<AreaCapacityCurve> areaCapacity = new ArrayList<>();
        for (AreaCapacityCurve areaCapacityCurve1 : areaCapacityCurves) {
            areaCapacityCurve1.setDamWeirDetails(id);
            areaCapacity.add(areaCapacityCurve1);
        }
        return areaCapacityCurveRepository.saveAll(areaCapacity);
    }

    @Override
    public List<WaterSourceMDto> getWaterSource() {
        return hydrologyRepositoryImpl.getWaterSource();
    }

    @Override
    public List<TankWeirCrestDto> getTankWeirCrest() {
        return hydrologyRepositoryImpl.getTankWeirCrest();
    }

    @Override
    public List<NatureOfWeirCrestDto> getNatureOfWeir() {
        return hydrologyRepositoryImpl.getNatureOfWeir();
    }

    @Override
    public List<BodyWallTypeDto> getBodyWallType() {
        return hydrologyRepositoryImpl.getBodyWallType();
    }

    @Override
    public List<AbutmentsTypeDto> getAbutmentsType() {
        return hydrologyRepositoryImpl.getAbutmentsType();
    }

    @Override
    public List<RiverBasinDto> getRiverBasinId() {

        return hydrologyRepositoryImpl.getRiverBasinId();
    }

    @Override
    public HydrologyDataEntity saveHydrology(HydrologyDataDto hydrologyDataDto) {

        HydrologyDataEntity hydrologyDataEntity = new HydrologyDataEntity();

        BeanUtils.copyProperties(hydrologyDataDto, hydrologyDataEntity);


        return hydrologyDataRepository.save(hydrologyDataEntity);
    }

    public  Page<HydrologyDataDto> getHydrologylist(HydrologyFilterDto hydrologyFilterDto){


        Page<HydrologyDataDto> hydrologyDataEntity=hydrologyRepositoryImpl.getHydrologylist(hydrologyFilterDto);
        return  hydrologyDataEntity;


    }

    public HydrologyDataDto  getHydrologyById(int hydrologyId){
        HydrologyDataDto hydrologyDataDtos=hydrologyRepositoryImpl.getHydrologyById(hydrologyId);
        return  hydrologyDataDtos;
    }

    public Integer  getHydrologyByProjectId(int id){
        Integer hydrologyDataDtos=hydrologyRepositoryImpl.getHydrologyByProjectId(id);
        return  hydrologyDataDtos;
    }
//    public List<HydrologyDataDto> getLastHydrologyById(int id){
//        List<HydrologyDataDto> hydrologyDataDtos=hydrologyRepositoryImpl.getLastHydrologyById(id);
////        int lastId=hydrologyDataDtos.getId();
////        System.out.println(lastId);
//        return hydrologyDataDtos ;
//    }

    public List<CropDetailsEntity> saveCropDetails(List<CropDetailsEntity> cropDetailsEntities, int hydroId, int createdBy) {
        for (CropDetailsEntity cropDetailsEntity : cropDetailsEntities) {
            cropDetailsEntity.setHydrologyDataId(hydroId);
            cropDetailsEntity.setCreatedBy(createdBy);
        }

        return cropDetailsRepository.saveAll(cropDetailsEntities);
    }

    public PageIndexEntity savePageIndex(PageIndexEntity pageIndexEntities, int hydroId, int createdBy) {
        pageIndexEntities.setHydrologyDataId(hydroId);
        pageIndexEntities.setCreatedBy(createdBy);
        return pageIndexRepository.save(pageIndexEntities);
    }

    public List<CcaRestoreEntity> saveCcaRestore(List<CcaRestoreEntity> ccaRestoreEntities, int hydroId, int createdBy, Date createdOn) {


        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str = formatter.format(createdOn);

        int FinYr = hydrologyRepositoryImpl.getFinYearId(str);
        System.out.println(createdOn);
        for (CcaRestoreEntity ccaRestoreEntity : ccaRestoreEntities) {
            ccaRestoreEntity.setHydrologyDataId(hydroId);
            ccaRestoreEntity.setCreatedBy(createdBy);
            ccaRestoreEntity.setFinYr(FinYr);
        }

        return ccaRestoreRepository.saveAll(ccaRestoreEntities);
    }
   public List<CropDetailsEntity>  getCropDetailsByHydroId(int hydroId){

      List<CropDetailsEntity> cropDetailsEntities= hydrologyRepositoryImpl.getCropDetailsByHydroId(hydroId);
       return cropDetailsEntities;

   }
    public PageIndexEntity  getPageIndexByHydroId(int hydroId){
        PageIndexEntity pageIndexEntities= hydrologyRepositoryImpl.getPageIndexByHydroId(hydroId);
        return pageIndexEntities;
    }
    public List<CcaRestoreDto>  getCcaRestoreByHydroId(int hydroId){
        List<CcaRestoreDto> ccaRestoreEntities= hydrologyRepositoryImpl.getCcaRestoreByHydroId(hydroId);
        return ccaRestoreEntities;
    }

    @Override
    public TankOtherDetails createTankOtherDetails(TankOtherDetailsDto tankOtherDetailsDto) {
        TankOtherDetails tankOtherDetails1 = new TankOtherDetails();
        BeanUtils.copyProperties(tankOtherDetailsDto, tankOtherDetails1);
        tankOtherDetails1.setIsActive(true);
        tankOtherDetails1.setCreatedBy(1);
        tankOtherDetails1.setUpdatedBy(1);
        tankOtherDetails1.setApprovedStatus(1);
        tankOtherDetails1 = hydrologyRepository.save(tankOtherDetails1);
        return tankOtherDetails1;
    }

    @Override
    public List<CroppingPattern> createCroppingPattern(List<CroppingPattern> croppingPatternList, Integer id) {
        for (CroppingPattern croppingPattern : croppingPatternList) {
            croppingPattern.setTankOtherDetailsId(id);
            croppingPattern.setIsActive(true);
            croppingPattern.setCreatedBy(1);
            croppingPattern.setUpdatedBy(1);
        }
        return croppingPatternRepository.saveAll(croppingPatternList);
    }

    @Override
    public HistoricalDetails createHistoricalDetails(HistoricalDetails historicalDetails, Integer id) {
        HistoricalDetails historicalDetails1 = new HistoricalDetails();
        BeanUtils.copyProperties(historicalDetails, historicalDetails1);
        historicalDetails1.setTankOtherDetailsId(id);
        historicalDetails1.setIsActive(true);
        historicalDetails1.setCreatedBy(1);
        historicalDetails1.setUpdatedBy(1);

        historicalDetails1 = historicalDetailsRepository.save(historicalDetails1);
        return historicalDetails1;
    }

    @Override
    public DemographicDetails createDemographicDetails(DemographicDetails demographicDetails, Integer id) {
        DemographicDetails demographicDetails1 = new DemographicDetails();
        BeanUtils.copyProperties(demographicDetails, demographicDetails1);
        demographicDetails1.setTankOtherDetailsId(id);
        demographicDetails1.setIsActive(true);
        demographicDetails1.setCreatedBy(1);
        demographicDetails1.setUpdatedBy(1);

        demographicDetails1 = demographicDetailsRepository.save(demographicDetails1);
        return demographicDetails1;
    }

    @Override
    public TankOtherDetailsDto getTankOtherDetailsById(Integer id){
        return hydrologyRepositoryImpl.getTankOtherDetailsById(id);
    }

    @Override
    public List<CroppingPatternDto> getCroppingPatternById(Integer id){
        return hydrologyRepositoryImpl.getCroppingPatternById(id);
    }

    @Override
    public HistoricalDetailsDto getHistoricalDetailsById(Integer id){
        return hydrologyRepositoryImpl.getHistoricalDetailsById(id);
    }

    @Override
    public DemographicDetails getDemographicDetails(Integer id){
        return hydrologyRepositoryImpl.getDemographicDetails(id);
    }

    @Override
    public Integer getTankOtherDetailsByProject(Integer tankId){
        return hydrologyRepositoryImpl.getTankOtherDetailsByProject(tankId);
    }

    @Override
    public Page<TankOtherDetailsListingDto> getAllTankOtherDetailsList(TankOtherDetailsListingDto tankOtherDetailsListingDto){
        return hydrologyRepositoryImpl.getAllTankOtherDetailsList(tankOtherDetailsListingDto);
    }

    @Override
    public Boolean updateDprApproval(Integer dprId, ApproveStatusDto updateDpr) {
        return hydrologyRepositoryImpl.updateDprApproval(dprId,updateDpr);
    }
    @Override
    public Boolean updateTankOtherInfoApproval(Integer tankOtherInfoId, ApproveStatusDto updateTankOtherInfo) {
        return hydrologyRepositoryImpl.updateTankOtherInfoApproval(tankOtherInfoId,updateTankOtherInfo);
    }
    @Override
    public Boolean updateHydrologyDataApproval(Integer hydrologyDataId, ApproveStatusDto updateHydrologyData) {
        return hydrologyRepositoryImpl.updateHydrologyDataApproval(hydrologyDataId,updateHydrologyData);
    }
    @Override
    public Boolean updateDamDetailsApproval(Integer damDataId, ApproveStatusDto updateDamDetails) {
        return hydrologyRepositoryImpl.updateDamDetailsApproval(damDataId,updateDamDetails);
    }

    @Override
    public Page<DamWeirDetailsDto> getAllDam(TankOtherDetailsListingDto tankOtherDetailsListingDto) {
        return hydrologyRepositoryImpl.getAllDam(tankOtherDetailsListingDto);
    }

    @Override
    public DamWeirDetailsDto getDamById(Integer damId) {
        return hydrologyRepositoryImpl.getDamById(damId);
    }

    @Override
    public List<AreaCapacityCurve> getAreaDetailsByDamId(Integer damId) {
        return hydrologyRepositoryImpl.getAreaDetailsByDamId(damId);
    }

    @Override
    public List<PaniPanchayatListDto> getAllPanipanchayatDetails(Integer blockId){
        return hydrologyRepositoryImpl.getAllPanipanchayatDetails(blockId);
    }

    @Override
    public Page<DprInformationDto> getAllDpr(TankOtherDetailsListingDto tankOtherDetailsListingDto) {
        Page<DprInformationDto> dprList= hydrologyRepositoryImpl.getAllDpr(tankOtherDetailsListingDto);
        return dprList;
    }
    public Integer  getDprByProjectId(int projectId){
        return   hydrologyRepositoryImpl.getDprByProjectId(projectId);

    }
    public Integer  getDamByProjectId(int projectId){
        return   hydrologyRepositoryImpl.getDamByProjectId(projectId);

    }



//    @Override
//    public TankOtherDetailsDto getTankOtherDetailsByTankId(Integer id){
//        return hydrologyRepositoryImpl.getTankOtherDetailsByTankId(id);
//    }
//
//    @Override
//    public List<CroppingPatternDto> getCroppingPatternDetailsByTankId(Integer id){
//        return hydrologyRepositoryImpl.getCroppingPatternDetailsByTankId(id);
//    }
//
//    @Override
//    public HistoricalDetailsDto getHistoricalDetailsByTankId(Integer id){
//        return hydrologyRepositoryImpl.getHistoricalDetailsByTankId(id);
//    }
//
//    @Override
//    public DemographicDetails getDemographicDetailsByTankId(Integer id){
//        return hydrologyRepositoryImpl.getDemographicDetailsByTankId(id);
//    }
}