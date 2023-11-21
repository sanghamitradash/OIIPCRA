package com.orsac.oiipcra.dto;

import lombok.Data;

@Data
public class DistrictBoundaryDto {

    private String district_name;
    private String district_code;
    private Integer dist_id;
    private String geojson;
    private Double estimateAmount;
    private Double expenditureAmount;
    private Double finalAlloctaion;
    private Integer noOfBenificiaries;
    private Integer noOfContractCompleted;
    private Integer noOfContractOngoing;
}
