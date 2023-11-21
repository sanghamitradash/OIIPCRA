package com.orsac.oiipcra.controller;


import com.amazonaws.util.IOUtils;
import com.orsac.oiipcra.bindings.*;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.helper.ExcelExporter;
import com.orsac.oiipcra.helper.ExcelHelper;
import com.orsac.oiipcra.repository.ActivityQryRepository;
import com.orsac.oiipcra.repositoryImpl.GraphRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.repositoryImpl.TenderRepositoryImpl;
import com.orsac.oiipcra.service.AWSS3StorageService;
import com.orsac.oiipcra.service.ExcelService;
import com.orsac.oiipcra.service.ExpenditureService;
import com.orsac.oiipcra.service.SurveyService;
import com.orsac.oiipcra.serviceImpl.MasterServiceImpl;
import lombok.extern.slf4j.Slf4j;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFFont;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/excel")
@Slf4j
public class ExcelController {
    @Autowired
    private MasterServiceImpl masterServiceImpl;
    @Autowired
    private TenderRepositoryImpl tenderRepositoryImpl;


    @Autowired
    private ExcelService excelService;
    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    ExpenditureService expenditureService;
    @Autowired
    private ActivityQryRepository activityQryRepository;

    @Autowired
    private TenderRepositoryImpl tenderRepository;

    @Autowired
    AWSS3StorageService awss3StorageService;
    @Autowired
    GraphRepositoryImpl graphRepositoryImpl;

//    @Autowired
//    private XSSFWorkbook workbook;

//    @Autowired
//    private XSSFSheet sheet;

    @PostMapping("/exportSurveyTankExcel")
    public void exportSurveyTankExcel(HttpServletResponse exportResponse,
                                      @RequestBody SurveyListRequest surveyListRequest) {
        exportResponse.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=tank_" + currentDateTime + ".xlsx";
        exportResponse.setHeader(headerKey, headerValue);
        try {
            excelService.exportSurveyTankExcel(exportResponse, surveyListRequest);
        } catch (Exception e) {
            log.info("Error in exportpdf" + e.getMessage());
        }
    }

    @PostMapping("/downloadSurveyTankExcel")
    public void downloadSurveyTankExcel(HttpServletResponse exportResponse,
                                        @RequestBody SurveyListRequest surveyListRequest) {
        exportResponse.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String[] HEADERs = {"id", "projectId", "Description"};
        String headerValue = "attachment; filename=tank_" + currentDateTime + ".xlsx";
        exportResponse.setHeader(headerKey, headerValue);

        try {

            Workbook workBook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workBook.createSheet(headerValue);

            //header
            Row headerRow = sheet.createRow(0);
            List<TankViewDto> dto = excelService.downloadSurveyTankExcel(exportResponse, surveyListRequest);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            int rowIdx = 1;
            for (TankViewDto data : dto) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(data.getTank_id());
                row.createCell(2).setCellValue(data.getProject_id());
            }
            workBook.write(out);
//            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            log.info("Error in exportpdf " + e.getMessage());
        }
    }

    @PostMapping("/downloadContractReport")
    public void downloadContractReport(HttpServletResponse response,@RequestBody ContractListRequestDto contractListRequestDto) throws IOException, ParseException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ContractDetailExcel.xlsx";
        response.setHeader(headerKey, headerValue);

//        List<ContractDto> contractList = excelService.getContractList(id, userId);
        Page<ContractInfoListing> contractListPage = surveyService.getContractList(contractListRequestDto);
        List<ContractInfoListing> contract=new ArrayList<>();

        for(ContractInfoListing contractListPage1:contractListPage){
            List<ContractMappingDto> contractMapping=surveyService.getContractMapping(contractListPage1.getContractId());
            String tankName1 = "";
            for (int j = 0; j < contractMapping.size(); j++) {
                if (contractMapping.get(j).getTankName() != null) {
                    if (j > 0) {
                        tankName1 += ",";
                    }
                    tankName1 += contractMapping.get(j).getTankName();

                }
            }
            String noticeId1 = "";
            List<NoticeListingDto> noticeId=tenderRepository.getTenderNoticeByTenderId(contractListPage1.getTenderId());
            for (int j = 0; j < noticeId.size(); j++) {
                if (noticeId.get(j).getId() != null) {
                    if (j > 0) {
                        noticeId1 += ",";
                    }
                    noticeId1 += noticeId.get(j).getId();
                }
            }
            String bidId="";
            for (int j = 0; j < contractMapping.size(); j++) {
                if (contractMapping.get(j).getBidId() != null) {
                    if(!bidId.contains(contractMapping.get(j).getBidId())) {
                        if (j > 0) {
                            bidId += ",";
                        }

                        bidId += contractMapping.get(j).getBidId();
                    }
                }
            }
            contractListPage1.setNoticeId(noticeId1);
            contractListPage1.setTankName(tankName1);
            contractListPage1.setBidId(bidId);
            contract.add(contractListPage1);

        }

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadContract(response, contract);
    }


    @PostMapping("/downloadSurveyReport")
    public void downloadSurveyReport(HttpServletResponse response,@RequestBody SurveyListRequest surveyListRequest) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=SurveyDetailExcel.xlsx";
        response.setHeader(headerKey, headerValue);

       // List<TankSurveyInfoResponse> surveyList = excelService.getSurveyList(id, userId);
        List<Integer> tankIdsByIssueId = new ArrayList<>();
        List<Integer> tankIdsByContractId = new ArrayList<>();
        List<Integer> tankIdsByEstimateId = new ArrayList<>();

        if( surveyListRequest.getIssueId()!=null && surveyListRequest.getIssueId()>0 ){
            tankIdsByIssueId = surveyRepositoy.getTankIdsByIssueId(surveyListRequest.getIssueId());
        }

        if( surveyListRequest.getContractId()!=null && surveyListRequest.getContractId()>0 ){
            tankIdsByContractId = surveyRepositoy.getTankIdsByContractId(surveyListRequest.getContractId());
        }

        if( surveyListRequest.getEstimateId()!=null && surveyListRequest.getEstimateId()>0 ){
            tankIdsByEstimateId = surveyRepositoy.getTankIdsByEstimateId(surveyListRequest.getEstimateId());
        }
        Page<TankSurveyInfoResponse> surveyListPage = surveyRepositoy.getTankSurveySearchList(surveyListRequest,tankIdsByIssueId,tankIdsByContractId,tankIdsByEstimateId);
        List<TankSurveyInfoResponse> surveyList = surveyListPage.getContent();

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadSurveyList(response, surveyList);
    }

    @PostMapping("/downloadExpenditureReport")
    public void downloadExpenditureReport(HttpServletResponse response,@RequestBody ExpenditureListDto expenditureListDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ExpenditureDetailExcel.xlsx";
        response.setHeader(headerKey, headerValue);

       // List<ExpenditureInfo> ExpenditureList = excelService.getExpenditureList(id, userId);
        Page<ExpenditureInfo> expenditureListPage = expenditureService.getExpenditureList(expenditureListDto);
        List<ExpenditureInfo> expenditureList = expenditureListPage.getContent();
        for (ExpenditureInfo item1 : expenditureList) {
            List<ActivityUpperHierarchyInfo> currentHierarchyInfoById = activityQryRepository.getUpperHierarchyInfoById(item1.getActivityId());
            String code = "";
            for (ActivityUpperHierarchyInfo item : currentHierarchyInfoById) {
                code += item.getCode();
                if (!code.isEmpty() && item.getMasterHeadId() <= 1) {
                    code = code + ".";
                }
            }
            item1.setCode(code);
        }

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadExpenditureList(response, expenditureList);
    }
    @PostMapping("/downloadAdaptFinancialProgressReport")
    public void downloadAdaptFinancialProgressReport(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=FinancialDetailExcel.xlsx";
        response.setHeader(headerKey, headerValue);
        Page<AdaptFinancialDto> adaptData = masterServiceImpl.getOiipcraDenormalizedFinancialAchievementDataList(adaptDto);
        List<AdaptFinancialDto> adaptFinancialProgressList = adaptData.getContent();

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadFinancialProgressList(response, adaptFinancialProgressList);
    }
    @PostMapping("/downloadAdaptPhysicalProgressReport")
    public void downloadAdaptPhysicalProgressReport(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PhysicalDetailExcel.xlsx";
        response.setHeader(headerKey, headerValue);
        Page<AdaptPhysicalDto> adaptData = masterServiceImpl.getOiipcraDenormalizedPhysicalAchievementDataList(adaptDto);
        List<AdaptPhysicalDto> adaptPhysicalProgressList = adaptData.getContent();

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadPhysicalProgressList(response, adaptPhysicalProgressList);
    }
    @PostMapping("/downloadCropIntensityReport")
    public void downloadCropIntensityReport(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=CropIntensityExcel.xlsx";
        response.setHeader(headerKey, headerValue);
        List<CropCycleAyacutDto> allData=new ArrayList<>();
        List<TankInfo> tank=graphRepositoryImpl.getAllReserviorTank();
        for(TankInfo t:tank) {
            List<CropCycleAyacutDto> crop = graphRepositoryImpl.getCropCycleIntensityProjectWise(t.getProjectId());
            allData.addAll(crop);
        }

     System.out.println(allData.size());
        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadCropIntensityReport(response, allData);
        System.out.println("Success");
    } @PostMapping("/downloadAdaptFinancialProgressAbstractReport")
    public void downloadAdaptFinancialProgressAbstractReport(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=FinancialDetailAbstractExcel.xlsx";
        response.setHeader(headerKey, headerValue);
        List<AdaptFinancialDto> adaptData = masterServiceImpl.getOiipcraDenormalizedFinancialAchievementAbstractData(adaptDto);


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadFinancialProgressAbstractReport(response, adaptData);
    }


//sheet = workbook.createSheet("SurveyTankExcel");
//        Row row = sheet.createRow(0);
//        CellStyle style = workbook.createCellStyle();
//        XSSFFont font = workbook.createFont();
//        font.setBold(true);
//        font.setFontHeight(16);
//        style.setFont(font);


 /*   @PostMapping("/readDetailsFromExcel")
    public OIIPCRAResponse insertAssetFromExcel(@RequestParam("file") MultipartFile file) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        String message = "";
        try {
            if (ExcelHelper.hasExcelFormat(file))
                try {

                    File f=new ClassPathResource("/OIIPCRA_Reqs.xlsx").getFile();
                    FileInputStream input = new FileInputStream(f);
                    MultipartFile multipartFile = new MockMultipartFile("master",
                            f.getName(), "text/plain", IOUtils.toByteArray(input));
                    MultipartFile multipartFile1 = new MockMultipartFile("master.xlsx", new FileInputStream(new ClassPathResource("pdf-resources/Master.xlsx").getFile()));
                    List<MasterDto> getMasterData=excelService.getMasterData(multipartFile1);
                    System.out.println(getMasterData.size());
                    List<TestDto> data = excelService.getData(file);
                    System.out.println(data.size());

                    if (data != null) {
                        message = "Data Fetched successfully: " + file.getOriginalFilename();
                        result.put("DataList", data.get(0));
                       // result.put("errorList", data.get(1));
                        response.setStatus(1);
                        response.setMessage(message);
                        response.setData(result);
                        response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                    } else {
                        message = "Excel Mismatch: " + file.getOriginalFilename();
                        result.put("contractList", data.get(0));
                       // result.put("errorList", data.get(1));
                        response.setStatus(0);
                        response.setMessage(message);
                        response.setData(result);
                        response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message = e.getMessage() + "Data Fetched unsuccessfully: " + file.getOriginalFilename() + "!";
                    response.setStatus(0);
                    response.setMessage(message);
                    response.setData(result);
                    response.setStatusCode(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

                }

        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
*/

    @PostMapping("/downloadExcelForPhysicalProgress")
    public void downloadExcelForPhysicalProgress(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PhysicalProgress.xlsx";
        response.setHeader(headerKey, headerValue);

        List<AdaptPhysicalDto> physicalDto = masterServiceImpl.downloadExcelForPhysicalProgress(adaptDto);

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadPhysicalProgress(response, physicalDto);
    }
    @PostMapping("/downloadActivityWiseFinancialProgressReport")
    public void downloadActivityWiseFinancialProgressReport(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ActivityWiseFinancialDetails.xlsx";
        response.setHeader(headerKey, headerValue);
        List<AdaptFinancialDto> activityWiseFinancialProgressList = masterServiceImpl.getOiipcraActivityWiseFinancialAchievementDataList(adaptDto);


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadActivityWiseFinancialProgressList(response, activityWiseFinancialProgressList);
    }


    @PostMapping("/activityWisePhysicalProgressExcelReport")
    public void activityWisePhysicalProgressExcelReport(HttpServletResponse response,@RequestBody AdaptFilterDto adaptDto) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ActivityWisePhysicalProgress.xlsx";
        response.setHeader(headerKey, headerValue);

        List<AdaptPhysicalDto> activityWisePhysicalProgress = masterServiceImpl.activityWisePhysicalProgressExcelReport(adaptDto);

        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.activityWisePhysicalProgress(response, activityWisePhysicalProgress);
    }
    @PostMapping("/downloadExcelNoticeData")
    public void downloadExcelNoticeData(HttpServletResponse response,@RequestParam Integer tenderId) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:MM:SS");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=tenderNoticeData.xlsx";
        response.setHeader(headerKey, headerValue);
        List<NoticeListingDto> tenderNotices = tenderRepositoryImpl.getTenderNoticeDataByTenderId(tenderId);
        List<NoticeListingDto> allNotice = new ArrayList<>();
        for (NoticeListingDto notice : tenderNotices) {
            String tankName = "";
            String distName = "";
            String blockName = "";
            List<NoticeLevelMappingDto> noticeListing = tenderRepositoryImpl.getTenderLevelByNoticeId(notice.getId());
            for (int j = 0; j < noticeListing.size(); j++) {
                if (noticeListing.get(j).getDistName() != null) {
                    if (!distName.contains(noticeListing.get(j).getDistName())) {
                        if (j > 0) {
                            distName += ",";
                        }

                        distName += noticeListing.get(j).getDistName();
                    }
                }
                if (noticeListing.get(j).getBlockName() != null) {
                    if (!blockName.contains(noticeListing.get(j).getBlockName())) {
                        if (j > 0) {
                            blockName += ",";
                        }

                        blockName += noticeListing.get(j).getBlockName();
                    }
                }
                if (noticeListing.get(j).getTankName() != null) {
                    if (!tankName.contains(noticeListing.get(j).getTankName())) {
                        if (j > 0) {
                            tankName += ",";
                        }

                        tankName += noticeListing.get(j).getTankName();
                    }
                }
                notice.setDistName(distName);
                notice.setBlockName(blockName);
                notice.setTankName(tankName);
                allNotice.add(notice);
            }
        }


        ExcelExporter excelExporter = new ExcelExporter();
        excelExporter.downloadExcelNoticeDataList(response, tenderNotices);
    }

}