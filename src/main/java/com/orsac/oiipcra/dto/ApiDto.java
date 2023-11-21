package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Setter
@Getter
public class ApiDto {
    @JsonProperty("year")
    private Integer year;
    @JsonProperty("districtName")
    private String districtName;

    @JsonProperty("blockName")
    private String blockName;

    @JsonProperty("gpName")
    private String gpName;

    @JsonProperty("directorate")
    private String directorate;


    @JsonProperty("masterComponent")
    private String masterComponent;

    @JsonProperty("schemeName")
    private String schemeName;

    @JsonProperty("componentName")
    private String componentName;

    @JsonProperty("target")
    private Integer target;

    @JsonProperty("achievement")
    private Integer achievement;

    @JsonProperty("noofBeneficiaries")
    private Integer noofBeneficiaries;

    @JsonProperty("achievementPercentage")
    private Integer achievementPercentage;


}
