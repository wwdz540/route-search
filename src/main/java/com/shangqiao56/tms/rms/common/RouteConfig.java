package com.shangqiao56.tms.rms.common;

import com.shangqiao56.tms.rms.route.core.StationStore;
import com.shangqiao56.tms.rms.route.service.PathLoader;
import com.shangqiao56.tms.rms.route.service.SqRouteService;
import com.shangqiao56.tms.rms.route.service.impl.PathLoaderImpl;
import com.shangqiao56.tms.rms.route.service.impl.SqLoadStationStore;
import com.shangqiao56.tms.rms.route.service.impl.SqLoadedRouteServiceImpl;
import com.shangqiao56.tms.rms.route.service.impl.SqRouteServiceImpl;
import com.shangqiao56.tms.rms.route.store.SqStationStore;
import com.shangqiao56.tms.rms.route.store.SqStationStoreImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    private static final SqRouteService routeService = new SqRouteServiceImpl();

    @Bean
     SqStationStore stationStore(){
        return new SqLoadStationStore(new SqStationStoreImpl());
    }

    @Bean
    public SqLoadedRouteServiceImpl  sqLoadedRouteService(PathLoader pathLoader,SqStationStore stationStore){
        SqLoadedRouteServiceImpl sqLoadedRouteService = new SqLoadedRouteServiceImpl();
        sqLoadedRouteService.setLoader(pathLoader);
        sqLoadedRouteService.setStationStore(stationStore);
        sqLoadedRouteService.setRouteService(routeService);
        return sqLoadedRouteService;
    }

    @Bean
    PathLoader pathLoader(StationStore stationStore){
        PathLoaderImpl pathLoader = new PathLoaderImpl();
        pathLoader.setRouteService(routeService);
        pathLoader.setStationStore(stationStore);
        return pathLoader;
    }
}
