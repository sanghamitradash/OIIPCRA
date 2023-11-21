package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractMappingDto {
    private Integer id;
    private Integer contractId;
    private Integer activityId;
    private Integer distId;
    private String distName;
    private Integer blockId;
    private String blockName;
    private Integer gpId;
    private String gpName;
    private Integer villageId;
    private String villageName;
    private Integer divisionId;
    private String divisionName;
    private Integer subDivisionId;
    private Integer sectionId;
    private Integer tankId;
    private String tankName;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer tenderNoticeId;
    private Double tankWiseContractAmount;
    private Integer tenderId;
    private Integer estimateId;
    private String bidId;
    private String noticeName;
    private Integer projectId;
    private String contractNumber;

}
