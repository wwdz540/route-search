package com.shangqiao56.tms.rms.route.store;

import com.shangqiao56.tms.rms.route.core.Station;
import com.shangqiao56.tms.rms.route.core.StationStore;

public interface SqStationStore extends StationStore {
    void saveStation(Station station);

}
