package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.Invoice;
import com.orsac.oiipcra.entities.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {

    private Integer id;
    private String invoiceNo;
    private double invoiceAmount;
    private Integer contractId;
    private Date invoiceDate;
    private Integer agencyId;
    private Integer finyrId;
    private Integer monthId;
    private Boolean isActive=true;
    private Integer status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String invoiceDocument;
    private Double gst;

    private List<InvoiceItem> invoiceItemList;
}
