package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QcbsDifferenceDto {
    private Integer termsOfReferenceDifference;
    private Integer expressionOfInterestDifference;

    private Integer shortListOfConsultantsDifference;
    private Integer requestForProposalsAsIssuedDifference;
    private Integer amendmentsToRequestForProposalsDifference;
    private Integer openingOfTechnicalProposalMinutesDifference;
    private Integer evaluationOfTechnicalProposalsDifference;
    private Integer openingOfFinancialProposalsMinutesDifference;
    private Integer draftNegotiatedContractDifference;
    private Integer notificationOfIntentionOfAwardDifference;
    private Integer signedContractDifference;

    private Integer contractCompletionDifference;




}