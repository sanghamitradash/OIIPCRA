package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyListDto {

    private Integer userId;

    private Integer agencyId;

    private  String panNo;

    private Integer distId;

    private Integer licenseClassId;

    private Integer page;

    private Integer size;

    private String sortOrder;

    private String sortBy;








}
