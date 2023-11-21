package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.dto.TenderDto;
import com.orsac.oiipcra.dto.TenderListDto;
import com.orsac.oiipcra.dto.TenderOpeningDto;
import com.orsac.oiipcra.dto.TenderStipulationList;
import com.orsac.oiipcra.entities.*;

import com.orsac.oiipcra.repository.FinancialBidRepository;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.repository.TenderStipulationRepository;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.repositoryImpl.TenderRepositoryImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.TenderService;
import com.orsac.oiipcra.service.UserService;
import com.orsac.oiipcra.serviceImpl.TenderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Console;
import java.util.*;


@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/tender")
public class TenderController {

    @Autowired
    private TenderService tenderService;
    @Autowired
    private TenderStipulationRepository tenderStipulationRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AWSS3StorageService awss3StorageService;

    @Autowired
    private TenderRepositoryImpl tenderRepositoryImpl;
    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;
    @Autowired
    private TenderServiceImpl tenderServiceImpl;

    @Autowired
    private MasterQryRepository masterQryRepository;

    @Autowired
    FinancialBidRepository financialBidRepository;
    @Autowired
    private UserService userService;

    @PostMapping("/addTender")
    public OIIPCRAResponse saveNewTender(@RequestParam(name = "data", required = false) String data,
                                         @RequestParam(name = "tenderDoc", required = false) MultipartFile[] tenderDoc) throws JsonProcessingException {
        return tenderService.addTender(data, tenderDoc);
    }

    @PostMapping("/addNewCorrigendum")
    public OIIPCRAResponse saveNewCorrigendum(@RequestParam(name = "data", required = false) String data,
                                              @RequestParam int tenderId) throws JsonProcessingException {
        return tenderService.saveTenderCorrigendum(data, tenderId);
    }

    @PostMapping("/getAllCorrigendumByTenderId")
    public OIIPCRAResponse getAllCorrigendumByTenderId(@RequestParam int tenderId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderCorrigendum> tenderCorrigendumList = tenderService.getTenderCorrigendumListByTenderId(tenderId);
            result.put("CorrigendumList", tenderCorrigendumList);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Tender Update Successfully");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;

    }

    //update Tender
    @PostMapping("/updateTender")
    public OIIPCRAResponse updateUser(@RequestParam Integer id,
                                      @RequestParam(name = "data") String data,
                                      @RequestParam(name = "files", required = false) MultipartFile[] files) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<TenderPublished> tenderPublished=new ArrayList<>();
            TenderRequest tenderRequest = mapper.readValue(data, TenderRequest.class);
            Tender updateTender = tenderService.updateTender(id, tenderRequest);
            tenderService.deactivateTenderPublish(updateTender.getId());
            if(tenderRequest.getTenderPublishedInfo()!=null) {
                tenderPublished = tenderService.saveTenderPublish(tenderRequest.getTenderPublishedInfo(), updateTender.getId());
            }
            if (files!=null) {
                for (MultipartFile mult : files) {
                    boolean saveDocument = awss3StorageService.uploadPublishedDocument(mult, String.valueOf(updateTender.getId()), mult.getOriginalFilename());
                }
            }
            result.put("tender", updateTender);
            result.put("tenderPublished", tenderPublished);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Tender Update Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    /**
     * Tender search list
     */
    @PostMapping("/tenderSearchList")
    public OIIPCRAResponse tenderSearchList(@RequestBody TenderDto tenderDto) {
        return tenderService.tenderSearchList(tenderDto);
    }


    //TenderViewDetails
    @PostMapping("/viewTenderByTenderId")
    public OIIPCRAResponse viewTenderByTenderId(@RequestParam(name = "tenderId", required = false) Integer tenderId) {
        return tenderService.viewTenderByTenderId(tenderId);
    }



    // getTenderNoticeDate By Bid Id
    @PostMapping("/getTenderNoticeDate")
    public OIIPCRAResponse getTenderNoticeDate(@RequestParam(name = "tenderId", required = false) Integer tenderId) {
        return tenderService.getTenderNoticeDate(tenderId);
    }

    //tenderListing
    @PostMapping("/getTenderList")
    public OIIPCRAResponse getTenderList(@RequestBody TenderListDto tenderListDto) {
        return tenderService.getTenderList(tenderListDto);
    }


    @PostMapping("/updateTenderApproval")
    public OIIPCRAResponse updateTenderApproval(@RequestParam int userId, @RequestParam(name = "data") String data, @RequestParam Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        try {
            Integer roleId = masterQryRepository.getRoleByUserId(userId);
            if (roleId > 4) {
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("You have no permission to approve!!");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                ApproveStatusDto updateTenderApproval = mapper.readValue(data, ApproveStatusDto.class);
                boolean result = tenderService.updateTenderApproval(tenderId, updateTenderApproval);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                response.setMessage("TenderApproval Updated");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    response);
        }
        return response;
    }

    //add TenderNotice
    @PostMapping("/addTenderNotice")
    public OIIPCRAResponse addTenderNotice(@RequestParam(name = "data") String data) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TenderNoticeDto tenderNoticeDto = objectMapper.readValue(data, TenderNoticeDto.class);
            TenderNotice saveTenderNotice = tenderService.addTenderNotice(tenderNoticeDto);
            List<TenderNoticeLevelMapping> tenderNoticeLevelMapping = tenderService.saveTenderNoticeLevelMapping(tenderNoticeDto.getTenderNoticeLevelMapping(), saveTenderNotice.getId());
            List<WorkProjectMapping> workProjectMapping = tenderService.addWorkProjectMapping(tenderNoticeDto.getWorkProjectMapping(), saveTenderNotice.getId(), saveTenderNotice.getTenderId());
            result.put("TenderNotice", saveTenderNotice);
            result.put("TenderNoticeLevelMapping", tenderNoticeLevelMapping);
            result.put("WorkProjectMapping", workProjectMapping);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New Tender Notice Created");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }

        return response;
    }
/*    //TenderNotice ById
    @PostMapping("/getTenderNoticeByTenderId")
    public OIIPCRAResponse getTenderNoticeByTenderId(@RequestParam Integer tenderId) {
        return tenderService.getTenderNoticeByTenderId(tenderId);
    }

    @PostMapping("/getTenderNoticeById")
    public OIIPCRAResponse getTenderNoticeById(@RequestParam Integer noticeId) {
        return tenderService.getTenderNoticeById(noticeId);
    }*/

    @PostMapping("/updateTenderNotice")
    public OIIPCRAResponse updateTenderNotice(@RequestParam Integer id,
                                              @RequestParam(name = "data") String data) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            TenderNoticeDto tenderNoticeRequest = mapper.readValue(data, TenderNoticeDto.class);
            TenderNotice updateTender = tenderService.updateTenderNotice(id, tenderNoticeRequest);
            for (int j = 0; j < tenderNoticeRequest.getWorkProjectMapping().size(); j++) {
                if (tenderNoticeRequest.getWorkProjectMapping().get(j).getId() != null) {
                    tenderService.updateWorkProjectMappingByNoticeId(updateTender.getId(), tenderNoticeRequest.getWorkProjectMapping().get(j).getId(), tenderNoticeRequest.getWorkProjectMapping().get(j));
                } else {
                    tenderService.saveWorkProjectMapping(tenderNoticeRequest.getWorkProjectMapping().get(j), updateTender.getId(), updateTender.getTenderId());
                }
            }
            Boolean changeTenderNoticeLevelMapping = tenderService.changeTenderNoticeLevelMapping(updateTender.getId());
            for (int j = 0; j < tenderNoticeRequest.getTenderNoticeLevelMapping().size(); j++) {
                if (tenderNoticeRequest.getTenderNoticeLevelMapping().get(j).getId() != null) {
                    tenderService.updateTenderNoticeLevelMappingByNoticeId(updateTender.getId(), tenderNoticeRequest.getTenderNoticeLevelMapping().get(j).getId(), tenderNoticeRequest.getTenderNoticeLevelMapping().get(j));
                } else{
                    tenderService.saveTenderNoticeLevelMappingSingle(tenderNoticeRequest.getTenderNoticeLevelMapping().get(j), updateTender.getId());
                }
            }

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Tender Notice Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //TenderNotice ById
    @PostMapping("/getTenderNoticeByTenderId")
    public OIIPCRAResponse getTenderNoticeByTenderId(@RequestParam Integer tenderId) {
        return tenderService.getTenderNoticeByTenderId(tenderId);
    }
    @PostMapping("/getTenderNoticeByTenderIdListing")
    public OIIPCRAResponse getTenderNoticeByTenderIdListing(@RequestBody TenderNoticeListingDto tenderNotice) {
        return tenderService.getTenderNoticeByTenderIdListing(tenderNotice);
    }


    @PostMapping("/getTenderNoticeById")
    public OIIPCRAResponse getTenderNoticeById(@RequestParam Integer noticeId) {
        return tenderService.getTenderNoticeById(noticeId);
    }



    //add TenderStipulation
    @PostMapping("/addTenderStipulation")
    public OIIPCRAResponse addTenderStipulation(@RequestParam(name = "data") String data) throws JsonProcessingException {
        return tenderService.addTenderStipulation(data);
    }

    //ViewTenderStipulationByTenderId
    @PostMapping("/viewTenderStipulationByTenderId")
    public OIIPCRAResponse viewTenderStipulationByTenderId(@RequestParam(name = "tenderId", required = false) Integer tenderId) {
        return tenderService.viewTenderStipulationByTenderId(tenderId);
    }

    //ViewTenderStipulationByStipulationId
    @PostMapping("/viewTenderStipulationById")
    public OIIPCRAResponse viewTenderStipulationById(@RequestParam(name = "stipulationId", required = false) Integer stipulationId) {
        return tenderService.viewTenderStipulationById(stipulationId);
    }

    //GetAllWork
    @PostMapping("/getAllWork")
    public OIIPCRAResponse getAllWork() {
        return tenderService.getAllWork();
    }

    //TenderStipulationListing
    @PostMapping("/getTenderStipulationList")
    public OIIPCRAResponse getTenderStipulationList(@RequestBody TenderStipulationList tenderStipulationList) {
        return tenderService.getTenderStipulationList(tenderStipulationList);
    }


    //Update TenderStipulation ById
   /* @PostMapping ("/updateTenderStipulation)
            public OIIPCRAResponse updateTenderStipulation(@RequestParam (name = id) Integer id,
                                                      @RequestParam(name = "data") String data);

            OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
            Map<String, Object> result = new HashMap<>();
            try {
            ObjectMapper mapper = new ObjectMapper();
            TenderNoticeDto tenderNoticeRequest = mapper.readValue(data, TenderNoticeDto.class);
            TenderStipulation tenderStipulation1 = tenderService.updateTenderStipulation(id,tenderStipulation);
            result.put("tenderStipulation1", tenderStipulation1);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("TenderStipulation Updated successfully.");
        }catch (Exception ex){
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
        }*/
    @PostMapping("/updateTenderStipulation")
    public OIIPCRAResponse updateTenderStipulation(@RequestParam Integer id,
                                                   @RequestParam(name = "data") String data) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            TenderStipulationDto tenderStipulation = mapper.readValue(data, TenderStipulationDto.class);
            TenderStipulation updateStipulation = tenderService.updateTenderStipulation(id, tenderStipulation);
            result.put("updateStipulation", updateStipulation);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("TenderStipulation Updated successfully.");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //add MeetingProceedings
    @PostMapping("/addMeetingProceedings")
    public OIIPCRAResponse addMeetingProceedings(@RequestParam(name = "data") String data) throws JsonProcessingException {
        return tenderService.addMeetingProceedings(data);
    }

    // get All BiddingType
    @PostMapping("/getBiddingType")
    public OIIPCRAResponse getBiddingType() {
        return tenderService.getBiddingType();
    }

    //getTenderWorkType
    @PostMapping("/getTenderWorkType")
    public OIIPCRAResponse getTenderWorkType() {
        return tenderService.getTenderWorkType();
    }


    //GetAllProject
    @PostMapping("/getAllProject")
    public OIIPCRAResponse getAllProject() {
        return tenderService.getAllProject();
    }
    @PostMapping("/getAllSchemeOfFunding")
    public OIIPCRAResponse getAllSchemeOfFunding() {
        return tenderService.getAllSchemeOfFunding();
    }

    //TenderNoticeListing
    @PostMapping("/getTenderNoticeList")
    public OIIPCRAResponse getTenderNoticeList(@RequestBody TenderListDto tenderListDto) {
        return tenderService.getTenderNoticeList(tenderListDto);
    }

    @PostMapping("/getEstimateIdAndWork")
    public OIIPCRAResponse getEstimateIdAndWork(@RequestParam Integer id) {
        return tenderService.getEstimateIdAndWork(id);
    }

    @PostMapping("/getEstimateForTender")
    public OIIPCRAResponse getEstimateForTender(@RequestParam Integer userId, @RequestParam Integer estimateId) {
        return tenderService.getEstimateForTender(userId, estimateId);
    }

    @PostMapping("/getAllMeetingType")
    public OIIPCRAResponse getAllMeetingType(@RequestParam Integer roleId) {
        return tenderService.getAllMeetingType(roleId);
    }

    @PostMapping("/getAllTenderPublishType")
    public OIIPCRAResponse getAllTenderPublishType() {
        return tenderService.getAllTenderPublishType();
    }

    @PostMapping("/saveAnnualFinancialTurnover")
    public OIIPCRAResponse saveAnnualFinancialTurnover(@RequestParam(name = "data") String data, @RequestParam Integer  agencyId,@RequestParam Integer bidderId,@RequestParam Boolean annualTurnOverValidity,@RequestParam Double maxBidCapacity) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try {
            List<AnnualFinancialTurnoverMaster> annualWork=new ArrayList<>();
            JSONObject jsonObject = new JSONObject(data);
            List<AnnualFinancialTurnoverMasterDto> annualFinancialTurnover = objectMapper.readValue(jsonObject.get("annualFinancialTurnOver").toString(), new TypeReference<List<AnnualFinancialTurnoverMasterDto>>() {});
            for(AnnualFinancialTurnoverMasterDto c:annualFinancialTurnover){
                Integer year=  tenderRepositoryImpl.getFinancialyearId(c.getFinancialYearName());
                c.setFinyrId(year);
                AnnualFinancialTurnoverMaster details = new AnnualFinancialTurnoverMaster();
                BeanUtils.copyProperties(c, details);
                annualWork.add(details);
            }
            List<AnnualFinancialTurnoverMaster> annualFinance = tenderService.saveAnnualFinancialTurnover(annualWork, agencyId,bidderId);
           /* BidderDetails bidder=tenderService.getBidderDetailsById(bidderId);*/
/*            Boolean emdValid=tenderServiceImpl.emdValid(bidder.getTenderId(),bidderId);
            //     Boolean liquidValid=  tenderServiceImpl.getLiquidAsset(agencyId,bidder.getTenderId(),bidder.getWorkId());
            Boolean financialTurnOverValid=tenderServiceImpl.getFinancialTurnOverValidity(agencyId,bidder.getTenderId(),bidder.getWorkId());
            Boolean similarTypeOfWorkValid=tenderServiceImpl.getSimilarTypeOfWorkValidity(bidderId,bidder.getTenderId(),bidder.getWorkId());
            Boolean update= tenderRepositoryImpl.validationUpdate(emdValid, similarTypeOfWorkValid, financialTurnOverValid,  true,bidderId);*/
            Integer updateBidder=tenderService.updateAnnualTurnOverValidation(bidderId,annualTurnOverValidity,maxBidCapacity);
            result.put("annualFinancialTurnover", annualFinance);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Annual Financial Turnover Saved/Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Annual Finance Exception : {}", e.getMessage());
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //Tender Opening APIs
    @PostMapping("/saveCompletionOfSimilarTypeOfWorkValue")
    public OIIPCRAResponse saveCompletionOfSimilarTypeOfWorkValue(@RequestParam(name = "data") String data, @RequestParam Integer bidderId,@RequestParam Integer agencyId,@RequestParam Boolean completionValidity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
       // List<CompletionOfSimilarTypeOfWorkDto> completionOfSimilarWork = mapper.readValue(data,new TypeReference<List<CompletionOfSimilarTypeOfWorkDto>>(){});
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CompletionOfSimilarTypeOfWork> completionOfSimilarTypeOfWork=new ArrayList<>();
             JSONObject jsonObject =  new JSONObject(data);
            ObjectMapper obj=new ObjectMapper();
            List<CompletionOfSimilarTypeOfWorkDto> completionOfSimilarWork= objectMapper.readValue(jsonObject.get("completionOfSimilarTypeOfWork").toString(),new TypeReference<List<CompletionOfSimilarTypeOfWorkDto>>(){});
            //List<CompletionOfSimilarTypeOfWorkDto> cst=obj.readValue(data,new TypeReference<List<CompletionOfSimilarTypeOfWorkDto>>(){});
            for(CompletionOfSimilarTypeOfWorkDto c:completionOfSimilarWork){
              Integer year=  tenderRepositoryImpl.getFinancialyearId(c.getFinYrName());
                c.setFinyrId(year);
                CompletionOfSimilarTypeOfWork details = new CompletionOfSimilarTypeOfWork();
                BeanUtils.copyProperties(c, details);
                completionOfSimilarTypeOfWork.add(details);

            }
          //  List<CompletionOfSimilarTypeOfWork> completionOfSimilarTypeOfWork= objectMapper.readValue(jsonObject.get("completionOfSimilarTypeOfWork").toString(),new TypeReference<List<CompletionOfSimilarTypeOfWork>>(){});
            List<CompletionOfSimilarTypeOfWork> completion=tenderService.saveCompletionOfSimilarWorkValue(completionOfSimilarTypeOfWork,bidderId);
            /*BidderDetails bidder=tenderService.getBidderDetailsById(bidderId);
            Boolean emdValid=tenderServiceImpl.emdValid(bidder.getTenderId(),bidderId);
            Boolean liquidValid=  tenderServiceImpl.getLiquidAsset(agencyId,bidder.getTenderId(),bidder.getWorkId());
            Boolean financialTurnOverValid=tenderServiceImpl.getFinancialTurnOverValidity(agencyId,bidder.getTenderId(),bidder.getWorkId());
            Boolean similarTypeOfWorkValid=tenderServiceImpl.getSimilarTypeOfWorkValidity(bidderId,bidder.getTenderId(),bidder.getWorkId());
            Boolean update= tenderRepositoryImpl.validationUpdate(emdValid, similarTypeOfWorkValid, financialTurnOverValid,  liquidValid,bidderId);
            result.put("completionOfSimilarWorkType", completion);*/
            Integer updateBidder=tenderService.updateCompletionOfSimilarTypeOfWorkValidation(bidderId,completionValidity);
            result.put("completionOfSimilarWorkType",completion);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            oiipcraResponse.setMessage("Completion Of Similar Type Of Work Value Created/Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Create Bidder Details
    @PostMapping("/createBidderDetails")
    public OIIPCRAResponse createBidderDetails(@RequestBody BidderDetails bidderDetails) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            BidderDetails bidderDetails1 = tenderService.createBidderDetails(bidderDetails);
            if (bidderDetails1 != null) {
                result.put("bidderDetails1", bidderDetails1);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage(" Bidder Details Created Successfully");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }



    //Create liquid Asset
    @PostMapping("/createLiquidAsset")
    public OIIPCRAResponse createLiquidAsset(@RequestParam(name = "data") String data, @RequestParam Integer agencyId,@RequestParam Integer bidderId,@RequestParam Boolean liquidValidity) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            LiquidAssetAvailability liquidAsset = objectMapper.readValue(data, LiquidAssetAvailability.class);

/*            BidderDetails bidder=tenderService.getBidderDetailsById(bidderId);
            Boolean emdValid=tenderServiceImpl.emdValid(bidder.getTenderId(),bidderId);
          //    Boolean liquidValid=  tenderServiceImpl.getLiquidAsset(agencyId,bidder.getTenderId(),bidder.getWorkId());
            Boolean financialTurnOverValid=tenderServiceImpl.getFinancialTurnOverValidity(agencyId,bidder.getTenderId(),bidder.getWorkId());
            Boolean similarTypeOfWorkValid=tenderServiceImpl.getSimilarTypeOfWorkValidity(bidderId,bidder.getTenderId(),bidder.getWorkId());*/
            LiquidAssetAvailability liquidAsset1 = tenderService.createLiquidAsset(liquidAsset, bidderId,agencyId);
         //   Boolean update= tenderRepositoryImpl.validationUpdate(emdValid, similarTypeOfWorkValid, financialTurnOverValid,  true,bidderId);
             Integer updateBidder=tenderService.updateLiquidAssetValidation(bidderId,liquidValidity);
            if (liquidAsset1 != null) {
                result.put("liquidAsset1", liquidAsset1);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage(" liquid Asset Created/Updated Successfully");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/createTenderOpening")
    public OIIPCRAResponse createTenderOpening(@RequestBody TenderOpeningDto tenderOpeningDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            BidderDetails bidder = tenderService.createBidderDetails(tenderOpeningDto.getBidderDetails());
            List<CompletionOfSimilarTypeOfWork> work = tenderService.saveCompletionOfSimilarWorkValue(tenderOpeningDto.getCompletionOfSimilarTypeOfWorkList(), bidder.getId());
            List<AnnualFinancialTurnoverMaster> annualTurnOver = tenderService.saveAnnualFinancialTurnover(tenderOpeningDto.getAnnualFinancialTurnoverMasters(), bidder.getId(),bidder.getAgencyId());
            LiquidAssetAvailability liquidAsset = tenderService.createLiquidAsset(tenderOpeningDto.getLiquidAssetAvailabilities(), bidder.getId(),bidder.getAgencyId());

            result.put("bidder", bidder);
            result.put("work", work);
            result.put("annualTurnOver", annualTurnOver);
            result.put("liquidAsset", liquidAsset);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" Tender Opening Created Successfully");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTenderDetailsForStipulationByBidId")
    public OIIPCRAResponse getTenderDetailsForStipulationByBidId(@RequestParam(name = "tenderId", required = false) Integer tenderId) {
        return tenderService.getTenderDetailsForStipulationByBidId(tenderId);
    }

    //Api For Getting The Left Side Details For Tender Opening
    @PostMapping("/getBidDetailsForComparison")
    public OIIPCRAResponse getBidDetailsForComparison(@RequestParam Integer workId, @RequestParam Integer tenderId) {
        return tenderService.getBidDetailsForComparison(workId, tenderId);
    }

    @PostMapping("/getWorkSlNoByBidId")
    public OIIPCRAResponse getWorkSlNoByBidId(@RequestParam(name = "tenderId", required = false) Integer tenderId) {
        return tenderService.getWorkSlNoByBidId(tenderId);
    }


    //Create FinancialBid Details
    @PostMapping("/addFinancialBidDetails")
    public OIIPCRAResponse addFinancialBidDetails(@RequestParam(name = "data") String data) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FinancialBidMasterDto financialBidDto = objectMapper.readValue(data, FinancialBidMasterDto.class);
            FinancialBidDetails saveFinancialBid = tenderService.addFinancialBidDetails(financialBidDto);
            if(saveFinancialBid!=null) {
                Integer updateFinancialBidOpeningDate = tenderRepositoryImpl.updateFinancialBidOPeningDate(financialBidDto.getTenderId(), financialBidDto.getFinancialBidOpeningDate());
                // TenderResult tenderResult = tenderService.addTenderResult(financialBidDto.getTenderResult(), financialBidDto.getTenderId(), financialBidDto.getWorkId(), financialBidDto.getBidderId());
                if (saveFinancialBid != null) {
                    result.put("financialBidDetails1", saveFinancialBid);
                    //    result.put("tenderResult", tenderResult);
                    response.setData(result);
                    response.setStatus(1);
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                    response.setMessage("FinancialBid Created Successfully");
                }
            }
            else{
                response.setData(result);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Not Eligible For Financial Bid");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/addFinancialAbstract")
    public OIIPCRAResponse addFinancialAbstract(@RequestParam(name = "data") String data) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TenderResultDto tenderResult = objectMapper.readValue(data, TenderResultDto.class);
             TenderResult financialAbstract = tenderService.addTenderResult(tenderResult);
            if (financialAbstract != null) {
                result.put("financialAbstract", financialAbstract);
                //    result.put("tenderResult", tenderResult);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("FinancialAbstract Created Successfully");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/updateFinancialAbstract")
    public OIIPCRAResponse updateFinancialAbstract(@RequestParam(name = "data") String data,@RequestParam Integer id) throws JsonProcessingException {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TenderResultDto tenderResult = objectMapper.readValue(data, TenderResultDto.class);
            TenderResult financialAbstract = tenderService.updateFinancialAbstract(tenderResult,id);
            if (financialAbstract != null) {
                result.put("financialAbstract", financialAbstract);
                //    result.put("tenderResult", tenderResult);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("FinancialAbstract Updated Successfully");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getBidderDetailsById")
    public OIIPCRAResponse getBidderDetailsById(@RequestParam Integer bidderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            BidderDetails bidderDetails = tenderService.getBidderDetailsById(bidderId);
            if (bidderDetails != null) {
                result.put("BidderDetails", bidderDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Bidder Details By Id");
            } else {
                result.put("BidderDetails", bidderDetails);
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


    //Create FinancialBid Details BY ID
    @PostMapping("/getFinancialBidDetailsById")
    public OIIPCRAResponse getFinancialBidDetailsById(@RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinancialBidInfo> financialBid = tenderService.getFinancialBidDetailsById(id);
            if (financialBid != null) {
                result.put("FinancialBid", financialBid);
//                result.put("FinancialBid", financialBid);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All FinancialBid Details");
            } else {
                result.put("FinancialBid", financialBid);
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
    @PostMapping("/getFinancialAbstract")
    public OIIPCRAResponse getFinancialAbstract(@RequestParam Integer bidId, @RequestParam Integer workId, @RequestParam Integer bidderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinancialBidInfo> financialAbstract = tenderService.getFinancialAbstract(bidId,workId,bidderId);
            FinancialBidInfo financialBid = tenderService.getFinancialBidDetailsByBidderId(bidderId);
            BidderDetails  bidder=tenderRepositoryImpl.getBidderDetailsById(bidderId);
            AgencyInfo agency=tenderRepositoryImpl.getAgencyIdAndNameByBidderId(bidderId);
            if(financialBid!=null) {
                financialBid.setAgencyName(agency.getAgencyName());
            }
            if(financialAbstract!=null && financialAbstract.size()>0){
                for(int i=0;i<financialAbstract.size();i++){
                    financialAbstract.get(i).setFlagId(1);
                }
            }
            else{
                for(int i=0;i<financialAbstract.size();i++){
                    financialAbstract.get(i).setFlagId(0);
                }
            }
            if(financialAbstract.size()>0){
                result.put("flag",1);
            }
            if(financialAbstract.size()<=0){
                result.put("flag",0);
            }
            if (financialAbstract != null) {
                result.put("financialAbstract", financialAbstract);
                result.put("FinancialBid", financialBid);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All FinancialBid Details");
            } else {
                result.put("financialAbstract", financialAbstract);
                result.put("financialBid", financialBid);
                result.put("flag",0);
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


    @PostMapping("/updateFinancialBidDetailsById")
    public OIIPCRAResponse updateFinancialBidDetailsById(@RequestParam Integer id, @RequestParam(name = "data") String data) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FinancialBidMasterDto financialBidDto = objectMapper.readValue(data, FinancialBidMasterDto.class);
            FinancialBidDetails financialBidDetails1 = tenderService.updateFinancialBidDetailsById(id, financialBidDto);
            Integer updateFinancialBidOpeningDate=tenderRepositoryImpl.updateFinancialBidOPeningDate(financialBidDto.getTenderId(),financialBidDto.getFinancialBidOpeningDate());
            //TenderResult tenderResult = tenderService.updateTenderResult(financialBidDto.getTenderResult(), financialBidDto.getTenderId(), financialBidDto.getWorkId(), financialBidDto.getBidderId());
            result.put("financialBidDetails1", financialBidDetails1);
            //result.put("tenderResult",tenderResult);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("FinancialBid Updated successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

/*    @PostMapping("/updateLiquidAsset/{id}")
    public OIIPCRAResponse updateLiquidAssetById(@PathVariable Integer id, @RequestBody FinancialBidDetails financialBidDetails) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            FinancialBidDetails financialBidDetails1 = tenderService.updateFinancialBidDetailsById(id, financialBidDetails);
            result.put("financialBidDetails1", financialBidDetails1);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("FinancialBid Updated successfully.");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }*/



    @PostMapping("/getBidderDetailsByTenderIdAndWorkId")
    public OIIPCRAResponse getBidderDetailsByTenderId(@RequestParam Integer tenderId, @RequestParam Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BidderDetailsDto> bidderDetails = tenderService.getBidderDetailsByTenderId(tenderId, workId);
            if (bidderDetails != null) {
                result.put("BidderDetails", bidderDetails);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Bidder Details Against TenderId And WorkId ");
            } else {
                result.put("BidderDetails", bidderDetails);
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



    //Get All AgencyName
    @PostMapping("/getAllAgencyName")
    public OIIPCRAResponse getAllAgencyName(@RequestParam Integer tenderId, @RequestParam Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyMaster> agencyMasters = tenderService.getAllAgencyName(tenderId, workId);
            result.put("agencyMasters", agencyMasters);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Agency List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getAgencyDetailsById")
    public OIIPCRAResponse getAgencyDetailsById(@RequestParam Integer id) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyDto> agencyMaster = tenderService.getAgencyDetailsById(id);
            if (agencyMaster != null) {
                result.put("AgencyMaster", agencyMaster);
                result.put("AgencyMaster", agencyMaster);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All AgencyMaster Details");
            } else {
                result.put("AgencyMaster", agencyMaster);
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

    @PostMapping("/getBidderIdExistsOrNotByAgencyIdAndNoticeId")
    public OIIPCRAResponse getBidderIdExistsOrNotByAgencyIdAndNoticeId(@RequestParam Integer agencyId,@RequestParam Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
          Integer bidderId = tenderService.getBidderIdExistsOrNotByAgencyIdAndNoticeId(agencyId,workId);
            if (bidderId != null) {
                result.put("bidderId", bidderId);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Bidder Id");
            } else {
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


    @PostMapping("/getAllBidderCategory")
    public OIIPCRAResponse getAllBidderCategory() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<BidderCategoryDto> bidderCategory = tenderService.getAllBidderCategory();
            result.put("BidderCategory", bidderCategory);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All BidderCategory List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    @PostMapping("/getDepositType")
    public OIIPCRAResponse getDepositType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<EmdDepositTypeDto> emdDeposit = tenderService.getDepositType();
            result.put("EmdDeposit", emdDeposit);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Emd Deposit Type List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }


    //getTenderDetails,ContractDetails,ExpenditureDetailsByBidId
    @PostMapping("/getWorkProgressDetails")
    public OIIPCRAResponse getWorkProgressDetails(@RequestBody WorkProgressViewDto workProgressView) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            TenderDetailsInfo tenderDetails = tenderRepositoryImpl.getTenderDetailsByTankId(workProgressView.getTankId());
            WorkStatusDto workStatusDto = surveyRepositoy.getWorkStatusDetails(tenderDetails.getId(), tenderDetails.getWorkId());
            // ContractInfo contractInfo = tenderService.getContractDetailsByTenderId(tenderDetails.getId(),tenderDetails.getWorkId());
            List<ExpenditureDataInfo> expenditureDataInfo = tenderService.getExpenditureDetailsBy(tenderDetails.getId(), tenderDetails.getWorkId());
            //  result.put("TenderMaster", tenderDetails);
            result.put("workStatusDto", workStatusDto);
            result.put("expenditureDataInfo", expenditureDataInfo);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Work Progress Details");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    //get tenderOpening table data against bidderId
    @PostMapping("/getCompletionOfSimilarWorkTypeByBidderId")
    public OIIPCRAResponse getCompletionOfSimilarWorkTypeByBidderId(@RequestParam Integer bidderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CompletionOfSimilarTypeOfWorkDto> completionOfSimilarWorkType = tenderService.getCompletionOfSimilarWorkTypeByBidderId(bidderId);
            BidderDetails qualified = tenderRepositoryImpl.getBidderDetailsById(bidderId);
            result.put("completionOfSimilarWorkType", completionOfSimilarWorkType);
            result.put("qualified", qualified);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Completion Of Similar WorkType By BidderId");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAnnualFinancialTurnOverByBidderId")
    public OIIPCRAResponse getAnnualFinancialTurnOver(@RequestParam Integer bidderId, @RequestParam Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
           // AgencyInfo agency =tenderRepositoryImpl.getAgencyIdAndNameByBidderId(bidderId);
            List<AnnualFinancialTurnoverMasterDto> annualFinancialTurnOver = tenderService.getAnnualFinancialTurnOver(bidderId);
           // Integer bidderId=tenderRepositoryImpl.getBidderIdByAgencyIdAndWorkId(agencyId,workId);
         /*   tenderRepositoryImpl.getMaximumBidCapacity(bidderId);*/
            BidderDetails qualified = tenderRepositoryImpl.getBidderDetailsById(bidderId);
            result.put("annualFinancialTurnOver", annualFinancialTurnOver);
            result.put("qualified", qualified);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("AnnualFinancial TurnOver By AgencyId");
        } catch (Exception e) {

            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getLiquidAssetAvailabilityByBidderId")
    public OIIPCRAResponse getLiquidAssetAvailabilityByBidderId(@RequestParam Integer bidderId, @RequestParam Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
           // AgencyInfo agency =tenderRepositoryImpl.getAgencyIdAndNameByBidderId(bidderId);
            List<LiquidAssetAvailability> liquidAssetAvailability = tenderService.getLiquidAssetAvailabilityByBidderId(bidderId);
           // Integer bidderId=tenderRepositoryImpl.getBidderIdByAgencyIdAndWorkId(agency.getAgencyId(),workId);

                BidderDetails qualified = tenderRepositoryImpl.getBidderDetailsById(bidderId);
                result.put("liquidAssetAvailability", liquidAssetAvailability);
                result.put("qualified", qualified);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("LiquidAssetAvailability By AgencyId");

         /*   else{
              //  BidderDetails qualified = tenderRepositoryImpl.getBidderDetailsById(bidderId);
               // result.put("liquidAssetAvailability", liquidAssetAvailability);
                // response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("ADD BIDDER FIRST");
            }*/
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/getExpenditureDataByTankId")
    public OIIPCRAResponse getExpenditureDataByTankId(@RequestBody ExpenditureWorkProgress expenditure) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ExpenditureYearMonthDto> expenditureData = tenderService.getExpenditureDataByTankId(expenditure);
            result.put("expenditureData", expenditureData);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get Expenditure Data By TankId");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/getAllBidders")
    public OIIPCRAResponse getAllBidders() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyDto> bidder = tenderService.getAllBidders();
            result.put("Bidder", bidder);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Bidder Details");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllAwardType")
    public OIIPCRAResponse getAllAwardType() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AwardTypeDto> awardType = tenderService.getAllAwardType();
            result.put("AwardType", awardType);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All Award Type");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getFinancialBidDetailsByBidderId")
    public OIIPCRAResponse getFinancialBidDetailsByBidderId(@RequestParam Integer bidderId,Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Integer flag=0;
            FinancialBidInfo financialBid = tenderService.getFinancialBidDetailsByBidderId(bidderId);
            BidderDetails bidderDetails = tenderService.getBidderDetailsById(bidderId);
            FinancialBidDto financial = tenderService.getTenderBidderDetailsByBidId(bidderId,workId);
            AgencyInfo agency  = masterQryRepository.getAgencyById(bidderDetails.getAgencyId());
            if(financial!=null) {
                financial.setAgencyName(agency.getAgencyName());
            }
          //  List<FinancialBidInfo> financialAbstract = tenderService.getFinancialAbstract(bidderId);
           // financialBid.setMaxBidCapacity(50000.00);
            if (!checkBidderIdExistsInFinancial(bidderId)) {
                flag=0;
            }
            else {
                flag=1;
            }
            result.put("financialBid", financialBid);
            result.put("bidderDetails", bidderDetails);
            result.put("financial", financial);
            result.put("flag",flag);
         //   result.put("financialAbstract",financialAbstract);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("GetFinancialDetailsByBidderId");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    private boolean checkBidderIdExistsInFinancial(Integer bidderId) {
        return financialBidRepository.existsByBidderId(bidderId);
    }

/*
    @PostMapping("/getTenderBidderDetailsByBidderId")
    public OIIPCRAResponse getTenderBidderDetailsByBidId(@RequestParam Integer bidderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            FinancialBidDto financialBid = tenderService.getTenderBidderDetailsByBidId(bidderId);
            //hard coded
           // financialBid.setMaxBidCapacity(5000000.00);
            result.put("financialBid", financialBid);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get Tender Bidder Details By BidderId");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
*/

    @PostMapping("/getAllAgencyNameByWorkId")
    public OIIPCRAResponse getAllAgencyNameByWorkId(@RequestParam Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinancialBidDto> agencyList = tenderService.getAllAgencyNameByWorkId(workId);
            TenderNoticeResponse tenderWork=tenderRepositoryImpl.getTenderNotice(workId);
            if(agencyList!=null && agencyList.size()>0) {
                result.put("agencyList", agencyList);
                result.put("workName", tenderWork.getNameOfWork());
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Get All Agency Details");
            }
            else{
                result.put("agencyList", agencyList);
                result.put("workName", tenderWork.getNameOfWork());
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("No agency qualifiedw");
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }

    @PostMapping("/getAllAgencyByContractId")
    public OIIPCRAResponse getAllAgencyByContractId(@RequestParam Integer contractId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyDto> agencyList = tenderService.getAllAgencyByContractId(contractId);
            result.put("agencyList", agencyList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get All Agency Details");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;

    }
    @PostMapping("/getAllBidIdForContractDone")
    public OIIPCRAResponse getAllBidIdForContractDone() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> tenderIds=tenderService.getTenderIds();
            List<TenderDto> bidList=new ArrayList<>();
            if(tenderIds.size()>0) {
                bidList = tenderService.getAllBidIdForContractDone(tenderIds);
            }
            result.put("bidList", bidList);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get All Contract Done BidId");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getAllWorkIdForContractDone")
    public OIIPCRAResponse getAllWorkIdForContractDone(@RequestParam Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderNoticeDto> workIds=tenderService.getAllWorkIdForContractDone(tenderId);
            result.put("workIds", workIds);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Get All Contract Done WorkId");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/bidIdExistsOrNot")
    public OIIPCRAResponse bidIdExistsOrNot(@RequestParam String bidId){
        return tenderService.bidIdExistsOrNot(bidId);
    }

//    @PostMapping("/workIdentificationCodeExistsOrNotByBidId")
//    public OIIPCRAResponse workIdentificationCodeExistsOrNotByBidId(@RequestParam(name = "bidId")Integer tenderId){
//        return tenderService.workIdentificationCodeExistsOrNotByBidId(tenderId);
//    }
    @PostMapping("/getDistinctPaperNameFromPublication")
    public OIIPCRAResponse getDistinctPaperNameFromPublication(){
        return tenderService.getDistinctPaperNameFromPublication();
    }
    @PostMapping("/getTenderByEstimateId")
    public OIIPCRAResponse getTenderByEstimateId(@RequestParam Integer estimateId){
        return tenderService.getTenderByEstimateId(estimateId);
    }
    @PostMapping("/getEeUserList")
    public OIIPCRAResponse getEeUserList(@RequestBody UserListRequest userListRequest) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<UserInfo> userListPage = userService.getUserList(userListRequest);
            List<UserInfo> userList=new ArrayList<>(userListPage.getContent());
           /* List<UserInfo> userList = userListPage.getContent();*/
            UserInfo user=new UserInfo();
            user.setId(-99);
            user.setUserName("Other");
            user.setDesignation("Executive Engineer");
            userList.add(user);
            result.put("userList", userList);
            result.put("currentPage", userListPage.getNumber());
            result.put("totalItems", userListPage.getTotalElements()+1);
            result.put("totalPages", userListPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("List of Users.");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/changeTenderStatus")
    public OIIPCRAResponse changeTenderStatus(@RequestParam String bidId,
                                              @RequestParam Integer statusId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            Boolean changedStatus = tenderService.changeTenderStatus(bidId,statusId);
            if(changedStatus==true) {
                response.setData(changedStatus);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tender Status Changed Successfully");
            }
            else{
                response.setData(changedStatus);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Tender Status Changed Unsuccessfully");
            }
        }catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getFinancialYearForTenderOpening")
    public OIIPCRAResponse getFinancialYearForTenderOpening(@RequestParam String financialYear){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            List<FinYrDto> finYr = tenderRepositoryImpl.getFinancialYearForTenderOpening(financialYear);
           result.put("financialYear",finYr);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Financial Year For Completion Of Similar Work TYpe");
        }catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/addPreviousTenderData")
    public OIIPCRAResponse addPreviousTenderData(@RequestParam(name = "data", required = false) String data,
                                         @RequestParam(name = "draftTenderNotice", required = false) MultipartFile draftTenderNotice,
                                             @RequestParam(name = "bidDocument", required = false) MultipartFile bidDocument) throws JsonProcessingException {
        return tenderService.addPreviousTender(data,draftTenderNotice, bidDocument);
    }
    @PostMapping("/deactivateTenderDocumentData")
    public OIIPCRAResponse deactivateTenderDocumentData(@RequestParam(name = "draftDocumentId", required = false) Integer  draftDocumentId) throws JsonProcessingException {
        return tenderService.deactivateTenderDocumentData(draftDocumentId);
    }

    @PostMapping("/getPreviousTenderData")
    public OIIPCRAResponse2 getPreviousTenderData(@RequestParam(name = "distId", required = false) Integer distId,
                                                 @RequestParam(name = "draw", required = false) Integer draw,
                                                  @RequestParam(name = "start", required = false) Integer start,
                                                  @RequestParam(name = "length", required = false) Integer length,
                                                  @RequestParam(name = "type", required = false) Integer type,
                                                  @RequestParam(name = "toDate", required = false) String toDate,
                                                  @RequestParam(name = "fromDate", required = false) String fromDate) throws JsonProcessingException {
        TenderNoticePublishListDto tender=new TenderNoticePublishListDto();
        if(distId!=null ){
            tender.setDistId(distId);
        }
        if(draw!=null){
            tender.setDraw(draw);
        }
        if(start!=null){
            tender.setStart(start);
        }
        if(length!=null){
            tender.setLength(length);
        }
        if(type!=null){
            tender.setType(type);
        }
        if(toDate!=null){
            tender.setToDate(toDate);
        }
        if(fromDate!=null){
            tender.setFromDate(fromDate);
        }
        return tenderService.getPreviousTenderData(tender);
    }

    @PostMapping("/getTenderDataByDistId")
    public OIIPCRAResponse2 getTenderData(@RequestParam(name = "distId", required = false) Integer distId,
                                                  @RequestParam(name = "draw", required = false) Integer draw,
                                                  @RequestParam(name = "start", required = false) Integer start,
                                                  @RequestParam(name = "length", required = false) Integer length,
                                                  @RequestParam(name = "type", required = false) Integer type,
                                                  @RequestParam(name = "toDate", required = false) String toDate,
                                                  @RequestParam(name = "fromDate", required = false) String fromDate) throws JsonProcessingException {
        TenderNoticePublishListDto tender=new TenderNoticePublishListDto();
        if(distId!=null ){
            tender.setDistId(distId);
        }
        if(draw!=null){
            tender.setDraw(draw);
        }
        if(start!=null){
            tender.setStart(start);
        }
        if(length!=null){
            tender.setLength(length);
        }
        if(type!=null){
            tender.setType(type);
        }
        if(toDate!=null){
            tender.setToDate(toDate);
        }
        if(fromDate!=null){
            tender.setFromDate(fromDate);
        }
        OIIPCRAResponse2 response = new OIIPCRAResponse2();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<TenderNoticePublishDto> list = tenderService.getTenderData(tender);
            List<TenderNoticePublishDto> tenderData=list.getContent();
            result.put("tenderList", tenderData);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All Tender List");
            response.setRecordsFiltered(list.getTotalElements());
            response.setRecordsTotal(list.getTotalElements());
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        } catch (Exception e) {
            response = new OIIPCRAResponse2(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getAllDistrictForWebsite")
    public OIIPCRAResponse getAllDistrictForWebsite() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DistrictBoundaryDto> districtList = tenderService.getAllDistrictForWebsite();
            if (!districtList.isEmpty() && districtList.size() > 0) {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                result.put("districtList", districtList);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getWorkSlNoAndAgencyByBidId")
    public OIIPCRAResponse getWorkSlNoAndAgencyByBidId(@RequestParam(name = "tenderId", required = false) Integer tenderId) {
        return tenderService.getWorkSlNoAndAgencyByBidId(tenderId);
    }


    @PostMapping("/getBidIdListForTender")
    public OIIPCRAResponse getBidIdListForTender(@RequestParam(name="userId",required = false)Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderDto> bidId = tenderService.getBidIdListForTender(userId);
            result.put("bidId", bidId);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Bid Id List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getQualifiedAgencyByBidIdAndWorkId")
    public OIIPCRAResponse getQualifiedAgencyByBidId(@RequestParam(name="tenderId",required = false)Integer tenderId,@RequestParam(name="workId",required = false)Integer workId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AgencyDto> agency = tenderService.getQualifiedAgencyByBidId(tenderId,workId);
            result.put("agency", agency);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Agency  List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getTenderStipulationByTenderId")
    public OIIPCRAResponse getTenderStipulationByTenderId(@RequestParam(name="tenderId",required = false)Integer tenderId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            TenderStipulation stipulation = tenderStipulationRepository.findByTenderId(tenderId);
            result.put("stipulation", stipulation);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Stipulation By TenderId  ");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getSubDivisionOfficerNameBySubDivisionId")
    public OIIPCRAResponse getSubDivisionOfficerNameBySubDivisionId(@RequestParam(name="subDivisionId",required = false)Integer subDivisionId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TenderNoticeDto> subDivisionOfficer = tenderService.getSubDivisionOfficerNameBySubDivisionId(subDivisionId);
            TenderNoticeDto sdo=new TenderNoticeDto();
            sdo.setSubDivOfficerName("Other");
            sdo.setSubDivisionOfficer(-99);
            subDivisionOfficer.add(sdo);
            result.put("subDivisionOfficer", subDivisionOfficer);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("subDivisionOfficer  List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getMeetingProceedingsByTenderId")
    public OIIPCRAResponse getMeetingProceedingsByTenderId(@RequestParam Integer tenderId,@RequestParam Integer meetingTypeId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            MeetingProceedingDto meetingProceeding = tenderService.getMeetingProceedingsByTenderId(tenderId,meetingTypeId);
            if(meetingProceeding!=null){
                List<CommitteeMembersDto> committeeMember=tenderService.getCommitteeMemberByMeetingProceedingId(meetingProceeding.getId());
                List<PreBidClarificationsDto> preBidClarifications=tenderService.getPreBidClarificationByMeetingProceedingId(meetingProceeding.getId());
                result.put("committeeMembers", committeeMember);
                result.put("preBidClarifiactions", preBidClarifications);
            }
            int flagId=0;
            if(meetingProceeding!=null){
                flagId=1;
            }
            result.put("flag",flagId);
            result.put("meetingProceeding", meetingProceeding);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Meeting Proceedings Data");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getShlcMeeting")
    public OIIPCRAResponse getShlcMeeting(  @RequestParam  java.sql.Date date) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ShlcMeetingDto shlcMeeting = tenderService.getShlcMeeting(date);
            if(shlcMeeting!=null){
                List<CommitteeMembersDto> committeeMember=tenderService.getCommitteeMemberByShlcMeetingId(shlcMeeting.getId());
                List<ShlcMeetingProceedingsEntity> shlcMeetingProceedings=tenderService.getShlcMeetingProceedings(shlcMeeting.getId());
                result.put("committeeMember", committeeMember);
                result.put("shlcMeetingProceedings", shlcMeetingProceedings);
            }
            int flagId=0;
            if(shlcMeeting!=null){
                flagId=1;
            }
            result.put("flag",flagId);
            result.put("shlcMeeting", shlcMeeting);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("Shlc Meeting Data");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @GetMapping("/getData")
    public FARDResponse getData() {
        FARDResponse response = new FARDResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DenormalizedFinancialAchievementDto> data=new ArrayList<>();
            for(int i=0;i<2;i++){
                DenormalizedFinancialAchievementDto d=new DenormalizedFinancialAchievementDto();
                d.setDirectorate("XYZ");
                d.setYear("2023");
                d.setDistrictName("Anugul");
                d.setSchemeName("XYZ");
                d.setExpenditure(10.3);
                d.setActualFundAllocated(15.0);
                d.setFinancialAllocationInApp(20.4);
                data.add(d);
            }

            response.setDenormalizedFinancial(data);
            response.setStatus(1);
            response.setMessage("DenormalizedList");
        } catch (Exception e) {
          e.printStackTrace();
        }
        return response;
    }


    @PostMapping("/getTankNameByActivityId")
    public OIIPCRAResponse getTankNameByActivityId(@RequestParam(name="activityId",required = false)Integer activityId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityEstimateTankMappingDto> activityEstimateTank = tenderService.getTankNameByActivityId(activityId);
            result.put("activityEstimateTank", activityEstimateTank);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("ActivityEstimateTank  List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }





}
