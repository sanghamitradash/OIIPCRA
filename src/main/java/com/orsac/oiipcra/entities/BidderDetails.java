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
@Table(name = "bidder_details")
public class BidderDetails {

    @Id
    @SequenceGenerator(name = "bidder_details_sequence", sequenceName = "bidder_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bidder_details_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "work_id")
    private Integer workId;

    @Column(name = "agency_id")
    private Integer agencyId;

    @Column(name = "license_validity")
    private Boolean licenseValidity;

    @Column(name = "affidavit_validity")
    private Boolean affidavitValidity;

    @Column(name = "paper_cost_amount")
    private Double paperCostAmount;

    @Column(name = "paper_cost_submission_type")
    private Integer paperCostSubmissionType;

    @Column(name = "is_paper_cost_valid")
    private Boolean isPaperCostValid;

    @Column(name = "emd_bank_name")
    private String emdBankName;

    @Column(name = "emd_amount")
    private Double emdAmount;

    @Column(name = "emd_deposit_type")
    private Integer emdDepositType;

    @Column(name = "is_emd_valid")
    private Boolean isEmdValid;

    @Column(name = "completion_work_value_qualified")
    private Boolean completionWorkValueQualified;

    @Column(name = "turn_over_qualified")
    private Boolean turnOverQualified;

    @Column(name = "liquid_asset_qualified")
    private Boolean liquidAssetQualified;

    @Column(name = "is_bid_qualified")
    private Boolean isBidQualified;

    @Column(name = "max_bid_capacity")
    private Double maxBidCapacity;

    @Column(name = "work_in_hand")
    private Double workInHand;

    @Column(name = "bid_result")
    private Integer bidResult;

    @Column(name = "award_type")
    private Integer awardType;

    @Column(name = "bid_final_rank")
    private Integer bidFinalRank;

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
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "bidder_category_id")
    private Integer bidderCategoryId;

    @Column(name = "is_bid_awarded")
    private Boolean isBidAwarded;

}
