package com.orsac.oiipcra.controller;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.*;

import com.orsac.oiipcra.dto.AdaptPhysicalDto;
import com.orsac.oiipcra.dto.AdaptFilterDto;
import com.orsac.oiipcra.dto.AdaptFinancialDto;
import com.orsac.oiipcra.dto.AdaptPhysicalDto;
import com.orsac.oiipcra.helper.ExcelExporter;
import com.orsac.oiipcra.service.ExcelService;
import com.orsac.oiipcra.serviceImpl.MasterServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/fisheriesExcel")
@Slf4j
public class FisheriesExcelController {
    @Autowired
    private MasterServiceImpl masterServiceImpl;

    @Autowired
    private ExcelService excelService;


    @PostMapping("/downloadAdaptFisheriesExcelReport")
    public void downloadAdaptFisheriesExcelReport(HttpServletResponse response, @RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=FisheriesDetailAbstractExcel.xlsx";
        response.setHeader(headerKey, headerValue);
        List<AdaptFinancialDto> adaptData = masterServiceImpl.getOiipcraFisheriesAbstractData(adaptDto);


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadFisheriesAbstractReport(response, adaptData);
    }



    //Excel for All Data with Filter

    @PostMapping("/downloadFisheriesReport")
    public void downloadFisheriesReport(HttpServletResponse response, @RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=FisheriesDetailExcel.xlsx";
        response.setHeader(headerKey, headerValue);

        List<AdaptFinancialDto> fisheriesReport = masterServiceImpl.downloadFisheriesReport(adaptDto);


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadFisheriesReportList(response, fisheriesReport);
    }


    //Excel for sum of Fisheries Data

    @PostMapping("/downloadForFisheriesExcelReport")
    public void downloadForFisheriesExcelReport(HttpServletResponse response, @RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=FisheriesDetailExcelReport.xlsx";
        response.setHeader(headerKey, headerValue);

        List<AdaptFinancialDto> fisheriesExcelReport = masterServiceImpl.downloadForFisheriesExcelReport(adaptDto);


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadFisheriesExcelReportList(response, fisheriesExcelReport);
    }


    @PostMapping("/getOiipcraFardPhysicalAchievementList")
    public OIIPCRAResponse getOiipcraFardPhysicalAchievementList(@RequestBody AdaptFilterDto adaptDto)
    {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<FardPhysicalAchievementDto> adaptPhysicalAchievementData = masterServiceImpl.getOiipcraFardPhysicalAchievementList(adaptDto);
            List<FardPhysicalAchievementDto> adaptPhysicalAchievementList = adaptPhysicalAchievementData.getContent();

            result.put("adaptPhysicalAchievementList", adaptPhysicalAchievementList);
            result.put("currentPage", adaptPhysicalAchievementData.getNumber());
            result.put("totalItems", adaptPhysicalAchievementData.getTotalElements());
            result.put("totalPages", adaptPhysicalAchievementData.getTotalPages());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("FardPhysicalAchievementList");

        }catch (Exception e){
            log.info("Fard List Exception : {}", e.getMessage());
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }



    @PostMapping("/activityWiseFardPhysicalAchievementExcelReport")
    public void activityWiseFardPhysicalAchievementExcelReport(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=FardPhysicalAchievementList.xlsx";
        response.setHeader(headerKey, headerValue);

        List<FardPhysicalAchievementDto> adaptPhysicalAchievementList = masterServiceImpl.activityWiseFardPhysicalAchievementExcelReport(adaptDto);

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.activityWiseAdaptPhysicalAchievement(response, adaptPhysicalAchievementList);
    }
    @PostMapping("/downloadFardPhysicalAchievementAbstractReport")
    public void downloadFardPhysicalAchievementAbstractReport(HttpServletResponse response, @RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PhysicalAchievementAbstractExcel.xlsx";
        response.setHeader(headerKey, headerValue);
        List<AdaptPhysicalDto> adaptData = masterServiceImpl.getOiipcraPhysicalAchievementReport(adaptDto);


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadPhysicalAchievementAbstractReport(response, adaptData);
    }

}

