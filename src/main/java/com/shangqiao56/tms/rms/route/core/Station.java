package com.shangqiao56.tms.rms.route.core;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Station  {

    protected final Map<Station,Path> pathMap = new ConcurrentHashMap<>();

    private Serializable id;

    public Station(Serializable id) {
        this.id = id;
    }



    public void setPath(Path path){
        pathMap.put(path.getArrival(),path);
    }

    public Path getPath(Station arrival){
        return pathMap.get(arrival);
    }



    public void removePath(Station arrival, Station next){
        Path path = pathMap.get(arrival);
        if(path != null) {
            path.remove(next);
            if(path.getAllNext().size() <= 0){
                pathMap.remove(arrival);
            }
        }
    }



    public Serializable getId() {
        return id;
    }



    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ",pathMap=" + pathMap +
                '}';
    }
}
