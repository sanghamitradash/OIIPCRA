package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AdaptPhysicalBeneficiaryDto {
    private Integer id;
    private Integer yearId ;
    private Integer deptId ;
    private Integer districtId ;
    private Integer blockId ;
    private Integer gpId ;
    private Integer villageId ;
    private String villageName ;
    private Integer schemeId ;
    private String schemeName ;
    private Integer componentId ;
    private String componentName ;
    private String mobileNumber ;
    private String farmerName ;
    private String districtName;
    private String blockName;
    private String gpName;
    private String aadharNumber ;
    private String latLong ;
    private Boolean isActive;
    private String year;
    private Double latitude;
    private Double longitude;
}
