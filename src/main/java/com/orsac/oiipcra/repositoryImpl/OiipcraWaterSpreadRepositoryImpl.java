package com.orsac.oiipcra.repositoryImpl;

import com.orsac.oiipcra.dto.OiipcraWaterSpreadDto;
import com.orsac.oiipcra.dto.PaperDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Slf4j
@Repository
public class OiipcraWaterSpreadRepositoryImpl {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    public List<OiipcraWaterSpreadDto> getDistinctWaterSpreadYear() {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT year as year,finyr_id as finYrId from oiipcra_oltp.oiipcra_water_spread ";

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(OiipcraWaterSpreadDto.class));
    }
    public List<OiipcraWaterSpreadDto> getDistinctWaterSpreadMonthByYear(String year) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "SELECT DISTINCT month as month,month_id as monthId from oiipcra_oltp.oiipcra_water_spread where year=:year";

        sqlParam.addValue("year",year);
        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(OiipcraWaterSpreadDto.class));
    }
    public List<OiipcraWaterSpreadDto> getWaterSpreadAreaByYearAndMonth(Integer finYrId,Integer monthId) {
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "select sum(area) as area,month,year from oiipcra_oltp.oiipcra_water_spread where "+
                "month_id=:monthId and finyr_id=:finYr group by month,year";
        sqlParam.addValue("monthId",monthId);
        sqlParam.addValue("finYr",finYrId);

        return namedJdbc.query(qry, sqlParam, new BeanPropertyRowMapper<>(OiipcraWaterSpreadDto.class));
    }

}
