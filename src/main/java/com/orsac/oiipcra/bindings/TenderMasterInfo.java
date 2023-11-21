package com.orsac.oiipcra.bindings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderMasterInfo {

    private Integer id;
    private String bidId;
    private Date approvalForProcurementDate;
    private Date technicalBidOpeningDate;
    private Date technicalBidOpeningDateRevised;
    private Date bidSubmissionDate;
    private Date bidSubmissionDateRevised;
    private Date financialBidOpeningDate;
    private String nameOfWork;
    private Integer preBidMeetingType;
    private Date preBidMeetingDate;
    private Date tenderPublicationDate;
    private Date publicationPeriodUpto;
    private Date dateOfFirstCorrigendum;
    private Date dateOfSecondCorrigendum;
    private Integer tenderType;
    private Integer tenderLevelId;
    private Integer finyrId;
    private Date tenderOpeningDate;
    private Date dateOfTenderNotice;


}
