package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.dto.GrievanceDto;
import com.orsac.oiipcra.dto.IssueTrackerDto;
import com.orsac.oiipcra.dto.WaterSpreadDto;

import java.util.List;

public interface GraphRepository {
    List<WaterSpreadDto> getWaterSpreadMonth(String year,String projectId);
   // List<WaterSpreadDto> getWaterSpreadByProjectId(String projectId);

    List<WaterSpreadDto> getWaterSpreadByProjectId(String projectId, String yearId);

    Integer getIssueCount(Integer tankId, String year);

    Integer getGrievanceCount(Integer tankId,String year);

    Integer getResolvedCount(Integer tankId, String year);

    Integer getRejectedCount(Integer tankId, String year);

    Integer getResolvedGrievanceCount(Integer tankId, String year);

    Integer getRejectedGrievanceCount(Integer tankId, String year);
}
