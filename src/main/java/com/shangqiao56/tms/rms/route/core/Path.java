package com.shangqiao56.tms.rms.route.core;

import java.util.*;

public class Path {

    private Station start;
    private Station arrival;
    private Map<Station,NextItem> nextMap = new HashMap<>();



    public Path(Station start, Station arrival) {
        this.start = start;
        this.arrival = arrival;
    }

    public Station getStart() {
        return start;
    }

    public Station getArrival() {
        return arrival;
    }

    public Collection<NextItem> getAllNext() {
        return nextMap.values();
    }


    public void saveNext(NextItem next){
        nextMap.put(next.station,next);
    }

    public void remove(Station next){
        nextMap.remove(next);
    }



    public static class  NextItem{
         protected Station station;

        public Station getStation() {
            return station;
        }

        public NextItem(Station station) {
            this.station = station;
        }
    }

}
