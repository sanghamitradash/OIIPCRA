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
@Table(name = "shlc_meeting")
public class ShlcMeetingEntity {

    @Id
    @SequenceGenerator(name = "shlc_meeting_id_seq", sequenceName = "shlc_meeting_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shlc_meeting_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "meeting_serial_no")
    private Integer meetingSerialNo;

    @Column(name = "meeting_sequence_no")
    private String meetingSequenceNo;

    @Column(name = "date_of_meeting")
    private Date dateOfMeeting;

    @Column(name = "proceedings_issued_lt_nc")
    private String proceedingsIssuedLtNc;

    @Column(name = "committee_formed_date")
    private Date committeeFormedDate;

    @Column(name = "vote_of_thanks")
    private String voteOfThanks;

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

    @Column(name = "proceedings_issue_letter_date")
    private Date proceedingsIssueLetterDate;
}
