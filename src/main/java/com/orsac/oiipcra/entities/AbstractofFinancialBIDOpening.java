package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractofFinancialBIDOpening {

    private String bid_id;
    private Date financial_bid_opening_date;
    private String workId;
    private String agency;
    private String licenseClass;
    private Double tender_amount;
    private Double bid_price;
    private Double quoted;
    private Double emd_amount;

}
