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
@Table(name = "committee_members")
public class CommitteeMembers {

    @Id
    @SequenceGenerator(name = "committee_members_sequence", sequenceName = "committee_members_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "committee_members_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "meeting_proceeding_id")
    private Integer meetingProceedingId;

    @Column(name = "designation_id")
    private Integer designationId;
    @Column(name = "member_id")
    private Integer memberId;

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
