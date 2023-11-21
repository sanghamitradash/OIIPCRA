package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropCycleAyacutDto {
    private Integer gridCode;
    private String gridType;
    private BigDecimal area;
    private String year;

    private BigDecimal commandArea;
    private BigDecimal cropArea;
    private String tankName;
    private Double percentage;
    private String projectId;

}
