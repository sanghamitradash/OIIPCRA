package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class FardPhysicalCallDto {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("message")
    private String message;

    @JsonProperty("denormalizedAchievement")
    private List<FardPhysicalDto> denormalizedAchievement;
}
