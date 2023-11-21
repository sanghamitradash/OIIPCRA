package com.orsac.oiipcra.service;

import com.orsac.oiipcra.dto.ActivityStatusDto;
import com.orsac.oiipcra.dto.ContractCountDto;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.dto.FinanceDashboardInvoiceBlockWise;

import java.util.List;

public interface FinanceService {
    List<ContractCountDto> getContractStatusCount();

    List<ContractCountDto> getInvoiceStatusCount();

    List<FinanceDashBoardInvoiceDistWise> getDistrictWiseInvoiceCount();

    List<FinanceDashboardInvoiceBlockWise>getBlockWiseInvoiceCount();

    List<ContractCountDto> getDistrictWiseGrievanceCount();
    List<ContractCountDto> getBlockWiseGrievanceCount();

    List<FinanceDashBoardInvoiceDistWise> getDistWiseSurveyCount(int distId);
    public List<FinanceDashboardInvoiceBlockWise> getBlockWiseSurveyCount(int distId, int blockId);

    List<ContractCountDto> getDistrictWiseIssueCountByTender();
    List<ContractCountDto> districtWiseIssueCountByContract();
   List<ContractCountDto> districtWiseIssueCountByTank();
    List<ContractCountDto>  getBlockWiseIssueCount();



    List<ContractCountDto>  getBlockAndDepartmentWiseIssueCount();




}
