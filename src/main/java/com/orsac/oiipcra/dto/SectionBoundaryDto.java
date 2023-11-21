package com.orsac.oiipcra.dto;

import lombok.Data;

@Data
public class SectionBoundaryDto {

    private String divisionName;
    private Integer divisionId;
    private String subDivisionName;
    private Integer subDivisionId;
    private String sectionName;
    private Integer sectionId;
    private String geojson;

}
