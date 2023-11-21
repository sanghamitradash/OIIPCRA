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
@Table(name = "tender_corrigendum")
public class TenderCorrigendum {

    @Id
    @SequenceGenerator(name = "tender_corrigendum_sequence", sequenceName = "tender_corrigendum_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tender_corrigendum_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name="technical_bid_opening_date")
    private Date technicalBidOpeningDate ;

    @Column(name = "bid_submission_date")
    private Date bidSubmissionDate;

    @Column(name="financial_bid_opening_date")
    private Date financialBidOpeningDate;

    @Column(name = "name_of_work")
    private String nameOfWork;

    @Column(name = "pre_bid_meeting_type")
    private Integer preBidMeetingType;

    @Column(name="pre_bid_meeting_date")
    private Date preBidMeetingDate;

    @Column(name="tender_publication_date")
    private Date tenderPublicationDate ;

    @Column(name="publication_period_upto")
    private Date publicationPeriodUpto;

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
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name="tender_type")
    private Integer tenderType ;

    @Column(name="tender_level_id")
    private Integer tenderLevelId;

    @Column(name="finyr_id")
    private Integer finyrId;

    @Column(name = "tender_opening_date")
    private Date tenderOpeningDate;

    @Column(name = "date_of_tender_notice")
    private Date dateOfTenderNotice;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name = "tender_status")
    private Integer tenderStatus;

    @Column(name = "meeting_location")
    private Integer meetingLocation;

}
