package com.orsac.oiipcra.service;


import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.bindings.SurveyListRequest;
import com.orsac.oiipcra.dto.*;
import org.springframework.data.domain.Page;
import java.util.List;

public interface TankSurveyService {

    List<BoundaryDto> getUserListByUserId(int id);
}
