package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CroppingPatternDto {

    private Integer id;
    private Integer tankId;
    private String villageName;
    private Integer croppingPatternType;
    private Integer villageId;
    private Double ayacutInHa;
    private Double khPaddyHa;
    private Double khNonPaddyHa;
    private Double rabiPaddyHa;
    private Double rabiNonPaddyHa;
    private Boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer tankOtherDetailsId;
}
