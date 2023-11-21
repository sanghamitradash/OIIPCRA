package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinYrDto {

    private int id;
    private String startDate;
    private String endDate;
    private String name;
    private BigDecimal expenditureAmount;
    private BigDecimal contractAmount;
    private BigDecimal estimateAmount;
    private BigDecimal balanceContractAmount;
}
