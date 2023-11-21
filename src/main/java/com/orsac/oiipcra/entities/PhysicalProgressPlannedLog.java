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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "physical_progress_planned_log")
public class PhysicalProgressPlannedLog {

    @Id
    @SequenceGenerator(name = "physical_progress_planned_log_sequence", sequenceName = "physical_progress_planned_log_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "physical_progress_planned_log_sequence")
    @Column(name = "id")
    private Integer id;
    @Column(name = "tank_id")
    private Integer tankId;
    @Column(name = "total_length_of_canal_as_per_estimate")
    private Double totalLengthOfCanalAsPerEstimate;
    @Column(name = "no_of_cd_structures_to_be_repared")
    private Integer noOfCdStructuresToBeRepared;
    @Column(name = "total_length_of_cad")
    private Double totalLengthOfCad;
    @Column(name = "contract_id")
    private Integer contractId;
    @Column(name = "tank_m_id")
    private Integer tankMId;
    @Column(name = "estimate_id")
    private Integer estimateId;
    @Column(name = "is_active")
    private boolean isActive;
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
}
