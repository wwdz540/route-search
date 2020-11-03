package com.shangqiao56.tms.rms.controller;

import com.shangqiao56.Entity;
import com.shangqiao56.tms.rms.route.model.SqStation;
import com.shangqiao56.tms.rms.route.service.impl.SqLoadedRouteServiceImpl;
import com.shangqiao56.tms.rms.route.store.SqStationStore;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private SqStationStore stationStore;

    @PutMapping
    public void putStation(@RequestBody StationEntity station){
        SqStation mStation = (SqStation) stationStore.getStation(station.getId());
        if(mStation == null){       //不存在，创建保存
            SqLoadedRouteServiceImpl.MyStation myStation =
                    new SqLoadedRouteServiceImpl.MyStation(station.getId(),station.getName());
            stationStore.saveStation(myStation);
        }else{
            mStation.setName(station.getName());
        }
    }


     public static class StationEntity implements Entity{
        private Long id;
        private String name;

         @Override
         public Long getId() {
             return id;
         }

         public void setId(Long id) {
             this.id = id;
         }

         @Override
         public String getName() {
             return name;
         }

         public void setName(String name) {
             this.name = name;
         }
     }
}
