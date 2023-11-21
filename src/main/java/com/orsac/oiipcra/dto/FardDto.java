package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class FardDto {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("message")
    private String message;

    @JsonProperty("denormalizedFinancial")
    private List<FardFinacialDto> denormalizedFinancial;
}
