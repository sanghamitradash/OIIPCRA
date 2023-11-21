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
@Table(name = "qcbs_contract_details")
public class QcbsEntity {
    @Id
    @SequenceGenerator(name = "qcbs_contract_sequence", sequenceName = "qcbs_contract_details_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qcbs_contract_sequence")
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
    private Date termsOfReferencePlanned ;
    @Column(name = "terms_of_reference_actual")
    private Date termsOfReferenceActual ;
    @Column(name = "expression_of_interest_planned")
    private Date expressionOfInterestPlanned ;
    @Column(name = "expression_of_interest_actual")
    private Date expressionOfInterestActual ;
    @Column(name = "short_list_of_consultants_planned")
    private Date shortListOfConsultantsPlanned ;
    @Column(name = "short_list_of_consultants_actual")
    private Date shortListOfConsultantsActual ;
    @Column(name = "draft_request_for_proposals_planned")
    private Date draftRequestForProposalsPlanned ;
    @Column(name = "short_list_and_draft_request_for_proposals_actual")
    private Date shortListAndDraftRequestForProposalsActual ;
    @Column(name = "request_for_proposals_as_issued_planned")
    private Date requestForProposalsAsIssuedPlanned;
    @Column(name = "request_for_proposals_as_issued_actual")
    private Date requestForProposalsAsIssuedActual ;
    @Column(name = "amendments_to_request_for_proposals_planned")
    private Date amendmentsToRequestForProposalsPlanned ;
    @Column(name = "amendments_to_request_for_proposals_actual")
    private Date amendmentsToRequestForProposalsActual ;
    @Column(name = "opening_of_technical_proposals_minutes_planned")
    private Date openingOfTechnicalProposalsMinutesPlanned ;
    @Column(name = "opening_of_technical_proposals_minutes_actual")
    private Date openingOfTechnicalProposalsMinutesActual;
    @Column(name = "evaluation_of_technical_proposals_planned")
    private Date evaluationOfTechnicalProposalsPlanned ;
    @Column(name = "evaluation_of_technical_proposals_actual")
    private Date evaluationOfTechnicalProposalsActual ;
    @Column(name = "opening_of_financial_proposals_minutes_planned")
    private Date openingOfFinancialProposalsMinutesPlanned ;
    @Column(name = "opening_of_financial_proposals_minutes_actual")
    private Date openingOfFinancialProposalsMinutesActual ;
    @Column(name = "draft_negotiated_contract_planned")
    private Date draftNegotiatedContractPlanned ;
    @Column(name = "draft_negotiated_contract_actual")
    private Date draftNegotiatedContractActual ;
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
    @Column(name = "contract_amendments_planned")
    private Date  contractCompletionPlanned ;
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
