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
@Table(name = "pre_bid_clarifications")
public class PreBidClarifications {

    @Id
    @SequenceGenerator(name = "pre_bid_clarifications_sequence", sequenceName = "pre_bid_clarifications_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pre_bid_clarifications_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "meeting_proceeding_id")
    private Integer meetingProceedingId;

    @Column(name = "pre_bid_clarifications_sought_by_bidders")
    private String  preBidClarificationsSoughtByBidders;

    @Column(name = "pre_bid_clarifications_disposed_with_reply")
    private String  preBidClarificationsDisposedWithReply;

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
