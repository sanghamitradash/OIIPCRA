package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDataDto {
    private Integer id;
    private String officeName;
    private String headOfDept;
    private String headOfOffice;
    private Integer designation;
    private String designationName;
    private String description;
    private String spuAddress;
    private String spuPost;
    private String spuEmail;
    private String landLineNo;
    private Integer spuPinNo;
    private Integer distId;
    private String districtName;
    private Integer divisionId;
    private String divisionName;
    private String copyTo1;
    private String copyTo2;
    private String copyTo3;
    private String copyTo4;
    private String copyTo5;
    private String copyTo6;
    private String copyTo7;
    private String copyTo8;
    private String copyTo9;
    private String copyTo10;
    private String copyTo11;
    private boolean active;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn ;
    private  String createdByUser;
    private Integer userId;
    private Integer userLevelId;

    private String circleName;
    private String subDivisionName;
}
