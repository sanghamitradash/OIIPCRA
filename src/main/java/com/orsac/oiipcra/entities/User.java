package com.orsac.oiipcra.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_m")
public class User {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_m_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "designation_id")
    private Integer designationId;

    @Column(name = "dept_id")
    private Integer departmentId;

    @Column(name = "password")
    private String password;

    @Column(name = "user_level_id")
    private Integer userLevelId;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "is_active")
    private Boolean active=true;

    @Column(name = "is_agency")
    private Boolean isAgency;

    @Column(name = "agency_id")
    private Integer agencyId;

    @Column(name = "is_surveyor")
    private Boolean surveyor;

    @Column(name = "sub_dept_id")
    private Integer subDeptId;

    @Column(name = "created_by", updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer createdBy;

    @Column(name = "created_on", updatable = false)
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_by")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer updatedBy;

    @Column(name = "updated_on")
    @JsonIgnore
    @UpdateTimestamp
    private Date updatedOn;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dept_id",insertable = false,updatable = false)
    private DepartmentMaster departmentMaster;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "designation_id",insertable = false,updatable = false)
    private DesignationMaster designationMaster;

/*    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id")
            }
    )
    private Set<Role> roles;*/

}
