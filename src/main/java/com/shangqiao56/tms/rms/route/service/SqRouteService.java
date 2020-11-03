package com.shangqiao56.tms.rms.route.service;

import com.shangqiao56.tms.rms.route.core.*;
import com.shangqiao56.tms.rms.route.model.SqNextItem;

import java.io.Serializable;
import java.util.Map;

public interface SqRouteService<T> extends RouteService {
     void savePath(Station start, Station arrival, SqNextItem nextItem) throws RouteException;
     void deletePath(Station start, Station arrival, Station next);
}
