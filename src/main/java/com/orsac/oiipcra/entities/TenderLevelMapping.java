package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tender_notice_level_mapping")
public class TenderLevelMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private int id;

    @Column(name="tender_id")
    private int tenderId;

    @Column(name="activity_id")
    private int activityId;

    @Column(name="dist_id")
    private int distId ;

    @Column(name="block_id")
    private int blockId ;

    @Column(name="gp_id")
    private int gpId ;

    @Column(name="village_id")
    private int villageId;

    @Column(name="division_id")
    private int divisionId;

    @Column(name="sub_division_id")
    private int subDivisionId;

    @Column(name="section_id")
    private int sectionId;

    @Column(name="tank_id")
    private int tankId;

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
    private boolean active=true;
}
