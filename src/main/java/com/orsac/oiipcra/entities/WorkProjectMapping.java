package com.orsac.oiipcra.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.orsac.oiipcra.dto.WorkProjectDto;
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
@Table(name = "work_project_mapping")
public class WorkProjectMapping  {

    @Id
    @SequenceGenerator(name = "work_project_mapping_sequence", sequenceName = "work_project_mapping_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_project_mapping_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_notice_id")
    private Integer tenderNoticeId;

    @Column(name = "tank_id")
    private Integer tankId;

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

    @Column(name="is_active",nullable = false)
    private Boolean isActive=true;

    @Column(name = "tender_id")
    private Integer tenderId;





}
