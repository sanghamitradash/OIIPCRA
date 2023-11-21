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
@Table(name = "cds_contract_details")
public class CdsEntity {
    @Id
    @SequenceGenerator(name = "cds_contract_sequence", sequenceName = "cds_contract_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cds_contract_sequence")
    @Column(name = "id")
    private Integer id ;
    @Column(name = "contract_id")
    private Integer contractId ;
    @Column(name = "activity_reference_no_description")
    private String activityReferenceNoDescription ;
    @Column(name = "activity_id")
    private Integer activityId ;
    @Column(name = "in_process")
    private String inProcess ;
    @Column(name = "loan_credit_no")
    private String loanCreditNo ;
    @Column(name = "component")
    private String component ;
    @Column(name = "review_type")
    private String reviewType ;
    @Column(name = "category")
    private String category;
    @Column(name = "market_approach")
    private String marketApproach ;
    @Column(name = "estimated_amount")
    private String estimatedAmount ;
    @Column(name = "process_status")
    private String processStatus ;
    @Column(name = "activity_status")
    private String activityStatus ;
    @Column(name = "terms_of_reference_planned")
    private String termsOfReferencePlanned ;
    @Column(name = "terms_of_reference_actual")
    private String termsOfReferenceActual ;
    @Column(name = "justification_for_direct_selection_planned")
    private String justificationForDirectSelectionPlanned ;
    @Column(name = "justification_for_direct_selection_actual")
    private String justificationForDirectSelectionActual;
    @Column(name = "invitation_identified_consultant_planned")
    private String invitationIdentifiedConsultantPlanned ;
    @Column(name = "invitation_identified_consultant_actual")
    private String invitationToIdentifiedConsultantActual ;
    @Column(name = "amendments_to_terms_of_reference_planned")
    private String amendmentsToTermsOfReferencePlanned ;
    @Column(name = "amendments_to_terms_of_reference_actual")
    private String amendmentsToTermsOfReferenceActual ;
    @Column(name = "draft_negotiated_contract_planned")
    private String draftNegotiatedContractPlanned ;
    @Column(name = "draft_negotiated_contract_actual")
    private String draftNegotiatedContractActual ;
    @Column(name = "notification_of_intention_of_award_planned")
    private String notificationOfIntentionOfAwardPlanned ;
    @Column(name = "notification_of_intention_of_award_actual")
    private String notificationOfIntentionOfAwardActual ;
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
