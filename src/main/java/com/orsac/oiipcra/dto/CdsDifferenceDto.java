package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdsDifferenceDto {
    private Integer termsOfReferenceDifference;
    private Integer justificationForDirectSelectionDifference;
    private Integer invitationToIdentifiedConsultantDifference;
    private Integer amendmentsToTermsOfReferenceDifference;
    private Integer draftNegotiatedContractDifference;
    private Integer notificationOfIntentOfAwarDifference;
    private Integer signedContractDifference;
    private Integer contractCompletionPlanned;


}
