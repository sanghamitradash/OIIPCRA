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
@Table(name = "activity_estimate_mapping")
public class ActivityAddEntity {
    @Id
    @SequenceGenerator(name = "activity_extimate_sequence", sequenceName = "activity_estimate_mapping_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_extimate_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "activity_id")
    private Integer activityId;
    @Column(name = "level_id")
    private Integer levelId;
    @Column(name = "status_id")
    private Integer statusId;
    @Column(name = "work_type")
    private Integer workType;
    @Column(name = "approval_order")
    private String approvalOrder;
    @Column(name = "name_of_work")
    private String nameOfWork;
    @Column(name = "technical_sanction_no")
    private String technicalSanctionNo;
    @Column(name = "project_id")
    private Integer projectId;
    @Column(name = "procurement_type")
    private Integer procurementType;
    @Column(name = "district_zone_identification")
    private String districtZoneIdentification;
    @Column(name = "nol_of_tor_by_wb")
    private Date nolOfTorByWb;
    @Column(name = "approval_ref")
    private String approvalRef;
    @Column(name = "correspondance_file_no")
    private String correspondanceFileNo;
    @Column(name = "period_of_completion")
    private Integer periodOfCompletion;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "estimated_amount")
    private Double estimatedAmount;
    @Column(name = "approved_status")
    private Integer approvedStatus;
    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "is_active")
    private boolean activeFlag;

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

    @Column(name = "approval_date")
    private Date approvalDate;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_path")
    private String documentPath;

    @Column(name = "estimate_type")
    private Integer estimateType;

    @Column(name = "review_type")
    private Integer reviewType;

    @Column(name = "market_approach")
    private Integer marketApproach;

    @Column(name = "loan_credit_no")
    private String loanCreditNo;

    @Column(name = "procurement_document_type")
    private Integer procurementDocumentType;

    @Column(name = "high_sea_sh_risk")
    private String highSeaShRisk;

    @Column(name = "procurement_process")
    private Integer procurementProcess;

    @Column(name = "evaluation_options")
    private Integer evaluationOptions;
}
