package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.repositoryImpl.TenderRepositoryImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.SurveyService;
import lombok.extern.slf4j.Slf4j;
import com.orsac.oiipcra.service.TankSurveyService;
import com.orsac.oiipcra.serviceImpl.TankSurveyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/survey")
public class SurveyController {

    /**
     * @author ${Prasnajit}
     */

    @Autowired
    private SurveyService surveyService;


    @Autowired
    TankSurveyService tankSurveyService;

    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;

    @Autowired
    private TankSurveyServiceImpl tankSurveyServiceImpl;

    @Autowired
    private TenderRepositoryImpl tenderRepository;

    @Autowired
    private AWSS3StorageService awss3StorageService;
    @Value("${accessDepthImagePath}")
    private String accessDepthImagePath;


    /**
     * Here we have surveyed the tank details with geotagged image
     */
    @PostMapping("/surveyTank")
    public OIIPCRAResponse surveyTank(
            @RequestParam(name = "data", required = false) String data,
            @RequestParam(required = false) MultipartFile trainingImage,
            @RequestParam(required = false) MultipartFile surveyorImage,
            @RequestParam(name = "tankImages", required = false) MultipartFile[] tankImages,
            @RequestParam(name = "shaftImages", required = false) MultipartFile[] shaftImages) throws JsonProcessingException {
        return surveyService.insertTankSurveyData(data, trainingImage, surveyorImage, tankImages, shaftImages);
    }


    /**
     * View the surveyed tank details with geotagged image and parameter passes as primary key of tank survey data tale
     */
    @PostMapping("/tankSurveyInfoById")
    public OIIPCRAResponse getSurveyTankById(@RequestBody Map<String, String> param) {
        return surveyService.getTankSurveyInfoById(Integer.parseInt(param.get("id")), Integer.parseInt(param.get("flagId")));
    }
    @PostMapping("/tankImageById")
    public OIIPCRAResponse getTankImageById(@RequestParam Integer tankId) {
        return surveyService.getTankImageById(tankId);
    }


    /**
     * Update the surveyed tank data and parameter passes as primary key of tank survey data tale
     */
   /* @PostMapping("/updateTank")
    public OIIPCRAResponse updateTank(
            @RequestParam(name = "data",required = false) String data) throws JsonProcessingException {
        return surveyService.UpdateTankById(data);
    }*/
    @PostMapping("/updateTank")
    public OIIPCRAResponse updateTank(@RequestBody TankSurveyInfo tankSurveyInfo) {
        return surveyService.updateTankById(tankSurveyInfo);
    }

    /**
     * tank search survey list
     */
    @PostMapping("/tankSurveySearchList")
    public OIIPCRAResponse searchSurveyTankList(@RequestBody SurveyListRequest surveyListRequest) {
        return surveyService.searchSurveyTankList(surveyListRequest);
    }

    /**
     * Here we have surveyed the tank details with geotagged image
     */
    //save workProgress
    @PostMapping("/createActivity")
    public OIIPCRAResponse surveyActivity(
            @RequestParam(name = "data", required = false) String data,
            @RequestParam(required = false) MultipartFile surveyorImage,
            @RequestParam(name = "activityImages", required = false) MultipartFile[] activityImages) throws JsonProcessingException {
        return surveyService.insertActivitySurveyData(data, surveyorImage, activityImages);
    }

    /**
     * Update the Activity data and updated by primary key of activity table
     */
    @PostMapping("/updateActivity")
    public OIIPCRAResponse updateActivity(
            @RequestParam(name = "data", required = false) String data) throws JsonProcessingException {
        return surveyService.UpdateActivitySurveyById(data);
    }

    /**
     * activity search list
     */
    @PostMapping("/activitySearchList")
    public OIIPCRAResponse activitySearchList(@RequestBody ActivitySearchRequest activitySearchRequest) {
        return surveyService.searchActivityList(activitySearchRequest);
    }

    @PostMapping("/getActivityById")
    public OIIPCRAResponse getActivityById(@RequestParam Integer activityId) {
        return surveyService.getActivityById(activityId);
    }

    @PostMapping("/getContractList")
    public OIIPCRAResponse getContractList(@RequestBody ContractListRequestDto contractListRequestDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try {
            Page<ContractInfoListing> contractListPage = surveyService.getContractList(contractListRequestDto);
            List<ContractInfoListing> contract = new ArrayList<>();

            for (ContractInfoListing contractListPage1 : contractListPage) {
                List<ContractMappingDto> contractMapping = surveyService.getContractMapping(contractListPage1.getContractId());
                String tankName1 = "";
                for (int j = 0; j < contractMapping.size(); j++) {
                    if (contractMapping.get(j).getTankName() != null) {
                        if (j > 0) {
                            tankName1 += ",";
                        }
                        tankName1 += contractMapping.get(j).getTankName();

                    }
                }
                String noticeId1 = "";
                List<NoticeListingDto> noticeId = tenderRepository.getTenderNoticeByTenderId(contractListPage1.getTenderId());
                for (int j = 0; j < noticeId.size(); j++) {
                    if (noticeId.get(j).getId() != null) {
                        if (j > 0) {
                            noticeId1 += ",";
                        }
                        noticeId1 += noticeId.get(j).getId();
                    }
                }
                String bidId = "";
                for (int j = 0; j < contractMapping.size(); j++) {
                    if (contractMapping.get(j).getBidId() != null) {
                        if (!bidId.contains(contractMapping.get(j).getBidId())) {
                            if (j > 0) {
                                bidId += ",";
                            }

                            bidId += contractMapping.get(j).getBidId();
                        }
                    }
                }
                contractListPage1.setNoticeId(noticeId1);
                contractListPage1.setTankName(tankName1);
                contractListPage1.setBidId(bidId);
                contract.add(contractListPage1);

            }
            List<ContractInfoListing> contractList = contractListPage.getContent();
            result.put("contractList", contractList);
            result.put("currentPage", contractListPage.getNumber());
            result.put("totalItems", contractListPage.getTotalElements());
            result.put("totalPages", contractListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Contract List");
        } catch (Exception e) {
            log.info("Contract List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getInvoiceList")
    public OIIPCRAResponse getInvoiceList(@RequestBody InvoiceListRequestDto invoiceListRequestDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            Page<invoiceListingInfo> invoiceListPage = surveyService.getInvoiceList(invoiceListRequestDto);
            List<invoiceListingInfo> invoiceList = invoiceListPage.getContent();
            result.put("invoiceList", invoiceList);
            result.put("currentPage", invoiceListPage.getNumber());
            result.put("totalItems", invoiceListPage.getTotalElements());
            result.put("totalPages", invoiceListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Invoice List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    /**
     * tank search list
     */
    @PostMapping("/tankSearchList")
    public OIIPCRAResponse searchTankList(@RequestBody SurveyListRequest surveyListRequest) {
        List<Integer> tankIdsAgainstContract = new ArrayList<>();
        return surveyService.searchTankList(surveyListRequest);
    }

    @PostMapping("/tank538masterSearchList")
    public OIIPCRAResponse tank538masterSearchList(@RequestBody SurveyListRequest surveyListRequest) {
        return surveyService.tank538masterSearchList(surveyListRequest);
    }

    @PostMapping("/tankSearchListForWebsite")
    public OIIPCRAResponse tankSearchListForWebsite(@RequestParam(name = "blockId", required = false) Integer blockId,
                                                    @RequestParam(name = "start", required = false) Integer start,
                                                    @RequestParam(name = "length", required = false) Integer length,
                                                    @RequestParam(name = "draw", required = false) Integer draw) {
        return surveyService.tankSearchListForWebsite(blockId, start, length, draw);
    }

    @PostMapping("/tankCount")
    public OIIPCRAResponse tankCount(@RequestBody SurveyListRequest surveyListRequest) {
        List<Integer> tankIdsAgainstContract = new ArrayList<>();
        return surveyService.tankCount(surveyListRequest);
    }

    @PostMapping("/activityInfoSearchList")
    public OIIPCRAResponse activityInfoSearchList(@RequestParam(name = "data", required = false) String data,
                                                  @RequestParam Integer activityId) {
        return surveyService.activityInfoSearchList(data, activityId);
    }

    /**
     * api used for ui dropdown listing
     */
    @GetMapping("/getTankNameAndProjectId/{userId}")
    public OIIPCRAResponse getTankNameAndProjectId(@PathVariable int userId) {
        return surveyService.getTankNameAndProjectId(userId);
    }

    @GetMapping("/getSurveyTankNameAndProjectId/{userId}")
    public OIIPCRAResponse getSurveyTankNameAndProjectId(@PathVariable int userId) {
        return surveyService.getSurveyTankNameAndProjectId(userId);
    }

    /**
     * api used for get tank details by id
     */
    @PostMapping("/tankListById")
    public OIIPCRAResponse getTanListById(@RequestBody Map<String, String> param) {
        return surveyService.getTankListById(Integer.parseInt(param.get("id")), Integer.parseInt(param.get("flagId")));
    }

    @PostMapping("/getWaterSpreadData")
    public OIIPCRAResponse getWaterSpreadData(@RequestParam String projectId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            DecimalFormat df = new DecimalFormat("0.000");
            WaterSpreadDto val = new WaterSpreadDto();
//            WaterSpreadDto maxValue = surveyService.getMaxWaterSpreadData(projectId);
//            WaterSpreadDto minValue = surveyService.getMinWaterSpreadData(projectId);
//            WaterSpreadDto avg = surveyService.getAvgWaterSpreadData(projectId);

            val.setMaxValue(Double.valueOf(df.format(surveyService.getMaxWaterSpreadData(projectId))));
            val.setMinValue(Double.valueOf(df.format(surveyService.getMinWaterSpreadData(projectId))));
            val.setAverage(Double.valueOf(df.format(surveyService.getAvgWaterSpreadData(projectId))));
            val.setTotalWsa(Double.valueOf(df.format(surveyService.getTotalWaterSpreadData(projectId))));
            val.setLessThan50(Double.valueOf(df.format(surveyService.getLessThan50WaterSpreadData(projectId))));

            result.put("value", val);
//            result.put("minValue", minValue);
//            result.put("average", avg);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Value of WSA");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    /**
     * api used for get tank details by project id
     */
    @PostMapping("/tankListByProjectId")
    public OIIPCRAResponse getTanListByProjectId(@RequestBody Map<String, String> param) {
        return surveyService.getTankListByProjectId(Integer.parseInt(param.get("projectId")), Integer.parseInt(param.get("flagId")));
    }


    @PostMapping("/getWorkProgressList")
    public OIIPCRAResponse getWorkProgressList(@RequestBody WorkProgressDto workProgressDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<WorkProgressInfo> workProgressListPage = surveyService.getWorkProgressList(workProgressDto);
            List<WorkProgressInfo> workProgressList = workProgressListPage.getContent();
            result.put("workProgressList", workProgressList);
            result.put("currentPage", workProgressListPage.getNumber());
            result.put("totalItems", workProgressListPage.getTotalElements());
            result.put("totalPages", workProgressListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("WorkProgressList");
        } catch (Exception e) {
            log.info("Work Progress List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllClosedBidId")
    public OIIPCRAResponse getAllClosedBidId() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Tender> tenderMaster = surveyService.getAllClosedBidId();
            result.put("tenderMaster", tenderMaster);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Closed BidId List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllResultDeclaredBidId")
    public OIIPCRAResponse getAllResultDeclaredBidId() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Tender> tenderMaster = surveyService.getAllResultDeclaredBidId();
            result.put("tenderMaster", tenderMaster);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Closed BidId List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getWorkStatusDetails")
    public OIIPCRAResponse getWorkStatusDetails(@RequestParam(name = "tenderId", required = false) Integer tenderId, @RequestParam(name = "workId", required = false) Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            WorkStatusDto workStatusDto = surveyService.getWorkStatusDetails(tenderId, workId);
            // TenderDto tenderDto=surveyService.checkBidId(workStatusDto.getTenderId());
            List<ContractMappingDto> var = surveyService.existBidId(workStatusDto.getTenderId());
            if (var.size() > 0) {
                workStatusDto.setContractSigned(true);
            } else {
                workStatusDto.setContractSigned(false);
            }
            result.put("workStatusDto", workStatusDto);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("WorkStatus Details");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getWorkDetailsByTankId")
    public OIIPCRAResponse getWorkDetailsByTankId(@RequestParam(name = "tankId", required = false) Integer tankId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TankInfo> tankList = surveyRepositoy.getTankDetailsById(tankId);
            result.put("tankList", tankList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("TankList Details");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/createFeeder")
    public OIIPCRAResponse saveFeeder(@RequestParam(name = "data") String data,
                                      @RequestParam(name = "image", required = false) MultipartFile feederImage,
                                      @RequestParam(name = "surveyImages", required = false) MultipartFile[] surveyImages) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FeederDto feederDto = objectMapper.readValue(data, FeederDto.class);
            if (feederImage != null) {
                feederDto.setSurveyorImage(feederImage.getOriginalFilename());
            }
            FeederEntity feederObj = surveyService.saveFeeder(feederDto);
            if (feederImage != null) {
                boolean saveDocument = awss3StorageService.uploadFeederImage(feederImage, String.valueOf(feederObj.getId()), feederImage.getOriginalFilename());
                feederObj.setSurveyorImage(feederImage.getOriginalFilename());
            }

            if (surveyImages != null) {
                List<FeederImage> feederImageDto = surveyService.saveFeederImage(feederObj.getId(), surveyImages);
                for (MultipartFile mult : surveyImages) {
                    boolean saveDocument = awss3StorageService.uploadFeederSurveyImages(mult, String.valueOf(feederObj.getId()), mult.getOriginalFilename());
                }
            }
            List<FeederLocationEntity> feederLocation = surveyService.createFeederLocation(feederDto.getFeederLocation(), feederObj.getId());
            result.put("FeederMaster", feederObj);
            result.put("feederLocation", feederLocation);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Feeder Created");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/createDepth")
    public OIIPCRAResponse saveDepth(@RequestParam(name = "data") String data,
                                     @RequestParam(name = "surveyorImage", required = false) MultipartFile surveyorImage,
                                     @RequestParam(name = "image", required = false) MultipartFile surveyImage) {
        //@RequestParam(name = "surveyImages",required = false)MultipartFile[] surveyImages) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DepthDto depthDto = objectMapper.readValue(data, DepthDto.class);
            if (surveyorImage != null) {
                depthDto.setSurveyorImage(surveyorImage.getOriginalFilename());
            }
            else{
                depthDto.setSurveyorImage(null);
            }
            if (surveyImage != null) {
                depthDto.setImage(surveyImage.getOriginalFilename());
            }
            else{
                depthDto.setImage(null);
            }
            DepthEntity depthObj = surveyService.saveDepth(depthDto);

            if (surveyorImage != null) {
                boolean saveDocument = awss3StorageService.uploadSurveyorImageDepth(surveyorImage, String.valueOf(depthObj.getId()), surveyorImage.getOriginalFilename());
                depthObj.setSurveyorImage(surveyorImage.getOriginalFilename());
            }

            if (surveyImage != null) {
                boolean saveDocument = awss3StorageService.uploadSurveyImage(surveyImage, String.valueOf(depthObj.getId()), surveyImage.getOriginalFilename());
                depthObj.setImage(surveyImage.getOriginalFilename());
            }

//            if (surveyImages!=null ) {
//                List<DepthImageEntity> depthImages = surveyService.saveDepthImages(depthObj.getId(),surveyImages );
//                for (MultipartFile mult : surveyImages) {
//                    boolean saveDocument = awss3StorageService.uploadDepthSurveyImages(mult, String.valueOf(depthObj.getId()), mult.getOriginalFilename());
//                }
//            }
            result.put("DepthMaster", depthObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Depth Created");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/createCad")
    public OIIPCRAResponse saveCad(@RequestParam(name = "data") String data,
                                   @RequestParam(name = "image", required = false) MultipartFile cadImage,
                                   @RequestParam(name = "surveyImage", required = false) MultipartFile[] surveyImages) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CadDto cadDto = objectMapper.readValue(data, CadDto.class);
            if (cadImage != null) {
                cadDto.setSurveyorImage(cadImage.getOriginalFilename());
            }
            CadEntity cadObj = surveyService.saveCad(cadDto);
            if (cadImage != null) {
                boolean saveDocument = awss3StorageService.uploadCadImage(cadImage, String.valueOf(cadObj.getId()), cadImage.getOriginalFilename());
                cadObj.setSurveyorImage(cadImage.getOriginalFilename());
            }

            if (surveyImages != null) {
                List<CadImageEntity> cadImages = surveyService.saveCadImage(cadObj.getId(), surveyImages);
                for (MultipartFile mult : surveyImages) {
                    boolean saveDocument = awss3StorageService.uploadCadSurveyImages(mult, String.valueOf(cadObj.getId()), mult.getOriginalFilename());
                }
            }
            List<CadLocationEntity> cadLocation = surveyService.createCadLocation(cadDto.getCadLocation(), cadObj.getId());
            result.put("CadMaster", cadObj);
            result.put("cadLocation", cadLocation);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Cad Created");

        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getDepthImageByTankId")
    public OIIPCRAResponse getDepthImageByTankId(@RequestParam(name = "tankId") Integer tankId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DepthDto> depthImage = surveyService.getDepthIdByTankId(tankId);
            for (int i = 0; i < depthImage.size(); i++) {
                if(depthImage.get(i).getImage()!=null) {
                    depthImage.get(i).setImageUrl(accessDepthImagePath + "/" + depthImage.get(i).getId() + "/" + depthImage.get(i).getImage());
                }

                if(depthImage.get(i).getSurveyorImage()!=null) {
                    depthImage.get(i).setSurveyorImageUrl(accessDepthImagePath + "/" + depthImage.get(i).getId() + "/" + depthImage.get(i).getSurveyorImage());
                }

            }
            // List<DepthImageDto> depthImage = surveyService.getDepthImagesByDepthId(depthId.getId());
            result.put("depthImage", depthImage);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Depth Image By TankId");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getCadDetailsByTankId")
    public OIIPCRAResponse getCadDetailsByTankId(@RequestParam(name = "tankId") Integer tankId,
                                                 @RequestParam(name = "typeId", required = false) Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CadDto> cadDetails = surveyService.getCadDetailsByTankId(tankId);
            for (CadDto geom : cadDetails) {
                if (geom.getGeomm() == null) {
                    List<Integer> cadIds = surveyService.getCadIdsByTankId(tankId);
                    for (int i = 0; i < cadIds.size(); i++) {
                        List<CadDto> cadDto = surveyService.getLatLongByCadId(cadIds.get(i));
                        Integer updateGeom = surveyService.updateCadGeom(cadDto, cadIds.get(i));
                    }
                }
            }
           // CadDto  cadLatLong=surveyRepositoy.getCadLatLongByTankId(tankId);
//            for (int i = 0; i < cadDetails.size(); i++) {
//                List<CadLocationDto> cadLocation = surveyService.getCadLocationByTankId(cadDetails.get(i).getId());
//                List<CadImageDto> cadImage = surveyService.getCadImageDetails(cadDetails.get(i).getId());
//                result.put("cadLocation", cadLocation);
//                result.put("cadImage", cadImage);
            result.put("cadDetails", cadDetails);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Cad Details By TankId");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getCadDetailsByCadId")
    public OIIPCRAResponse getCadDetailsByCadId(@RequestParam(name = "cadId") Integer cadId,
                                                @RequestParam(name = "typeId", required = false) Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CadDto> cadDetails = surveyService.getCadDetailsByCadId(cadId);
            List<CadLocationDto> cadLocation = new ArrayList<>();
            List<CadImageDto> cadImage = new ArrayList<>();

            for(CadDto data : cadDetails){
                cadLocation = surveyService.getCadLocationByTankId(data.getId());
                cadImage = surveyService.getCadImageDetails(data.getId());
            }
            result.put("cadDetails", cadDetails);
            result.put("cadLocation", cadLocation);
            result.put("cadImage", cadImage);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Cad Details By TankId");

        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getAllCadByCadId")
    public OIIPCRAResponse getAllCadByCadId(@RequestParam(name = "cadId", required = false) Integer cadId,
                                            @RequestParam(name = "typeId", required = false) Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            CadDto cadDetails = surveyService.getAllCadByCadId(cadId, typeId);
            result.put("AllCadDetails", cadDetails);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Cad Details");

        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getAllFeederByFeederId")
    public OIIPCRAResponse getAllFeederByFeederId(@RequestParam(name = "feederId", required = false) Integer feederId,
                                                  @RequestParam(name = "typeId", required = false) Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            FeederDto feederDto = surveyService.getAllFeederByFeederId(feederId, typeId);
            result.put("AllFeederDetails", feederDto);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Feeder Details");

        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getFeederDetails")
    public OIIPCRAResponse getFeederDetails(@RequestParam(name = "tankId") Integer tankId,
                                            @RequestParam(name = "typeId", required = false) Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeederDto> feederDto = surveyService.getFeederDetails(tankId, typeId);
            for (FeederDto geom : feederDto) {
                if (geom.getGeom() == null) {
                    List<Integer> feederIds = surveyService.getfeederIdsByTankId(tankId);
                    for (int i = 0; i < feederIds.size(); i++) {
                        List<FeederDto> feederDtos = surveyService.getLatLongByFeederId(feederIds.get(i));
                        Integer updateGeom = surveyService.updateFeederGeom(feederDtos, feederIds.get(i));
                    }
                }
            }

//            for (int i = 0; i < feederDto.size(); i++) {
//                List<FeederImageDto> feederImageDtos = surveyService.getFeederImageByTankId(feederDto.get(i).getId());
//                List<FeederLocationDto> feederLocation = surveyService.getFeederLocation(feederDto.get(i).getId());
            result.put("feederDetails", feederDto);
//                result.put("feederImage", feederImageDtos);
//                result.put("feederLocation", feederLocation);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Feeder Details By TankId");

        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


    @PostMapping("/getFeederDetailsById")
    public OIIPCRAResponse getFeederDetailsById(@RequestParam(name = "feederId") Integer feederId,
                                                @RequestParam(name = "typeId", required = false) Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeederDto> feederDto = surveyService.getFeederDetailsById(feederId);

            List<FeederImageDto> feederImageDtos = new ArrayList<>();
            List<FeederLocationDto> feederLocation = new ArrayList<>();
            for (FeederDto data : feederDto) {
                feederImageDtos = surveyService.getFeederImageByTankId(data.getId());

                feederLocation = surveyService.getFeederLocation(data.getId());
            }
            result.put("feederDetails", feederDto);
            result.put("feederImage", feederImageDtos);
            result.put("feederLocation", feederLocation);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Feeder Details By TankId");

        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getAllFeederDetails")
    public OIIPCRAResponse getAllFeederDetails() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeederDto> feederDto = surveyService.getAllFeederDetails();

            result.put("feederDetails", feederDto);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Feeder Details");

        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getAllCadDetails")
    public OIIPCRAResponse getAllCadDetails() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CadDto> cadDto = surveyService.getAllCadDetails();

            result.put("cadDto", cadDto);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Cad Details");

        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


    @PostMapping("/getAllContractType")
    public OIIPCRAResponse getAllContractType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractTypeDto> contractType = surveyService.getAllContractType();
            if (!contractType.isEmpty() && contractType.size() > 0) {
                result.put("ContractType", contractType);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("AllContractType.");
            } else {
                result.put("ContractType", contractType);
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


    @PostMapping("/getPhysicalProgressDetails")
    public OIIPCRAResponse getPhysicalProgressDetails(@RequestParam(name = "contractId") Integer contractId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<PhysicalProgressPlannedDto> planned = surveyService.getPlannedDetails(contractId);
            List<PhysicalProgressExecutedDto> executed = surveyService.getExecutedDetails(contractId);
            result.put("planned", planned);
            result.put("executed", executed);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("PhysicalProgressDetails");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/updateCadGeom")
    public OIIPCRAResponse updateCadGeom(@RequestParam(name = "cadId") Integer cadId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CadDto> cadDto = surveyService.getLatLongByCadId(cadId);
            Integer updateGeom = surveyService.updateCadGeom(cadDto, cadId);
            result.put("updateGeom", updateGeom);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Geom Updated");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/updateFeederGeom")
    public OIIPCRAResponse updateFeederGeom(@RequestParam(name = "feederId") Integer feederId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeederDto> feederDto = surveyService.getLatLongByFeederId(feederId);
            Integer updateGeom = surveyService.updateFeederGeom(feederDto, feederId);
            result.put("updateGeom", updateGeom);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Geom Updated");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


}
