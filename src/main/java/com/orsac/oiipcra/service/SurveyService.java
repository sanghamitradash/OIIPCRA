package com.orsac.oiipcra.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface SurveyService {
     /** Tank Survey CURD Operation */
     OIIPCRAResponse insertTankSurveyData(String data, MultipartFile trainingImage, MultipartFile surveyorImage, MultipartFile[] tankImages,MultipartFile[] shaftImages) throws JsonProcessingException;
     OIIPCRAResponse  getTankSurveyInfoById(int id,int flagId);
    OIIPCRAResponse getTankImageById(Integer tankId);
     OIIPCRAResponse searchSurveyTankList(SurveyListRequest surveyListRequest);
     //OIIPCRAResponse updateTankById(TankSurveyData tankSurveyData);
     OIIPCRAResponse updateTankById(TankSurveyInfo tankSurveyInfo);

     /** Activity Survey CURD Operation */
     OIIPCRAResponse insertActivitySurveyData(String data, MultipartFile surveyorImage, MultipartFile[] activityImages) throws JsonProcessingException;
     OIIPCRAResponse UpdateActivitySurveyById(String data) throws JsonProcessingException;
     OIIPCRAResponse searchActivityList(ActivitySearchRequest activitySearchRequest);
     OIIPCRAResponse getActivityById(Integer activityId);


     Page<ContractInfoListing> getContractList(ContractListRequestDto contractListRequestDto);

     List<ContractMappingDto> getContractMapping(Integer contractId);
     Page<invoiceListingInfo> getInvoiceList(InvoiceListRequestDto invoiceListRequestDto);

     /** search 538 tank list*/
     OIIPCRAResponse searchTankList(SurveyListRequest surveyListRequest);
     OIIPCRAResponse tank538masterSearchList(SurveyListRequest surveyListRequest);

     OIIPCRAResponse tankSearchListForWebsite(Integer blockId,Integer start,Integer length,Integer draw);
     OIIPCRAResponse tankCount(SurveyListRequest surveyListRequest);
     OIIPCRAResponse getTankListById(int id,int flagId);
     OIIPCRAResponse getTankListByProjectId(int projectId,int flagId);

     OIIPCRAResponse getTankNameAndProjectId(int userId);
     OIIPCRAResponse getSurveyTankNameAndProjectId(int userId);

     OIIPCRAResponse activityInfoSearchList(String data, int activityId);

     Page<WorkProgressInfo> getWorkProgressList(WorkProgressDto workProgressDto);

     List<Tender> getAllClosedBidId();


   // WorkStatusDto getWorkStatusById(Integer bidId, Integer workId);


     TenderDto checkBidId(Integer tenderId);

    List<ContractMappingDto>existBidId(int id);

     WorkStatusDto getWorkStatusDetails(Integer tenderId, Integer workId);

     List<Tender> getAllResultDeclaredBidId();

    FeederEntity saveFeeder(FeederDto feederDto);

     List<FeederLocationEntity> createFeederLocation(List<FeederLocationEntity> feederLocation, Integer id);

     DepthEntity saveDepth(DepthDto depthDto);

     CadEntity saveCad(CadDto cadDto);

    List<CadLocationEntity> createCadLocation(List<CadLocationEntity> cadLocation, Integer id);

    List<FeederImage> saveFeederImage( Integer id, MultipartFile[] surveyImages);

    List<DepthImageEntity> saveDepthImages(Integer id, MultipartFile[] surveyImages);

    List<DepthDto> getDepthIdByTankId(Integer tankId);

    List<DepthImageDto> getDepthImagesByDepthId(Integer depthId);

    List<CadImageEntity> saveCadImage(Integer id, MultipartFile[] surveyImages);

    List<FeederDto> getFeederDetails(Integer tankId, Integer typeId);

    List<FeederImageDto> getFeederImageByTankId(Integer feederId);

    List<FeederLocationDto> getFeederLocation(Integer feederId);

    List<CadDto> getCadDetailsByTankId(Integer tankId);

    List<CadLocationDto> getCadLocationByTankId(Integer id);

    List<CadImageDto> getCadImageDetails(Integer id);

    List<CadDto> getCadDetailsByCadId(Integer cadId);

    List<FeederDto> getFeederDetailsById(Integer feederId);

   // List<FeederDto> getAllFeederDetails();

   // List<CadDto> getAllCadDetails();

    BigDecimal getMaxWaterSpreadData(String projectId);

    BigDecimal getMinWaterSpreadData(String projectId);

    BigDecimal getAvgWaterSpreadData(String projectId);

    BigDecimal getTotalWaterSpreadData(String projectId);

    BigDecimal getLessThan50WaterSpreadData(String projectId);

    List<ContractTypeDto> getAllContractType();

    List<PhysicalProgressPlannedDto> getPlannedDetails(Integer contractId);

    List<PhysicalProgressExecutedDto> getExecutedDetails(Integer contractId);

    List<CadDto> getLatLongByCadId(Integer cadId);

    Integer updateCadGeom(List<CadDto> cadDto, Integer cadId);

    List<FeederDto> getLatLongByFeederId(Integer feederId);

    Integer updateFeederGeom(List<FeederDto> feederDto, Integer feederId);

    List<FeederDto> getAllFeederDetails();

    List<CadDto> getAllCadDetails();

    List<Integer> getCadIdsByTankId(int tankId);

    List<Integer> getfeederIdsByTankId(Integer tankId);

    CadDto getAllCadByCadId(Integer cadId, Integer typeId);

    FeederDto getAllFeederByFeederId(Integer feederId, Integer typeId);
}
