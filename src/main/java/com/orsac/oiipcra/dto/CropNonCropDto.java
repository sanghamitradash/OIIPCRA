package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropNonCropDto {
    private BigDecimal area;
    private Integer gridCode;
    private String month;
    private String cropType;
    private String monthName;

    private Integer gid;
}
