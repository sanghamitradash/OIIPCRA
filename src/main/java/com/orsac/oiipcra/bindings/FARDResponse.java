package com.orsac.oiipcra.bindings;

import com.orsac.oiipcra.dto.DenormalizedFinancialAchievementDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FARDResponse {
    private int status;
    private String message;
    public List<DenormalizedFinancialAchievementDto> denormalizedFinancial ;
}
