package com.orsac.oiipcra.controller;

import com.orsac.oiipcra.bindings.Activity;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.repository.MasterQryRepository;
import com.orsac.oiipcra.repository.MasterRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.repositoryImpl.DashboardRepositoryImpl;
import com.orsac.oiipcra.service.DashboardService;
import com.orsac.oiipcra.service.GraphService;
import com.orsac.oiipcra.service.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private DashboardRepositoryImpl dashboardRepositoryImpl;
    @Autowired
    private GraphService graphService;

    @Autowired
    private UserQueryRepository userQryRepo;
    @Autowired
    private MasterService masterService;


    @Autowired
    private MasterQryRepository masterRepository;

    //Count For Execution Status Of MIP
    @PostMapping("/getCountForExecutionOfMip")
    public OIIPCRAResponse getCountForExecutionOfMip() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DashboardStatusOfMIP> executionCountOfMip = dashboardService.getCountForExecutionOfMip();
            result.put("ExecutionStatusOfMip", executionCountOfMip);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Count For Execution Status Of MIP");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Status of MIP based on the District
    @PostMapping("/getStatusOfMipByDistrict")
    public OIIPCRAResponse getStatusOfMipByDistrict(@RequestParam(required = false, defaultValue = "0") Integer districtId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DashboardStatusOfMIPByDist> statusOfMIPByDistrict = dashboardService.getDashboardStatusOfMIPByDist(districtId);
            result.put("statusOfMIPByDistrict", statusOfMIPByDistrict);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Status Of MIP District Wise");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Status of MIP based on the Division
    @PostMapping("/getStatusOfMipByDivision")
    public OIIPCRAResponse getStatusOfMipByDivision(@RequestParam Integer districtId,
                                                    @RequestParam(required = false, defaultValue = "0") Integer divisionId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DashboardStatusOfMIPByDivision> statusOfMIPByDivision = dashboardService.getDashboardStatusOfMIPByDivision(districtId, divisionId);
            result.put("statusOfMIPByDivision", statusOfMIPByDivision);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Status Of MIP Division Wise");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Expenditure of MIP based on the District
    @PostMapping("/getExpenditureOfMipByDistrict")
    public OIIPCRAResponse getExpenditureOfMipByDistrict(@RequestParam(required = false, defaultValue = "0") Integer districtId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DashboardExpenditureOfMIPByDistrict> expndInMIPByDist = dashboardService.getDashboardExpenditureInMIPByDistrict(districtId);
            result.put("expndInMIPByDist", expndInMIPByDist);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Expenditure in MIP District Wise");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Expenditure of MIP based on the Division
    @PostMapping("/getExpenditureOfMipByDivision")
    public OIIPCRAResponse getExpenditureOfMipByDivision(@RequestParam Integer districtId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer divisionId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DashboardExpenditureOfMIPByDivision> expndInMIPByDivision = dashboardService.getDashboardExpenditureInMIPByDivision(districtId, divisionId);
            result.put("expndInMIPByDivision", expndInMIPByDivision);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Expenditure in MIP Division Wise");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Component Estimated & Expenditure API For Dashboard
    @PostMapping("/getComponentEstdAndExp")
    public OIIPCRAResponse getComponentEstdAndExp() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<DashboardComponentEstExp> componentListEstExpList = dashboardService.getComponentListEstExpList();
            result.put("componentListEstExpList", componentListEstExpList);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Component List Expenditure & Estimated");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getMipInfoById")
    public OIIPCRAResponse getMipInfoById(@RequestParam Integer tankId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            DashboardMipDto mip = new DashboardMipDto();
            Double total = 0.0;
            String tankName = dashboardRepositoryImpl.getTankName(tankId);
            List<String> villageName = dashboardRepositoryImpl.getVillageNameByTankId(tankId);
            Integer count = dashboardRepositoryImpl.getNoOfPaniPanchayatByTankId(tankId);
            List<String> paniPanchayatName = dashboardRepositoryImpl.getPaniPanchayatNameByTankId(tankId);
            DprInformationDto dprInfo = dashboardRepositoryImpl.getAyacutClassification(tankId);
            Double estimateCostOfCivilWork = dashboardRepositoryImpl.getEstimateCost(tankId);
            if (estimateCostOfCivilWork == null) {
                estimateCostOfCivilWork = 0.0;
            }
            Double expenditureByPreviousMonth = dashboardService.getExpenditureByPreviousMonth(tankId);
            if (expenditureByPreviousMonth == null) {
                expenditureByPreviousMonth = 0.0;
            }
            Double expenditureThisMonth = dashboardService.getExpenditureThisMonth(tankId);
            if (expenditureThisMonth == null) {
                expenditureThisMonth = 0.0;
            }
            if (expenditureByPreviousMonth != null && expenditureThisMonth != null) {
                total = expenditureByPreviousMonth + expenditureThisMonth;
            }
            mip.setVillageName(villageName);
            mip.setCount(count);
            mip.setPaniPanchayatName(paniPanchayatName);
            mip.setDprInfo(dprInfo);
            mip.setEstimateCostOfCivilWork(estimateCostOfCivilWork);
            mip.setExpenditureByPreviousMonth(expenditureByPreviousMonth);
            mip.setExpenditureThisMonth(expenditureThisMonth);
            mip.setTotalExpenditure(total);
            mip.setTankName(tankName);

            result.put("mipInfo", mip);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("MipInfo By Id");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getCivilWorkStatusDivisionWise")
    public OIIPCRAResponse getCivilWorkStatusDivisionWise() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CivilWorkStatusDivisionDto> civilWorkStatusDivisionWise = dashboardService.getCivilWorkStatusDivisionWise();
            Integer sumTotalTank = 0;
            Integer sumWorkTakenUpInTanks = 0;
            Integer sumWorksInTankCompleted = 0;
            Integer sumOfBalanceNoOfTanksOngoing = 0;
            Integer sumOfTankDropped = 0;
            Integer sumOfTanksTakenUp = 0;

            for (int i = 0; i < civilWorkStatusDivisionWise.size(); i++) {
                sumTotalTank = sumTotalTank + civilWorkStatusDivisionWise.get(i).getNoOfTanks();
                sumWorkTakenUpInTanks = sumWorkTakenUpInTanks + civilWorkStatusDivisionWise.get(i).getWorksTakenInTanks();
                sumWorksInTankCompleted = sumWorksInTankCompleted + civilWorkStatusDivisionWise.get(i).getWorksCompleted();
                sumOfBalanceNoOfTanksOngoing = sumOfBalanceNoOfTanksOngoing + civilWorkStatusDivisionWise.get(i).getNoOfTanksOngoing();
                sumOfTankDropped = sumOfTankDropped + civilWorkStatusDivisionWise.get(i).getNoOfTanksDropped();
                sumOfTanksTakenUp = sumOfTanksTakenUp + civilWorkStatusDivisionWise.get(i).getNoOfTankTakenUp();
            }
            result.put("civilWorkStatusDivisionWise", civilWorkStatusDivisionWise);
            result.put("sumTotalTank", sumTotalTank);
            result.put("sumWorkTakenUpInTanks", sumWorkTakenUpInTanks);
            result.put("sumWorksInTankCompleted", sumWorksInTankCompleted);
            result.put("sumOfBalanceNoOfTanksOngoing", sumOfBalanceNoOfTanksOngoing);
            result.put("sumOfTankDropped", sumOfTankDropped);
            result.put("sumOfTanksTakenUp", sumOfTanksTakenUp);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("civilWorkStatusDivisionWise Value");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getCropCycleAyacutArea")
    public OIIPCRAResponse getCropCycleAyacutArea(@RequestParam Integer projectId, @RequestParam List<String> year) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            for (int i = 0; i < year.size(); i++) {
                List<CropCycleAyacutDto> crop = dashboardService.getCropCycleAyacut(projectId, year.get(i));
                result.put(year.get(i), crop);
            }
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Component List Expenditure & Estimated");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getCropCycleAyacutArea1")
    public OIIPCRAResponse getCropCycleAyacutArea1(@RequestParam Integer projectId, @RequestParam List<String> year) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CropCycleAyacutDto> componentListEstExpList = dashboardService.getCropCycleAyacut1(projectId, year);
            result.put("componentListEstExpList", componentListEstExpList);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Component List Expenditure & Estimated");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getCivilWorkStatus")
    public OIIPCRAResponse getCivilWorkStatus(@RequestParam(value = "typeId", required = false) Integer typeId,
                                              @RequestParam(value = "yearId", required = false) Integer yearId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<CivilWorkDto> civilWorkStatusDivisionWise = dashboardService.getCivilWorkStatus(typeId, yearId);
            result.put("civilWorkStatusDivisionWise", civilWorkStatusDivisionWise);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("civilWorkStatusDivisionWise");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getValue")
    public OIIPCRAResponse getValue(@RequestParam(value = "yearId", required = false) Integer yearId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ValueDto> value = dashboardService.getValue(yearId);
            result.put("value", value);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Value");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getTankWiseCount")
    public OIIPCRAResponse getTankWiseCount(@RequestParam(value = "tankId") Integer tankId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            TankWiseCountDto TankWiseCount = dashboardService.getTankWiseCount(tankId);
            result.put("TankWiseCount", TankWiseCount);
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Value");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getTankWiseCatchmentArea")
    public OIIPCRAResponse getTankWiseCatchmentArea(@RequestParam(value = "tankId") Integer tankId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //  TankWiseCountDto TankWiseCatchmentArea = dashboardService.getTankWiseCatchmentArea(tankId);
            result.put("TankWiseCatchmentArea", dashboardService.getTankWiseCatchmentArea(tankId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Value");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getTankWiseAyaCut")
    public OIIPCRAResponse getTankWiseAyaCut(@RequestParam(value = "tankId") Integer tankId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            // CropCycleAyacutDto CropCycleAyacut = dashboardService.getTankWiseAyaCut(tankId);
            result.put("TankWiseAyaCut", dashboardService.getTankWiseAyacutArea(tankId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Value");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Iswara Sir Project Dashboard
    @PostMapping("/getDetailsByDivisionAndTankWise")
    public OIIPCRAResponse getDetailsByDivisionAndTankWise(@RequestParam(value = "projectId") Integer projectId, @RequestParam(value = "divisionName") String divisionName,
                                                           @RequestParam(value = "userId", required = false) Integer userId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> result1 = new HashMap<>();
        try {
                if(projectId<0 && divisionName==null){
                    UserInfoDto userInfoById = userQryRepo.getUserById(userId);
                    if (userInfoById.getRoleId() > 4) {
                        List<UserAreaMappingDto> userAreaMappingList = userQryRepo.getUserAuthority(userId);
                        if (userAreaMappingList.get(0).getDivision_id()!=null){
                            DivisionBoundaryDto divisionList = masterService.getDivisionById(userAreaMappingList.get(0).getDivision_id());
                            divisionName=divisionList.getDivisionName();
                        }
                    }
                }


            // CropCycleAyacutDto CropCycleAyacut = dashboardService.getTankWiseAyaCut(tankId);
//            result.put("Category", dashboardService.getCategoryByTankId(tankId));
            result.put("WSA", dashboardService.getWSA(projectId, divisionName));
            result.put("WSATillMonth", dashboardService.getWSAtillMonth(projectId, divisionName));
            result.put("ayaCut", dashboardService.getAyaCut(projectId, divisionName));
            result.put("contractAmount", dashboardService.getcontractAmount(projectId, divisionName));
            result.put("catchmentArea", dashboardService.getcatchmentArea(projectId, divisionName));
            result.put("workAwarded", dashboardService.workAwarded(projectId, divisionName));


            List<String> year = dashboardService.getDistinctYearBYProjectIdAndDivisionId(projectId, divisionName);
            // List<String> year=graphService.getDistinctYearBYProjectId(projectId);

            for (int i = 0; i < year.size(); i++) {
                List<CropCycleAyacutDto> crop = dashboardService.getCropCycleIntensity(projectId, year.get(i), divisionName);
                result1.put(year.get(i), crop);
            }

            result.put("croppingIntensity", result1);
            result.put("tanksByCategory", dashboardService.tanksByCategory(projectId, divisionName));

            result.put("commulativeExpenditure", dashboardService.commulativeExpenditure(projectId, divisionName));
            result.put("totalCommulativeExpenditure", dashboardService.totalCommulativeExpenditure(projectId, divisionName));

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("Values");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //Tapaswini  Finance Dashboard
    @PostMapping("/getComponentWiseExpenditureAmount")
    public OIIPCRAResponse getComponentWiseValue(@RequestParam(value = "userId") Integer userId,
                                                 @RequestParam(value = "typeId") Integer typeId,
                                                 @RequestParam(value = "workTypeId",required = false)Integer workTypeId,
                                                 @RequestParam(value = "componentId", required = false) Integer componentId,
                                                 @RequestParam(value = "yearId", required = false) Integer yearId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("component", dashboardService.getComponents(userId, typeId,workTypeId,componentId, yearId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("expenditureAmount");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    //unused For Contract Amount
    @PostMapping("/getComponentWiseContractAmount")
    public OIIPCRAResponse getComponentWiseContractAmount(@RequestParam(value = "userId") Integer userId,
                                                          @RequestParam(value = "typeId") Integer typeId,
                                                          @RequestParam(value = "componentId", required = false) Integer componentId,
                                                          @RequestParam(value = "yearId", required = false) Integer yearId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            result.put("component", dashboardService.getComponentWiseContractAmount(userId, typeId, componentId, yearId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("contractAmount");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getComponentWiseContractStatus")
    public OIIPCRAResponse getComponentWiseContractStatus(@RequestParam(value = "userId", required = false) Integer userId,
                                                          @RequestParam(value = "typeId", required = false) Integer typeId,
                                                          @RequestParam(value = "statusId", required = false) Integer statusId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("component", dashboardService.getComponentWiseContractStatus(userId, typeId, statusId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("ComponentWiseStatus");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getComponentWiseEstimateStatus")
    public OIIPCRAResponse getComponentWiseEstimateStatus(@RequestParam(value = "userId", required = false) Integer userId,
                                                          @RequestParam(value = "typeId", required = false) Integer typeId,
                                                          @RequestParam(value = "statusId", required = false) Integer statusId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("component", dashboardService.getComponentWiseEstimateStatus(userId, typeId, statusId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("EstimateWiseStatus");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


    @PostMapping("/getPhysicalProgressValue")
    public OIIPCRAResponse getPhysicalProgressValue() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("progress", dashboardService.getPhysicalProgressValue());
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("progress Values");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getTotalPhysicalProgressValue")
    public OIIPCRAResponse getTotalPhysicalProgressValue(@RequestParam Integer circleId, Integer distId, Integer divisionId, Integer blockId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //PhysicalProgressValueDto totalLengthOfCanalAsPerEstimate = dashboardService.getTotalLengthOfCanalAsPerEstimateVale(divisionId,distId);

            result.put("totalLengthOfCanalAsPerEstimate", dashboardService.getTotalLengthOfCanalAsPerEstimate(circleId, distId, divisionId, blockId));
            result.put("lengthOfCanalImproved", dashboardService.getLengthOfCanalImproved(circleId, distId, divisionId, blockId));
            result.put("noOfCdStructuresRepared", dashboardService.getNoOfCdStructuresRepared(circleId, distId, divisionId, blockId));
            result.put("noOfCdStructuresToBeRepared", dashboardService.getNoOfCdStructuresToBeRepared(circleId, distId, divisionId, blockId));
            result.put("totalLengthOfCad", dashboardService.getTotalLengthOfCad(circleId, distId, divisionId, blockId));
            result.put("noOfOutletConstructed", dashboardService.getNoOfOutletConstructed(circleId, distId, divisionId, blockId));
            result.put("CadConstructed", dashboardService.getCadConstructed(circleId, distId, divisionId, blockId));
            result.put("tankSurveyed",dashboardService.getTankSurveyedCount(circleId,distId,divisionId,blockId));
            result.put("totalCadSurveyed",dashboardService.getCadSurveyedCount(circleId,distId,divisionId,blockId));
            result.put("totalFeederSurveyed",dashboardService.getFeederSurveyedCount(circleId,distId,divisionId,blockId));
            result.put("totalDepthSurveyed",dashboardService.getDepthCount(circleId,distId,divisionId,blockId));

            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("progress Values");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getProjectIdByDivisionName")
    public OIIPCRAResponse getProjectIdByDivisionName(@RequestParam String divisionName) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("projectId", dashboardService.getProjectIdByDivisionName(divisionName));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("projectId");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getValueByComponentId")
    public OIIPCRAResponse getValueByComponentId(@RequestParam Integer componentId, @RequestParam(value = "yearId", required = false) Integer yearId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("ComponentWiseValue", dashboardService.getValueByComponentId(componentId, yearId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("ComponentWiseValue");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getValueByComponentIdAndDistId")
    public OIIPCRAResponse getValueByComponentId(@RequestParam Integer componentId,
                                                 @RequestParam(value = "yearId", required = false) Integer yearId,
                                                 @RequestParam(value = "distId",required = false)Integer distId,
                                                 @RequestParam(value = "typeId",required = false)Integer typeId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            Activity data =null;

            Activity details=masterRepository.getDetailsByParentId(componentId);
            if(details.getMasterHeadId() > 1) {
                data = dashboardRepositoryImpl.getParentData(componentId);
                if (typeId == 3 || typeId == 4 || typeId == 5) {
//                 data = dashboardRepositoryImpl.getParentData(componentId);
                    data.setTypeId(typeId - 1);
                /*if(data.getMasterHeadId()==1){
                    data.setTypeId(2);
                }
                if(data.getMasterHeadId()==2){
                    data.setTypeId(3);
                }
                if(data.getMasterHeadId()==3){
                    data.setTypeId(4);
                }*/
                    result.put("typeId", data.getTypeId());
                    result.put("componentId", data.getId());

                } else {
                    result.put("typeId", 0);
                    result.put("componentId", 0);
                }
            }
            else{
                result.put("typeId",typeId);
                result.put("componentId",componentId);
            }

            result.put("ComponentWiseValue", dashboardService.getValueByComponentIdAndDistId(componentId, yearId,distId,typeId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("ComponentWiseValue");
        } catch (Exception e) {
            e.printStackTrace();
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }

    @PostMapping("/getWSALastMonthYear")
    public OIIPCRAResponse getWSALastMonthYear() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            //List<ComponentDto> value = dashboardService.getComponents(userId);
            result.put("WSALastMonthYear", dashboardService.getWSAtillMonth(null, null));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("WSALastMonthYear");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }


    @PostMapping("/getExpenditureEstimateContractYearWise")
    public OIIPCRAResponse getExpenditureEstimateContractYearWise(@RequestParam(value = "userId", required = false) Integer userId,
                                                                  @RequestParam(value = "componentId", required = false) Integer componentId) {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            //List<ComponentDto> value = dashboardService.getComponents(userId);
            //System.out.println();
            result.put("data", dashboardService.getExpenditureEstimateContractYearWise(userId, componentId));
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("ExpenditureEstimateContractYearWise");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }
    @PostMapping("/getLastMonthYear")
    public OIIPCRAResponse getLastMonthYear() {
        OIIPCRAResponse oiipcraResponse = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            result.put("WSALastMonthYear", dashboardRepositoryImpl.getLastMonthAndYear());
            result.put("ayacutWiseCropNonCropMonthYear",dashboardRepositoryImpl.getAyacutWiseCropNonCropMonthYear());
            result.put("cropCycleAyacutMonthYear",dashboardRepositoryImpl.getCropCycleAyacutMonthYear());
            oiipcraResponse.setData(result);
            oiipcraResponse.setStatus(1);
            oiipcraResponse.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            oiipcraResponse.setMessage("WSALastMonthYear");
        } catch (Exception e) {
            oiipcraResponse = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return oiipcraResponse;
    }




}
