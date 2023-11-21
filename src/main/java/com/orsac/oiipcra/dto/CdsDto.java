package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdsDto {
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
    private String termsOfReferencePlanned ;
    private String termsOfReferenceActual ;
    private String justificationForDirectSelectionPlanned ;
    private String justificationForDirectSelectionActual;
    private String invitationIdentifiedConsultantPlanned ;
    private String invitationToIdentifiedConsultantActual ;
    private String amendmentsToTermsOfReferencePlanned ;
    private String amendmentsToTermsOfReferenceActual ;
    private String draftNegotiatedContractPlanned ;
    private String draftNegotiatedContractActual ;
    private String notificationOfIntentionOfAwardPlanned ;
    private String notificationOfIntentionOfAwardActual ;
    private Integer estimateId;
}
