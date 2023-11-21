package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardExpenditureOfMIPByDistrict {

    private Integer distId;
    private String distName;
    private Double estimatedCost;
    private Double agrmtCost;
    private Double expByPrevMonth;
    private Double expCurrentMonth;
    private Double cumExp;
}
