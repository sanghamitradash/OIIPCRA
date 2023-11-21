package com.orsac.oiipcra.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mapstruct.Named;

import javax.persistence.*;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financial_bid_details")
public class FinancialBidDetails {


    @Id
    @SequenceGenerator(name = "financial_bid_details_sequence", sequenceName = "financial_bid_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "financial_bid_details_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "work_id")
    private Integer workId;

   /* @Column(name = "tender_notice_id")
    private Integer tenderNoticeId;*/

    @Column(name = "bidder_id")
    private Integer bidderId;

    @Column(name = "amount_quoted")
    private Double amountQuoted;

    @Column(name = "amount_percentage")
    private Double amountPercentage;

    @Column(name = "additional_performance_sec_required")
    private Double additionalPerformanceSecRequired;

    @Column(name = "additional_submitted")
    private Double additionalSubmitted;

    @Column(name = "balance_aps_required")
    private Double balanceApsRequired;

    @Column(name = "review_tech_bid_date")
    private Date reviewTechBidDate;

    @Column(name = "review_fin_bid_date")
    private Date reviewFinBidDate;

    @Column(name = "work_in_hand")
    private Double workInHand;

    @Column(name = "balance_bid_capacity")
    private Double balanceBidCapacity;

    @Column(name="is_active",nullable = false)
    private Boolean active;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private java.util.Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private java.util.Date updatedOn;

}
