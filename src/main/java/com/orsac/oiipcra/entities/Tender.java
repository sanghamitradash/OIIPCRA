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
@Table(name = "tender_m")
public class Tender {

    @Id
    @SequenceGenerator(name = "tender_master_sequence", sequenceName = "tender_m_id_seq1", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tender_master_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "bid_id")
    private String bidId;

    @Column(name="approval_for_procurement_date")
    private Date approvalForProcurementDate ;

    @Column(name="technical_bid_opening_date")
    private Date technicalBidOpeningDate ;

    @Column(name="technical_bid_opening_date_revised")
    private Date technicalBidOpeningDateRevised;

    @Column(name = "bid_submission_date")
    private Date bidSubmissionDate;

    @Column(name="bid_submission_date_revised")
    private Date bidSubmissionDateRevised ;

    @Column(name="financial_bid_opening_date")
    private Date financialBidOpeningDate ;

    @Column(name = "name_of_work")
    private String nameOfWork ;

    @Column(name = "pre_bid_meeting_type")
    private Integer preBidMeetingType ;

    @Column(name="pre_bid_meeting_date")
    private Date preBidMeetingDate;

    @Column(name="tender_publication_date")
    private Date tenderPublicationDate ;

    @Column(name="publication_period_upto")
    private Date publicationPeriodUpto;

    /*@Column(name="date_of_first_corrigendum")
    private Date dateOfFirstCorrigendum;

    @Column(name="date_of_second_corrigendum")
    private Date dateOfSecondCorrigendum ;*/

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
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name="tender_type")
    private Integer tenderType ;

    @Column(name="tender_level_id")
    private Integer tenderLevelId;

    @Column(name="finyr_id")
    private Integer finyrId;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name = "tender_status")
    private Integer tenderStatus;

    @Column(name = "meeting_location")
    private Integer meetingLocation;

    @Column(name = "tender_opening_date")
    private Date tenderOpeningDate;

    @Column(name = "date_of_tender_notice")
    private Date dateOfTenderNotice;

    @Column(name = "approved_status")
    private Integer approvedStatus;

    @Column(name = "approved_remarks")
    private String approvedRemarks;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved_on")
    @CreationTimestamp
    private Date approvedOn;


    @Column(name = "activity_id")
    private Integer activityId;





}
