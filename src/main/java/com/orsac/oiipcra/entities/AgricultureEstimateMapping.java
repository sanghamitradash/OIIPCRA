package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agriculture_estimate_mapping")
public class AgricultureEstimateMapping {
    @Id
    @SequenceGenerator(name = "agriculture_estimate_mapping_sequence", sequenceName = "agriculture_estimate_mapping_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agriculture_estimate_mapping_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name = "dist_id")
    private Integer distId;

    @Column(name = "block_id")
    private Integer blockId;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "is_active")
    private Boolean activeFlag;

    @Column(name = "created_by")
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

    @Column(name = "division_id")
    private Integer divisionId;
}
