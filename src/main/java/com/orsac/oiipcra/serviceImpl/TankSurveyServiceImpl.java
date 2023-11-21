package com.orsac.oiipcra.serviceImpl;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.service.TankSurveyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TankSurveyServiceImpl implements TankSurveyService {

    @Autowired
    private SurveyRepositoyImpl tankSurveyRepository;

    @Override
    public List<BoundaryDto> getUserListByUserId(int id) {
        Integer userLevelId = tankSurveyRepository.getUserLevelIdByUserId(id);
        List<BoundaryDto> userList = tankSurveyRepository.getUserListByUserLevelId(userLevelId);
        return userList;
    }


}

