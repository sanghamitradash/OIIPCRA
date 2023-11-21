package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenderNoticePublishDto {
    private Integer id;
    private Integer draftTenderNoticeId;
    private Integer distId;
    private String districtName;
    private String bidId;
    private String workName;
    private Date closingDate;
    private String draftTenderNoticeDoc;
    private String bidDocument;
    private Integer type;
    private String typeName;
    private Boolean active;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer draw;
}
