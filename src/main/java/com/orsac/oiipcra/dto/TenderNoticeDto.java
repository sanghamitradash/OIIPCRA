package com.orsac.oiipcra.dto;


import com.orsac.oiipcra.entities.TenderNoticeLevelMapping;
import com.orsac.oiipcra.entities.WorkProjectMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderNoticeDto {

    private Integer id;
    private Integer tenderId;
    private Integer biddingType;
    private Integer typeOfWork;
    private Integer workSlNoInTcn;
    private String workIdentificationCode;
    private String nameOfTheMip;
    private String nameOfWork;
    private Integer distId;
    private String distName;
    private Integer blockId;
    private String blockName;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdToBeDeposited;
    private Double timeForCompletion;
    private Long contactNo;
    private Integer tenderLevelId;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer circleId;
    private Integer divisionId;
    private Integer eeId;
    private Integer subDivisionId;
    private Integer subDivisionOfficer;
    private String subDivOfficerName;
    private Integer projectId;
    private Integer eeType;
    private String otherEe;
    private Long eeContactNo;
    private Integer sectionId;
    private String otherSubDivisionOfficer;
    private List<TenderNoticeLevelMapping> tenderNoticeLevelMapping;
    private List<WorkProjectMapping> workProjectMapping;
    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;
}
