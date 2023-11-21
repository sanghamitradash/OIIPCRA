package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueInfoListing {

    private Integer issueId;
    private String IssueTypeName;
    private String issueDescription;
    private String tankName;
    private String contractNumber;
    private String department;
    private String issueStatus;
    private String tenderId;
    private String issueDate;
    private String code;
    private Integer activityId;
    private Integer tankId;
    private Integer contractId;
    private Integer workId;
    private Integer projectId;
    private String issueType;
    private  String bidId;


}
