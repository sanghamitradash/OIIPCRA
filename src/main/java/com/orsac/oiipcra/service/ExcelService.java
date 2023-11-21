package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.ExpenditureInfo;
import com.orsac.oiipcra.bindings.SurveyListRequest;
import com.orsac.oiipcra.bindings.TankSurveyInfoResponse;
import com.orsac.oiipcra.dto.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ExcelService {

    void exportSurveyTankExcel(HttpServletResponse exportResponse, SurveyListRequest surveyListRequest) throws IOException;

    List<TankViewDto> downloadSurveyTankExcel(HttpServletResponse exportResponse, SurveyListRequest surveyListRequest) throws IOException;

    List<ContractDto> getContractList(int id, int userId);

    List<TankSurveyInfoResponse> getSurveyList(int id, int userId);

    List<ExpenditureInfo> getExpenditureList(int id, int userId);
    List<TestDto> getData(MultipartFile file) throws IOException;
    List<MasterDto> getMasterData(MultipartFile file) throws IOException;
}
