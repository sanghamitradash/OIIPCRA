package com.orsac.oiipcra.bindings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice_status")
public class InvoiceStatus {

    @Id
    @SequenceGenerator(name = "invoice_status_sequence", sequenceName = "invoice_status_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_item_sequence")
    @Column(name = "id")
    private Integer id;

    private String name;
}
