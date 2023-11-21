package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;

import com.orsac.oiipcra.bindings.UserInfo;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.dto.HydrologyDataDto;
import com.orsac.oiipcra.dto.RiverBasinDto;
import com.orsac.oiipcra.entities.CcaRestoreEntity;
import com.orsac.oiipcra.entities.CropDetailsEntity;
import com.orsac.oiipcra.entities.HydrologyDataEntity;
import com.orsac.oiipcra.entities.PageIndexEntity;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.service.HydrologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/hydrology")
public class HydrologyController {

    @Autowired
    HydrologyService hydrologyService;
    @Autowired
    private MasterQryRepository masterQryRepository;

    @PostMapping("/getRiverBasinId")
    public OIIPCRAResponse getRiverBasinId() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<RiverBasinDto> riverId = hydrologyService.getRiverBasinId();
            result.put("RiverId", riverId);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("RiverBasin Id List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

//    @PostMapping("/createHydrology")
//    public OIIPCRAResponse createHydrology() {
//        OIIPCRAResponse response = new OIIPCRAResponse();
//        Map<String, Object> result = new HashMap<>();
//        try {
//            List<RiverBasinDto> riverId = hydrologyService.getRiverBasinId();
//            result.put("bidId", riverId);
//            response.setData(result);
//            response.setStatus(1);
//            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
//            response.setMessage("RiverBasin Id List");
//        } catch (Exception e) {
//            response = new OIIPCRAResponse(0,
//                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
//                    e.getMessage(),
//                    result);
//        }
//        return response;
//    }
    @PostMapping("/createHydrology")
    public OIIPCRAResponse createHydrology(
            @RequestParam(name = "data") String data) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HydrologyDataDto hydrologyDataDto = mapper.readValue(data, HydrologyDataDto.class);
            HydrologyDataEntity savedHydrology=hydrologyService.saveHydrology(hydrologyDataDto);
            List<CropDetailsEntity> cropDetailsEntityList=hydrologyService.saveCropDetails(hydrologyDataDto.getCropDetailsEntities(),savedHydrology.getId(),savedHydrology.getCreatedBy());
            PageIndexEntity pageIndexEntityList=hydrologyService.savePageIndex(hydrologyDataDto.getPageIndexEntities(),savedHydrology.getId(),savedHydrology.getCreatedBy());
            List<CcaRestoreEntity> ccaRestoreEntityList=hydrologyService.saveCcaRestore(hydrologyDataDto.getCcaRestoreEntities(),savedHydrology.getId(),savedHydrology.getCreatedBy(),savedHydrology.getCreatedOn());

            result.put("Hydro", savedHydrology);
            result.put("cropDetails",cropDetailsEntityList);
            result.put("PageIndex",pageIndexEntityList);
            result.put("CcaRestore",ccaRestoreEntityList);


            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            oiipcraResponse.setMessage("Hydrology Created Successfully");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getHydrologyList")
    public OIIPCRAResponse getHydrologyList(@RequestBody(required = false) HydrologyFilterDto hydrologyFilterDto){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            Page<HydrologyDataDto> hydrologyDataPage = hydrologyService.getHydrologylist(hydrologyFilterDto);
            List<HydrologyDataDto> hydrologyDataDtos1 = hydrologyDataPage.getContent();
            if (!hydrologyDataDtos1.isEmpty() && hydrologyDataDtos1.size() > 0) {
                result.put("hydrologyList", hydrologyDataDtos1);
                result.put("currentPage", hydrologyDataPage.getNumber());
                result.put("totalItems", hydrologyDataPage.getTotalElements());
                result.put("totalPages", hydrologyDataPage.getTotalPages());
                oiipcraResponse.setData(result);
                oiipcraResponse.setStatus(1);
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                oiipcraResponse.setMessage("Hydrology List");
            } else {
                result.put("hydrologyList", hydrologyDataDtos1);
                oiipcraResponse.setData(result);
                oiipcraResponse.setStatus(1);
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                oiipcraResponse.setMessage("Record not found.");
            }
        }catch (Exception e) {
        oiipcraResponse = new OIIPCRAResponse(0,
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                e.getMessage(),
                result);
    }
        return oiipcraResponse;
    }
    @PostMapping("/getHydrologyByProjectId")
    public OIIPCRAResponse getHydrologyByProjectId(@RequestParam Integer id){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try {
            Integer hydrologyId = hydrologyService.getHydrologyByProjectId(id);

            HydrologyDataDto hydrologyDataDtos=hydrologyService.getHydrologyById(hydrologyId);
            List<CropDetailsEntity> cropDetailsEntityList=hydrologyService.getCropDetailsByHydroId(hydrologyDataDtos.getId());
            PageIndexEntity pageIndexEntityList=hydrologyService.getPageIndexByHydroId(hydrologyDataDtos.getId());
            List<CcaRestoreDto> ccaRestoreEntityList=hydrologyService.getCcaRestoreByHydroId(hydrologyDataDtos.getId());
            result.put("hydrologyList",hydrologyDataDtos);
            result.put("crop",cropDetailsEntityList);
            result.put("pageIndex",pageIndexEntityList);
            result.put("cca",ccaRestoreEntityList);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Hydrology List By Project Id");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getHydrologyById")
    public OIIPCRAResponse getHydrologyById(@RequestParam Integer hydrologyId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try {

            HydrologyDataDto hydrologyDataDtos=hydrologyService.getHydrologyById(hydrologyId);
            List<CropDetailsEntity> cropDetailsEntityList=hydrologyService.getCropDetailsByHydroId(hydrologyDataDtos.getId());
            PageIndexEntity pageIndexEntityList=hydrologyService.getPageIndexByHydroId(hydrologyDataDtos.getId());
            List<CcaRestoreDto> ccaRestoreEntityList=hydrologyService.getCcaRestoreByHydroId(hydrologyDataDtos.getId());
            result.put("hydrologyList",hydrologyDataDtos);
            result.put("crop",cropDetailsEntityList);
            result.put("pageIndex",pageIndexEntityList);
            result.put("cca",ccaRestoreEntityList);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Hydrology List By Id");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

//    @PostMapping("/getLastHydrologyById")
//    public OIIPCRAResponse getLastHydrologyById(@RequestParam Integer id){
//        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
//        Map<String, Object> result = new HashMap<>();
//
//        try {
//
//            List<HydrologyDataDto> hydrologyDataDtos=hydrologyService.getLastHydrologyById(id);
//            result.put("hydrologyList",hydrologyDataDtos);
//            oiipcraResponse.setData(result);
//            oiipcraResponse.setStatus(1);
//            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
//            oiipcraResponse.setMessage("Hydrology List");
//        } catch (Exception e) {
//            oiipcraResponse = new OIIPCRAResponse(0,
//                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
//                    e.getMessage(),
//                    result);
//        }
//        return oiipcraResponse;
//    }

    @PostMapping("/createDprInformation")
    public OIIPCRAResponse saveDpr(
            @RequestParam(name = "data") String data) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            DprInformationDto dprInfo = mapper.readValue(data, DprInformationDto.class);
            DprInformationEntity saveDpr = hydrologyService.saveDpr(dprInfo);
            List<CatchmentDetails> catchmentDetails = hydrologyService.saveCatchmentDetails(dprInfo.getCatchmentDetails(), saveDpr.getId());
            List<SoilDetailsEntity> soilDetails = hydrologyService.soilDetails(dprInfo.getSoilDetails(), saveDpr.getId());
            List<PaniPanchayatDetailsEntity> paniPanchayatDetails = hydrologyService.savepaniPanchayatDetails(dprInfo.getPaniPanchayatDetails(), saveDpr.getId());
            result.put("dpr", saveDpr);
            result.put("catchmentDetails", catchmentDetails);
            result.put("soilDetails", soilDetails);
            result.put("paniPanchayatDetails", paniPanchayatDetails);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            oiipcraResponse.setMessage("Dpr Created Successfully");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


    @PostMapping("/createTankOtherDetails")
    public OIIPCRAResponse createTankOtherDetails(@RequestParam(name = "data") String data){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            TankOtherDetailsDto tankOtherDetailsDto = objectMapper.readValue(data, TankOtherDetailsDto.class);
            TankOtherDetails tankOtherDetails = hydrologyService.createTankOtherDetails(tankOtherDetailsDto);
            List<CroppingPattern> croppingPatternList = hydrologyService.createCroppingPattern(tankOtherDetailsDto.getCroppingPatternList(), tankOtherDetails.getId());
            HistoricalDetails historicalDetails = hydrologyService.createHistoricalDetails(tankOtherDetailsDto.getHistoricalDetails(), tankOtherDetails.getId());
            DemographicDetails demographicDetails = hydrologyService.createDemographicDetails(tankOtherDetailsDto.getDemographicDetails(), tankOtherDetails.getId());

            if(tankOtherDetails != null){
                result.put("tankOtherDetails", tankOtherDetails);
                result.put("CroppingPattern", croppingPatternList);
                result.put("HistoricalDetails", historicalDetails);
                result.put("DemographicDetails", demographicDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("Tank other details created");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTankOtherDetailsById")
    public OIIPCRAResponse getTankOtherDetailsById(@RequestParam Integer id){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            TankOtherDetailsDto tankOtherDetailsDto = hydrologyService.getTankOtherDetailsById(id);
            List<CroppingPatternDto> croppingPattern = hydrologyService.getCroppingPatternById(id);
            HistoricalDetailsDto historicalDetails = hydrologyService.getHistoricalDetailsById(id);
            DemographicDetails demographicDetails = hydrologyService.getDemographicDetails(id);

            if(tankOtherDetailsDto != null){
                result.put("TankOtherDetails", tankOtherDetailsDto);
                result.put("CroppingPattern", croppingPattern);
                result.put("HistoricalDetails", historicalDetails);
                result.put("DemographicDetails", demographicDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tank Other Details By Id");
            } else {
                result.put("TankOtherDetails", tankOtherDetailsDto);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                ex.getMessage(),
                result);
        }
        return response;
    }

    @PostMapping("/getTankOtherDetailsByProjectId")
    public OIIPCRAResponse getTankOtherDetailsByTankId(@RequestParam Integer projectId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{

            Integer tankOtherDetailsId = hydrologyService.getTankOtherDetailsByProject(projectId);

            TankOtherDetailsDto tankOtherDetailsDto = hydrologyService.getTankOtherDetailsById(tankOtherDetailsId);
            List<CroppingPatternDto> croppingPattern = hydrologyService.getCroppingPatternById(tankOtherDetailsId);
            HistoricalDetailsDto historicalDetails = hydrologyService.getHistoricalDetailsById(tankOtherDetailsId);
            DemographicDetails demographicDetails = hydrologyService.getDemographicDetails(tankOtherDetailsId);

            if(tankOtherDetailsDto != null){
                result.put("TankOtherDetailsByTankId", tankOtherDetailsDto);
                result.put("CroppingPatternByTankId", croppingPattern);
                result.put("HistoricalDetailsByTankId", historicalDetails);
                result.put("DemographicDetailsByTankId", demographicDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tank Other Details By tank Id");
            } else {
                result.put("TankOtherDetails", tankOtherDetailsDto);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getDprByProjectId")
    public OIIPCRAResponse getDprByProjectId(@RequestParam Integer projectId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Integer dprId = hydrologyService.getDprByProjectId(projectId);
            DprInformationDto dprInfo = hydrologyService.getDprInfo(dprId);
            List<CatchmentDetailsDto> catchmentDetails=hydrologyService.getCatchmentDetails(dprId);
            List<SoilDetailsDto> soilDetails=hydrologyService.getSoilDetails(dprId);
            List<PaniPanchayatDetailsDto> paniPanchayatDetails=hydrologyService.getPaniPanchayatDetails(dprId);
            if (dprInfo!=null) {
                result.put("dprInfo", dprInfo);
                result.put("catchmentDetails", catchmentDetails);
                result.put("soilDetails", soilDetails);
                result.put("paniPanchayatDetails", paniPanchayatDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Dpr By Id");
            } else {
                result.put("dprInfo", dprInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAllTankOtherDetailsList")
    public OIIPCRAResponse getAllTankOtherDetailsList(@RequestBody TankOtherDetailsListingDto tankOtherDetailsListingDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<TankOtherDetailsListingDto> tankOtherDetailsListPage = hydrologyService.getAllTankOtherDetailsList(tankOtherDetailsListingDto);
            List<TankOtherDetailsListingDto> tankOtherDetailsListingDtos = tankOtherDetailsListPage.getContent();
            if(!tankOtherDetailsListingDtos.isEmpty() && tankOtherDetailsListingDtos.size() > 0){
                result.put("TankOtherDetailsDtoList",tankOtherDetailsListingDtos);
                result.put("currentPage", tankOtherDetailsListPage.getNumber());
                result.put("totalItems", tankOtherDetailsListPage.getTotalElements());
                result.put("totalPages", tankOtherDetailsListPage.getTotalPages());
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("TankOtherDetailsDtoList", tankOtherDetailsListingDtos);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
        }

    @PostMapping("/getDprById")
    public OIIPCRAResponse getDprById(@RequestParam Integer dprId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            DprInformationDto dprInfo = hydrologyService.getDprInfo(dprId);
            List<CatchmentDetailsDto> catchmentDetails=hydrologyService.getCatchmentDetails(dprId);
            List<SoilDetailsDto> soilDetails=hydrologyService.getSoilDetails(dprId);
            List<PaniPanchayatDetailsDto> paniPanchayatDetails=hydrologyService.getPaniPanchayatDetails(dprId);
            if (dprInfo!=null) {
                result.put("dprInfo", dprInfo);
                result.put("catchmentDetails", catchmentDetails);
                result.put("soilDetails", soilDetails);
                result.put("paniPanchayatDetails", paniPanchayatDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Dpr By Id");
            } else {
                result.put("dprInfo", dprInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllPanipanchayatDetails")
    public OIIPCRAResponse getAllPanipanchayatDetails(@RequestParam Integer blockId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            List<PaniPanchayatListDto> ppList = hydrologyService.getAllPanipanchayatDetails(blockId);
            result.put("paniPanchayatDetail", ppList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Panipanchayat.");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/getAllDpr")
    public OIIPCRAResponse getAllDpr(@RequestBody TankOtherDetailsListingDto tankOtherDetailsListingDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
           Page<DprInformationDto> dprListPage=hydrologyService.getAllDpr(tankOtherDetailsListingDto);
            List<DprInformationDto> dprList = dprListPage.getContent();
            result.put("dprList", dprList);
            result.put("currentPage", dprListPage.getNumber());
            result.put("totalItems", dprListPage.getTotalElements());
            result.put("totalPages", dprListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Dpr.");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllDam")
    public OIIPCRAResponse getAllDam(@RequestBody TankOtherDetailsListingDto tankOtherDetailsListingDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<DamWeirDetailsDto> damWeirDetails =hydrologyService.getAllDam(tankOtherDetailsListingDto);
            List<DamWeirDetailsDto> damList = damWeirDetails.getContent();
            result.put("damList", damList);
            result.put("currentPage", damWeirDetails.getNumber());
            result.put("totalItems", damWeirDetails.getTotalElements());
            result.put("totalPages", damWeirDetails.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Dam.");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getGwLevelTrend")
    public OIIPCRAResponse getGwLevelTrend() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
           List<GwLevelTrendDto> levelTrend = hydrologyService.getGwLevelTrend();
           result.put("levelTrend", levelTrend);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" GW Level Trend List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/createEarthDam")
    public OIIPCRAResponse saveEarthDam(@RequestParam(name = "data") String data) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            DamWeirDetailsDto damInfo = mapper.readValue(data, DamWeirDetailsDto.class);
            DamWeirDetails saveEarthDam = hydrologyService.saveEarthDam(damInfo);
            List<AreaCapacityCurve> areaCapacityDetails = hydrologyService.saveAreaCapacityDetails(damInfo.getAreaCapacityCurves(),saveEarthDam.getId());
            result.put("saveEarthDam", saveEarthDam);
            result.put("areaCapacityDetails", areaCapacityDetails);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            oiipcraResponse.setMessage("EarthDam Created Successfully");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getDamById")
    public OIIPCRAResponse getDamById(@RequestParam Integer damId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            DamWeirDetailsDto damInfo = hydrologyService.getDamById(damId);
            List<AreaCapacityCurve>areaCapacityCurve = hydrologyService.getAreaDetailsByDamId(damId);
            if (damInfo!=null) {
                result.put("damInfo", damInfo);
                result.put("areaCapacityCurve", areaCapacityCurve);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Dam By Id");
            } else {
                result.put("damInfo", damInfo);
                result.put("areaCapacityCurve", areaCapacityCurve);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getDamByProjectId")
    public OIIPCRAResponse getDamByProjectId(@RequestParam Integer projectId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Integer damId = hydrologyService.getDamByProjectId(projectId);
            DamWeirDetailsDto damInfo = hydrologyService.getDamById(damId);
            List<AreaCapacityCurve>areaCapacityCurve = hydrologyService.getAreaDetailsByDamId(damId);
            if (damInfo!=null) {
                result.put("damInfo", damInfo);
                result.put("areaCapacityCurve", areaCapacityCurve);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Dam By Id");
            } else {
                result.put("damInfo", damInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getWaterSource")
    public OIIPCRAResponse getWaterSource() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<WaterSourceMDto> waterSource = hydrologyService.getWaterSource();
            result.put("waterSource", waterSource);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Water Source List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTankWeirCrest")
    public OIIPCRAResponse getTankWeirCrest() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankWeirCrestDto> tankWeir = hydrologyService.getTankWeirCrest();
            result.put("tankWeir", tankWeir);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Tank Weir List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getNatureOfWeir")
    public OIIPCRAResponse getNatureOfWeir() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<NatureOfWeirCrestDto> nature = hydrologyService.getNatureOfWeir();
            result.put("nature", nature);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Nature of Weir List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getBodyWallType")
    public OIIPCRAResponse getBodyWallType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BodyWallTypeDto> bodyWall = hydrologyService.getBodyWallType();
            result.put("bodyWall", bodyWall);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Body Wall Type List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAbutmentsType")
    public OIIPCRAResponse getAbutmentsType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AbutmentsTypeDto> abutmentsType = hydrologyService.getAbutmentsType();
            result.put("abutmentsType", abutmentsType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Abutments Type List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/updateDprApproval")
    public OIIPCRAResponse updateDprApproval(@RequestParam int userId, @RequestParam(name = "data") String data, @RequestParam Integer dprId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            Integer roleId = masterQryRepository.getRoleByUserId(userId);
            if (roleId > 4) {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("You have no permission to approve!!");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ApproveStatusDto updateDpr = mapper.readValue(data, ApproveStatusDto.class);
                boolean result = hydrologyService.updateDprApproval(dprId, updateDpr);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("DprApproval Updated");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    response);
        }
        return response;
    }

    @PostMapping("/updateTankOtherInfoApproval")
    public OIIPCRAResponse updateTankOtherInfoApproval(@RequestParam int userId, @RequestParam(name = "data") String data, @RequestParam Integer tankOtherInfoId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            Integer roleId = masterQryRepository.getRoleByUserId(userId);
            if (roleId > 4) {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("You have no permission to approve!!");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ApproveStatusDto updateTankOtherInfo = mapper.readValue(data, ApproveStatusDto.class);
                boolean result = hydrologyService.updateTankOtherInfoApproval(tankOtherInfoId, updateTankOtherInfo);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("TankOtherInfoApproval Updated");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    response);
        }
        return response;
    }


    @PostMapping("/updateHydrologyDataApproval")
    public OIIPCRAResponse updateHydrologyDataApproval(@RequestParam int userId, @RequestParam(name = "data") String data, @RequestParam Integer hydrologyDataId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            Integer roleId = masterQryRepository.getRoleByUserId(userId);
            if (roleId > 4) {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("You have no permission to approve!!");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ApproveStatusDto updateHydrologyData= mapper.readValue(data, ApproveStatusDto.class);
                boolean result = hydrologyService.updateHydrologyDataApproval(hydrologyDataId, updateHydrologyData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("HydrologyDataApproval Updated");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    response);
        }
        return response;
    }
    @PostMapping("/updateDamDetailsApproval")
    public OIIPCRAResponse updateDamDetailsApproval(@RequestParam int userId, @RequestParam(name = "data") String data, @RequestParam Integer damDetailsId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            Integer roleId = masterQryRepository.getRoleByUserId(userId);
            if (roleId > 4) {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("You have no permission to approve!!");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ApproveStatusDto updateDamDetailsData= mapper.readValue(data, ApproveStatusDto.class);
                boolean result = hydrologyService.updateDamDetailsApproval(damDetailsId, updateDamDetailsData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("DamDetailsApproval Updated");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    response);
        }
        return response;
    }



}
