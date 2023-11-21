package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.dto.CatchmentDetailsDto;
import com.orsac.oiipcra.dto.DprInformationDto;
import com.orsac.oiipcra.dto.PaniPanchayatDetailsDto;
import com.orsac.oiipcra.dto.SoilDetailsDto;
import com.orsac.oiipcra.entities.TankOtherDetails;
import com.orsac.oiipcra.dto.HydrologyDataDto;
import com.orsac.oiipcra.dto.RiverBasinDto;
import com.orsac.oiipcra.entities.HydrologyDataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface HydrologyRepository extends JpaRepository <TankOtherDetails,Integer>{
}
