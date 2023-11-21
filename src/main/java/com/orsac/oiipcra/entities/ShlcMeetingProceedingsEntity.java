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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shlc_meeting_proceedings")
public class ShlcMeetingProceedingsEntity {

    @Id
    @SequenceGenerator(name = "shlc_meeting_proceedings_id_seq", sequenceName = "shlc_meeting_proceedings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shlc_meeting_proceedings_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "shlc_meeting_id")
    private Integer shlcMeetingId;

    @Column(name = "momendrum")
    private String momendrum;

    @Column(name = "details_discussion")
    private String detailsDiscussion;

    @Column(name = "committee_proceedings")
    private String committeeProceedings;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn = new Date(System.currentTimeMillis());

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;
}
