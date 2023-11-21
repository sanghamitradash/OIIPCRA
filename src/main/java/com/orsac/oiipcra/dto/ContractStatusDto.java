package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractStatusDto {
    private Integer statusId;
    private String statusName;
    private String contractNumber;
    private Integer contractId;
    private Integer contractTypeId;
    private String contractType;
}
