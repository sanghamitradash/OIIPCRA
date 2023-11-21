package com.orsac.oiipcra.helper;


import com.orsac.oiipcra.dto.MasterDto;
import com.orsac.oiipcra.dto.TankDto;
import com.orsac.oiipcra.dto.TestDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Component
public class ExcelHelper {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Asset";

    int heading=1;

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }



    public ExcelHelper () {
        workbook = new XSSFWorkbook();
        //   this.heading = authorityId;
    }

    private void writeHeader() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "District Name", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    public List<TestDto> getExcelData(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<TestDto> allData = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int column = currentRow.getLastCellNum();
                if (column >= 46){

                    // skip header
                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }
                  //  System.out.println(currentRow);

                    TestDto data = new TestDto();
                    //get value from  Excel rowWise

                    String landformL = (getCellValue(currentRow, 1));
                    String lulc = (getCellValue(currentRow, 2));
                    String lulc1 = (getCellValue(currentRow, 3));
                    String tmu = (getCellValue(currentRow, 4));
                    String soilPhase = (getCellValue(currentRow, 5));
                    String soilDepth = (getCellValue(currentRow, 6));
                    String soilDrain = (getCellValue(currentRow, 7));
                    String newSlope = (getCellValue(currentRow, 8));
                    String surfaceTe =(getCellValue(currentRow, 9));
                    String textureCo = (getCellValue(currentRow, 10));
                    Double areaHa = Double.valueOf((getCellValue(currentRow, 44)));
                    String soilConservationMeasures = (getCellValue(currentRow, 45));
                    //setData to Dto
                     data.setLandformL(landformL);
                    data.setLulc(lulc);
                    data.setLulc1(lulc1);
                    data.setTmu(tmu);
                    data.setSoilPhase(soilPhase);
                    data.setSoilDepth(soilDepth);
                    data.setSoilDrain(soilDrain);
                    data.setNewSlope(newSlope);
                    data.setSurfaceTe(surfaceTe);
                    data.setTextureCo(textureCo);
                    data.setAreaHa(areaHa);
                    data.setSoilConservationMeasures(soilConservationMeasures);

                    allData.add(data);
                }
                else{
                    return null;
                }
            }
            workbook.close();
            return allData;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse  Excel file: " + e.getMessage());
        }
    }
    public List<MasterDto> getMasterData(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<MasterDto> masterData = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int column = currentRow.getLastCellNum();

                if (column == 7) {
                    // skip header
                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }

                    MasterDto data = new MasterDto();
                    //get value from  Excel rowWise

                    Integer serialNo = Integer.valueOf((getCellValue(currentRow, 0)));
                    String structures = (getCellValue(currentRow, 1));
                    String landUse = (getCellValue(currentRow, 2));
                    String landForms = (getCellValue(currentRow, 3));
                    String slopePercentage = (getCellValue(currentRow, 4));
                    String surfaceStructure = (getCellValue(currentRow, 5));
                    String soilDepth = (getCellValue(currentRow, 6));
                    data.setSerialNo(serialNo);
                    data.setStructures(structures);
                    data.setLandUse(landUse);
                    data.setLandForms(landForms);
                    data.setSlopePercentage(slopePercentage);
                    data.setSurfaceStructure(surfaceStructure);
                    data.setSoilDepth(soilDepth);

                    masterData.add(data);
                }
                else{
                    return null;
                }
            }
            workbook.close();
            return masterData;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse Contract Excel file: " + e.getMessage());
        }
    }
    private String getCellValue(Row row, int cellNo) {
        DataFormatter formatter = new DataFormatter();
        Cell cell = row.getCell(cellNo);
        return formatter.formatCellValue(cell);
    }


}