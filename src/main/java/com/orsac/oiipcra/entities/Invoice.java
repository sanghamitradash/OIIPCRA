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
@Table(name = "invoice_m")
public class Invoice {

        @Id
        @SequenceGenerator(name = "invoice_m_sequence", sequenceName = "invoice_m_new_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_m_sequence")
        @Column(name = "id")
        private Integer id;

        @Column(name = "invoice_no")
        private String invoiceNo;

        @Column(name = "contract_id")
        private Integer contractId;

        @Column(name ="invoice_amount")
        private double invoiceAmount;

        @Column(name = "gst")
        private double gst;

        @Column(name ="invoice_date")
        private Date invoiceDate;

        @Column(name = "agency_id")
        private Integer agencyId;

        @Column(name = "finyr_id")
        private Integer finyrId;

        @Column(name = "month_id")
        private Integer monthId;

        @Column(name="is_active",nullable = false)
        private Boolean isActive=true;

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

        @Column(name = "status")
        private Integer status;

        @Column(name = "invoice_document")
        private String invoiceDocument;





}

