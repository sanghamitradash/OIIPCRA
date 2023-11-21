package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.OiipcraWaterSpreadDto;
import com.orsac.oiipcra.dto.PaperDto;
import com.orsac.oiipcra.repositoryImpl.OiipcraWaterSpreadRepositoryImpl;
import com.orsac.oiipcra.service.OiipcraWaterSpreadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class OiipcraWaterSpreadServiceImpl implements OiipcraWaterSpreadService {

    @Autowired
    private OiipcraWaterSpreadRepositoryImpl oiipcraWaterSpreadRepositoryImpl;
    @Override
    public OIIPCRAResponse getDistinctWaterSpreadYear() {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<OiipcraWaterSpreadDto> year=new ArrayList<>();
        year=oiipcraWaterSpreadRepositoryImpl.getDistinctWaterSpreadYear();
        if(year!=null){
            response.setStatus(1);
            response.setData(year);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All WaterSpread Year");
        }
        else{
            response.setStatus(1);
            response.setData(year);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All WaterSpread Year");
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getDistinctWaterSpreadMonthByYear(String year) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<OiipcraWaterSpreadDto> month=new ArrayList<>();
        month=oiipcraWaterSpreadRepositoryImpl.getDistinctWaterSpreadMonthByYear(year);
        if(month!=null){
            response.setStatus(1);
            response.setData(month);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All WaterSpread Month");
        }
        else{
            response.setStatus(1);
            response.setData(month);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All WaterSpread Month");
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getWaterSpreadAreaByYearAndMonth(Integer finYrId,Integer monthId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        List<OiipcraWaterSpreadDto> data=new ArrayList<>();
        data=oiipcraWaterSpreadRepositoryImpl.getWaterSpreadAreaByYearAndMonth(finYrId,monthId);;
        if(data!=null){
            response.setStatus(1);
            response.setData(data);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All WaterSpread Data");
        }
        else{
            response.setStatus(1);
            response.setData(data);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage("All WaterSpread Data");
        }
        return response;
    }
}
