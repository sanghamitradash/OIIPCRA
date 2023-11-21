package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenditureYearMonthDto {


    private Integer monthId;
    private String monthName;
    private BigDecimal expenditure;
    private Integer finYrId;
    private String finYrName;
    private Integer totalCompletionPeriod;
    private String agreementNumber;
    private String agencyName;
    private Integer contractAmount;
    private String stipulatedDateOfCompletion;
    private String stipulatedDateOfComencement;

}
