package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StepExcelDto {

    //common data for cds & qcbs
    private Integer id;
    private Integer contract_id;
    private Integer activity_id;
    private String activity_reference_no_description;
    private String in_process;
    private String loan_credit_no;
    private String component;
    private String review_type;
    private String category;
    private String market_approach;
    private String estimated_amount;
    private String process_status;
    private String activity_status;
    private Date terms_of_reference_planned;
    private Date terms_of_reference_actual;
    private Date draft_negotiated_contract_planned;
    private Date draft_negotiated_contract_actual;
    private Date notification_of_intention_of_award_planned;
    private Date notification_of_intention_of_award_actual;
    private Date signed_contract_planned;
    private Date signed_contract_actual;
    private Date contract_amendments_actual=null;
    private Date contract_completion_planned;
    private Date contract_completion_actual;
    private Date contract_termination_actual;
    private Boolean is_active;
    private Integer created_by;
    private Date created_on;
    private Integer updated_by;
    private Date updated_on;
    private Integer estimate_id;

    //qcbs data
    private Date expression_of_interest_planned;
    private Date expression_of_interest_actual;
    private Date short_list_of_consultants_planned;
    private Date short_list_of_consultants_actual;
    private Date draft_request_for_proposals_planned;
    private Date short_list_and_draft_request_for_proposals_actual;
    private Date request_for_proposals_as_issued_planned;
    private Date request_for_proposals_as_issued_actual;
    private Date amendments_to_request_for_proposals_planned;
    private Date amendments_to_request_for_proposals_actual;
    private Date opening_of_technical_proposals_minutes_planned;
    private Date opening_of_technical_proposals_minutes_actual;
    private Date evaluation_of_technical_proposals_planned;
    private Date evaluation_of_technical_proposals_actual;
    private Date opening_of_financial_proposals_minutes_planned;
    private Date opening_of_financial_proposals_minutes_actual;

    //cds data
    private Date justification_for_direct_selection_planned;
    private Date justification_for_direct_selection_actual;
    private Date invitation_identified_consultant_planned;
    private Date invitation_to_identified_consultant_actual;
    private Date amendments_to_terms_of_reference_planned;
    private Date amendments_to_terms_of_reference_actual;

}
