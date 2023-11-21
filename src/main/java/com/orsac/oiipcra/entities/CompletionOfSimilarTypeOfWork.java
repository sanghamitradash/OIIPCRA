package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "completion_of_similar_type_work")
public class CompletionOfSimilarTypeOfWork {

    @Id
    @SequenceGenerator(name = "completion_of_similar_type_work_sequence", sequenceName = "completion_of_similar_type_work_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "completion_of_similar_type_work_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name = "bidder_id")
    private Integer bidderId;

    @Column(name = "finyr_id")
    private Integer finyrId;

    @Column(name = "value")
    private Double value;

    @Column(name = "is_maximum")
    private Boolean isMaximum;

    @Column(name = "similar_work_amount")
    private Double similarWorkAmount;

    @Column(name = "completed_amount")
    private Double completedAmount;

    @Column(name = "percentage_completed")
    private Double percentageCompleted;

    @Column(name = "is_active")
    private Boolean active;

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
    @Column(name = "executed_year")
    private Integer executedYear;

}
