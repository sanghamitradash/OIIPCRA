package com.orsac.oiipcra.helper;

import com.orsac.oiipcra.dto.StepExcelDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class StepExcelExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    int typeId = 4;
    //private List<User> listUsers;

    public StepExcelExporter(int typeId) {

        workbook = new XSSFWorkbook();
        this.typeId = typeId;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeStepExcelHeader() {
        sheet = workbook.createSheet("StepExcelDetails");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        //common headings
        createCell(row, 0, "Sl No.", style);
        createCell(row, 1, "Activity Reference No Description", style);
        createCell(row, 2, "Activity Id", style);
        createCell(row, 3, "In Progress", style);
        createCell(row, 4, "Loan Credit No.", style);
        createCell(row, 5, "Component", style);
        createCell(row, 6, "Review Type", style);
        createCell(row, 7, "Category", style);
        createCell(row, 8, "Market Approach", style);
        createCell(row, 9, "Estimate Amount", style);
        createCell(row, 10, "Process Status", style);
        createCell(row, 11, "Activity Status", style);
        createCell(row, 12, "Terms of Reference Planned", style);
        createCell(row, 13, "Terms of Reference Actual", style);
        createCell(row, 14, "Draft Negotiate Contract Planned", style);
        createCell(row, 15, "Draft Negotiate Contract Actual", style);
        createCell(row, 16, "Notification of Intention of Award Planned", style);
        createCell(row, 17, "Notification of Intention of Award Actual", style);
        createCell(row, 18, "Signed Contract Planned", style);
        createCell(row, 19, "Signed Contract Actual", style);
//        createCell(row, 20, "Contract Amendments Planned", style);
        createCell(row, 20, "Contract Amendments Actual", style);
        createCell(row, 21, "Contract Completion Planned", style);
        createCell(row, 22, "Contract Completion Actual", style);
        createCell(row, 23, "Contract Termination Actual", style);
        createCell(row, 24, "Estimate Id", style);
        createCell(row, 25, "Contract id", style);

        //for cds
        if (typeId == 4) {
            createCell(row, 26, "CDS Id", style);
            createCell(row, 27, "Justification for Direct Selection Planned", style);
            createCell(row, 28, "Justification for Direct Selection Actual", style);
            createCell(row, 29, "Invitation Identified Consultant Planned", style);
            createCell(row, 30, "Invitation Identified Consultant Actual", style);
            createCell(row, 31, "Amendments to Terms of Reference Planned", style);
            createCell(row, 32, "Amendments to Terms of Reference Actual", style);
            createCell(row, 33, "Contract Amendments Planned", style);
        }

        //for qcbs
        else {
            createCell(row, 26, "QCBS Id", style);
            createCell(row, 27, "Expression of Interest Planned", style);
            createCell(row, 28, "Expression of Interest Actual", style);
            createCell(row, 29, "Shortlist of Consultants Planned", style);
            createCell(row, 30, "Shortlist of Consultants Actual", style);
            createCell(row, 31, "Draft Request for Proposals Planned", style);
            createCell(row, 32, "Shortlist and Draft Request for Proposals Actual", style);
            createCell(row, 33, "Request for Proposals as Issued Planned", style);
            createCell(row, 34, "Request for Proposals as Issued Actual", style);
            createCell(row, 35, "Amendments to Request for Proposals Planned", style);
            createCell(row, 36, "Amendments to Request for Proposals Actual", style);
            createCell(row, 37, "Opening of Technical Proposals Minutes Planned", style);
            createCell(row, 38, "Opening of Technical Proposals Minutes Actual", style);
            createCell(row, 39, "Evaluation of Technical Proposals Planned", style);
            createCell(row, 40, "Evaluation of Technical Proposals Actual", style);
            createCell(row, 41, "Opening of Financial Proposals Minutes Planned", style);
            createCell(row, 42, "Opening of Financial Proposals Minutes Actual", style);
        }

    }

    private void writeStepExcelData(List<StepExcelDto> stepExcelList) throws Exception {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        Integer slno = 1;

        for (StepExcelDto info : stepExcelList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String contractAmendmentsActual = "";
            String contractCompletionActual = "";
            String contract_termination_actual = "";
            String amendments_to_request_for_proposals_actual = "";
            String request_for_proposals_as_issued_actual = "";
            String request_for_proposals_as_issued_planned = "";
            String expression_of_interest_actual = "";
            String amendments_to_terms_of_reference_actual = "";
            String activityId = "";
            String draft_negotiated_contract_actual = "";
            String notification_of_intention_of_award_planned = "";
            String notification_of_intention_of_award_actual = "";
            String terms_of_reference_planned = "";
            String activity_status = "";
            String signed_contract_actual = "";
            String terms_of_reference_actual = "";
            String signed_contract_planned = "";
            String ativity_reference_no_description = "";
            String in_process = "";
            String loan_credit_no = "";
            String component = "";
            String review_type = "";
            String category = "";
            String market_approach = "";
            String estimated_amount = "";
            String process_status = "";
            String draft_negotiated_contract_planned = "";
            String contract_completion_planned = "";
            String estimate_id = "";
            String contract_id = "";
            String id = "";
            String justification_for_direct_selection_planned = "";
            String justification_for_direct_selection_actual = "";
            String invitation_identified_consultant_planned = "";
            String invitation_to_identified_consultant_actual = "";
            String amendments_to_terms_of_reference_planned = "";
            String getExpression_of_interest_planned = "";
            String getShort_list_of_consultants_planned = "";
            String getShort_list_of_consultants_actual = "";
            String getDraft_request_for_proposals_planned = "";
            String getShort_list_and_draft_request_for_proposals_actual = "";
            String getAmendments_to_request_for_proposals_planned = "";
            String getOpening_of_technical_proposals_minutes_planned = "";
            String getEvaluation_of_technical_proposals_planned = "";
            String getEvaluation_of_technical_proposals_actual = "";
            String getOpening_of_financial_proposals_minutes_planned = "";
            String getOpening_of_financial_proposals_minutes_actual = "";
            String opening_of_technical_proposals_minutes_actual="";

            if (info.getContract_amendments_actual() != null) {
                contractAmendmentsActual = dateFormat.format(info.getContract_amendments_actual());
            }
            if (info.getContract_completion_actual() != null) {
                contractCompletionActual = dateFormat.format(info.getContract_completion_actual());
            }
            if (info.getContract_termination_actual() != null) {
                contract_termination_actual = dateFormat.format(info.getContract_termination_actual());
            }
            if (info.getAmendments_to_request_for_proposals_actual() != null) {
                amendments_to_request_for_proposals_actual = dateFormat.format(info.getAmendments_to_request_for_proposals_actual());
            }
            if (info.getRequest_for_proposals_as_issued_actual() != null) {
                request_for_proposals_as_issued_actual = dateFormat.format(info.getRequest_for_proposals_as_issued_actual());
            }
            if (info.getRequest_for_proposals_as_issued_planned() != null) {
                request_for_proposals_as_issued_planned = dateFormat.format(info.getRequest_for_proposals_as_issued_planned());
            }
            if (info.getExpression_of_interest_actual() != null) {
                expression_of_interest_actual = dateFormat.format(info.getExpression_of_interest_actual());
            }
            if (info.getAmendments_to_terms_of_reference_actual() != null) {
                amendments_to_terms_of_reference_actual = dateFormat.format(info.getAmendments_to_terms_of_reference_actual());
            }
            if (info.getActivity_id() != null) {
                activityId = dateFormat.format(info.getActivity_id());
            }
            if (info.getDraft_negotiated_contract_actual() != null) {
                draft_negotiated_contract_actual = dateFormat.format(info.getDraft_negotiated_contract_actual());
            }
            if (info.getNotification_of_intention_of_award_planned() != null) {
                notification_of_intention_of_award_planned = dateFormat.format(info.getNotification_of_intention_of_award_planned());
            }
            if (info.getNotification_of_intention_of_award_actual() != null) {
                notification_of_intention_of_award_actual = dateFormat.format(info.getNotification_of_intention_of_award_actual());
            }
            if (info.getTerms_of_reference_planned() != null) {
                terms_of_reference_planned = dateFormat.format(info.getTerms_of_reference_planned());
            }
            if (info.getActivity_status() != null) {
                activity_status = info.getActivity_status();
            }
            if (info.getSigned_contract_actual() != null) {
                signed_contract_actual = dateFormat.format(info.getSigned_contract_actual());
            }
            if (info.getSigned_contract_planned() != null) {
                signed_contract_planned = dateFormat.format(info.getSigned_contract_planned());
            }
            if (info.getTerms_of_reference_actual() != null) {
                terms_of_reference_actual = dateFormat.format(info.getTerms_of_reference_actual());
            }
            if (info.getActivity_reference_no_description() != null) {
                ativity_reference_no_description = (info.getActivity_reference_no_description());
            }
            if (info.getIn_process() != null) {
                in_process = (info.getIn_process());
            }
            if (info.getLoan_credit_no() != null) {
                loan_credit_no = (info.getLoan_credit_no());
            }
            if (info.getComponent() != null) {
                component = (info.getComponent());
            }
            if (info.getReview_type() != null) {
                review_type = (info.getReview_type());
            }
            if (info.getCategory() != null) {
                category = (info.getCategory());
            }
            if (info.getMarket_approach() != null) {
                market_approach = (info.getMarket_approach());
            }
            if (info.getEstimated_amount() != null) {
                estimated_amount = (info.getEstimated_amount());
            }
            if (info.getProcess_status() != null) {
                process_status = (info.getProcess_status());
            }
            if (info.getDraft_negotiated_contract_planned() != null) {
                draft_negotiated_contract_planned = dateFormat.format(info.getDraft_negotiated_contract_planned());
            }
            if (info.getContract_completion_planned() != null) {
                contract_completion_planned = dateFormat.format(info.getContract_completion_planned());
            }
            if (info.getEstimate_id() != null) {
                estimate_id = (info.getEstimate_id().toString());
            }
            if (info.getContract_id() != null) {
                contract_id = (info.getContract_id().toString());
            }
            if (info.getId() != null) {
                id = info.getId().toString();
            }
            if (info.getJustification_for_direct_selection_planned() != null) {
                justification_for_direct_selection_planned = dateFormat.format(info.getJustification_for_direct_selection_planned());
            }
            if (info.getJustification_for_direct_selection_actual() != null) {
                justification_for_direct_selection_actual = dateFormat.format(info.getJustification_for_direct_selection_actual());
            }
            if (info.getInvitation_identified_consultant_planned() != null) {
                invitation_identified_consultant_planned = dateFormat.format(info.getInvitation_identified_consultant_planned());
            }
            if (info.getInvitation_to_identified_consultant_actual() != null) {
                invitation_to_identified_consultant_actual = dateFormat.format(info.getInvitation_to_identified_consultant_actual());
            }
            if (info.getAmendments_to_terms_of_reference_planned() != null) {
                amendments_to_terms_of_reference_planned = dateFormat.format(info.getAmendments_to_terms_of_reference_planned());
            }
            if (info.getExpression_of_interest_planned() != null) {
                getExpression_of_interest_planned = dateFormat.format(info.getExpression_of_interest_planned());
            }
            if (info.getShort_list_of_consultants_planned() != null) {
                getShort_list_of_consultants_planned = dateFormat.format(info.getShort_list_of_consultants_planned());
            }
            if (info.getShort_list_of_consultants_actual() != null) {
                getShort_list_of_consultants_actual = dateFormat.format(info.getShort_list_of_consultants_actual());
            }
            if (info.getDraft_request_for_proposals_planned() != null) {
                getDraft_request_for_proposals_planned = dateFormat.format(info.getDraft_request_for_proposals_planned());
            }
            if (info.getShort_list_and_draft_request_for_proposals_actual() != null) {
                getShort_list_and_draft_request_for_proposals_actual = dateFormat.format(info.getShort_list_and_draft_request_for_proposals_actual());
            }
            if (info.getAmendments_to_request_for_proposals_planned() != null) {
                getAmendments_to_request_for_proposals_planned = dateFormat.format(info.getAmendments_to_request_for_proposals_planned());
            }
            if (info.getOpening_of_technical_proposals_minutes_planned() != null) {
                getOpening_of_technical_proposals_minutes_planned = dateFormat.format(info.getOpening_of_technical_proposals_minutes_planned());
            }
            if (info.getEvaluation_of_technical_proposals_planned() != null) {
                getEvaluation_of_technical_proposals_planned = dateFormat.format(info.getEvaluation_of_technical_proposals_planned());
            }
            if (info.getEvaluation_of_technical_proposals_actual() != null) {
                getEvaluation_of_technical_proposals_actual = dateFormat.format(info.getEvaluation_of_technical_proposals_actual());
            }
            if (info.getOpening_of_financial_proposals_minutes_planned() != null) {
                getOpening_of_financial_proposals_minutes_planned = dateFormat.format(info.getOpening_of_financial_proposals_minutes_planned());
            }
            if (info.getOpening_of_financial_proposals_minutes_actual() != null) {
                getOpening_of_financial_proposals_minutes_actual = dateFormat.format(info.getOpening_of_financial_proposals_minutes_actual());
            }
            if (info.getOpening_of_technical_proposals_minutes_actual() != null) {
                opening_of_technical_proposals_minutes_actual = dateFormat.format(info.getOpening_of_technical_proposals_minutes_actual());
            }


            createCell(row, columnCount++, slno, style);
            createCell(row, columnCount++, ativity_reference_no_description, style);
            createCell(row, columnCount++, activityId, style);
            createCell(row, columnCount++, in_process, style);
            createCell(row, columnCount++, loan_credit_no, style);
            createCell(row, columnCount++, component, style);
            createCell(row, columnCount++, review_type, style);
            createCell(row, columnCount++, category, style);
            createCell(row, columnCount++, market_approach, style);
            createCell(row, columnCount++, estimated_amount, style);
            createCell(row, columnCount++, process_status, style);
            createCell(row, columnCount++, activity_status, style);
            createCell(row, columnCount++, terms_of_reference_planned, style);
            createCell(row, columnCount++, terms_of_reference_actual, style);
            createCell(row, columnCount++, draft_negotiated_contract_planned, style);
            createCell(row, columnCount++, draft_negotiated_contract_actual, style);
            createCell(row, columnCount++, notification_of_intention_of_award_planned, style);
            createCell(row, columnCount++, notification_of_intention_of_award_actual, style);
            createCell(row, columnCount++, signed_contract_planned, style);
            createCell(row, columnCount++, signed_contract_actual, style);
            createCell(row, columnCount++, contractAmendmentsActual, style);
            createCell(row, columnCount++, contract_completion_planned, style);
            createCell(row, columnCount++, contractCompletionActual, style);
            createCell(row, columnCount++, contract_termination_actual, style);
            createCell(row, columnCount++, estimate_id, style);
            createCell(row, columnCount++, contract_id, style);

            //for cds
            if (typeId == 4) {
                createCell(row, columnCount++, id, style);
                createCell(row, columnCount++, justification_for_direct_selection_planned, style);
                createCell(row, columnCount++, justification_for_direct_selection_actual, style);
                createCell(row, columnCount++, invitation_identified_consultant_planned, style);
                createCell(row, columnCount++, invitation_to_identified_consultant_actual, style);
                createCell(row, columnCount++, amendments_to_terms_of_reference_planned.toString(), style);
                createCell(row, columnCount++, amendments_to_terms_of_reference_actual, style);
            }
            //for qcbs
            else {
                createCell(row, columnCount++, id, style);
                createCell(row, columnCount++, getExpression_of_interest_planned, style);
                createCell(row, columnCount++, expression_of_interest_actual, style);
                createCell(row, columnCount++, getShort_list_of_consultants_planned, style);
                createCell(row, columnCount++, getShort_list_of_consultants_actual, style);
                createCell(row, columnCount++, getDraft_request_for_proposals_planned, style);
                createCell(row, columnCount++, getShort_list_and_draft_request_for_proposals_actual, style);
                createCell(row, columnCount++, request_for_proposals_as_issued_planned, style);
                createCell(row, columnCount++, request_for_proposals_as_issued_actual, style);
                createCell(row, columnCount++, getAmendments_to_request_for_proposals_planned, style);
                createCell(row, columnCount++, amendments_to_request_for_proposals_actual, style);
                createCell(row, columnCount++, getOpening_of_technical_proposals_minutes_planned, style);
                createCell(row, columnCount++, opening_of_technical_proposals_minutes_actual, style);
                createCell(row, columnCount++, getEvaluation_of_technical_proposals_planned, style);
                createCell(row, columnCount++, getEvaluation_of_technical_proposals_actual, style);
                createCell(row, columnCount++, getOpening_of_financial_proposals_minutes_planned, style);
                createCell(row, columnCount++, getOpening_of_financial_proposals_minutes_actual, style);
            }

            slno++;
        }
    }

    public void downloadStepExcel(HttpServletResponse response, List<StepExcelDto> stepExcelList) throws Exception {
        writeStepExcelHeader();
        writeStepExcelData(stepExcelList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
