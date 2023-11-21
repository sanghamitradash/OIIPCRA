package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.DesignationMaster;
import com.orsac.oiipcra.entities.PhysicalProgressExecutedLog;
import com.orsac.oiipcra.entities.PhysicalProgressPlannedLog;
import com.orsac.oiipcra.repositoryImpl.GraphRepositoryImpl;
import com.orsac.oiipcra.service.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/graph")
public class GraphController {
    @Autowired
    private GraphService graphService;
    @Autowired
    private GraphRepositoryImpl graphRepositoryImpl;
    @PostMapping("/getCropCycleIntensityYearWise")
    public OIIPCRAResponse getCropCycleIntensityYearWise(@RequestParam Integer projectId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
             List<String> year=graphService.getDistinctYearBYProjectId(projectId);
             for(int i=0;i<year.size();i++) {
                 List<CropCycleAyacutDto> crop = graphService.getCropCycleIntensity(projectId,year.get(i));
                 result.put(year.get(i), crop);
             }
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("CropIntensity By ProjectId");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getCropCycleIntensityMonthWise")
    public OIIPCRAResponse getCropCycleIntensityMonthWise(@RequestParam Integer projectId,@RequestParam String year){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CropNonCropDto> cropNonCrop = graphService.getCropCycleIntensityMonthWise(projectId,year);
            result.put("cropNonCrop",cropNonCrop);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("CropNonCrop By ProjectId");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getWaterSpread")
    public OIIPCRAResponse getWaterSpread(@RequestParam(name = "year")String year){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<WaterSpreadDto> month = graphService.getWaterSpreadMonth(year);
            //result.put("month", month);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("waterSpread");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getDepth")
    public OIIPCRAResponse getDepth(@RequestParam (name="projectId")Integer projectId,@RequestParam (name="year") String year){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DepthDto> depth = graphService.getDepth(projectId,year);
            result.put("depth", depth);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Depth");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getTankFinancialStatus")
    public OIIPCRAResponse getTankFinancialStatus(@RequestParam (name="tankId")Integer tankId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            BigDecimal estimate=graphRepositoryImpl.getEstimateByProjectId(tankId);
            BigDecimal contract=graphRepositoryImpl.getContractByProjectId(tankId);
            BigDecimal expenditure=graphRepositoryImpl.getExpenditureByProjectId(tankId);
            result.put("estimate", estimate);
            result.put("contract", contract);
            result.put("expenditure", expenditure);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("TankFinancialStatus");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


    @PostMapping("/getWaterSpreadByProjectId")
    public OIIPCRAResponse getWaterSpreadByProjectId(@RequestParam(name = "projectId")String projectId,
                                                     @RequestParam(name = "year")String yearId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();

        try {
            DecimalFormat df = new DecimalFormat("0.000");
            List<WaterSpreadDto> year = graphService.getWaterSpreadByProjectId(projectId,yearId);
            for(int i=0;i<year.size();i++) {
                List<WaterSpreadDto> month = graphService.getWaterSpreadMonth(projectId,yearId);
                year.get(i).setMonthData(month);
            }
            result.put("year", year);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("waterSpreadForYear");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


 /*   @PostMapping("/getCountOfIssueAndGrievance")
    public OIIPCRAResponse getCountOfIssueAndGrievance(@RequestParam(name = "tankId") Integer tankId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            IssueTrackerDto issueCount = graphService.getIssueCount(tankId);
            GrievanceDto grievanceCount = graphService.getGrievanceCount(tankId);
            result.put("issueCount", issueCount);
            result.put("grievanceCount",grievanceCount);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("CountOfIssueAndGrievance");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


}*/
    @PostMapping("/getCountOfIssue")
    public OIIPCRAResponse getCountOfIssue(@RequestParam(name = "tankId") Integer tankId,@RequestParam(name="year", required = false) String year){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Integer issueCount = graphService.getIssueCount(tankId,year);
            Integer resolved = graphService.getResolvedCount(tankId,year);
            Integer rejected =graphService.getRejectedCount(tankId,year);
            List<CountArrayDto> count=new ArrayList<>();
            for(int i=0;i<2;i++){
                CountArrayDto c=new CountArrayDto();
                if(i==0){
                    c.setName("Resolved");
                    c.setCount(resolved);
                    count.add(c);
                }
                if(i==1){
                    c.setName("Rejected");
                    c.setCount(rejected);
                    count.add(c);
                }
            }
            result.put("issueCount", issueCount);
            result.put("resolved", resolved);
            result.put("rejected", rejected);
            result.put("count",count);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Count Of Issue");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getCountOfGrievance")
    public OIIPCRAResponse getGrievanceCount(@RequestParam(name = "tankId") Integer tankId,@RequestParam(name="year", required = false) String year){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            Integer grievanceCount = graphService.getGrievanceCount(tankId,year);
            Integer resolved = graphService.getResolvedGrievanceCount(tankId,year);
            Integer rejected = graphService.getRejectedGrievanceCount(tankId,year);
            List<CountArrayDto> count=new ArrayList<>();
            for(int i=0;i<2;i++){
                CountArrayDto c=new CountArrayDto();
                if(i==0){
                    c.setName("Resolved");
                    c.setCount(resolved);
                    count.add(c);
                }
                if(i==1){
                    c.setName("Rejected");
                    c.setCount(rejected);
                    count.add(c);
                }
            }

            result.put("grievanceCount", grievanceCount);
            result.put("resolved", resolved);
            result.put("rejected", rejected);
            result.put("count",count);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Count Of GrievanceCount");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getPhysicalProgressDetails")
    public OIIPCRAResponse getPhysicalProgressDetails(@RequestParam (name="tankId")Integer tankId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<PhysicalProgressPlannedDto> planned = graphService.getPhysicalProgressPlannedDetails(tankId);
            List<PhysicalProgressExecutedDto> executed = graphService.getPhysicalProgressDetails(tankId);
            result.put("planned", planned);
            result.put("executed", executed);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("PhysicalProgressExecuted");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/updatePhysicalProgressDetails")
    public OIIPCRAResponse updatePhysicalProgressDetails(@RequestParam(name = "data") String data){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            PhysicalProgressUpdateDto physicalProgress = mapper.readValue(data, PhysicalProgressUpdateDto.class);
            PhysicalProgressPlannedDto planned = graphService.getPhysicalProgressPlannedDetailsByTankIdAndContractId(physicalProgress.getTankId(),physicalProgress.getContractId());
            if(planned!=null){
                PhysicalProgressPlannedLog logPlanned=graphService.savePhysicalProgressPlannedLog(planned,physicalProgress);
            }
            PhysicalProgressExecutedDto executed = graphService.getPhysicalProgressExecutedDetailsByTankIdAndContractId(physicalProgress.getTankId(),physicalProgress.getContractId());
            if(executed!=null){
                PhysicalProgressExecutedLog logExecuted=graphService.savePhysicalProgressExecutedLog(executed,physicalProgress);
            }
             Integer updateData=graphService.updatePhysicalData(physicalProgress);
            if(updateData==1) {
                result.put("physicalProgress", physicalProgress);
                oiipcraResponse.setData(result);
                oiipcraResponse.setStatus(1);
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                oiipcraResponse.setMessage("PhysicalProgress Updated Successfully");
            }
            else{
                oiipcraResponse.setStatus(0);
                oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                oiipcraResponse.setMessage("PhysicalProgress Updated UnSuccessfully");
            }
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


//    @PostMapping("/getPhysicalProgressDetails")
//    public OIIPCRAResponse getPhysicalProgressPlannedDetails(@RequestParam (name="tankId")Integer tankId){
//        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
//        Map<String, Object> result = new HashMap<>();
//        try {
//            List<PhysicalProgressPlannedDto> planned = graphService.getPhysicalProgressPlannedDetails(tankId);
//            List<PhysicalProgressExecutedDto> executed = graphService.getPhysicalProgressDetails(tankId);
//            result.put("planned", planned);
//            result.put("executed", executed);
//            oiipcraResponse.setData(result);
//            oiipcraResponse.setStatus(1);
//            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
//            oiipcraResponse.setMessage("PhysicalProgressDetails");
//        }catch (Exception e) {
//            e.printStackTrace();
//            oiipcraResponse = new OIIPCRAResponse(0,
//                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
//                    e.getMessage(),
//                    result);
//        }
//        return oiipcraResponse;
//    }


    @PostMapping("/getCropCycleIntensityList")
    public OIIPCRAResponse getCropCycleIntensityList(@RequestParam Integer projectId){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> year=graphService.getDistinctYearBYProjectId(projectId);
            for(int i=0;i<year.size();i++) {
                List<CropCycleAyacutDto> crop = graphService.getCropCycleIntensityList(projectId,year.get(i));
                result.put(year.get(i), crop);
            }
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("CropIntensity By ProjectId");
        }catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getLastYearMonthWaterSpread")
    public OIIPCRAResponse getLastYearMonthWaterSpread(){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            WaterSpreadDto waterSpread = graphService.getLastYearMonthWaterSpread();
            result.put("waterSpread", waterSpread);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("waterSpread");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getLastYearMonthCropNonCrop")
    public OIIPCRAResponse getLastYearMonthCropNonCrop(){
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            CropNonCropDto cropNonCrop = graphService.getLastYearMonthCropNonCrop();
            result.put("cropNonCrop", cropNonCrop);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("cropNonCrop");
        }catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }



}
