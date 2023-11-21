package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountDto {
    private Integer totalSubComponent;
    private Integer totalActivity;
    private Integer totalSubActivity;
}
