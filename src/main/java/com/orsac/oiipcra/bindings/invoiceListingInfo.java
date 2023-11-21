package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class invoiceListingInfo {

    private Integer id;
    private String invoiceNo;
    private Date invoiceDate;
    private Double invoiceAmount;
    private Integer contractId;
    private String contractNumber;
    private String workType;
    private String bidId;
    private Integer tenderId;
    private Integer workId;
    private String agencyName;
    private String invoiceStatus;

}
