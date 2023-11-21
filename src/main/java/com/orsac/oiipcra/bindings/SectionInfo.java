package com.orsac.oiipcra.bindings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionInfo {
    private int distId;
    private int miDivisionId;
    private int miSubDivisionId;
    private int sectionId;
    private String miSectionName;
}
