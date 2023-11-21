package com.orsac.oiipcra.controller;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.ActivityStatusDto;
import com.orsac.oiipcra.dto.ContractCountDto;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.dto.FinanceDashboardInvoiceBlockWise;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @PostMapping("/getContractStatusCount")
    public OIIPCRAResponse getContractStatusCount() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractCountDto> contractStatusCount = financeService.getContractStatusCount();
            if (!contractStatusCount.isEmpty() && contractStatusCount.size() > 0) {
                result.put("contractStatusCount", contractStatusCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ContractStatusCount.");
            } else {
                result.put("contractStatusCount", contractStatusCount);
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

    @PostMapping("/getDistrictAndBlockWiseGrievanceCount")
    public OIIPCRAResponse getDistrictWiseGrievanceCount() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractCountDto> districtWiseGrievanceCount = financeService.getDistrictWiseGrievanceCount();
            List<ContractCountDto> blockWiseGrievanceCount = financeService.getBlockWiseGrievanceCount();
            if (!districtWiseGrievanceCount.isEmpty() && districtWiseGrievanceCount.size() > 0) {
                result.put("districtWiseGrievanceCount", districtWiseGrievanceCount);
            }
            if (!blockWiseGrievanceCount.isEmpty() && blockWiseGrievanceCount.size() > 0) {
                result.put("blockWiseGrievanceCount", blockWiseGrievanceCount);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All DistrictAndBlockWiseGrievanceCount.");

        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
/*    @PostMapping("/getBlockWiseGrievanceCount")
    public OIIPCRAResponse getBlockWiseGrievanceCount(@RequestParam Integer distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractCountDto> blockWiseGrievanceCount=financeService.getBlockWiseGrievanceCount(distId);
            if (!blockWiseGrievanceCount.isEmpty() && blockWiseGrievanceCount.size() > 0) {
                result.put("blockWiseGrievanceCount", blockWiseGrievanceCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All BlockWiseGrievanceCount.");
            } else {
                result.put("blockWiseGrievanceCount", blockWiseGrievanceCount);
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
    }*/

    @PostMapping("/getDistrictAndBlockWiseIssueCount")
    public OIIPCRAResponse getDistrictWiseIssueCount() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractCountDto> districtWiseIssueCountByTender = financeService.getDistrictWiseIssueCountByTender();
            List<ContractCountDto> districtWiseIssueCountByContract = financeService.districtWiseIssueCountByContract();
            List<ContractCountDto> districtWiseIssueCountByTank = financeService.districtWiseIssueCountByTank();
            List<ContractCountDto> districtWiseIssue = new ArrayList<>();
            for (int i = 0; i < districtWiseIssueCountByTender.size(); i++) {
                for (int j = 0; j < districtWiseIssueCountByContract.size(); j++) {
                    if (districtWiseIssueCountByTender.get(i).getDistrictId() == districtWiseIssueCountByContract.get(j).getDistrictId()) {
                        for (int k = 0; k < districtWiseIssueCountByTank.size(); k++) {
                            if (districtWiseIssueCountByTender.get(i).getDistrictId() == districtWiseIssueCountByTank.get(k).getDistrictId()) {
                                ContractCountDto cd = new ContractCountDto();
                                cd.setDistrictId(districtWiseIssueCountByTender.get(i).getDistrictId());
                                cd.setDistrictName(districtWiseIssueCountByTender.get(i).getDistrictName());
                                cd.setCount(districtWiseIssueCountByTender.get(i).getCount() + districtWiseIssueCountByContract.get(j).getCount() + districtWiseIssueCountByTank.get(k).getCount());
                                districtWiseIssue.add(cd);

                            }
                        }
                    }
                }
            }
            List<ContractCountDto> blockWiseIssueCount = financeService.getBlockWiseIssueCount();

            List<ContractCountDto> blockAndDepartmentWiseIssueCount = financeService.getBlockAndDepartmentWiseIssueCount();

            if (!districtWiseIssue.isEmpty() && districtWiseIssue.size() > 0) {
                result.put("districtWiseIssue", districtWiseIssue);
            }
            if (!blockWiseIssueCount.isEmpty() && blockWiseIssueCount.size() > 0) {
                result.put("blockWiseIssueCount", blockWiseIssueCount);
            }
            if (!blockAndDepartmentWiseIssueCount.isEmpty() && blockAndDepartmentWiseIssueCount.size() > 0) {
                result.put("blockAndDepartmentWiseIssueCount", blockAndDepartmentWiseIssueCount);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All DistrictAndBlockWiseIssueCount.");

        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
/*  @PostMapping("/getBlockWiseIssueCount")
    public OIIPCRAResponse getBlockWiseIssueCount(@RequestParam Integer distId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractCountDto> blockWiseIssueCount=financeService.getBlockWiseIssueCount(distId);
            if (!blockWiseIssueCount.isEmpty() && blockWiseIssueCount.size() > 0) {
                result.put("blockWiseIssueCount", blockWiseIssueCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All BlockWiseIssueCount.");
            } else {
                result.put("blockWiseIssueCount", blockWiseIssueCount);
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
    }*/

    //Invoice Status Count
    @PostMapping("/getInvoiceStatusCount")
    public OIIPCRAResponse getInvoiceStatusCount() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContractCountDto> invoiceStatusCount = financeService.getInvoiceStatusCount();
            if (!invoiceStatusCount.isEmpty() && invoiceStatusCount.size() > 0) {
                result.put("invoiceStatusCount", invoiceStatusCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All ContractStatusCount.");
            } else {
                result.put("invoiceStatusCount", invoiceStatusCount);
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

    //District Wise Invoice Count
    @PostMapping("/getDistrictAndBlockWiseInvoiceCount")
    public OIIPCRAResponse getDistrictWiseInvoiceCount() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinanceDashBoardInvoiceDistWise> distWiseInvoiceCount = financeService.getDistrictWiseInvoiceCount();
            List<FinanceDashboardInvoiceBlockWise> blockWiseInvoiceCount = financeService.getBlockWiseInvoiceCount();
            if (!distWiseInvoiceCount.isEmpty() && distWiseInvoiceCount.size() > 0) {
                result.put("distWiseInvoiceCount", distWiseInvoiceCount);
            }
            if (!blockWiseInvoiceCount.isEmpty() && blockWiseInvoiceCount.size() > 0) {
                result.put("blockWiseInvoiceCount", blockWiseInvoiceCount);
            }
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("District And Block Wise Invoice Count.");
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }
/*    @PostMapping("/blockWiseInvoiceCount")
    public OIIPCRAResponse getBlockWiseInvoiceCount(@RequestParam Integer distId,
                                                    @RequestParam(required = false, defaultValue = "0") Integer blockId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinanceDashboardInvoiceBlockWise> blockWiseInvoiceCount = financeService.getBlockWiseInvoiceCount(distId);
            if (!blockWiseInvoiceCount.isEmpty() && blockWiseInvoiceCount.size() > 0) {
                result.put("blockWiseInvoiceCount", blockWiseInvoiceCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("District Wise Invoice Count.");
            } else {
                result.put("blockWiseInvoiceCount", blockWiseInvoiceCount);
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
    }*/


    @PostMapping("/districtWiseSurveyDataCount")
    public OIIPCRAResponse districtWiseSurveyDataCount(@RequestParam(required = false, defaultValue = "0") Integer distId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinanceDashBoardInvoiceDistWise> distWiseSurveyCount = financeService.getDistWiseSurveyCount(distId);
            if (!distWiseSurveyCount.isEmpty() && distWiseSurveyCount.size() > 0) {
                result.put("distWiseSurveyCount", distWiseSurveyCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("District Wise Invoice Count.");
            } else {
                result.put("distWiseSurveyCount", distWiseSurveyCount);
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

    @PostMapping("/blockWiseSurveyDataCount")
    public OIIPCRAResponse blockWiseSurveyDataCount(@RequestParam Integer distId,
                                                    @RequestParam(required = false, defaultValue = "0") Integer blockId){
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<FinanceDashboardInvoiceBlockWise> blockWiseSurveyDataCount = financeService.getBlockWiseSurveyCount(distId,blockId);
            if (!blockWiseSurveyDataCount.isEmpty() && blockWiseSurveyDataCount.size() > 0) {
                result.put("blockWiseSurveyDataCount", blockWiseSurveyDataCount);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("Block Wise Invoice Count.");
            } else {
                result.put("blockWiseSurveyDataCount", blockWiseSurveyDataCount);
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
}


