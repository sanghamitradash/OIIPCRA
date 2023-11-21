package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepthImageDto {

    private Integer id;
    private Integer depthId;
    private String imageName;
}
