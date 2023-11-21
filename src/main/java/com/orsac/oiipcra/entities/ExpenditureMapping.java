package com.orsac.oiipcra.entities;

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
@Table(name = "expenditure_mapping")
public class ExpenditureMapping {

    @Id
    @SequenceGenerator(name = "expenditure_mapping_sequence", sequenceName = "expenditure_mapping_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenditure_mapping_sequence")
    @Column(name = "id")
    private Integer id;


    @Column(name = "expenditure_id")
    private Integer expenditureId;

    @Column(name = "contract_id")
    private Integer contractId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "tender_notice_id")
    private Integer tenderNoticeId;

    @Column(name = "division_id")
    private Integer divisionId;

    @Column(name = "is_active")
    private boolean isActive;


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
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "block_id")
    private Integer blockId;


}
