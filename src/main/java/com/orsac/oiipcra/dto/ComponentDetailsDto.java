package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentDetailsDto {
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Long physicalTarget;
    private String financialYear;
    private String description;
    private Long financialTarget;
    private Double totalEstimatedAmount;
    private Integer totalEstimateApproved;
    private Integer totalEstimatePending;
    private Integer totalEstimateRejected;
    private Double totalExpenditure;
    private Integer totalCompleted;
    private Integer totalOngoing;
    private Integer totalSubComponent;
    private Integer totalActivity;
    private Integer totalSubActivity;

    private Integer totalSubComponentImplemented;
    private Integer totalActivityImplemented;
    private Integer totalSubActivityImplemented;

    private Integer totalSubComponentApproved;
    private Integer totalActivityApproved;
    private Integer totalSubActivityApproved;

    private Boolean isTerminal;
    private String  startDate;
    private String  deptName;
    private BigDecimal estimateAmount;
    private BigDecimal expenditureAmount;


}
