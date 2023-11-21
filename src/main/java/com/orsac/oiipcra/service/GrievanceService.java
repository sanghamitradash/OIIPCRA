package com.orsac.oiipcra.service;

import com.orsac.oiipcra.dto.GrievanceListingDto;
import com.orsac.oiipcra.dto.GrievanceDto;
import com.orsac.oiipcra.dto.GrievanceFileDto;
import com.orsac.oiipcra.dto.GrievanceStatusDto;
import com.orsac.oiipcra.entities.GrievanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

//@Slf4j
@Service
public interface GrievanceService {

    GrievanceEntity createGrivance(GrievanceFileDto grievanceFileDto);
    GrievanceEntity updateGrivance(GrievanceEntity grievanceEntity);

    GrievanceDto getGrievanceId(Integer id);

    Page<GrievanceDto> getAllGrievance(GrievanceListingDto grievanceListingDto);

    List<GrievanceStatusDto> getGrievanceStatus();



}
