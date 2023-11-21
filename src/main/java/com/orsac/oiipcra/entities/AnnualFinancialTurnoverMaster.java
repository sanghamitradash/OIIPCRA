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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "finyr_turnover")
public class AnnualFinancialTurnoverMaster {

    @Id
    @SequenceGenerator(name = "finyr_turnover_master_sequence", sequenceName = "finyr_turnover_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "finyr_turnover_master_sequence")
    @Column(name = "id")
    private Integer id;


    @Column(name = "finyr_id")
    private Integer finyrId;

    @Column(name = "value")
    private Double value;

    @Column(name = "is_maximum")
    private Boolean maximum;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "agency_id")
    private Integer agencyId;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "bidder_id")
    private Integer bidderId;

    @Column(name = "equivalent")
    private Double equivalent;

    @Column(name = "turn_over_required")
    private Double turnOverRequired;

    @Column(name = "turn_over_maximum_year")
    private String turnOverMaximumYear;

    @Column(name = "turn_over_maximum_value")
    private Double turnOverMaximumValue;


}
