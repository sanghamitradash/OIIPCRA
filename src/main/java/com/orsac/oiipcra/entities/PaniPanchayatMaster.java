package com.orsac.oiipcra.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pani_panchayat_m")
public class PaniPanchayatMaster {

    @Id
    @SequenceGenerator(name = "pani_panchayat_m_id_seq", sequenceName = "pani_panchayat_m_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pani_panchayat_m_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "block_id")
    private Integer blockId;


}
