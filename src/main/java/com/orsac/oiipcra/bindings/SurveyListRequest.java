package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyListRequest {

    private Integer id;
    private Integer userId;
    private Integer districtId;
    private Integer blockId;
    private Integer gpId;
    private Integer villageId;
    private Integer divisionId;
    private Integer subDivisionId;
    private Integer sectionId;
    private Integer progressStatus;
    private Integer tankId;
    private Integer projectId;
    private Integer contractId;
    private Integer invoiceId;
    private Integer expenditureId;
    private Integer estimateId;
    private Integer issueId;
    private String bidId;
    private String uploadFromDate;
    private String uploadToDate;
    private Integer page;
    private Integer size;
    private String sortOrder;
    private String sortBy;
    private  Integer isRequestForProjectDashboard;
    private Integer isCadSurveyed;
    private Integer isTankSurveyed;
    private Integer isDepthSurveyed;
    private Integer isFeederSurveyed;
    private Integer isCivilWorkCompleted;
    private  Integer isFpoAdded;
    private boolean proposedToBeDropped;
    private Integer proposedToBeDroppedTank;
    private Integer isDropped;



}
