package com.orsac.oiipcra.bindings;

import com.orsac.oiipcra.dto.ExpenditureMappingDto;
import com.orsac.oiipcra.entities.ExpenditureMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureDataInfo {

    private Integer id;
    private Integer type;
    private Integer finyrId;
    private String finYear;
    private Integer monthId;
    private String monthName;
    private Double value;
    private Integer deviceId;
    private Date paymentDate;
    private Integer invoiceId;
    private Integer paymentTypeId;
    private String paymentType ;
    private Integer contractId;
    private Integer activityId;
    private Integer estimateId;
    private Integer tankId;
    private Integer level;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer agencyId;
    private String agencyName;
    private String panNo;
    private String description;


    private List<ExpenditureMapping> expenditureMapping;



}
