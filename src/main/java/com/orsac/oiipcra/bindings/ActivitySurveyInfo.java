package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySurveyInfo {

    private Integer id;
    private Integer tankId;
    private Integer workStatusId;
    private Integer unitId ;
    private Integer noOfUnit;
    private Integer contractId;
    private Double longitude;
    private Double latitude ;
    private Double altitude ;
    private Double accuracy ;
    private Integer deviceId;
    private Integer divisionId;
    private Integer subDivisionId;
    private Integer sectionId;
    private Integer approvalStatus ;
    private Integer activityId;
    private  Double targetValue;
    private  Integer delayDays;
    private  Integer distId;
    private  Integer blockId;
    private  Integer gpId;
    private  Integer villageId;
    private  String surveyorImage;
    private  String imagePath;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Boolean isActive;
    private ActivitySurveyImageRequest activitySurveyImageRequest;
//    private String statusName;
//    private String contractName;
//    private String approvalName;
//    private String activityName;
//    private String agencyName;

    //private List<TankLocations> tank_location;

}
