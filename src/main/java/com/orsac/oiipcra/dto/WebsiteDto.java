package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteDto {
    private Integer blockId;
    private Integer start;
    private Integer length;
    private Integer draw;
}
