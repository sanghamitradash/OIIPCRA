package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.dto.GrievanceListingDto;
import com.orsac.oiipcra.dto.GrievanceDto;
import com.orsac.oiipcra.dto.GrievanceFileDto;
import com.orsac.oiipcra.dto.GrievanceStatusDto;
import com.orsac.oiipcra.entities.ContractMaster;
import com.orsac.oiipcra.entities.GrievanceEntity;
import com.orsac.oiipcra.exception.RecordNotFoundException;
import com.orsac.oiipcra.repository.GrievanceRepository;
import com.orsac.oiipcra.repositoryImpl.GrievanceRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.service.GrievanceService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GrievanceServiceImpl implements GrievanceService {

    @Autowired
    private GrievanceRepository grievanceRepository;

    @Autowired
    private GrievanceRepositoryImpl grievanceRepositoryImpl;

    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;

    @Override
    public GrievanceEntity createGrivance(GrievanceFileDto grievanceFileDto){
        grievanceFileDto.setStatus(1);
        grievanceFileDto.setIsActive(true);
        grievanceFileDto.setCreatedBy(1);
        grievanceFileDto.setUpdatedBy(1);
        GrievanceEntity grievance= new GrievanceEntity();
        BeanUtils.copyProperties(grievanceFileDto, grievance);
        return grievanceRepository.save(grievance);
    }


    @Override
    public GrievanceEntity updateGrivance(GrievanceEntity grievanceEntity) {
        GrievanceEntity existinggravience = grievanceRepository.findById(grievanceEntity.getId()).orElseThrow(() -> new RecordNotFoundException("GrievanceEntity", "id", grievanceEntity.getId()));

        existinggravience.setStatus(grievanceEntity.getStatus());
        existinggravience.setUpdatedBy(grievanceEntity.getUpdatedBy());
        existinggravience.setUpdtedOn(grievanceEntity.getUpdtedOn());
        if(grievanceEntity.getDeptId()!=null) {
            existinggravience.setDeptId(grievanceEntity.getDeptId());
        }
        if(grievanceEntity.getResolutionLevel()!=null) {
            existinggravience.setResolutionLevel(grievanceEntity.getResolutionLevel());
        }
        if(grievanceEntity.getDesignationId()!=null) {
            existinggravience.setDesignationId(grievanceEntity.getDesignationId());
        }
        if(grievanceEntity.getResolvedUserId()!=null) {
            existinggravience.setResolvedUserId(grievanceEntity.getResolvedUserId());
        }
        return grievanceRepository.save(existinggravience);
    }

    @Override
    public GrievanceDto getGrievanceId(Integer id){
        GrievanceDto grievanceDto = grievanceRepositoryImpl.getGrievanceId(id);
        return grievanceDto;
    }


    @Override
    public Page<GrievanceDto> getAllGrievance(GrievanceListingDto grievanceListingDto){
        List<Integer> tankIds = new ArrayList<>();

        if(grievanceListingDto.getTankId()!=null && grievanceListingDto.getTankId()>0){
            tankIds.add(grievanceListingDto.getTankId());
        }else if(grievanceListingDto.getContractId()!=null&&grievanceListingDto.getContractId()>0){
            //Get Grievance by Contract
            List<Integer> tankData = surveyRepositoy.getTankIdsByContractId(grievanceListingDto.getContractId());
            tankIds.addAll(tankData);
        }else if(grievanceListingDto.getInvoiceId()!=null&&grievanceListingDto.getInvoiceId()>0){
            //Get Grievance by Invoice
            List<Integer> tankData = surveyRepositoy.getTankIdsByInvoiceId(grievanceListingDto.getInvoiceId());
            tankIds.addAll(tankData);
        }else if(grievanceListingDto.getEstimateId()!=null&&grievanceListingDto.getEstimateId()>0){
            //Get Grievance by Estimate
            List<Integer> tankData = surveyRepositoy.getTankIdsByEstimateId(grievanceListingDto.getEstimateId());
            tankIds.addAll(tankData);
        }else if(grievanceListingDto.getExpenditureId()!=null&&grievanceListingDto.getExpenditureId()>0){
            //Get Grievance by Expenditure
            List<Integer> tankData = surveyRepositoy.getTankIdsByExpenditureId(grievanceListingDto.getExpenditureId());
            tankIds.addAll(tankData);
        }
        return grievanceRepositoryImpl.getAllGrievance2(grievanceListingDto,tankIds);
    }

    @Override
    public List<GrievanceStatusDto> getGrievanceStatus(){
        return grievanceRepositoryImpl.getGrievanceStatus();
    }

}
