package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalDetailsDto {

    private Integer id;

    private Integer tankId;

    private Boolean previousMajorInvestments;

    private Integer finYear;

    private Integer schemeFunded;

    private String pwtHeldDate;

    private Integer renoWorkTakenUp;

    private String impProposedNow1;

    private String impProposedNow2;

    private Boolean isActive;

    private Integer createdBy;

    private Date createdOn;

    private Integer updatedBy;

    private Date updatedOn;

    private Integer tankOtherDetailsId;

    private String finyrname;
}
