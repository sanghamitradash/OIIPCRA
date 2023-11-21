package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureMappingDto {

    private Integer id;
    private Integer expenditureId;
    private Integer contractId;
    private Integer activityId;
    private Integer estimateId;
    private Integer tankId;
    private String tankName;
    private String contractName;
    private String agencyName;
    private  Integer agencyId;
    private Integer tenderId;
    private String bidId;
    private Integer tenderNoticeId;
    private String nameOfWork;
    private String workIdentificationCode;
    private Integer workSlNoInTcn;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private  Integer divisionId;
    private String divisionName;
    private  Integer districtId;
    private String districtName;
    private  Integer blockId;
    private String blockName;
    private Integer projectId;
}
