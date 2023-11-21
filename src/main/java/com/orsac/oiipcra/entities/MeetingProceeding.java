package com.orsac.oiipcra.entities;


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
@Table(name = "meeting_proceeding")
public class MeetingProceeding {

    @Id
    @SequenceGenerator(name = "meeting_proceeding_sequence", sequenceName = "meeting_proceeding_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meeting_proceeding_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "tender_id")
    private Integer tenderId;

    @Column(name = "meeting_type")
    private Integer meetingType;

    @Column(name = "no_of_participants")
    private Integer noOfParticipants;

    @Column(name = "no_of_online_clarification")
    private Integer noOfOnlineClarification;

    @Column(name = "no_of_clarification_sought")
    private Integer noOfClarificationSought;

    @Column(name = "clarification_disposed_online")
    private Integer clarificationDisposedOnline;

    @Column(name = "clarification_disposed_in_pre_bid")
    private Integer clarificationDisposedInPreBid;

    @Column(name = "created_by")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name="is_active")
    private Boolean active;











}
