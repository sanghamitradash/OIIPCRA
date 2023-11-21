package com.orsac.oiipcra.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentDto {
    private BigDecimal expenditureAmount;
    private BigDecimal contractAmount;
    private BigDecimal contingencyAmount;




    //private Integer statusCount;
   // private BigDecimal expenditureAmount2;

    private BigDecimal estimatedAmount;
//    private BigDecimal estimatedAmount;

    private Integer componentId;
    private String componentName;
    private Integer parentId;
    private Integer masterHeadId;
    private String code;
    private Integer statusId;
    private Integer statusCount;
    private Integer divisionId;


    private BigDecimal expenditureVal1;
    private BigDecimal expenditureVal2;

    private BigDecimal expenditureVal3;


    private Integer noOfContractOnGoing;
    private Double adaptFinancialAllocationInApp;
    private Integer adaptBeneficiaries;
    private Double adaptAchievementPercentage;
    private Double adaptActualFundAllocated;
    private Integer noOfContractComplete;
    private Double adaptExpenditure;


    private Integer distId;
    private String distName;

    private Double estimate;
    private Double expenditure;
    private Double contract;


    private Integer noOfEstimateApproved;
    private BigDecimal totalApproxEstCost;

    private BigDecimal workWiseEstimateAmount;
    private BigDecimal consultancyWiseEstimateAmount;
    private BigDecimal goodsWiseEstimateAmount;

    private BigDecimal upToDateExpenditure;
    private BigDecimal workWiseExpenditureAmount;
    private BigDecimal consultancyWiseExpenditureAmount;
    private BigDecimal agricultureWiseExpenditureAmount;
    private Double totalCanalLength;
    private Double canalImproved;
    private Integer cdStructurePrepared;
    private Integer cdStructureToBePrepared;
    private Double totalCadLength;
    private Double cadConstructed;
    private Integer noOfOutletConstructed;


    private BigDecimal consultancyWiseContractAmount ;
    private BigDecimal workWiseContractAmount;
    private BigDecimal goodsWiseContractAmount;

    private BigDecimal goodsWiseExpenditureAmount;














}
