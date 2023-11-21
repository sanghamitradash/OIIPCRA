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
@Table(name = "pip_details")
public class PipDetailsEntity {
    @Id
    @SequenceGenerator(name = "pip_dtls_sequence", sequenceName = "pip_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pip_dtls_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "pip_mapping_id")
    private Integer pipMappingId;

    @Column(name = "finyr_id")
    private Integer finyrId;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "physical_target")
    private Double physicalTarget;

    @Column(name = "unit_cost_rs")
    private Double unitCostRs;

    @Column(name = "financial_target")
    private Double financialTarget;

    @Column(name = "is_active")
    private Boolean activeFlag;

    @Column(name = "contract_amount")
    private Double contractAmount;

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
