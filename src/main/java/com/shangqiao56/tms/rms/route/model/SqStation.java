package com.shangqiao56.tms.rms.route.model;

import com.shangqiao56.tms.rms.route.core.Station;

import java.io.Serializable;

public class SqStation extends Station {
    private String name;
    public SqStation(Serializable id) {
        super(id);
    }

    public SqStation(Serializable id,String name) {
        super(id);
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
