package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardExpenditureOfMIPByDivision {

    private Integer divisionId;
    private String divisionName;
    private Double estimatedCost;
    private Double agrmtCost;
    private Double expByPrevMonth;
    private Double expCurrentMonth;
    private Double cumExp;
}
