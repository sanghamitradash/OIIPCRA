package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CivilWorkStatusDivisionDto {
    private Integer divisionId;
    private String divisionName;
    private Integer noOfTanks;
    private Double ccaInHa;
    private Integer worksTakenInTanks;
    private Double contractAmount;
    private Integer worksCompleted;
    private Double upToDateExpenditure;
    private Integer noOfTanksOngoing;
    private Double balanceContractValue;
    private Integer noOfTanksDropped;
    private Integer noOfTankTakenUp;
    private BigDecimal totalEstimateCost;
    private BigDecimal balanceWorkForContract;
    private String contractAmountString;
    private String upToDateExpenditureString;
    private String ccaInHaString;

}
