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
@Table(name = "dir_contract_details")
public class DirEntity {
    @Id
    @SequenceGenerator(name = "dir_contract_sequence", sequenceName = "dir_contract_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dir")
    @Column(name = "id")
    private Integer id ;
    @Column(name = "contract_id")
    private Integer contractId ;
    @Column(name = "activity_reference_no_description")
    private String activityReferenceNoDescription ;
    @Column(name = "activity_id")
    private Integer activityId ;
    @Column(name = "in_process")
    private String  inProcess ;
    @Column(name = "loan_credit_no")
    private String  loanCreditNo ;
    @Column(name = "component")
    private String  component ;
    @Column(name = "review_type")
    private String  reviewType ;
    @Column(name = "procurement_category")
    private String   procurementCategory ;
    @Column(name = "evaluation_options")
    private String evaluationOptions ;
    @Column(name = "estimated_amount")
    private String estimatedAmount ;
    @Column(name = "high_sea_risk")
    private String highSeaRisk;
    @Column(name = "procurement_document_type")
    private String procurementDocumentType ;
    @Column(name = "process_status")
    private String processStatus ;
    @Column(name = "activity_status")
    private String activityStatus ;
    @Column(name = "justification_for_direct_selection_planned")
    private Date justificationForDirectSelectionPlanned ;
    @Column(name = "justification_for_direct_selection_actual")
    private Date justificationForDirectSelectionActual;
    @Column(name = "invitation_to_contractorPlanned")
    private Date invitationToContractorPlanned ;
    @Column(name = "invitation_to_contractor_actual")
    private Date invitationToContractorActual ;
    @Column(name = "draft_contract_planned")
    private Date draftContractPlanned ;
    @Column(name = "draft_contract_actual")
    private Date draftContractActual;
    @Column(name = "notification_of_intention_of_award_planned")
    private Date notificationOfIntentionOfAwardPlanned ;
    @Column(name = "notification_of_intention_of_award_actual")
    private Date notificationOfIntentionOfAwardActual ;
    @Column(name = "signed_contract_planned")
    private Date signedContractPlanned ;
    @Column(name = "signed_contract_actual")
    private Date signedContractActual ;
    @Column(name = "contract_amendments_actual")
    private Date contractAmendmentsActual ;
    @Column(name = "contract_completion_planned")
    private Date contractCompletionPlanned ;
    @Column(name = "contract_completion_actual")
    private Date contractCompletionActual ;
    @Column(name = "contract_termination_actual")
    private Date contractTerminationActual ;
    @Column(name = "estimate_id")
    private Integer estimateId;
    @Column(name = "is_active")
    private Boolean is_active;
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
