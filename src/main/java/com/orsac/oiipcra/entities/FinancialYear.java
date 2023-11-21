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
@Table(name="fin_year_m")
public class FinancialYear {


    @Id
    @SequenceGenerator(name = "fin_year_sequence", sequenceName = "fin_year_m_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fin_year_m_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name ="start_date")
    private Date startDate;

    @Column(name ="end_date" )
    private Date endDate;

    @Column(name = "is_active")
    @JsonProperty
    private boolean isActive;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_on")
    private Date createdOn ;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_on")
    private Date updatedOn ;

    @Column(name = "name")
    private String name;
}
