package com.orsac.oiipcra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MessageDto {
    @JsonProperty("message")
    private String  message;

    @JsonProperty("data")
    private DataDto  data;
}
