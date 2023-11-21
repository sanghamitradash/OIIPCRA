package com.orsac.oiipcra.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class PhysicalBeneficiaryDto {

    @JsonProperty("year")
    private String year;

    @JsonProperty("directorate")
    private String directorate;

    @JsonProperty("district")
    private String districtName;

    @JsonProperty("block")
    private String blockName;

    @JsonProperty("gp")
    private String gpName;

    @JsonProperty("village")
    private String villageName;

    @JsonProperty("schemeName")
    private String schemeName;

    @JsonProperty("component")
    private String componentName;

    @JsonProperty("mobileNo")
    private String mobileNo;

    @JsonProperty("farmerName")
    private String farmerName;

    @JsonProperty("aadhaarNo")
    private String aadhaarNo;

    @JsonProperty("latLong")
    private String latLong;


}
