package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueSearchRequest {



    private Integer distId;
    private Integer estimateId;
    private Integer blockId;
    private Integer contractId;
    private Integer issueType;
    private String  issueTypeName;
    private Integer deptId;
    private Integer workId;
    private Integer resolutionLevel;
    private String levelName;
    private Integer issueStatus;
    private String contractNumber;
    private Integer subActivityId;
    private Integer tankId;
    private Integer userId;
    private Integer invoiceId;
    private Integer expenditureId;
    private String uploadFromDate;
    private String uploadToDate;
    private Integer page;

   // private Integer parenId;

    private Integer size;

    private String sortOrder;

    private String sortBy;
}
