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
@Table(name = "tender_result")
public class TenderResult {

    @Id
    @SequenceGenerator(name = "tender_result_sequence", sequenceName = "tender_result_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tender_result_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "work_id")
    private Integer workId;

    @Column(name = "bidder_id")
    private Integer bidderId;

    @Column(name = "lottery_required")
    private Boolean lotteryRequired;

    @Column(name = "date_of_lottery")
    private Date dateOfLottery;

    @Column(name = "award_type")
    private Integer awardType;

    @Column(name = "acceptance_letter_no")
    private String acceptanceLetterNo;

    @Column(name = "contract_id")
    private Integer contractId;

    @Column(name = "completion_period")
    private Double completionPeriod;

    @Column(name = "completion_date")
    private Date completionDate;

    @Column(name = "agreement_no")
    private String agreementNo;

    @Column(name ="legal_case")
    private String legalCase;

    @Column(name = "remarks")
    private String remarks;

    @Column(name="is_active",nullable = false)
    private Boolean isActive=true;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;

    @Column(name="tender_awarded")
    private Boolean tenderAwarded;


    @Column(name="review_fin_bid_date")
    private Date reviewFinBidDate;

    @Column(name="tender_not_awarded_reason")
    private String tenderNotAwardedReason;
}
