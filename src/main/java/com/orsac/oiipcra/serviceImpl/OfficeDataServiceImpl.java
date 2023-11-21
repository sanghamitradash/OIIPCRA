package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.UserAreraInfo;
import com.orsac.oiipcra.dto.BlockBoundaryDto;
import com.orsac.oiipcra.dto.OfficeDataDto;
import com.orsac.oiipcra.dto.UserAreaMappingDto;
import com.orsac.oiipcra.entities.OfficeDataEntity;
import com.orsac.oiipcra.repository.OfficeDataRepository;
import com.orsac.oiipcra.repository.OfficeDataSaveRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.service.OfficeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfficeDataServiceImpl implements OfficeDataService {
    @Autowired
    UserQueryRepository userQueryRepository;
    @Autowired
    OfficeDataRepository officeDataRepository;
    @Autowired
    OfficeDataSaveRepository officeDataSaveRepository;
    @Override
    public List<OfficeDataDto> getOfficeData(Integer distId, Integer divisionId,Integer userId) {
        Integer userLevel=userQueryRepository.getUserLevelIdByUserId(userId);
        List<UserAreaMappingDto> userAuth=userQueryRepository.getUserAuthority(userId);
        if (userLevel == 1) {
            distId = 0;
        }
        else if(userLevel == 2){
            distId=userAuth.get(0).getDistrict_id();
        }

        else if(userLevel == 6){
            distId=-1;
            divisionId=userAuth.get(0).getDivision_id();
        }
      else{
          distId=-1;
          divisionId=-1;
        }


        return officeDataRepository.getOfficeData(distId,divisionId,userId);
    }

    @Override
    public List<OfficeDataDto> getOfficeDataDetails(Integer distId, Integer divisionId, Integer userId, List<Integer> disIds) {
        return officeDataRepository.getOfficeDataDetails(distId,divisionId,userId,disIds);
    }

    @Override
    public OfficeDataEntity saveOfficeData(OfficeDataEntity officeData) {
/*        List<OfficeDataEntity> office=new ArrayList<>();
        if(userLevelId==6){
            List<UserAreraInfo> list=userQueryRepository.getAreaInfo(userId);
            for(int i=0;i<list.size();i++){
                officeData.setDistId(list.get(i).getDistId());
                officeData.setDivisionId(list.get(i).getDivisionId());
                office.add(officeData);
            }
        }
        if(userLevelId==6){
            List<UserAreraInfo> list=userQueryRepository.getAreaInfo(userId);
            for(int i=0;i<list.size();i++){
                officeData.setDistId(list.get(i).getDistId());
                office.add(officeData);
            }
        }*/

        if (officeDataSaveRepository.existsByDivisionId(officeData.getDivisionId())){

            OfficeDataEntity officeDataEntity = officeDataSaveRepository.findByDivisionId(officeData.getDivisionId());

            officeDataEntity.setOfficeName(officeData.getOfficeName());

            officeDataEntity.setHeadOfOffice(officeData.getHeadOfOffice());

            officeDataEntity.setHeadOfDept(officeData.getHeadOfDept());

            officeDataEntity.setDesignation(officeData.getDesignation());

            officeDataEntity.setSpuAddress(officeData.getSpuAddress());

            officeDataEntity.setSpuPost(officeData.getSpuPost());

            officeDataEntity.setSpuEmail(officeData.getSpuEmail());

            officeDataEntity.setLandLineNo(officeData.getLandLineNo());

            officeDataEntity.setSpuPinNo(officeData.getSpuPinNo());

            officeDataEntity.setDivisionId(officeData.getDivisionId());

            officeDataEntity.setDistId(officeData.getDistId());

            officeDataEntity.setActive(officeData.isActive());

            officeDataEntity.setCreatedBy(officeData.getCreatedBy());

            return officeDataSaveRepository.save(officeDataEntity);

        }
        else {
            return officeDataSaveRepository.save(officeData);

        }


        //return officeDataSaveRepository.save(officeData);
    }

    @Override
    public Integer getDivisionId(Integer userId) {
        return officeDataRepository.getDivisionId(userId);
    }

    @Override
    public List<Integer> getDistId(Integer userId) {
        return officeDataRepository.getDistId(userId);
    }

    @Override
    public Integer getDivisionIdByDistId(Integer distId) {
        return officeDataRepository.getDivisionIdByDistId(distId);
    }


}
