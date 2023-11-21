package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySearchRequest {
    private Integer userId;
    private  Integer agencyId;
    private String contractNumber;
    private Integer tankCode;
    private Integer workStatusId;
    private Integer workType;
    private String bidId;
    private Integer workId;
    private Integer activityId;
    private String uploadFromDate;
    private String uploadToDate;
    private Integer blockId;
    private Integer districtId;
    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;


}
