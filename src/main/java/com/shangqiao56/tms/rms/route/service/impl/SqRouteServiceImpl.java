package com.shangqiao56.tms.rms.route.service.impl;

import com.shangqiao56.tms.rms.route.core.*;
import com.shangqiao56.tms.rms.route.model.SqNextItem;
import com.shangqiao56.tms.rms.route.model.SqPath;
import com.shangqiao56.tms.rms.route.model.SqRoute;
import com.shangqiao56.tms.rms.route.model.SqStation;
import com.shangqiao56.tms.rms.route.service.SqRouteService;
import com.shangqiao56.tms.rms.route.util.DayOfWeekTranslate;

import java.util.Collection;

public class SqRouteServiceImpl<T> implements SqRouteService<T> {

    private StationStore stationStore;

    @Override
    public void savePath(Station start, Station arrival, SqNextItem nextItem) throws RouteException {
/*
        if(start == nextItem.getStation()){
            throw new RouteException("起点{"+((SqStation)start).getName()+"}与下一站{"+
                    ((SqStation)nextItem.getStation()).getName()+"}不可以相同");
        }*/
        SqPath path = (SqPath) start.getPath(arrival);
        if(path == null){
            path = new SqPath(start,arrival);
            start.setPath(path);
        }else{  //验证其合法性
            Collection<Path.NextItem> allNext = path.getAllNext();
            int day = nextItem.getOnDay();
            for (Path.NextItem mNextItem : allNext) {

                if(mNextItem.getStation() == nextItem.getStation()) break;

                SqNextItem<T> mItem = (SqNextItem<T>) mNextItem;
                int onDay = ((SqNextItem<T>) mNextItem).getOnDay();

                if((onDay & day) > 1){
                    throw new RouteException("设制路由[起始：{"
                            +((SqStation)start).getName()+"| id:" +start.getId()+
                            "} 终点：{"
                            +((SqStation)arrival).getName()+"| id:" +arrival.getId()+
                            "} 下一站：{"
                            +((SqStation)nextItem.getStation()).getName()+"|id:" + nextItem.getStation().getId()
                            +"}" +
                            Integer.toBinaryString(nextItem.getOnDay())+
                            "]与路由[ 下一站：{" + ((SqStation)mNextItem.getStation()).getName() +"|"+mNextItem.getStation().getId()+
                            "(起始与终点同上)}" +
                            Integer.toBinaryString(((SqNextItem)mNextItem).getOnDay())
                                 +   " ]冲突");
                }
            }
        }
        nextItem.setPath(path);
        path.saveNext(nextItem);
    }

    @Override
    public void deletePath(Station start, Station arrival, Station next) {
        start.removePath(arrival,next);
    }


    @Override
    public Route getRoute(Station start, Station arrival) throws RouteException {
        SqStation mStart = (SqStation) start;
        SqStation mArrival = (SqStation) arrival;

        SqPath path;
        SqNextItem nextItem;
        SqRoute route = new SqRoute();
        int len = 0;
        while((path = (SqPath)mStart.getPath(mArrival)) != null
                && ( nextItem = getNextItem(path)) != null){
            mStart = (SqStation) nextItem.getStation();

            route.addPath(nextItem);
            if(mStart == arrival) break;    //已到达终点，返回

            if(start == mStart || (len ++ > 15)){
                throw new RouteEndlessException("存在死循环路由："+start.getId()+","+ arrival.getId(),route.getPaths());
            }

        }
        return route;
    }


    public SqNextItem getNextItem(SqPath path){

        int dayMask = getDayMask();
        Collection<Path.NextItem> allNext = path.getAllNext();
        SqNextItem item = null;
        for (Path.NextItem nextItem : allNext) {
            SqNextItem mItem = (SqNextItem) nextItem;
            if((mItem.getOnDay() & dayMask)>0){
                item = mItem;
                break;
            }
        }

        return item;
    }



    protected int getDayMask(){
        return DayOfWeekTranslate.getInstance().todayMask();
    }

}
