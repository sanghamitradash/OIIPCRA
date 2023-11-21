package com.orsac.oiipcra.controller;


import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.service.TankSurveyService;
import com.orsac.oiipcra.serviceImpl.TankSurveyServiceImpl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TankSurveyController {

    private static final Logger logger = LoggerFactory.getLogger(TankSurveyController.class);

    @Autowired
    TankSurveyService tankSurveyService;

    @Autowired
    private TankSurveyServiceImpl tankSurveyServiceImpl;


    @PostMapping("/getUserListByUserId")
    public OIIPCRAResponse getUserListByUserId(@RequestBody String body)  {
    JSONObject jsonObject = new JSONObject(body);
    OIIPCRAResponse response = new OIIPCRAResponse();
    Map<String, Object> result = new HashMap<>();
       try {
           List<BoundaryDto> userList = tankSurveyService.getUserListByUserId(jsonObject.getInt("id"));
           if(!userList.isEmpty() && userList.size() >0){
               result.put("userList", userList);
               response.setData(result);
               response.setStatus(1);
               response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
           }else{
               response.setData(result);
               response.setStatus(1);
               response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
               response.setMessage("Record not found.");
           }
       } catch (Exception e){
           response = new OIIPCRAResponse(0,
                   new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                   e.getMessage(),
                   result);
       }
       return response;
   }

}



