package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.dto.ContractCountDto;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.dto.FinanceDashBoardInvoiceDistWise;
import com.orsac.oiipcra.dto.FinanceDashboardInvoiceBlockWise;
import com.orsac.oiipcra.repository.FinanceRepository;
import com.orsac.oiipcra.repositoryImpl.FinanceRepositoryImpl;
import com.orsac.oiipcra.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private FinanceRepository financeRepository;

    @Autowired
    private FinanceRepositoryImpl financeRepositoryImpl;
    @Override
    public List<ContractCountDto> getContractStatusCount() {
        return financeRepository.getContractStatusCount();
    }

    @Override
    public List<ContractCountDto> getDistrictWiseGrievanceCount() {
        return financeRepository.getDistrictWiseGrievanceCount();
    }

    @Override
    public List<ContractCountDto> getBlockWiseGrievanceCount() {
        return financeRepository.getBlockWiseGrievanceCount();
    }

    @Override
    public List<ContractCountDto> getDistrictWiseIssueCountByTender() {
        return financeRepository.getDistrictWiseIssueCountByTender();
    }
    @Override
    public List<ContractCountDto> districtWiseIssueCountByContract(){
        return financeRepository.districtWiseIssueCountByContract();
    }
    @Override
    public List<ContractCountDto> districtWiseIssueCountByTank(){
        return financeRepository.districtWiseIssueCountByTank();
    }
    @Override
    public List<ContractCountDto> getBlockWiseIssueCount() {
        List<ContractCountDto> getBlockWiseIssueCount=new ArrayList<>();
        List<ContractCountDto> tender=financeRepository.getBlockWiseIssueCountForTender();
        List<ContractCountDto> contract=financeRepository.getBlockWiseIssueCountForContract();
        List<ContractCountDto> tank=financeRepository.getBlockWiseIssueCountForTank();
        for(int i=0;i<tender.size();i++){
            for(int j=0;j<contract.size();j++){
                if(tender.get(i).getDistrictId()==contract.get(j).getDistrictId()){
                    for(int k=0;k<tank.size();k++){
                        if(tender.get(i).getDistrictId()==tank.get(k).getDistrictId()) {
                            ContractCountDto cd=new ContractCountDto();
                            cd.setDistrictId(tender.get(i).getDistrictId());
                            cd.setDistrictName(tender.get(i).getDistrictName());
                            cd.setBlockId(tender.get(i).getBlockId());
                            cd.setBlockName(tender.get(i).getBlockName());
                            cd.setCount(tender.get(i).getCount()+contract.get(j).getCount()+tank.get(k).getCount());
                            getBlockWiseIssueCount.add(cd);
                        }
                    }
                }
            }
        }
        return getBlockWiseIssueCount;
    }
    @Override
    public List<ContractCountDto> getBlockAndDepartmentWiseIssueCount() {
        List<ContractCountDto> getCount1=new ArrayList<>();
        List<ContractCountDto> getBlockAndDepartmentWiseIssueCount=new ArrayList<>();
        List<ContractCountDto> tender=financeRepository.getBlockAndDeptWiseIssueCountForTender();
        List<ContractCountDto> contract=financeRepository.getBlockAndDeptWiseIssueCountForContract();
        List<ContractCountDto> tank=financeRepository.getBlockAndDeptWiseIssueCountForTank();
  /*      //comparing tender and contract
       for(int i=0;i<tender.size();i++){
           for(int j=0;j<contract.size();j++){
               if(tender.get(i).getBlockId()==contract.get(j).getBlockId() && tender.get(i).getDepartmentId()==contract.get(j).getDepartmentId()){
                   ContractCountDto cd=new ContractCountDto();
                   cd.setBlockId(tender.get(i).getBlockId());
                   cd.setBlockName(tender.get(i).getBlockName());
                   cd.setDepartmentId(tender.get(i).getDepartmentId());
                   cd.setDepartmentName(tender.get(i).getDepartmentName());
                   cd.setCount(tender.get(i).getCount()+contract.get(j).getCount());
                   getCount1.add(cd);
               }
               else{
                   getCount1.add(contract.get(j));

               }
           }

           getCount1.add(tender.get(i));
       }
       //comparing previous compare data with tank
        for(int i=0;i<getCount1.size();i++){
            for(int j=0;j<tank.size();j++){
                if(getCount1.get(i).getBlockId()==tank.get(j).getBlockId() && getCount1.get(i).getDepartmentId()==tank.get(j).getDepartmentId()){
                    ContractCountDto cd=new ContractCountDto();
                    cd.setBlockId(getCount1.get(i).getBlockId());
                    cd.setBlockName(getCount1.get(i).getBlockName());
                    cd.setDepartmentId(getCount1.get(i).getDepartmentId());
                    cd.setDepartmentName(getCount1.get(i).getDepartmentName());
                    cd.setCount(getCount1.get(i).getCount()+tank.get(j).getCount());
                    getBlockAndDepartmentWiseIssueCount.add(cd);
                }
                else{
                    getBlockAndDepartmentWiseIssueCount.add(tank.get(j));
                }
            }
            getBlockAndDepartmentWiseIssueCount.add(getCount1.get(i));
        }*/
        for(int i=1;i<5;i++){
            ContractCountDto cd=new ContractCountDto();
            cd.setBlockId(2);
            cd.setBlockName("Ambabhana");
            cd.setDepartmentId(1);
            cd.setDepartmentName("WaterResource");
            cd.setCount(5);
            getCount1.add(cd);

        }
        /*getCount1.addAll(tender);
        getCount1.addAll(contract);
        getCount1.addAll(tank);
        for(ContractCountDto count:getCount1){

        }*/

        return getCount1;
    }

    @Override
    public List<ContractCountDto> getInvoiceStatusCount() {
        return financeRepository.getInvoiceStatusCount();
    }

    @Override
    public List<FinanceDashBoardInvoiceDistWise> getDistrictWiseInvoiceCount() {
        return financeRepositoryImpl.getDistrictWiseInvoiceCount();
    }

    @Override
    public List<FinanceDashboardInvoiceBlockWise> getBlockWiseInvoiceCount() {
        return financeRepositoryImpl.getBlockWiseInvoiceCount();
    }
    @Override
    public List<FinanceDashBoardInvoiceDistWise> getDistWiseSurveyCount(int distId) {
        return financeRepositoryImpl.getDistrictWiseSurveyCount(distId);
    }

    @Override
    public List<FinanceDashboardInvoiceBlockWise> getBlockWiseSurveyCount(int distId, int blockId) {
        return financeRepositoryImpl.getBlockWiseSurveyCount(distId, blockId);
    }
}
