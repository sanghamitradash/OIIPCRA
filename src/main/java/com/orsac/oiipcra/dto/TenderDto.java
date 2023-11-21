package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderDto {

    private int id;
    private int userId;
    private String bidId;
    private Date technicalBidOpeningDate;
    private Date preBidMeetingDate;
    private int tenderLevelId;
    private int tenderStatusId;
    private String tenderCode;
    private int tankId;
    private int districtId;
    private int blockId;
    private String uploadFromDate;
    private String uploadToDate;
    private int activityId;
    private int page;
    private int size;
    private String sortOrder;
    private String sortBy;

}
