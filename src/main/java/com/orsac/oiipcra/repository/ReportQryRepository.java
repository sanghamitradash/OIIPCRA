package com.orsac.oiipcra.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ReportQryRepository {

    @Value("${dbschema}")
    private String DBSCHEMA;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    public List<Map<String, Object>> getReportInfo(int finYearId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String reportQuery = "SELECT * from oiipcra_oltp.expenditure4(:finyear)";
        sqlParam.addValue("finyear", finYearId);
        return namedJdbc.queryForList(reportQuery, sqlParam);
    }
}
