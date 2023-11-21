package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDto {

    private Integer id;
    private Integer itemSerialNo;
    private String description;
    private Double quantity;
    private Double rate;
    private Double total;
}
