package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatusEstimateIdsByDist {

    private Integer distId;
    private String distName;
    private Integer estimateIds;
}
