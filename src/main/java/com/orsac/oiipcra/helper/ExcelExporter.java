package com.orsac.oiipcra.helper;

import com.orsac.oiipcra.bindings.ContractInfoListing;
import com.orsac.oiipcra.bindings.ExpenditureInfo;
import com.orsac.oiipcra.bindings.TankInfo;
import com.orsac.oiipcra.bindings.TankSurveyInfoResponse;
import com.orsac.oiipcra.dto.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    //private List<User> listUsers;

    public ExcelExporter() {

        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(10);
        style.setFont(font);

        createCell(row, 0, "Activity ID", style);
        createCell(row, 1, "Activity Code", style);
        createCell(row, 2, "Activity Name", style);
        createCell(row, 3, "Unit", style);
        createCell(row, 4, "Unit Cost (Rs.)", style);

    }
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if(value instanceof BigDecimal){
            cell.setCellValue((String) value);
        }
        else if(value instanceof Double){
            cell.setCellValue((Double) value);
        }
        else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    private void writeDataLines(List<Map<String, Object>> infoList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Map<String, Object> info : infoList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, info.get("activityid").toString(), style);
            createCell(row, columnCount++, info.get("code").toString(), style);
            createCell(row, columnCount++, info.get("activityname").toString(), style);
            createCell(row, columnCount++, info.get("unit").toString(), style);
            createCell(row, columnCount++, info.get("unit_cost_rs").toString(), style);
        }
    }

    public void export(HttpServletResponse response, List<Map<String, Object>> infoList) throws IOException {
        writeHeaderLine();
        writeDataLines(infoList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

    //for download excel of contract details

    private void writeContractHeader() {
        sheet = workbook.createSheet("ContractDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "ContractId", style);
        createCell(row, 2, "Contract Number", style);
        createCell(row, 3, "Work Description", style);
        createCell(row, 4, "Contract Date", style);
        createCell(row, 5, "Contract Level", style);
        createCell(row, 6, "Contract Type", style);
        createCell(row, 7, "Contract Status", style);
        createCell(row, 8, "Agency Name", style);
        createCell(row, 9, "Contract Name", style);
        createCell(row, 10, "Work Type Name", style);
        createCell(row, 11, "Bid Id", style);
        createCell(row, 12, "Tank Name", style);
    }
    private void writeContractData(List<ContractInfoListing> contractList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;



        for (ContractInfoListing info : contractList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
          /*  Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = dateFormat.format(info.getContractDate());*/

//            total = total + Integer.valueOf(info.getCount());
            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getContractId(), style);
            createCell(row, columnCount++, info.getContractNumber(), style);
            createCell(row, columnCount++, info.getWorkDescription(), style);
            createCell(row, columnCount++, info.getContractDate(), style);
            createCell(row, columnCount++, info.getContractLevel(), style);
            createCell(row, columnCount++, info.getContractType(), style);
            createCell(row, columnCount++, info.getContractStatus(), style);
            createCell(row, columnCount++, info.getAgencyName(), style);
            createCell(row, columnCount++, info.getContractName(), style);
            createCell(row, columnCount++, info.getWorkTypeName(), style);
            createCell(row, columnCount++, info.getBidId(), style);
            createCell(row, columnCount++, info.getTankName(), style);

            slno++;
        }
        Row row = sheet.createRow(rowCount++);
//        if (deptId == 1) {
//            createCell(row, 1, "State Total Fisheries ", style);
//        } else {
//            createCell(row, 1, "State Total ARD ", style);
//        }

        createCell(row, 4, total, style);

    }
    public void downloadContract(HttpServletResponse response,List<ContractInfoListing> contractList) throws IOException {
        writeContractHeader();
        writeContractData(contractList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

    //download  excel for survey details
    private void writeSurveyHeader() {
        sheet = workbook.createSheet("SurveyDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Id", style);
        createCell(row, 2, "Tank Id", style);
        createCell(row, 3, "Project Id", style);
        createCell(row, 4, "Tank Name", style);
        createCell(row, 5, "District Name", style);
        createCell(row, 6, "Block Name", style);
        createCell(row, 7, "Grampanchayat Name", style);
        createCell(row, 8, "Village Name", style);
        createCell(row, 9, "Division Name", style);
        createCell(row, 10, "Subdivision Name", style);
        createCell(row, 11, "Section Name", style);
        createCell(row, 12, "Survey By", style);
        createCell(row, 13, "Progress Status", style);


    }
    private void writeSurveyData(List<TankSurveyInfoResponse> surveyList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (TankSurveyInfoResponse info : surveyList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getId(), style);
            createCell(row, columnCount++, info.getTankId(), style);
            createCell(row, columnCount++, info.getProjectId(), style);
            createCell(row, columnCount++, info.getTankName(), style);
            createCell(row, columnCount++, info.getDistrictName(), style);
            createCell(row, columnCount++, info.getBlockName(), style);
            createCell(row, columnCount++, info.getGpName(), style);
            createCell(row, columnCount++, info.getVillageName(), style);
            createCell(row, columnCount++, info.getDivisionName(), style);
            createCell(row, columnCount++, info.getSubDivisionName(), style);
            createCell(row, columnCount++, info.getSectionName(), style);
            createCell(row, columnCount++, info.getSurveyorName(), style);
            createCell(row, columnCount++, info.getProgressStatus(), style);


            slno++;
        }
       /* Row row = sheet.createRow(rowCount++);
        createCell(row, 4, total, style);*/

    }
    public void downloadSurveyList(HttpServletResponse response, List<TankSurveyInfoResponse> contractList) throws IOException {
        writeSurveyHeader();
        if(contractList!=null) {
            writeSurveyData(contractList);
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

    //download excel for expenditure list
    private void writeExpenditureHeader() {
        sheet = workbook.createSheet("ExpenditureDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Id", style);
        createCell(row, 2, "Work Id", style);
        createCell(row, 3, "Bid Id", style);
        createCell(row, 4, "Contract Id", style);
       /* createCell(row, 5, "Invoice No", style);
        createCell(row, 6, "Invoice Date", style);*/
        createCell(row, 5, "Contract Number", style);
        createCell(row, 6, "Agency Name", style);
        //createCell(row, 9, "Activity Id", style);
        createCell(row, 7, "Activity Name", style);
        createCell(row, 8, "Contract Amount", style);
        createCell(row, 9, "Value", style);
        createCell(row, 10, "Payment Date", style);
        createCell(row, 11, "Payment Type", style);
    }
    private void writeExpenditureData(List<ExpenditureInfo> expenditureList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (ExpenditureInfo info : expenditureList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String invoiceDate="";
            String paymentDate="";
            if(info.getInvoiceDate() != null){
                 invoiceDate = dateFormat.format(info.getInvoiceDate());
            }
            if(info.getPaymentDate() != null){
                 paymentDate = dateFormat.format(info.getPaymentDate());
            }

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getId(), style);
            createCell(row, columnCount++, info.getWorkId(), style);
            createCell(row, columnCount++, info.getBidId(), style);
            createCell(row, columnCount++, info.getContractId(), style);
           /* createCell(row, columnCount++, info.getInvoiceNo(), style);
            createCell(row, columnCount++, invoiceDate, style);*/
            createCell(row, columnCount++, info.getContractNumber(), style);
            createCell(row, columnCount++, info.getAgencyName(), style);
            /*createCell(row, columnCount++, info.getActivityId(), style);*/
            createCell(row, columnCount++, info.getActivityName(), style);
            createCell(row, columnCount++, String.valueOf(info.getContractAmount()), style);
            createCell(row, columnCount++, String.valueOf(info.getValue()), style);
            createCell(row, columnCount++, paymentDate, style);
            createCell(row, columnCount++, info.getPaymentType(), style);
            slno++;
        }
        Row row = sheet.createRow(rowCount++);
        createCell(row, 4, total, style);

    }

    public void downloadExpenditureList(HttpServletResponse response, List<ExpenditureInfo> expenditureList) throws IOException {
        writeExpenditureHeader();
        writeExpenditureData(expenditureList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }  public void downloadPhysicalProgressList(HttpServletResponse response, List<AdaptPhysicalDto>  physicalList) throws IOException {
        writePhysicalHeader();
        writePhysicalData(physicalList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    private void writeFinancialHeader() {
        sheet = workbook.createSheet("FinancialProgressDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Year", style);
        createCell(row, 2, "Directorate", style);
        createCell(row, 3, "Activity Name", style);
        createCell(row, 4, "District", style);
        createCell(row, 5, "Scheme Name", style);
        createCell(row, 6, "Financial Allocation (In Lakhs)", style);
        createCell(row, 7, "Actual Fund Allocated(In Lakhs)", style);
        createCell(row, 8, "Expenditure(In Lakhs)", style);
        createCell(row, 9, "Achievement(%)", style);
    }
    private void writeFinancialAbstractHeader() {
        sheet = workbook.createSheet("FinancialProgressAbstractDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "District", style);
        createCell(row, 3, "Financial Allocation (In Lakhs)", style);
        createCell(row, 4, "Actual Fund Allocated(In Lakhs)", style);
        createCell(row, 5, "Expenditure(In Lakhs)", style);
        createCell(row, 6, "Achievement(%)", style);
    }
    private void writeFinancialData(List<AdaptFinancialDto>  financialList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (AdaptFinancialDto info : financialList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getYear(), style);
            createCell(row, columnCount++, info.getDirectorate(), style);
            createCell(row, columnCount++, info.getActivityName(), style);
            createCell(row, columnCount++, info.getDistrictName(), style);
            createCell(row, columnCount++, info.getSchemeName(), style);
            createCell(row, columnCount++, String.valueOf(info.getFinancialAllocationInApp()), style);
            createCell(row, columnCount++, String.valueOf(info.getActualFundAllocated()), style);
            createCell(row, columnCount++, String.valueOf(info.getExpenditure()), style);
            createCell(row, columnCount++, String.valueOf(info.getPercentageAllocated()), style);

            slno++;
        }


    }
    private void writeFinancialAbstractData(List<AdaptFinancialDto>  financialList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (AdaptFinancialDto info : financialList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(), style);
            createCell(row, columnCount++, info.getDistrictName(), style);
            createCell(row, columnCount++, String.valueOf(info.getFinancialAllocationInApp()), style);
            createCell(row, columnCount++, String.valueOf(info.getActualFundAllocated()), style);
            createCell(row, columnCount++, String.valueOf(info.getExpenditure()), style);
            createCell(row, columnCount++, String.valueOf(info.getPercentageAllocated()), style);

            slno++;
        }


    }

    public void downloadFinancialProgressList(HttpServletResponse response, List<AdaptFinancialDto>  financialList) throws IOException {
        writeFinancialHeader();
        writeFinancialData(financialList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    public void downloadFinancialProgressAbstractReport(HttpServletResponse response, List<AdaptFinancialDto>  financialList) throws IOException {
        writeFinancialAbstractHeader();
        writeFinancialAbstractData(financialList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    private void writePhysicalHeader() {
        sheet = workbook.createSheet("PhysicalProgressDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Year", style);
        createCell(row, 2, "Directorate", style);
        createCell(row, 3, "District", style);
        createCell(row, 4, "Block", style);
        createCell(row, 5, "Gramapanchayat", style);
        createCell(row, 6, "Scheme Name", style);
        createCell(row, 7, "Master Component", style);
        createCell(row, 8, "Component Name", style);
        createCell(row, 9, "Activity Name", style);
        createCell(row, 10, "Unit", style);
        createCell(row, 11, "Target", style);
        createCell(row, 12, "Achievement", style);
        createCell(row, 13, "Achievement Percentage(%)", style);
        createCell(row, 14, "No Of Beneficiaries", style);



    }
    private void writePhysicalData(List<AdaptPhysicalDto>  physicalList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (AdaptPhysicalDto info : physicalList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getYear(), style);
            createCell(row, columnCount++, info.getDirectorate(), style);
            createCell(row, columnCount++, info.getDistrictName(), style);
            createCell(row, columnCount++, info.getBlockName(), style);
            createCell(row, columnCount++, info.getGpName(), style);
            createCell(row, columnCount++, info.getSchemeName(), style);
            createCell(row, columnCount++, info.getMasterComponent(), style);
            createCell(row, columnCount++, info.getComponentName(), style);
            createCell(row, columnCount++, info.getActivityName(), style);
            createCell(row, columnCount++, info.getUnitName(), style);
            createCell(row, columnCount++, String.valueOf(info.getTarget()), style);
            createCell(row, columnCount++, String.valueOf(info.getAchievement()), style);
            createCell(row, columnCount++, String.valueOf(info.getAchievementPercentage()), style);
            createCell(row, columnCount++, String.valueOf(info.getNoofBeneficiaries()), style);
            System.out.println(slno);

            slno++;
        }


    }




    //download survey excel
    private void writeHeader() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(10);
        style.setFont(font);

        createCell(row, 0, "Project Id", style);
        createCell(row, 1, "Tank Id", style);
        createCell(row, 2, "Tank Name", style);
        createCell(row, 3, "Name Of The Mip", style);
        createCell(row, 4, "Dist Id", style);

    }
//    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
//        sheet.autoSizeColumn(columnCount);
//        Cell cell = row.createCell(columnCount);
//        if (value instanceof Integer) {
//            cell.setCellValue((Integer) value);
//        } else if (value instanceof Boolean) {
//            cell.setCellValue((Boolean) value);
//        }else {
//            cell.setCellValue((String) value);
//        }
//        cell.setCellStyle(style);
//    }

    private void writeData(List<TankInfo> tankInfoList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (TankInfo info : tankInfoList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, info.getProjectId().toString(),style);
            createCell(row, columnCount++, info.getTankId().toString(), style);
            createCell(row, columnCount++, info.getTankName(), style);
            createCell(row, columnCount++, info.getNameOfTheMip(), style);
            createCell(row, columnCount++, info.getDistId().toString(), style);
        }
    }

    public void downloadSurvey(HttpServletResponse response, List<TankInfo> surveyTank) throws IOException {
        writeHeader();
        writeData(surveyTank);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

    //download step excel
    private void writeStepExcelHeader() {
        sheet = workbook.createSheet("StepExcelDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
    }
    private void writeStepExcelData(List<StepExcelDto> stepExcelList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (StepExcelDto info : stepExcelList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, info.getId().toString(),style);
        }
    }
    public void downloadStepExcel(HttpServletResponse response, List<StepExcelDto> stepExcelList) throws IOException {
        writeStepExcelHeader();
        writeStepExcelData(stepExcelList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    public void downloadCropIntensityReport(HttpServletResponse response, List<CropCycleAyacutDto>  allData) throws IOException {
        writeCropIntensityHeader();
        writeCropIntensityData(allData);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    private void writeCropIntensityHeader() {
        sheet = workbook.createSheet("CropIntensityDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "ProjectId", style);
        createCell(row, 2, "TankName", style);
        createCell(row, 3, "Area", style);
        createCell(row, 4, "Percentage", style);
        createCell(row, 5, "Year", style);

    }
    private void writeCropIntensityData(List<CropCycleAyacutDto>  allData) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (CropCycleAyacutDto info : allData) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            Double percentage=0.0;
            if(info.getPercentage()!=null){
                percentage=info.getPercentage();
            }
            else{
                percentage=percentage;
            }

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getProjectId(), style);
            createCell(row, columnCount++, info.getTankName(), style);
            createCell(row, columnCount++, info.getArea().toString(), style);
            createCell(row, columnCount++, percentage.toString(), style);
            createCell(row, columnCount++, info.getYear(), style);

            slno++;
        }


    }

    private void writeHeaderForPhysical() {
        sheet = workbook.createSheet("PhysicalProgressList");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "District Name", style);
        createCell(row, 3, "Target", style);
        createCell(row, 4, "Achievement",style);
        createCell(row, 5, "Achievement Percentage(%)", style);
        createCell(row, 6, "No of Beneficiaries", style);
    }

    private void writeDataForPhysical(List<AdaptPhysicalDto> physicalDto) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        // Integer total = 0;
        Integer slno = 1;

        for (AdaptPhysicalDto info : physicalDto) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(),style);
            createCell(row, columnCount++, info.getDistrictName(),style);
            createCell(row, columnCount++, info.getTarget(),style);
            createCell(row, columnCount++, info.getAchievement(),style);
            createCell(row, columnCount++, info.getAchievementPercentage(),style);
            createCell(row, columnCount++, info.getNoofBeneficiaries(),style);


            // createCell(row, columnCount++, info.getCount().toString(), style);
            slno++;
        }
        Row row = sheet.createRow(rowCount++);
//        if (userId == 1) {
//            createCell(row, 1, "State Total Fisheries ", style);
//        } else {
//            createCell(row, 1, "State Total ARD ", style);
//        }

//        createCell(row, 2, total, style);

    }


    public void downloadPhysicalProgress(HttpServletResponse response, List<AdaptPhysicalDto> physicalDto) throws IOException {
        writeHeaderForPhysical();
        writeDataForPhysical(physicalDto);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    private void writeFisheriesAbstractHeader() {
        sheet = workbook.createSheet("FisheriesAbstractDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "District", style);
        createCell(row, 3, "Financial Allocation(In Lakhs)", style);
        createCell(row, 4, "Actual Fund Allocated(In Lakhs)", style);
        createCell(row,5,"Expenditure(In Lakhs)",style);
        createCell(row,6,"Achivement(%)",style);
    }
    private void writeFisheriesAbstractData(List<AdaptFinancialDto> adaptData) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (AdaptFinancialDto info : adaptData) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(), style);
            createCell(row, columnCount++,info.getDistrictName(),style);
            createCell(row, columnCount++, String.valueOf(info.getFinancialAllocationInApp()), style);
            createCell(row, columnCount++, String.valueOf(info.getActualFundAllocated()), style);
            createCell(row, columnCount++, String.valueOf(info.getExpenditure()), style);
            createCell(row, columnCount++,String.valueOf(info.getPercentageAllocated()),style);

            slno++;
        }


    }

    public void downloadFisheriesAbstractReport(HttpServletResponse response, List<AdaptFinancialDto> adaptData) throws IOException {
        writeFisheriesAbstractHeader();
        writeFisheriesAbstractData( adaptData);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }


    private void writeHeaderForFisheriesReport() {
        sheet = workbook.createSheet("FisheriesReportList");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Year", style);
        createCell(row, 2, "Directorate", style);
        createCell(row, 3, "Activity Name", style);
        createCell(row, 4, "District",style);
        createCell(row, 5, "Scheme Name", style);
        createCell(row, 6, "Financial Allocation (in Lakhs)", style);
        createCell(row, 7, "Actual Fund Allocated (in Lakhs)", style);
        createCell(row, 8, "Expenditure (in Lakhs)", style);
        createCell(row, 9, "Achievement (%)", style);
    }

    private void writeDataForFisheriesReport(List<AdaptFinancialDto> adaptFisheriesList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        // Integer total = 0;
        Integer slno = 1;

        for (AdaptFinancialDto info : adaptFisheriesList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getYear(),style);
            createCell(row, columnCount++, info.getDirectorate(),style);
            createCell(row, columnCount++, info.getActivityName(),style);
            createCell(row, columnCount++, info.getDistrictName(),style);
            createCell(row, columnCount++, info.getSchemeName(),style);
            createCell(row, columnCount++, info.getFinancialAllocationInApp(),style);
            createCell(row, columnCount++, info.getActualFundAllocated(),style);
            createCell(row, columnCount++, info.getExpenditure(),style);
            createCell(row, columnCount++, info.getPercentageAllocated(),style);


            // createCell(row, columnCount++, info.getCount().toString(), style);
            slno++;
        }
        Row row = sheet.createRow(rowCount++);


    }


    public void downloadFisheriesReportList(HttpServletResponse response, List<AdaptFinancialDto> adaptFisheriesList) throws IOException {

        writeHeaderForFisheriesReport();
        writeDataForFisheriesReport(adaptFisheriesList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }


    private void writeHeaderForFisheriesExcelReport() {
        sheet = workbook.createSheet("FisheriesExcelReportList");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "District Name", style);
        createCell(row, 3, "ActivityName ",style);
        createCell(row, 4, "Financial Allocation (In Lakhs)", style);
        createCell(row, 5, "Actual Fund Allocated(In Lakhs)", style);
        createCell(row, 6, "Expenditure(In Lakhs)", style);
        createCell(row, 7, "Achievement(%)", style);
    }

    private void writeDataForFisheriesExcelReport(List<AdaptFinancialDto> adaptFisheriesList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        // Integer total = 0;
        Integer slno = 1;

        for (AdaptFinancialDto info : adaptFisheriesList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(),style);
            createCell(row, columnCount++, info.getDistrictName(),style);
            createCell(row, columnCount++, info.getActivityName(),style);
            createCell(row, columnCount++, info.getFinancialAllocationInApp(),style);
            createCell(row, columnCount++, info.getActualFundAllocated(),style);
            createCell(row, columnCount++, info.getExpenditure(),style);
            createCell(row, columnCount++, info.getPercentageAllocated(),style);


            // createCell(row, columnCount++, info.getCount().toString(), style);
            slno++;
        }
        Row row = sheet.createRow(rowCount++);


    }

    public void downloadFisheriesExcelReportList(HttpServletResponse response, List<AdaptFinancialDto> fisheriesExcelReport) throws IOException {

        writeHeaderForFisheriesExcelReport();
        writeDataForFisheriesExcelReport(fisheriesExcelReport);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }


    private void writeHeaderForActivityWisePhysicalProgress() {
        sheet = workbook.createSheet("ActivityWisePhysicalProgressList");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "Activity Name", style);
        createCell(row, 3, "Target", style);
        createCell(row, 4, "Achievement",style);
        createCell(row, 5, "Achievement Percentage(%)", style);
        createCell(row, 6, "No of Beneficiaries", style);
    }


    private void writeDataForActivityWisePhysicalProgress(List<AdaptPhysicalDto> activityWisePhysicalProgress) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        // Integer total = 0;
        Integer slno = 1;

        for (AdaptPhysicalDto info : activityWisePhysicalProgress) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(),style);
            createCell(row, columnCount++, info.getActivityName(),style);
            createCell(row, columnCount++, info.getTarget(),style);
            createCell(row, columnCount++, info.getAchievement(),style);
            createCell(row, columnCount++, info.getAchievementPercentage(),style);
            createCell(row, columnCount++, info.getNoofBeneficiaries(),style);


            // createCell(row, columnCount++, info.getCount().toString(), style);
            slno++;
        }
        Row row = sheet.createRow(rowCount++);
//        if (userId == 1) {
//            createCell(row, 1, "State Total Fisheries ", style);
//        } else {
//            createCell(row, 1, "State Total ARD ", style);
//        }

//        createCell(row, 2, total, style);

    }


    public void activityWisePhysicalProgress(HttpServletResponse response, List<AdaptPhysicalDto> activityWisePhysicalProgress) throws IOException {

        writeHeaderForActivityWisePhysicalProgress();
        writeDataForActivityWisePhysicalProgress(activityWisePhysicalProgress);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();


    }
    private void writeActivityWiseFinancialHeader() {
        sheet = workbook.createSheet("ActivityWiseFinancialProgressDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "Activity Name", style);
        createCell(row, 3, "Financial Allocation (In Lakhs)", style);
        createCell(row, 4, "Actual Fund Allocated(In Lakhs)", style);
        createCell(row, 5, "Expenditure(In Lakhs)", style);
        createCell(row, 6, "Achievement(%)", style);
    }
    private void writeActivityWiseFinancialData(List<AdaptFinancialDto>  activityWiseFinancialProgressList) throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (AdaptFinancialDto info : activityWiseFinancialProgressList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(), style);
            createCell(row, columnCount++, info.getActivityName(), style);
            createCell(row, columnCount++, String.valueOf(info.getFinancialAllocationInApp()), style);
            createCell(row, columnCount++, String.valueOf(info.getActualFundAllocated()), style);
            createCell(row, columnCount++, String.valueOf(info.getExpenditure()), style);
            createCell(row, columnCount++, String.valueOf(info.getPercentageAllocated()), style);

            slno++;
        }



    }


    public void downloadActivityWiseFinancialProgressList(HttpServletResponse response, List<AdaptFinancialDto> activityWiseFinancialProgressList) throws IOException {
        writeActivityWiseFinancialHeader();
        writeActivityWiseFinancialData(activityWiseFinancialProgressList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    private void writePhysicalAchievementHeader() {
        sheet = workbook.createSheet("PhysicalAchievementAbstractReport");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "District Name", style);
        createCell(row, 3,"target",style);
        createCell(row, 4, "Achievement", style);
        createCell(row, 5, "AchievementPercentage(%)", style);
        createCell(row, 6, "No. of beneficiary", style);

    }



    public void downloadPhysicalAchievementAbstractReport(HttpServletResponse response, List<AdaptPhysicalDto> adaptData) throws IOException {
        writePhysicalAchievementHeader();
        writePhysicalAchievementData(adaptData);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }

    private void writePhysicalAchievementData(List<AdaptPhysicalDto> PhysicalAchievementDataList) throws IOException {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer total = 0;
        Integer slno = 1;

        for (AdaptPhysicalDto info : PhysicalAchievementDataList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(), style);
            createCell(row, columnCount++, info.getDistrictName(), style);
            createCell(row, columnCount++, String.valueOf(info.getTarget()), style);
            createCell(row, columnCount++, String.valueOf(info.getAchievement()), style);
            createCell(row, columnCount++, String.valueOf(info.getAchievementPercentage()), style);
            createCell(row, columnCount++, String.valueOf(info.getNoofBeneficiaries()), style);

            slno++;
        }



    }






    private void writeActivityWiseFardPhysicalAchievementHeader() {
        sheet = workbook.createSheet("ActivityWiseFardPhysicalAchievementDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Directorate", style);
        createCell(row, 2, "Activity Name", style);
        createCell(row, 3, "Target", style);
        createCell(row, 4, "Achievement",style);
        createCell(row, 5, "Achievement Percentage(%)", style);
        createCell(row, 6, "No of Beneficiaries", style);
    }


    private void writeDataForActivityWiseFardPhysicalAchievement(List<FardPhysicalAchievementDto> adaptPhysicalAchievementList) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer slno = 1;

        for (FardPhysicalAchievementDto info : adaptPhysicalAchievementList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getDirectorate(),style);
            createCell(row, columnCount++, info.getActivityName(),style);
            createCell(row, columnCount++, info.getTarget(),style);
            createCell(row, columnCount++, info.getAchievement(),style);
            createCell(row, columnCount++, info.getAchievementPercentage(),style);
            createCell(row, columnCount++, info.getNoOfBeneficiaries(),style);

            slno++;
        }
        Row row = sheet.createRow(rowCount++);
//        if (userId == 1) {
//            createCell(row, 1, "State Total Fisheries ", style);
//        } else {
//            createCell(row, 1, "State Total ARD ", style);
//        }

//        createCell(row, 2, total, style);

    }

    public void activityWiseAdaptPhysicalAchievement(HttpServletResponse response, List<FardPhysicalAchievementDto> adaptPhysicalAchievementList) throws IOException {

        writeActivityWiseFardPhysicalAchievementHeader();
        writeDataForActivityWiseFardPhysicalAchievement(adaptPhysicalAchievementList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
    private void writeDownloadExcelNoticeDataListHeader() {
        sheet = workbook.createSheet("tendenNoticeDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Bid Id", style);
        createCell(row, 2, "Work Identification Code", style);
        createCell(row, 3, "Name of Mip", style);
        createCell(row, 4, "Estimated Amount put to Tender",style);
        createCell(row, 5, "District", style);
        createCell(row, 6, "Block", style);
    }


    private void writedownloadExcelNoticeDataList(List<NoticeListingDto> tenderNotices) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer slno = 1;

        for (NoticeListingDto info : tenderNotices) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, info.getBidId(),style);
            createCell(row, columnCount++, info.getWorkIdentificationCode(),style);
            createCell(row, columnCount++, info.getTankName(),style);
            createCell(row, columnCount++, info.getTenderAmount(),style);
            createCell(row, columnCount++, info.getDistName(),style);
            createCell(row, columnCount++, info.getBlockName(),style);

            slno++;
        }
        Row row = sheet.createRow(rowCount++);
//        if (userId == 1) {
//            createCell(row, 1, "State Total Fisheries ", style);
//        } else {
//            createCell(row, 1, "State Total ARD ", style);
//        }

//        createCell(row, 2, total, style);

    }

    public void downloadExcelNoticeDataList(HttpServletResponse response, List<NoticeListingDto> tenderNotices) throws IOException {
        writeDownloadExcelNoticeDataListHeader();
        writedownloadExcelNoticeDataList(tenderNotices);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
