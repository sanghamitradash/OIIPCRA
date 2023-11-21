package com.orsac.oiipcra.dto;

import lombok.Data;

@Data
public class SubDivisionBoundaryDto {
    private String divisionName;
    private Integer divisionId;
    private String subDivisionName;
    private Integer subDivisionId;
    private String geojson;

}
