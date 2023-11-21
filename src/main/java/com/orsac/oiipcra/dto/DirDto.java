package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirDto {
    private Integer id ;
    private Integer contractId ;
    private String activityReferenceNoDescription ;
    private Integer activityId ;
    private String  inProcess ;
    private String  loanCreditNo ;
    private String  component ;
    private String  reviewType ;
    private String   procurementCategory ;
    private String evaluationOptions ;
    private String estimatedAmount ;
    private String highSeaRisk;
    private String procurementDocumentType ;
    private String processStatus ;
    private String activityStatus ;
    private Date justificationForDirectSelectionPlanned ;
    private Date justificationForDirectSelectionActual;
    private Date invitationToContractorPlanned ;
    private Date invitationToContractorActual ;
    private Date draftContractPlanned ;
    private Date draftContractActual;
    private Date notificationOfIntentionOfAwardPlanned ;
    private Date notificationOfIntentionOfAwardActual ;
    private Date signedContractPlanned ;
    private Date signedContractActual ;
    private Date contractAmendmentsActual ;
    private Date contractAmendmentsPlanned ;
    private Date contractCompletionActual ;
    private Date contract_termination_actual ;
    private Boolean isActive;
    private Integer createdBy ;
    private Date createdOn ;
    private Integer updatedBy ;
    private Date updatedOn ;
    private Integer estimateId;
}
