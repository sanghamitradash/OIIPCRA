package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInfo {
    private  String contractNumber;
    private String  agencyName;
    private String contractDate;
    private String tankName;
    private Integer unit;
    private String workStatus;
    private String unitValue;
    private Long longitude;
    private Long latitude;
    private Integer delayDays;
    private String approvalStatus;
    private String workType;
    private String bid;
    private String surveyorImage;
    private String imagePath;


}
