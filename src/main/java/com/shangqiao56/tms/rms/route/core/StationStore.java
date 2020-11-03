package com.shangqiao56.tms.rms.route.core;

import java.io.Serializable;

//存储StationStore
public interface StationStore {
    /**
     * 根据Id获取Station
     * @param id
     * @return
     */
    Station getStation(Serializable id);
}
