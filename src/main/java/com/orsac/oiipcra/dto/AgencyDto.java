package com.orsac.oiipcra.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AgencyDto {
    private Integer id;
    private String name;
    private String address;
    private Long phone;
    private String panNo;
    private boolean active;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer licenseClassId;
    private String classOfLicense;
    private String imagePath;
    private String imageName;
    private Integer exemptId;
    private String gstinNo;
    private String licenseValidity;
    private String finyr;
    private String post;
    private Integer distId;
    private Integer pincode;
    private String agencyName;
    private Integer agencyId;

    private Integer bidderId;
    private Integer workId;
    List<FormGDto> formG;
    //private String sumString;


}
