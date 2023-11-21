package com.orsac.oiipcra.dto;

import com.orsac.oiipcra.entities.PhysicalProgressConsultancy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhysicalProgressConsultancyUpdateDto {
    List<PhysicalProgressUpdateDto> physicalProgressWork;
    List<PhysicalProgressConsultancy> physicalProgressConsultancy;
}
