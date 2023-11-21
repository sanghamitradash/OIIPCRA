package com.orsac.oiipcra.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "page_index")
public class PageIndexEntity {
    @Id
    @SequenceGenerator(name = "page_index_sequence", sequenceName = "page_index_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_index_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "hydrology_data_id")
    private Integer hydrologyDataId;
    @Column(name = "introduction")
    private String introduction;
    @Column(name = "hydrology_design")
    private String hydrologyDesign;
    @Column(name = "project_features")
    private String projectFeatures;
    @Column(name = "cost_benefits")
    private String costBenefits;
    @Column(name = "cost_estimat_of_impt")
    private String costEstimatOfImpt;

    @Column(name = "post_operation_m")
    private String postOperationM;

    @Column(name = "implementn_schedule")
    private String implementnSchedule;
    @Column(name = "drawings")
    private String drawings;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "created_by")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;




}
