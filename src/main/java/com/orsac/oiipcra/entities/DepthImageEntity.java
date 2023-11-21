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
@Table(name = "depth_image")
public class DepthImageEntity {

    @Id
    @SequenceGenerator(name = "depth_image_sequence", sequenceName = "depth_image_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "depth_image_sequence")
    @Column(name = "id")
    private Integer id;

    @Column(name ="depth_id")
    private Integer depthId;

    @Column(name = "image_name")
    private String imageName;

    @Column(name="created_by")
    private Integer createdBy;

    @Column(name="created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name="updated_by")
    private Integer updatedBy;

    @Column(name="updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    @Column(name = "is_active")
    private boolean isActive;

}
