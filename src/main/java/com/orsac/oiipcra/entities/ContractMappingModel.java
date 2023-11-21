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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract_mapping")
public class ContractMappingModel {
    @Id
    @SequenceGenerator(name = "contract_mapping_sequence", sequenceName = "contact_mapping_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_mapping_sequence")
    @Column(name = "id")
    private Integer id;
    @Column(name = "contract_id")
    private Integer contractId;
    @Column(name = "activity_id")
    private Integer activityId;
    @Column(name = "dist_id")
    private Integer distId;
    @Column(name = "block_id")
    private Integer blockId;
    @Column(name = "gp_id")
    private Integer gpId;
    @Column(name = "village_id")
    private Integer villageId;
    @Column(name = "division_id")
    private Integer divisionId;
    @Column(name = "sub_division_id")
    private Integer subDivisionId;
    @Column(name = "section_id")
    private Integer sectionId;
    @Column(name = "tank_id")
    private Integer tankId;
    @Column(name = "tender_notice_id")
    private Integer tenderNoticeId;
    @Column(name = "tender_id")
    private Integer tenderId;
    @Column(name = "estimate_id")
    private Integer estimateId;
    @Column(name = "tank_wise_contract_amount")
    private Double tankWiseContractAmount;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

}
