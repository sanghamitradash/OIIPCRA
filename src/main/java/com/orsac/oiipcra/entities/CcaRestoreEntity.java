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
@Table(name = "cca_restore")
public class CcaRestoreEntity {
    @Id
    @SequenceGenerator(name = "cca_restore_sequence", sequenceName = "cca_restore_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cca_restore_sequence")
    @Column(name = "id")
    private Integer id;



    @Column(name = "hydrology_data_id")
    private Integer hydrologyDataId;

    @Column(name = "finyr")
    private Integer FinYr;

    @Column(name = "cca_restore")
    private Double ccaRestore;

    @Column(name = "expenditure")
    private Double expenditure;

    @Column(name = "irr")
    private Double irr;

    @Column(name = "benifit_cost_ratio")
    private  Double benifitCostRatio;

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
