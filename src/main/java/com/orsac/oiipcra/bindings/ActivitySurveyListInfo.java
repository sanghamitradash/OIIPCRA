package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySurveyListInfo {
    private String activityName;
    private Long contractNumber;
    private String bid;
    private String workStatus;
    private String uploadDate;
    private String tankName;
    private Integer workId;
    private Integer activityId;
    private String workType;

}
