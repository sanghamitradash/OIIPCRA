package com.orsac.oiipcra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AyacutDto {
    List<CropCycleAyacutDto> ayacut;
}
