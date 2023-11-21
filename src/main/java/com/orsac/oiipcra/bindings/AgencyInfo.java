package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyInfo {
    private Integer agencyId;

    private String agencyName;

    private String address;

    private Long phoneNo;

    private String panNo;

    private boolean active;

    private Integer createdBy;


    private Date createdOn ;

    private Integer updatedBy;

    private Date updatedOn;

    private Integer licenseClassId;
    private String licenseClassName;

    private Integer exemptId;
    private String exemptName;

    private String gstinNo;

    private Date licenseValidity;

    private String postOffice;

    private Integer distId;
    private String distName;
    private String pincode;
    private String imagePath;
    private String imageName;

}
