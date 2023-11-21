package com.orsac.oiipcra.service;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;

public interface OiipcraWaterSpreadService {
    OIIPCRAResponse getDistinctWaterSpreadYear();
    OIIPCRAResponse getDistinctWaterSpreadMonthByYear(String year);
    OIIPCRAResponse getWaterSpreadAreaByYearAndMonth(Integer finYrId,Integer monthId);

}
