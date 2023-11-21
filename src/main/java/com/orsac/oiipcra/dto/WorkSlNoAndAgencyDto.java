package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkSlNoAndAgencyDto {
    private  Integer workId;
    private String workName;
    private String workIdentificationCode;
    private Integer agencyCount;
    private List<FinancialBidDto> agency;
 }
