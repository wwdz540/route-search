package com.shangqiao56.tms;

import com.shangqiao56.tms.rms.route.core.*;
import com.shangqiao56.tms.rms.route.core.Station;
import com.shangqiao56.tms.rms.route.model.SqStation;
import com.shangqiao56.tms.rms.route.service.PathLoader;
import com.shangqiao56.tms.rms.route.service.SqRouteService;
import com.shangqiao56.tms.rms.route.service.impl.SqLoadedRouteServiceImpl;
import com.shangqiao56.tms.rms.route.service.impl.SqRouteServiceImpl;
import com.shangqiao56.tms.rms.route.store.SqStationStore;
import com.shangqiao56.tms.rms.route.store.SqStationStoreImpl;
import com.shangqiao56.tms.rms.route.util.DayOfWeekTranslate;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Test3 {
    @Test
    public void test3(){
//        ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>(1,2);
//        String s = map.putIfAbsent("abc", "abc");
//        System.out.println(s);
//
//        s = map.putIfAbsent("abc","abc");
//        System.out.println(s);

        int i = 2 ;
        i  >>= 1;
        System.out.println( i);
    }


    @Test
    public void test2() throws RouteException {

        SqRouteService routeService = new SqRouteServiceImpl();
        SqStationStore stationStore = new SqStationStoreImpl();

        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("上海"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("北京"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("常州"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("南京"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("苏州"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("无锡"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("杭州"));
        stationStore.saveStation(new SqLoadedRouteServiceImpl.MyStation("广州"));


        SqLoadedRouteServiceImpl sqLoadedRouteService = new SqLoadedRouteServiceImpl();
        sqLoadedRouteService.setStationStore(stationStore);
        sqLoadedRouteService.setRouteService(routeService);


        sqLoadedRouteService.setLoader(new PathLoader() {
            @Override
            public void loader4Arrival(Station arrival) {

            }
        });

        DayOfWeekTranslate t = DayOfWeekTranslate.getInstance();
        int day = t.translateDaysString("5");

       // Assert.assertEquals(((1 << Calendar.MONDAY) |(1 << Calendar.WEDNESDAY)) ,day);


        sqLoadedRouteService.savePath("上海","广州","无锡",day,true,null,null,null);
        sqLoadedRouteService.savePath("无锡","广州","苏州",day,true,null,null,null);
        sqLoadedRouteService.savePath("苏州","广州","杭州",day,true,null,null,null);
        sqLoadedRouteService.savePath("杭州","广州","广州",day,true,null,null,null);

         day = t.translateDaysString("13");
        sqLoadedRouteService.savePath("上海","广州","北京",day,true,null,null,null);


        System.out.println(Integer.toBinaryString(day));

        day = t.translateDaysString("24");
        System.out.println(Integer.toBinaryString(day));

        sqLoadedRouteService.savePath("杭州","广州","南京",day,true,null,null,null);

        Route route = sqLoadedRouteService.getRoute("上海", "广州");

        List<Path.NextItem> paths = route.getPaths();
        for (Path.NextItem path : paths) {
            System.out.println(path.getStation().getId());
        }

       // DigestUtils.sha1()
    }
}
