package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDocumentDto {
    private Integer id;
    private Integer contractId;
    private String docName;
    private String docPath;
    private boolean isActive;
    private Integer createdBy;
    private Date createdOn ;
    private Integer updatedBy;

}
