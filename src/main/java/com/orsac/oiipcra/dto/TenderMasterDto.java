package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderMasterDto {


    private Date tenderPublicationDate;
    private Date financialBidOpeningDate;
    private Date preBidMeetingDate;
    private Date technicalBidOpeningDate;

    private Date approvalForProcurementDate;
    private Date technicalBidOpeningDateRevised;
    private Date bidSubmissionDate;
    private Date bidSubmissionDateRevised;
    private Date publicationPeriodUpto;
    private String nameOfWork;
    private Integer preBidMeetingType;
    private String name;
    private Integer workSlNo;
    private Integer totalNoticeCount;

}
