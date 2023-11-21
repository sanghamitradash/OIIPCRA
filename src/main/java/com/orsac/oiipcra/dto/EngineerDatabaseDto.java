package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineerDatabaseDto {
    private String circleName;
    private String miDivisionName;
    private String ee;
    private BigInteger mobileNo;
    private String email;
    private Integer designationId;
}
