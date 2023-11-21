package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderPublishDateCaluculateDto {
    private Date tenderPublicationDate;
    private Date dateOfFirstCorrigendum;
    private Date dateOfSecondCorrigendum;
    private Date publicationPeriodUpto;

}
