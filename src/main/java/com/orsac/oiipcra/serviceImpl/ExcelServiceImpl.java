package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.ExpenditureInfo;
import com.orsac.oiipcra.bindings.SurveyListRequest;
import com.orsac.oiipcra.bindings.TankSurveyInfo;
import com.orsac.oiipcra.bindings.TankSurveyInfoResponse;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.helper.ExcelHelper;
import com.orsac.oiipcra.repositoryImpl.SurveyRepositoyImpl;
import com.orsac.oiipcra.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Autowired
    private ExcelHelper excelHelper;

    private XSSFWorkbook workbook = new XSSFWorkbook();
    private XSSFSheet sheet = workbook.createSheet("SurveySearchList");;

   /* public ExcelServiceImpl(List<User> listUsers) {
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
    }*/

    @Autowired
    private SurveyRepositoyImpl surveyRepositoy;

    private void writeHeaderRow() {
        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("UserID");

        Cell cell1 = row.createCell(1);
        cell1.setCellValue("UserName");

        Cell cell2 = row.createCell(2);
        cell2.setCellValue("UserPassword");

        Cell cell3 = row.createCell(3);
        cell3.setCellValue("Address");


        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        //style.setFont(font);
        //createCell(row, 0, "ID", style);
        //createCell(row, 1, "Tank ID", style);
        //createCell(row, 2, "Project Id", style);
        //createCell(row, 3, "Catchment Area", style);
        //createCell(row, 4, "CCA Kharif", style);
        /*createCell(row, 5, "CCA Rabi", style);
        createCell(row, 6, "Water Spread Area", style);
        createCell(row, 7, "Tank Water Level Max", style);
        createCell(row, 8, "Tank Water Level Min", style);
        createCell(row, 9, "Turbidity", style);
        createCell(row, 10, "Solar Pump Installed", style);
        createCell(row, 11, "Aquatic Vegetation Cover", style);
        createCell(row, 12, "Get Status of Tank", style);
        createCell(row, 13, "No. of Beneficiary", style);
        createCell(row, 14, "Recharge Shaft Installation", style);
        createCell(row, 15, "No of Recharge Shaft Installed", style);
        createCell(row, 16, "Embankment", style);
        createCell(row, 17, "Usage", style);
        createCell(row, 18, "Training Conducted", style);
        createCell(row, 19, "No of Trainee", style);
        createCell(row, 20, "Longitude Surveyed", style);
        createCell(row, 21, "Latitude Surveyed", style);
        createCell(row, 22, "Altitude", style);
        createCell(row, 23, "Accuracy", style);
        createCell(row, 24, "Surveyed By Dept", style);*/
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataRows(SurveyListRequest surveyListRequest) {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        /*Page<TankSurveyInfoResponse> tankListPage = surveyRepositoy.getTankSurveySearchList(surveyListRequest);
        List<TankSurveyInfoResponse> tankList = tankListPage.getContent();
        for (int i=0; i< tankList.size(); i++) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, tankList.get(i).getId(), style);
            createCell(row, columnCount++, tankList.get(i).getTankId(), style);
            createCell(row, columnCount++, tankList.get(i).getProjectId(), style);
            createCell(row, columnCount++, tankList.get(i).getCatchmentArea(), style);
            createCell(row, columnCount++, tankList.get(i).getCcaKharif(), style);
            createCell(row, columnCount++, tankList.get(i).getCcaRabi(), style);
            createCell(row, columnCount++, tankList.get(i).getWaterSpreadArea(), style);
            createCell(row, columnCount++, tankList.get(i).getTankWaterLevelMax(), style);
            createCell(row, columnCount++, tankList.get(i).getTankWaterLevelMin(), style);
            createCell(row, columnCount++, tankList.get(i).getGroundWaterLevel(), style);
            createCell(row, columnCount++, tankList.get(i).getTurbidity(), style);
            createCell(row, columnCount++, tankList.get(i).isSolarPumpInstalled(), style);
            createCell(row, columnCount++, tankList.get(i).getAquaticVegetationCover(), style);
            createCell(row, columnCount++, tankList.get(i).getStatusOfTank(), style);
            createCell(row, columnCount++, tankList.get(i).getNoOfBeneficiary(), style);
            createCell(row, columnCount++, tankList.get(i).isRechargeShaftInstallation(), style);
            createCell(row, columnCount++, tankList.get(i).getNoOfRechargeShaftInstalled(), style);
            createCell(row, columnCount++, tankList.get(i).isEmbankment(), style);
            createCell(row, columnCount++, tankList.get(i).getUsage(), style);
            createCell(row, columnCount++, tankList.get(i).isTrainingConducted(), style);
            createCell(row, columnCount++, tankList.get(i).getNoOfTrainee(), style);
            createCell(row, columnCount++, tankList.get(i).getLongitudeSurveyed(), style);
            createCell(row, columnCount++, tankList.get(i).getLatitudeSurveyed(), style);
            createCell(row, columnCount++, tankList.get(i).getAltitude(), style);
            createCell(row, columnCount++, tankList.get(i).getAccuracy(), style);
            createCell(row, columnCount++, tankList.get(i).isSurveyedByDept(), style);
            *//*createCell(row, columnCount++, tankList.get(i).getProgressStatus(), style);
            createCell(row, columnCount++, tankList.get(i).getApprovedBy(), style);
            createCell(row, columnCount++, tankList.get(i).getApprovedOn(), style);
            createCell(row, columnCount++, tankList.get(i).getSurveyedBy(), style);
            createCell(row, columnCount++, tankList.get(i).getSurveyorImage(), style);
            createCell(row, columnCount++, tankList.get(i).getTrainingImage(), style);*//*


        }*/
        /*for (int i=0; i<=25; i++) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, "ABC",style);
            *//*createCell(row, columnCount++, "CYH",style);
            createCell(row, columnCount++, "dfr",style);
            createCell(row, columnCount++, "gfed",style);
            createCell(row, columnCount++, "dfdrer",style);*//*
        }*/
    }


    @Override
    public void  exportSurveyTankExcel(HttpServletResponse exportResponse, SurveyListRequest surveyListRequest) throws IOException {
        writeHeaderRow();
        writeDataRows(surveyListRequest);

        ServletOutputStream outputStream = exportResponse.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

    @Override
    public List<TankViewDto> downloadSurveyTankExcel(HttpServletResponse exportResponse, SurveyListRequest surveyListRequest) throws IOException {
        writeHeaderRow();
        writeDataRows(surveyListRequest);

        List<TankViewDto> dto = surveyRepositoy.downloadSurveyTankExcel(surveyListRequest);
        ServletOutputStream outputStream = exportResponse.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
        return dto;
    }

    @Override
    public List<ContractDto> getContractList(int id, int userId) {
        return surveyRepositoy.getContractListForExcel(id, userId);
    }

    @Override
    public List<TankSurveyInfoResponse> getSurveyList(int id, int userId) {
        return surveyRepositoy.getSurveyListForExcel(id, userId);
    }

    @Override
    public List<ExpenditureInfo> getExpenditureList(int id, int userId) {
        return surveyRepositoy.getExpenditureListForExcel(id, userId);
    }

    @Override
    public List<TestDto> getData(MultipartFile file) throws IOException {
        List<TestDto> data=excelHelper.getExcelData(file.getInputStream());
        return data;
    }
    @Override
    public List<MasterDto> getMasterData(MultipartFile file) throws IOException {
        List<MasterDto> data=excelHelper.getMasterData(file.getInputStream());
        return data;
    }
}


//    int rowCount = 1;
//
//    CellStyle style = workbook.createCellStyle();
//    XSSFFont font = workbook.createFont();
//            font.setFontHeight(14);
//                    style.setFont(font);
//
//                    for (ExamRecord record : listRecords) {
//                    Row row = sheet.createRow(rowCount++);
//                    int columnCount = 0;
//
//                    createCell(row, columnCount++, record.getId(), style);
//                    createCell(row, columnCount++, record.getStudentName(), style);
//                    createCell(row, columnCount++, record.getExamYear(), style);
//                    createCell(row, columnCount++, record.getScore(), style);
//
//                    }
