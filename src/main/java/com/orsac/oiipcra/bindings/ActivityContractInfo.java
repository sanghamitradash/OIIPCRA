package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityContractInfo {

    private Integer distId;
    private Integer divisionId;
    private Integer areaId;
    private Integer procurementMadeFor;
    private String fileNo;
    private String approvalOrder;
    private Integer technical_sanction_no;
    private String work_id;
    private Integer work_type_id;
    private String work_description;
    private String rfbId;
    private String rfdIssued_date;
    private String  rfdReceivedDate;
    private Double estimatedCost;
    private Integer eoiId;
    private String eoiDate;
    private Integer agencyId;
    private String agencyName;
    private String agencyAddress;
    private String panNo;
    private Long mobile;
    private String awardedTo;
    private Double rateQuoted;
    private String loaIssuedNo;
    private String loaIssuedDate;
    private String contractNo;
    private String contractDate;
    private Double contractAmt;
    private Double totalContractAmt;
    private Boolean contractSigned;
    private String agreementNo;
    private Double agreementAmt;
    private Double gst;
    private String comencementDate;
    private String completionDate;
    private Integer completionPeriod;
    private Integer eot1SanctionedUpto;
    private Integer eot2SanctionedUpto;
}
