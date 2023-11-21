package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveStatusDto {

    private Integer approvedBy;
    private Integer approvalStatus;
    private Date approvalDate;
    private String approvedRemarks;
}
