package com.orsac.oiipcra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orsac.oiipcra.bindings.SurveyListRequest;
import com.orsac.oiipcra.bindings.TankInfo;
import com.orsac.oiipcra.dto.ExpenditureMasterDto;
import com.orsac.oiipcra.helper.ExcelExporter;
import com.orsac.oiipcra.service.ReportService;
import com.orsac.oiipcra.serviceImpl.SurveyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private SurveyServiceImpl surveyServiceImpl;


    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam(name = "finYearId", required = false) Integer finYearId) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=report_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Map<String, Object>> reportList = reportService.getReport(finYearId);

        ExcelExporter excelExporter = new ExcelExporter();

        excelExporter.export(response,reportList);
    }

    @PostMapping("/downloadSurveyDetailExcel")
    public void downloadSurveyDetailExcel(HttpServletResponse response,
                                          @RequestParam String data) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String filename = "survey.xlsx";

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=report_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ObjectMapper objectMapper = new ObjectMapper();
        SurveyListRequest surveyListRequest = objectMapper.readValue(data, SurveyListRequest.class);

        Page<TankInfo> downloadSurveyList = surveyServiceImpl.getTankList(surveyListRequest);
        List<TankInfo> surveyTank=downloadSurveyList.getContent();

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadSurvey(response, surveyTank);
    }
}
