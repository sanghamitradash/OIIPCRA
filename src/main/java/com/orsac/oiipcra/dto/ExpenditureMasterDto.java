package com.orsac.oiipcra.dto;


import com.orsac.oiipcra.entities.ExpenditureMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureMasterDto {

    private Integer id;
    private Integer contractId;
    private Integer activityId;


    private Integer finyrId;


    private Integer monthId;


    private Double value;

    private Integer deviceId;


    private Date paymentDate;


    private Boolean isActive;


    private Integer createdBy;


    private Date createdOn ;


    private Integer updatedBy;


    private Date updatedOn ;


    private Integer paymentType;
    private  Integer level;
    private Integer type;
    private Integer agencyId;
    private String agencyName;
    private String panNo;
    private String description;

  private Integer estimateId;
    private List<ExpenditureMapping> expenditureMapping;




}
