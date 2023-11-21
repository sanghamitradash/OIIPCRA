package com.orsac.oiipcra.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContractMappingRepositoryImpl {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

    public boolean deactivateContractById(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE contract_mapping SET is_active = false WHERE contract_id=:id";
        sqlParam.addValue("id",id);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update>0;
    }
    public boolean deactivateContractDocument(int id){
        MapSqlParameterSource sqlParam = new MapSqlParameterSource();
        String qry = "UPDATE oiipcra_oltp.contract_document SET is_active = false WHERE contract_id=:id";
        sqlParam.addValue("id",id);
        Integer update = namedJdbc.update(qry, sqlParam);
        return update>0;
    }

}
