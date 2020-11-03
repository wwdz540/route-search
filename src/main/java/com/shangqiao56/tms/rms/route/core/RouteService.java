package com.shangqiao56.tms.rms.route.core;

public interface RouteService {

    Route getRoute(Station start,Station arrival) throws RouteException;
}
