package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.print.attribute.IntegerSyntax;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract_m")
public class ContractMaster {

    @Id
    @SequenceGenerator(name = "contract_sequence", sequenceName = "contract_m_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contract_sequence")
    @Column(name = "id")
    private Integer id;
    @Column(name = "contract_code")
    private String contractCode;
    @Column(name = "contract_name")
    private String contractName;
    @Column(name = "contract_date")
    private Date contractDate;
    @Column(name = "contract_status_id")
    private Integer contractStatusId;

    @Column(name = "contract_type_id")
    private Integer contractTypeId;
    @Column(name = "contract_level_id")
    private Integer contractLevelId;
    @Column(name = "procurement_made_for")
    private Integer procurementMadeFor;

    @Column(name = "zone")
    private String zone;
    @Column(name = "approval_order_date")
    private Date approvalOrderDate;
    @Column(name = "work_description")
    private String workDescription;
    @Column(name = "eoi_id")
    private String eoiId;
    @Column(name = "rfp_issued_on")
    private Date rfpIssuedOn;
    @Column(name = "rfp_received_on")
    private Date rfpReceivedOn;
    @Column(name = "area_id")
    private Integer areaId;

    @Column(name = "correspondence_file_no")
    private String correspondenceFileNo;
    @Column(name = "contract_number")
    private String contractNumber;
    @Column(name = "agency_id")
    private Integer agencyId;
    @Column(name = "contract_amount")
    private Double contractAmount;
    @Column(name = "gst")
    private Double gst;

    @Column(name = "stipulated_date_of_comencement")
    private Date stipulatedDateOfComencement;
    @Column(name = "stipulated_date_of_completion")
    private Date stipulatedDateOfCompletion;
    @Column(name = "approval_order")
    private String approvalOrder;
    @Column(name = "tachnical_sanction_no")
    private String tachnicalSanctionNo;
    @Column(name = "work_id")
    private Integer workId;
    @Column(name = "estimated_cost")
    private Double estimatedCost;
    @Column(name = "awarded_as")
    private Integer awardedAs;
    @Column(name = "agreement_number")
    private String agreementNumber;
    @Column(name = "loa_issued_no")
    private String loaIssuedNo;
    @Column(name = "loa_issued_date")
    private Date loaIssuedDate;
    @Column(name = "rate_of_premium")
    private Double rateOfPremium;
    @Column(name = "actual_date_of_commencement")
    private Date actualDateOfCommencement;
    @Column(name = "actual_date_of_completion")
    private Date actualDateOfCompletion;
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

    @Column(name = "finyr_id")
    private Integer finyrId;

    @Column(name = "date_eoi")
    private Date dateEoi;
    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "estimate_id")
    private Integer estimateId;

    @Column(name= "work_type_id")
    private Integer workTypeId;

    @Column(name= "type_id")
    private Integer typeId;
    @Column(name= "tender_id")
    private Integer tenderId;

    @Column(name= "procurement_type_id")
    private Integer procurementTypeId;


}
