package com.orsac.oiipcra.service;

import com.orsac.oiipcra.dto.BlockBoundaryDto;
import com.orsac.oiipcra.dto.OfficeDataDto;
import com.orsac.oiipcra.entities.OfficeDataEntity;

import java.util.List;

public interface OfficeDataService {
    List<OfficeDataDto> getOfficeData(Integer distId, Integer divisionId,Integer userId);
    List<OfficeDataDto> getOfficeDataDetails(Integer distId, Integer divisionId,Integer userId,List<Integer>disIds);

    OfficeDataEntity saveOfficeData(OfficeDataEntity officeData);
    Integer getDivisionId(Integer userId);



    Integer getDivisionIdByDistId(Integer distId);

    List<Integer> getDistId(Integer userId);
}
