package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.dto.BlockBoundaryDto;
import com.orsac.oiipcra.dto.OfficeDataDto;

import java.util.List;

public interface OfficeDataRepository {
    List<OfficeDataDto> getOfficeData(Integer distId, Integer divisionId,Integer userId);
    Integer getDivisionId(Integer userId);
    List<Integer> getDistId(Integer userId);

    Integer getDivisionIdByDistId(Integer distId);

    List<OfficeDataDto> getOfficeDataDetails(Integer distId, Integer divisionId, Integer userId, List<Integer> disIds);
}
