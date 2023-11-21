package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.repositoryImpl.MasterRepositoryImpl;
import com.orsac.oiipcra.serviceImpl.MasterServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/apiCall")
public class ApiController {
    @Autowired
    private MasterServiceImpl masterServiceImpl;

    @Autowired
    private MasterRepositoryImpl masterRepositoryImpl;

    @Autowired
    private ActivityQryRepository activityQryRepository;


    @PostMapping("/getOiipcraDenormalizedAchievementData")
    //@Scheduled(cron = "0 */1 * * * *")   // for minute
   // @Scheduled(cron = "0 0 */24 * * *")//for hour
//    @Scheduled(cron = "0 0 */12 * * *")  //for hour
    public OIIPCRAResponse getOiipcraDenormalizedAchievementData() {
        //https://www.youtube.com/watch?v=GaH_Dc1ey3o
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            final String url = "https://adaptdafe.odisha.gov.in/api/admin/schemes/oiipcraDenormalizedachivement";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "abg123hymf567frebdt435hdngti789mchcbe123ncbcbf8680fsrebbcjdyteue35346482gdgdvcbd43");


           HttpEntity<String> requestEntity = new HttpEntity<String>("body", header);
//            ResponseEntity<List<AgencyDto>> data = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<AgencyDto>>() {
//            });
//            List<AgencyDto> data1 = data.getBody();
//           HttpEntity<String> data = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class, params);
            //            ResponseEntity<List<ApiDto>> a = restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<ApiDto>>() {
//            },params);
//            List<ApiDto> a1=a.getBody();


//            System.out.println(data.getBody());
//            result.put("apiData",data.getBody());
//            response.setData(result);
//            response.setStatus(1);
//            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
//            response.setMessage("Api call Data");
            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put("year", null);


            ResponseEntity<MessageDto> data = restTemplate.exchange(url, HttpMethod.POST, requestEntity, MessageDto.class,params);
            List<ApiDto> allData=data.getBody().getData().getResult();
            System.out.println(allData.size());
            List<DenormalizedAchievement> allSavedData=masterServiceImpl.saveDenormalizedAchievement(allData);
            System.out.println("Saved");
            if(allSavedData.size()>0){
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Denormalized Achievement saved successfully.");
            }
            else{
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Denormalized Achievement saved unsuccessfully.");
            }
            return  response;

        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
   //@PostMapping("/getOiipcraDenormalizedFinancialAchivement")
   //@Scheduled(cron = "0 */1 * * * *")    //for minute
   // @Scheduled(cron = "0 0 */24 * * *")  //for hour
//   @Scheduled(cron = "0 0 */12 * * *")  //for hour
    public OIIPCRAResponse getOiipcraDenormalizedFinancialAchivement() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            final String url = "https://adaptdafe.odisha.gov.in/api/admin/schemes/oiipcraDenormalizedFinancialachivement";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "abg123hymf567frebdt435hdngti789mchcbe123ncbcbf8680fsrebbcjdyteue35346482gdgdvcbd43");


            HttpEntity<String> requestEntity = new HttpEntity<String>("body", header);

            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put("year", null);


            ResponseEntity<DenormalizedFinancialAchievementMessageDto> data = restTemplate.exchange(url, HttpMethod.POST, requestEntity, DenormalizedFinancialAchievementMessageDto.class,params);
            List<DenormalizedFinancialAchievementDto> allData=data.getBody().getData().getResult();
            System.out.println(allData.size());
            List<DenormalizedFinancialAchievement> allSavedData=masterServiceImpl.saveDenormalizedFinancialAchievement(allData);
            System.out.println("Saved");
            if(allSavedData.size()>0){
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Denormalized Financial Achievement saved successfully.");
            }
            else{
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Denormalized Financial  Achievement saved unsuccessfully.");
            }
            return  response;

        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getOiipcraDenormalizedPhysicalAchievementList")
    public OIIPCRAResponse getOiipcraDenormalizedPhysicalAchievementDataList( @RequestBody AdaptFilterDto adaptDto)
    {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<AdaptPhysicalDto> adaptData = masterServiceImpl.getOiipcraDenormalizedPhysicalAchievementDataList(adaptDto);
            List<AdaptPhysicalDto> adaptPhysicalProgressList = adaptData.getContent();
           /* Integer totalAchievement=0;
            Integer noOfBeneficiaries=0;*/
           /* for(int i=0;i<adaptPhysicalProgressList.size();i++){
                totalAchievement=totalAchievement+adaptPhysicalProgressList.get(i).getAchievement();
                noOfBeneficiaries=noOfBeneficiaries+adaptPhysicalProgressList.get(i).getNoofBeneficiaries();
            }*/
          //  Double achievementPercentage=Double.valueOf((totalAchievement*100)/noOfBeneficiaries) ;
            AdaptPhysicalDto adaptPhysicalProgress=masterRepositoryImpl.getOiipcraDenormalizedPhysicalAchievementData(adaptDto);;
            Double achievementPercentage=(Double.valueOf(adaptPhysicalProgress.getAchievement())*100)/Double.valueOf(adaptPhysicalProgress.getTarget());

            for(AdaptPhysicalDto item1 : adaptPhysicalProgressList) {
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getActivityId());
                String code = "";
//                for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
//                    code += item.getCode();
//                    if(!code.isEmpty() && item.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
                if (currentHierarchyInfoById.size() == 1) {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                    }
                    item1.setCode(code);
                    item1.setUpperHierarchy(currentHierarchyInfoById);


                } else {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                        if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);
                    item1.setUpperHierarchy(currentHierarchyInfoById);

                }
            }


            // System.out.println((totalAchievement*100)/noOfBeneficiaries);
            result.put("adaptPhysicalProgressList", adaptPhysicalProgressList);
            result.put("currentPage", adaptData.getNumber());
            result.put("totalItems", adaptData.getTotalElements());
            result.put("totalPages", adaptData.getTotalPages());
            result.put("totalAchievement",adaptPhysicalProgress.getAchievement());
            result.put("totalNoOfBeneficiaries",adaptPhysicalProgress.getNoofBeneficiaries());
            result.put("totalAchievementPercentage",achievementPercentage);

            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("adaptPhysicalProgressList");
        }catch (Exception e){
            log.info("Contract List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/getOiipcraDenormalizedFinancialAchivementList")
    public OIIPCRAResponse getOiipcraDenormalizedFinancialAchivementList( @RequestBody AdaptFilterDto adaptDto)
    {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<AdaptFinancialDto> adaptData = masterServiceImpl.getOiipcraDenormalizedFinancialAchievementDataList(adaptDto);
            List<AdaptFinancialDto> adaptFinancialProgressList = adaptData.getContent();
            AdaptFinancialDto financialData=masterRepositoryImpl.getOiipcraDenormalizedFinancialAchievementData(adaptDto);
            Double achievementPercentage=(Double.valueOf(financialData.getExpenditure())*100)/Double.valueOf(financialData.getActualFundAllocated());

            /*Double expenditure=0.00;
            Double actualFundAllocated=0.00;
            for(int i=0;i<adaptFinancialProgressList.size();i++){
              expenditure=expenditure+adaptFinancialProgressList.get(i).getExpenditure();
                actualFundAllocated=actualFundAllocated+adaptFinancialProgressList.get(i).getActualFundAllocated();
            }*/
            for(AdaptFinancialDto item1 : adaptFinancialProgressList) {
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getActivityId());
                String code = "";
//                for(ActivityUpperHierarchyInfo item : currentHierarchyInfoById){
//                    code += item.getCode();
//                    if(!code.isEmpty() && item.getMasterHeadId() <= 1){
//                        code = code+".";
//                    }
//                }
                if (currentHierarchyInfoById.size() == 1) {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                    }
                    item1.setCode(code);
                    item1.setUpperHierarchy(currentHierarchyInfoById);

                } else {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                        if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);
                    item1.setUpperHierarchy(currentHierarchyInfoById);

                }
            }

            result.put("adaptPhysicalProgressList", adaptFinancialProgressList);
            result.put("currentPage", adaptData.getNumber());
            result.put("totalItems", adaptData.getTotalElements());
            result.put("totalPages", adaptData.getTotalPages());
            result.put("totalExpenditure", financialData.getExpenditure());
            result.put("totalActualFundAllocated", financialData.getActualFundAllocated());
            result.put("achievementPercentage",achievementPercentage);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("adaptFinancialProgressList");
        }catch (Exception e){
            log.info("Adapt List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/updateActivityForAdapt")
    public OIIPCRAResponse updateActivityForAdapt(@RequestParam Integer id, @RequestParam(required = false,name = "estimateId") Integer estimateId,
                                                  @RequestParam(required = false,name = "activityId")Integer activityId) {

            OIIPCRAResponse response = new OIIPCRAResponse();
            Map<String, Object> result = new HashMap<>();
            try {
                boolean res = masterServiceImpl.updateDenormalizedFinancialAchievement(id, activityId);
                boolean res1 = masterServiceImpl.updateAgricultureEstimate(estimateId,activityId);
                if (res == true || res1 == true) {
                    response.setData(result);
                    response.setStatus(1);
                    response.setMessage("ActivityId updated ");
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                } else {
                    response.setStatus(0);
                    response.setMessage("Something went wrong");
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
                }
            } catch (Exception e) {
                response = new OIIPCRAResponse(0,
                        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                        e.getMessage(), result);
            }
            return response;

        }

    @PostMapping("/updateActivityForDenormalizedAchievement")
    public OIIPCRAResponse updateActivityForAdapt(@RequestParam Integer id, @RequestParam(required = false,name = "activityId")Integer activityId) {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            boolean res = masterServiceImpl.updateDenormalizedAchievement(id, activityId);
            if (res == true) {
                response.setData(result);
                response.setStatus(1);
                response.setMessage("ActivityId updated ");
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            } else {
                response.setStatus(0);
                response.setMessage("Something went wrong");
                response.setStatusCode(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;

    }
    @PostMapping("/getFinancialAchievementGraphData")
    public OIIPCRAResponse getFinancialAchievementGraphData() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AdaptFinancialDto> horticulture= masterServiceImpl.getFinancialAchievementGraphDataForHorticulture();
            List<AdaptFinancialDto> agriculture= masterServiceImpl.getFinancialAchievementGraphDataForAgriculture();
            result.put("horticulture",horticulture);
            result.put("agriculture",agriculture);
            response.setData(result);
            response.setStatus(1);
            response.setMessage("Agriculture and Horticulture Financial Data");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;
    }
    @PostMapping("/getFinancialAchievementGraphDataFisheries")
    public OIIPCRAResponse getFinancialAchievementGraphDataFisheries() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AdaptFinancialDto> financial= masterServiceImpl.getFinancialAchievementGraphDataFisheries();
            result.put("financial",financial);
            response.setData(result);
            response.setStatus(1);
            response.setMessage("Fisheries Financial Data");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;
    }


    @PostMapping("/getPhysicalAchievementGraphData")
    public OIIPCRAResponse getPhysicalAchievementGraphData() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<AdaptPhysicalDto> horticulture= masterServiceImpl.getPhysicalAchievementGraphDataForHorticulture();
            List<AdaptPhysicalDto> agriculture= masterServiceImpl.getPhysicalAchievementGraphDataForAgriculture();
            result.put("horticulture",horticulture);
            result.put("agriculture",agriculture);
            response.setData(result);
            response.setStatus(1);
            response.setMessage("Agriculture and Horticulture Physical Data");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;
    }
    //@PostMapping("/getOiipcraDenormalizedFinancialAchivementFard")
    //@Scheduled(cron = "0 */1 * * * *")    //for minute
    // @Scheduled(cron = "0 0 */24 * * *")  //for hour
//   @Scheduled(cron = "0 0 */12 * * *")  //for hour
    public OIIPCRAResponse getOiipcraDenormalizedFinancialAchivementFard() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            //final String url = "http://localhost:8080/api/v1/tender/getData";
            final String url = "https://api.fardodisha.com/api/oiipcra-data";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity<String>("body", header);
            ResponseEntity<String> list = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            String data=list.getBody();
            ObjectMapper mapper = new ObjectMapper();
            FardDto fard = mapper.readValue(data, FardDto.class);
            System.out.println(fard.getDenormalizedFinancial().size());
            List<FardFinancialAchievementEntity> allSavedData=masterServiceImpl.saveFardFinancialAchievement(fard.getDenormalizedFinancial());
            System.out.println("Saved");


            if(allSavedData.size()>0){
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("FARD Financial Achievement saved successfully.");
            }
            else{
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("FARD Financial  Achievement saved unsuccessfully.");
            }
            return  response;

        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getOiipcraFardFinancialAchievementList")
    public OIIPCRAResponse getOiipcraFardFinancialAchievementList( @RequestBody AdaptFilterDto adaptDto)
    {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<AdaptFinancialDto> adaptData = masterServiceImpl.getOiipcraFardFinancialAchievementList(adaptDto);
            List<AdaptFinancialDto> adaptFinancialProgressList = adaptData.getContent();
           /* AdaptFinancialDto financialData=masterRepositoryImpl.getOiipcraDenormalizedFinancialAchievementData(adaptDto);
            Double achievementPercentage=(Double.valueOf(financialData.getExpenditure())*100)/Double.valueOf(financialData.getActualFundAllocated());
*/
          /*  for(AdaptFinancialDto item1 : adaptFinancialProgressList) {
                List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getActivityId());
                String code = "";
                if (currentHierarchyInfoById.size() == 1) {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                    }
                    item1.setCode(code);
                    item1.setUpperHierarchy(currentHierarchyInfoById);

                } else {
                    for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                        code += item.getCode();
                        if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                            code = code + ".";
                        }
                    }
                    item1.setCode(code);
                    item1.setUpperHierarchy(currentHierarchyInfoById);

                }
            }*/

            result.put("adaptPhysicalProgressList", adaptFinancialProgressList);
            result.put("currentPage", adaptData.getNumber());
            result.put("totalItems", adaptData.getTotalElements());
            result.put("totalPages", adaptData.getTotalPages());
            /*result.put("totalExpenditure", financialData.getExpenditure());
            result.put("totalActualFundAllocated", financialData.getActualFundAllocated());
            result.put("achievementPercentage",achievementPercentage)*/;
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("FardFinancialProgressList");

        }catch (Exception e){
            log.info("Fard List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    //@PostMapping("/saveDenormalizedPhysicalAchivementFard")
    //@Scheduled(cron = "0 */1 * * * *")    //for minute
    // @Scheduled(cron = "0 0 */24 * * *")  //for hour
//   @Scheduled(cron = "0 0 */12 * * *")  //for hour
    public OIIPCRAResponse getOiipcraDenormalizedPhyscialAchivementFard() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            //final String url = "http://localhost:8080/api/v1/tender/getData";
            final String url = "https://api.fardodisha.com/api/oiipcra-physical-data";



            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity<String>("body", header);
            ResponseEntity<String> list = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            String data=list.getBody();
            ObjectMapper mapper = new ObjectMapper();
            FardPhysicalCallDto fard = mapper.readValue(data, FardPhysicalCallDto.class);
            System.out.println(fard.getDenormalizedAchievement().size());
            List<FardPhysicalAchievementEntity> allSavedData=masterServiceImpl.saveFardPhysicalAchievement(fard.getDenormalizedAchievement());
            System.out.println("Saved");


            if(allSavedData!=null && allSavedData.size()>0){
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("FARD Physical Achievement saved successfully.");
            }
            else{
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("FARD Physical  Achievement saved unsuccessfully.");
            }
            return  response;

        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    //@PostMapping("/getOiipcraBeneficiaryData")
    //@Scheduled(cron = "0 */1 * * * *")    //for minute
//    @Scheduled(cron = "0 0 */24 * * *")  //for hour
    //@Scheduled(cron = "0 0 */12 * * *")  //for hour
    public OIIPCRAResponse getOiipcraBeneficiary() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try{
            final String url = "https://adaptdafe.odisha.gov.in/api/admin/schemes/oiipcraBeneficiary";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "abg123hymf567frebdt435hdngti789mchcbe123ncbcbf8680fsrebbcjdyteue35346482gdgdvcbd43");


            HttpEntity<String> requestEntity = new HttpEntity<String>("body", header);

            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put("year", null);


            ResponseEntity<PhysicalBeneficiaryMessageDto> data = restTemplate.exchange(url, HttpMethod.POST, requestEntity, PhysicalBeneficiaryMessageDto.class,params);
            List<PhysicalBeneficiaryDto> allData=data.getBody().getData().getResult();
            System.out.println(allData.size());
            List<AdaptPhysicalBeneficiary> allSavedData=masterServiceImpl.savePhysicalBeneficiaryData(allData);
            System.out.println("Saved");
            if(allSavedData.size()>0){
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Physical Beneficiary saved successfully.");
            }
            else{
                result.put("allData", allSavedData);
                response.setData(result);
                response.setStatus(0);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Physical Beneficiary saved unsuccessfully.");
            }
            return  response;
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }



    @PostMapping("/getAdaptPhysicalBeneficiaryList")
    public OIIPCRAResponse getAdaptPhysicalBeneficiaryList(@RequestBody AdaptPhysicalBeneficiaryFilterDto adaptPhysicalBeneficiaryFilterDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDtoPage = masterServiceImpl.getAdaptPhysicalBeneficiaryList(adaptPhysicalBeneficiaryFilterDto);
            List<AdaptPhysicalBeneficiaryDto> adaptPhysicalBeneficiaryDtoList = adaptPhysicalBeneficiaryDtoPage.getContent();
            result.put("adaptPhysicalBeneficiaryList", adaptPhysicalBeneficiaryDtoList);
            result.put("currentPage", adaptPhysicalBeneficiaryDtoPage.getNumber());
            result.put("totalItems", adaptPhysicalBeneficiaryDtoPage.getTotalElements());
            result.put("totalPages", adaptPhysicalBeneficiaryDtoPage.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("AdaptPhysicalBeneficiaryList");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/getAdaptPhysicalBeneficiaryById")
    public OIIPCRAResponse getAdaptPhysicalBeneficiaryList(@RequestParam Integer id ) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            AdaptPhysicalBeneficiaryDto adaptPhysicalBeneficiary = masterServiceImpl.getAdaptPhysicalBeneficiaryById(id);

            result.put("adaptPhysicalBeneficiary", adaptPhysicalBeneficiary);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("AdaptPhysicalBeneficiary By Id");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
    @PostMapping("/test")
    public void test(){
        String x="lat: 21.7848298, long: 85.5549375";
        String y=x.replace("lat:","").replace("long:","").replace(" ","");
        System.out.println(y);
        String[] arrOfStr = y.split(",", 2);
        for (String a : arrOfStr)
            System.out.println(a);
        System.out.println(x);
    }


    @PostMapping("/getDenormalizedAchievementDataById")
    public OIIPCRAResponse getDenormalizedAchievementDataById(@RequestParam Integer id)
    {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            List<AdaptPhysicalBeneficiaryDto> adaptData = masterServiceImpl.getDenormalizedAchievementDataById(id);

            result.put("adaptData", adaptData);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("DenormalizedAchievementData By Id");
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }





    @PostMapping("/getFardPhysicalGraphData")
    public OIIPCRAResponse getFardPhysicalGraphData() {

        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FardPhysicalAchievementDto> fisheries= masterServiceImpl.getFardPhysicalAchievementGraphData();
            result.put("fisheries",fisheries);
            response.setData(result);
            response.setStatus(1);
            response.setMessage("fisheries Physical Data");
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(), result);
        }
        return response;
    }


}