package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.dto.ContractCountDto;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;

import java.util.List;

public interface FinanceRepository {
    List<ContractCountDto> getContractStatusCount();

    List<ContractCountDto> getDistrictWiseGrievanceCount();
    List<ContractCountDto> getBlockWiseGrievanceCount();



    List<ContractCountDto> getDistrictWiseIssueCountByTender();
    List<ContractCountDto> districtWiseIssueCountByContract();
    List<ContractCountDto> districtWiseIssueCountByTank();
    List<ContractCountDto> getBlockWiseIssueCountForTender();
    List<ContractCountDto> getBlockWiseIssueCountForContract();
    List<ContractCountDto> getBlockWiseIssueCountForTank();
    List<ContractCountDto> getBlockAndDeptWiseIssueCountForTender();
    List<ContractCountDto> getBlockAndDeptWiseIssueCountForContract();
    List<ContractCountDto> getBlockAndDeptWiseIssueCountForTank();



    List<ContractCountDto> getInvoiceStatusCount();

    List<FinanceDashBoardInvoiceDistWise> getDistrictWiseInvoiceCount();

    List<FinanceDashBoardInvoiceDistWise> getDistrictWiseSurveyCount(int distId);

}
