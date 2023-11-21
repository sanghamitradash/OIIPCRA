package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenderNoticeResponse {

    private Integer id;
    private String  bid;
    private Date dateOfTenderNotice;
    private Date tenderOpeningDate;
    private Date preBidMeetingDate;
    private Integer biddingTypeId;
    private String biddingType;
    private Integer workTypeId;
    private String workType;
    private Integer projectId;
    private String projectName;
    private Integer tenderLevelId;
    private String levelName;
   // private Integer distId;
    private String distName;
   // private Integer blockId;
    private String blockName;
    private String tankName;
    private Integer workSlNo;
    private String workIdentificationCode;
    private String nameOfWork;
    private Double tenderAmount;
    private Double paperCost;
    private Double emdDeposited;
    private Double timeOfCompletion;
    private Integer circleId;
    private Integer eeId;
    private Integer divisionId;
    private String miDivisionName;
    private Integer subDivisionId;
    private String miSubDivisionName;
    private Integer sectionId;
    private boolean active;
    private Integer createdBy;
    private Long contactNo;
    private Integer nextId;
    private Integer previousId;
    private String eeName;
    private Integer eeType;
    private String eeTypeName;
    private String otherEe;
    private Long eeContactNo;
    private String workName;
    private Integer subDivisionOfficerId;
    private String otherSubDivisionOfficerName;
    private String subDivisionOfficerName;

}
