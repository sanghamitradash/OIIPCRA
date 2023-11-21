package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "historical_details")
public class HistoricalDetails {

    @Id
    @SequenceGenerator(name = "historical_details", sequenceName = "historical_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historical_details")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tank_id")
    private Integer tankId;

    @Column(name = "previous_major_investments")
    private Boolean previousMajorInvestments;

    @Column(name = "fin_year")
    private Integer finYear;

    @Column(name = "scheme_funded")
    private Integer schemeFunded;

    @Column(name = "pwt_held_date")
    private Date pwtHeldDate;

    @Column(name = "reno_work_taken_up")
    private Integer renoWorkTakenUp;

    @Column(name = "imp_proposed_now_1")
    private String impProposedNow1;

    @Column(name = "imp_proposed_now_2")
    private String impProposedNow2;

    @Column(name="is_active",nullable = false)
    private Boolean isActive;

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

    @Column(name = "tank_other_details_id")
    private Integer tankOtherDetailsId;

}
