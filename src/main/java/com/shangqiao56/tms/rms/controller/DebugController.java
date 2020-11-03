package com.shangqiao56.tms.rms.controller;


import com.shangqiao56.tms.rms.route.core.StationStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/debug")
@RestController
public class DebugController {

    @Resource
    private StationStore stationStore;

    @GetMapping
    public String get(@RequestParam("id") Long id){
        return stationStore.getStation(id).toString();
    }


}
