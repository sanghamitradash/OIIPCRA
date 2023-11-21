package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tender_published")
public class TenderPublished {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name="serial_no")
    private Integer serialNo;

    @Column(name="tender_published_type")
    private Integer tenderPublishedType;

    @Column(name = "name")
    private String name;

    @Column(name="newspaper_type")
    private Integer newspaperType;

    @Column(name="published_date")
    private Date publishedDate;

    @Column(name="is_active")
    private Boolean active;

    @Column(name = "created_by")
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    //  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name="publication_period_upto")
    private Date publicationPeriodUpto;

    @Column(name = "document")
    private String document;

}
