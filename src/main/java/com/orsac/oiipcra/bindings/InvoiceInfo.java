package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceInfo {

    private Integer id;
    private String invoiceNo;
    private String invoiceDate;
    private String invoiceDocument;
    private String invoiceStatus;
    private Integer tenderId;
    private String bidId;
    private Integer workId;
    private String agencyName;
    private Double invoiceAmount;
    private Double gst;
    private Double gstAmount;
    private Double totalAmount;

    private Integer paymentTypeId;
    private String paymentType;
    private Double amountPaid;
    private Integer finyrId;
    private String yearName;
    private Integer monthId;
    private String monthName;
    private Date paymentDate;

    private Integer contractId;
    private String contractNumber;

    private Integer tankId;
    private Integer projectId;
    private String  nameOfTheMip;

    private String workIdentificationCode;












}
