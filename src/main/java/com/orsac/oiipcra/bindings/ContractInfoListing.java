package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractInfoListing {
    private Integer contractId;
    private String contractName;
    private String contractNumber;
    private String workDescription;
    private String contractDate;
    private String contractLevel;
    private String contractType;
    private String agencyName;
    private String contractStatus;
    private Integer tenderId;
    private String bidId;
    private String noticeId;
    private String tankName;
    private String contractTypeName;
    private String workTypeName;
    private Integer workTypeId;
}
