package com.shangqiao56.tms.rms.route.store;

import com.shangqiao56.tms.rms.route.core.Station;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqStationStoreImpl implements SqStationStore {
    private Map<Serializable,Station> map = new ConcurrentHashMap<>();
    @Override
    public void saveStation(Station station) {
        Station s = map.putIfAbsent(station.getId(), station);  //必需何证station的引用是原引用，不然可能会遇到问题、
        if(s != null){
            throw new RuntimeException("试图添加已存在的网点{}" + station.getId());
        }
    }

    @Override
    public Station getStation(Serializable id) {
        return map.get(id);
    }
}
