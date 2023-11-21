package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.bindings.ApprovalStatusInfo;
import com.orsac.oiipcra.props.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApprovalStatusRepository {

    @Autowired
    private AppProperties aapProps;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbc;

   public List<ApprovalStatusInfo> getApprovalStatus(int currentApvStatus){
       MapSqlParameterSource sqlParam = new MapSqlParameterSource();
       String QueryString = "SELECT id, name FROM oiipcra_oltp.approval_status_m where is_active=true";

       if(currentApvStatus==-1 || currentApvStatus==1 || currentApvStatus==4){
           QueryString += " AND id in (aapProps.getMessages().get(AppConstants.APPROVE_ID),aapProps.getMessages().get(AppConstants.REJECTED_ID))";
       }else if(currentApvStatus==2 || currentApvStatus==3){
           QueryString += " AND id in( aapProps.getMessages().get(AppConstants.RE_PROVISIONAL_ID))";
       }
       sqlParam.addValue("currentApvStatus", currentApvStatus);
       return namedJdbc.query(QueryString,sqlParam,new BeanPropertyRowMapper<>(ApprovalStatusInfo.class));
    }

}
