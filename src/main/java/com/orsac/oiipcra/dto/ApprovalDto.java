package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalDto {
    private Integer approvedBy;
    private Integer approvalStatusId;
    private String approvalOrder;
    private Date approvalDate;
}
