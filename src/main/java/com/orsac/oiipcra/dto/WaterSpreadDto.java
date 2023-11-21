package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterSpreadDto {

    private Double sum;
    private String month;
    private Integer monthId;
    private String year;
    private Integer yearId;
   private List<WaterSpreadDto> monthData;

    private Double maxValue;
    private Double minValue;
    private Double average;
    private Double totalWsa;
    private Double lessThan50;
    private Double moreThan50;
    private String cropNonCropYearMonth;



}
