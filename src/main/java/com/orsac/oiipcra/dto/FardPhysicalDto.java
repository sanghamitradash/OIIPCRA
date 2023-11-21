package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Setter
@Getter
public class FardPhysicalDto {
    @JsonProperty("year")
    private String year;
    @JsonProperty("district")
    private String districtName;

    @JsonProperty("block")
    private String blockName;

    @JsonProperty("gp")
    private String gpName;

    @JsonProperty("directorate")
    private String directorate;

    @JsonProperty("schemeName")
    private String schemeName;

    @JsonProperty("target")
    private String target;

    @JsonProperty("achievement")
    private String achievement;

    @JsonProperty("noofBeneficiaries")
    private Integer noofBeneficiaries;


    @Column(name = "districtId")
    private Integer districtId;

    @JsonProperty("schemeId")
    private Integer fardSchemeId;
    @JsonProperty("subSchemeId")
    private Integer subSchemeId;
}
