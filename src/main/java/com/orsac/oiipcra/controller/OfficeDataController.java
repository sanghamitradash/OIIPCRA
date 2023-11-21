package com.orsac.oiipcra.controller;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.dto.BlockBoundaryDto;
import com.orsac.oiipcra.dto.OfficeDataDto;
import com.orsac.oiipcra.entities.MenuMaster;
import com.orsac.oiipcra.entities.OfficeDataEntity;
import com.orsac.oiipcra.service.OfficeDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/officeData")
public class OfficeDataController {
    @Autowired
    OfficeDataService officeDataService;
    @PostMapping("/getOfficeData")
    public OIIPCRAResponse getOfficeData(@RequestParam (required = false)Integer distId,
                                         @RequestParam(required = false) Integer divisionId,
                                         @RequestParam(required = false) Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            List<OfficeDataDto> officeData = officeDataService.getOfficeData(distId,divisionId,userId);
                result.put("officeData", officeData);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @PostMapping("/createOfficeData")
    public OIIPCRAResponse createOfficeData(@RequestBody OfficeDataDto officeData) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            OfficeDataEntity obj=new OfficeDataEntity();
            BeanUtils.copyProperties(officeData,obj);
            OfficeDataEntity officeDataObj = officeDataService.saveOfficeData(obj);
            result.put("officeData", officeDataObj);
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.CREATED));
            response.setMessage("New OfficeData Created");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }
}
