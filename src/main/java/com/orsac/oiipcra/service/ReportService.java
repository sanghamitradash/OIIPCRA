package com.orsac.oiipcra.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String,Object>> getReport(int finYearId);
}
