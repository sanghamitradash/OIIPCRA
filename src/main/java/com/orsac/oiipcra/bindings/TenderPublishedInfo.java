package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderPublishedInfo {

    private Integer id;
    private Integer tenderId;
    private Integer serialNo;
    private Integer tenderPublishedType;
    private String name;
    private Integer newspaperType;
    private Date publishedDate;
    private Date publicationPeriodUpto;
    private Boolean active;
    private Integer createdBy;
    private Integer updatedBy;
    private String tenderPublishedName;
    private  String newsPaperTypeName;
    private  String document;
}
