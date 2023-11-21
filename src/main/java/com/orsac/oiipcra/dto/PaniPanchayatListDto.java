package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaniPanchayatListDto {

    private Integer id;
//    private Integer blockId;
    private String paniPanchayatName;
}
