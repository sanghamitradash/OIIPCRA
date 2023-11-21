package com.orsac.oiipcra.controller;

import com.orsac.oiipcra.bindings.OIIPCRAResponse;
import com.orsac.oiipcra.service.OiipcraWaterSpreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/waterSpread")
public class OiipcraWaterSpreadController {
    @Autowired
    private OiipcraWaterSpreadService oiipcraWaterSpreadService;
    @PostMapping("/getDistinctWaterSpreadYear")
    public OIIPCRAResponse getDistinctWaterSpreadYear(){
        return oiipcraWaterSpreadService.getDistinctWaterSpreadYear();
    }

    @PostMapping("/getDistinctWaterSpreadMonthByYear")
    public OIIPCRAResponse getDistinctWaterSpreadMonthByYear(@RequestParam String year){
        return oiipcraWaterSpreadService.getDistinctWaterSpreadMonthByYear(year);
    }
    @PostMapping("/getWaterSpreadAreaByYearAndMonth")
    public OIIPCRAResponse getWaterSpreadAreaByYearAndMonth(@RequestParam Integer finYrId,@RequestParam Integer monthId){
        return oiipcraWaterSpreadService.getWaterSpreadAreaByYearAndMonth(finYrId,monthId);
    }
}
