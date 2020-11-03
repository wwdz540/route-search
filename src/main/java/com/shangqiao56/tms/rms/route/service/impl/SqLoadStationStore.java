package com.shangqiao56.tms.rms.route.service.impl;


import com.shangqiao56.tms.rms.route.core.Station;
import com.shangqiao56.tms.rms.route.core.StationStore;
import com.shangqiao56.tms.rms.route.store.SqStationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.io.Serializable;

public class SqLoadStationStore implements SqStationStore {

    private  final Logger logger = LoggerFactory.getLogger(SqLoadStationStore.class);
    private static final String QUERY_STATION_SQL ="select * from B_STAT where  ISDEL = 0 ";
    private static final String QUERY_STATION_BY_ID_SQL ="select * from B_STAT where  Id =  ?";



    @Autowired
    private JdbcTemplate jdbcTemplate;


    private SqStationStore stationStore;

    public SqLoadStationStore(SqStationStore stationStore) {
        this.stationStore = stationStore;
    }

    @Override
    public SqLoadedRouteServiceImpl.MyStation getStation(Serializable id) {
        SqLoadedRouteServiceImpl.MyStation station = (SqLoadedRouteServiceImpl.MyStation)stationStore.getStation(id);
        if(station == null){
            station = getFromDB(id);
            if(station != null)
            stationStore.saveStation(station);
        }
        return station;
    }

    private SqLoadedRouteServiceImpl.MyStation getFromDB(Serializable id) {
        logger.info("load station data from "+id);
        SqLoadedRouteServiceImpl.MyStation station = null;
        try {
           station = jdbcTemplate.queryForObject(QUERY_STATION_BY_ID_SQL, (rs, i) ->
                    new SqLoadedRouteServiceImpl.MyStation(rs.getLong("ID"), rs.getString("STATION")), id);
        }catch (EmptyResultDataAccessException ex){
            logger.warn("load station from DB is error :"+id+" "+ex.getMessage());
        }
        return  station;
    }


    @PostConstruct
    public void loadStation(){
        logger.info("load station start");
        jdbcTemplate.query(QUERY_STATION_SQL, rs -> {
            SqLoadedRouteServiceImpl.MyStation station = new SqLoadedRouteServiceImpl.MyStation(rs.getLong("ID"),rs.getString("STATION"));
            stationStore.saveStation(station);
        });
        logger.info("load station complete");
    }

    @Override
    public void saveStation(Station station) {
        stationStore.saveStation(station);
    }
}
