package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Setter
@Getter
public class FardFinacialDto {

        @JsonProperty("year")
        private String year;
        @JsonProperty("district")
        private String districtName;
        @JsonProperty("directorate")
        private String directorate;

        @JsonProperty("financialAllocationInapp")
        private Double financialAllocationInApp;

        @JsonProperty("schemeName")
        private String schemeName;

        @JsonProperty("actualfundAllocated")
        private Double actualFundAllocated;

        @JsonProperty("expenditure")
        private Double expenditure;
        @JsonProperty("districtId")
        private Integer districtId;
        @JsonProperty("schemeId")
        private Integer schemeId;
        @JsonProperty("subSchemeId")
        private Integer subSchemeId;


    }


