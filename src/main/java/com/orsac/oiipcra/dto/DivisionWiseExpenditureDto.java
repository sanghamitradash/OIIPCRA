package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DivisionWiseExpenditureDto {

    private String divisionName;
    private Integer divisionId;
    private BigDecimal value;
}
