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
@Table(name = "liquid_asset_availability")
public class LiquidAssetAvailability {

    @Id
    @SequenceGenerator(name = "liquid_asset_availability_sequence", sequenceName = "liquid_asset_availability_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "liquid_asset_availability_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "bidder_id")
    private Integer bidderId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "credit_amount")
    private Double creditAmount;

    @Column(name = "liquidity_amount")
    private Double liquidityAmount;

    @Column(name = "total_liquid_asset")
    private Double totalLiquidAsset;

    @Column(name = "agency_id")
    private Integer agencyId;

    @Column(name="is_active",nullable = false)
    private Boolean active;

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

}
