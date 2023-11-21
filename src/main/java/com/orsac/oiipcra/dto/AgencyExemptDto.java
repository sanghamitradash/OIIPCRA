package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyExemptDto {
    private Integer exemptId;
    private String exemptName;
    private boolean active;
    private Integer createdBy;
    private String createdOn ;
    private Integer updatedBy;
    private String updatedOn;

}
