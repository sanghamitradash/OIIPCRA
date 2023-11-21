package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkStatusDto {

    private Integer finyrId;
    private String finYear;
    private Integer distId;
    private String districtName;
    private Integer divisionId;
    private String divisionName;
    private String approvalOrder;
    private String tachnicalSanctionNo;
    private Integer projectId;
    private String projectScheme;
    private String workDescription;
    private Integer workId;
    private String rfbId;
    private Double estimatedCost;
    private String agencyName;
    private String panNo;
    private String phone;
    private Integer awardedAs;
    private String awardedType;
    private String loaIssuedNo;
    private Date loaIssuedDate;
    private Double agreementAmount;
    private Date actualDateOfCompletion;
    private Date createdOn;
    private Boolean contractSigned;
    private Integer tenderId;






}
