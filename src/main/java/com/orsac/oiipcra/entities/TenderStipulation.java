package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tender_stipulation")
public class TenderStipulation {


    @Id
    @SequenceGenerator(name = "tender_stipulation_sequence", sequenceName = "tender_stipulation_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tender_stipulation_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

/*    @Column(name = "work_id")
    private Integer workId;*/

    @Column(name = "similar_work_value")
    private Double similarWorkValue;

    @Column(name = "similar_work_completion")
    private Double similarWorkCompletion;

    @Column(name = "annual_financial_turnover")
    private Double annualFinancialTurnover;

    @Column(name = "previous_yr_weightage")
    private Double previousYrWeightage;

    @Column(name = "credit_lines_amount")
    private Double creditLinesAmount;

    @Column(name = "bid_capacity_turnover")
    private Double bidCapacityTurnover;

    @Column(name = "completion_of_work_value_target")
    private Double completionOfWorkValueTarget;

    @Column(name = "turnover_target")
    private Double turnoverTarget;

    @Column(name = "liquid_asset_target")
    private Double liquidAssetTarget;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name="is_active",nullable = false)
    private Boolean isActive=true;

}
