package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueDocumentDto {
    private Integer id;
    private Integer issueId;
    private String docName;
    private String docPath;
    private Integer createdBy;
    private String createdOn;
    private Integer updatedBy;
    private Boolean isActive;


}
