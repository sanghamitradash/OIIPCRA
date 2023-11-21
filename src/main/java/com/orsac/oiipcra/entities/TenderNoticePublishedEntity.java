package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tender_notice_published")
public class TenderNoticePublishedEntity {
    @Id
    @SequenceGenerator(name = "tender_notice_published_sequence", sequenceName = "tender_notice_published_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tender_notice_published_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name =  "draft_tender_notice_id")
    private Integer draftTenderNoticeId;

//    @Column(name = "dist_id")
//    private Integer distId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "bid_id")
    private String bidId;

    @Column(name = "work_name")
    private String workName;

    @Column(name = "closing_date")
    private Date closingDate;

    @Column(name = "draft_tender_notice_doc")
    private String draftTenderNoticeDoc;

    @Column(name = "bid_document")
    private String bidDocument;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;
}
