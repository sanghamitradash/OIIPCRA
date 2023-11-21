package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QcbsDto {
    private Integer id ;
    private Integer contractId ;
    private String activityReferenceNoDescription ;
    private Integer activityId ;
    private String inProcess ;
    private String loanCreditNo ;
    private String component ;
    private String reviewType ;
    private String category;
    private String marketApproach ;
    private String estimatedAmount ;
    private String processStatus ;
    private String activityStatus ;

    private Date termsOfReferencePlanned ;
    private Date termsOfReferenceActual ;
    private Date expressionOfInterestPlanned ;
    private Date expressionOfInterestActual ;
    private Date shortListOfConsultantsPlanned ;
    private Date shortListOfConsultantsActual ;
    private Date draftRequestForProposalsPlanned ;
    private Date shortListAndDraftRequestForProposalsActual ;
    private Date requestForProposalsAsIssuedPlanned;
    private Date requestForProposalsAsIssuedActual ;
    private Date amendmentsToRequestForProposalsPlanned ;
    private Date amendmentsToRequestForProposalsActual ;
    private Date openingOfTechnicalProposalsMinutesPlanned ;
    private Date openingOfTechnicalProposalsMinutesActual;
    private Date evaluationOfTechnicalProposalsPlanned ;
    private Date evaluationOfTechnicalProposalsActual ;
    private Date openingOfFinancialProposalsMinutesPlanned ;
    private Date openingOfFinancialProposalsMinutesActual ;
    private Date draftNegotiatedContractPlanned ;
    private Date draftNegotiatedContractActual ;
    private Date notificationOfIntentionOfAwardPlanned ;
    private Date notificationOfIntentionOfAwardActual ;
    private Date signedContractPlanned ;
    private Date signedContractActual ;
    private Date contractAmendmentsActual ;
    private Date  contractCompletionPlanned ;
    private Date contractCompletionActual ;
    private Date contractTerminationActual ;
    private Integer estimateId;

}
