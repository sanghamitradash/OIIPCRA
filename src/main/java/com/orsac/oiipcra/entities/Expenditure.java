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
@Table(name="expenditure_data")
public class Expenditure {

    @Id
    @SequenceGenerator(name = "expenditure_data_sequence", sequenceName = "expenditure_data_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expenditure_data_sequence")
    @Column(name = "id")
    private Integer id;


    @Column(name = "contract_id")
    private Integer contractId;

    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "finyr_id")
    private Integer finyrId;

    @Column(name = "month_id")
    private Integer monthId;

    @Column(name = "value")
    private Double value;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "level")
    private Integer level;

    @Column(name = "type")
    private Integer type;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "is_active",nullable = false)
    private Boolean active;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_on")
    private Date createdOn ;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_on")
    private Date updatedOn ;
/*

    @Column(name = "invoice_id")
    private Integer invoiceId;
*/

    @Column(name = "payment_type")
    private Integer paymentType;

/*    @Column(name = "estimate_id")
    private Integer estimateId;*/

   /* @Column(name = "tank_id")
    private Integer tankId;*/

    @Column(name = "agency_id")
    private Integer agencyId;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name = "agency_name")
    private String agencyName;

    @Column(name = "pan_no")
    private String panNo;
    @Column(name = "description")
    private String description;

}
