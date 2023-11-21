package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompletionOfSimilarTypeOfWorkDto {
    private Integer id;
    private Integer bidderId;
    private Integer finyrId;
    private String finYrName;
    private Double value;
    private Boolean isMaximum;
    private Double similarWorkAmount;
    private Double completedAmount;
    private Double percentageCompleted;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer executedYear;
    private String executedYearName;
}
