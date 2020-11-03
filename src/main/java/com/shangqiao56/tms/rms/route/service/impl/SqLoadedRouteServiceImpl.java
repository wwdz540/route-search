package com.shangqiao56.tms.rms.route.service.impl;

import com.shangqiao56.tms.rms.common.RmsRouteErrorCode;
import com.shangqiao56.tms.rms.route.core.Path;
import com.shangqiao56.tms.rms.route.core.Route;
import com.shangqiao56.tms.rms.route.core.RouteException;
import com.shangqiao56.tms.rms.route.core.Station;
import com.shangqiao56.tms.rms.route.model.SqNextItem;
import com.shangqiao56.tms.rms.route.model.SqStation;
import com.shangqiao56.tms.rms.route.service.PathLoader;
import com.shangqiao56.tms.rms.route.service.SqRouteService;
import com.shangqiao56.tms.rms.route.store.SqStationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/***
 * 这外是对外的主要接口，控制加载，加锁
 * @param <T>
 */
public class SqLoadedRouteServiceImpl<T>  {

    private static final Logger logger = LoggerFactory.getLogger(SqLoadedRouteServiceImpl.class);
    private SqStationStore stationStore;
    private SqRouteService<T> routeService;
    private PathLoader loader;


    public void setStationStore(SqStationStore stationStore) {
        this.stationStore = stationStore;
    }

    public void setRouteService(SqRouteService<T> routeService) {
        this.routeService = routeService;
    }

    public void setLoader(PathLoader loader) {
        this.loader = loader;
    }

    public Route getRoute(Serializable startId, Serializable arrivalId) throws RouteException {
        Station start = this.getStation(startId);
         MyStation arrival = (MyStation)this.getStation(arrivalId);

        loadRoute(arrival);  //在儿取路径之前，确保所有数据已加载

        arrival.readLock.lock();
        try {
            return routeService.getRoute(start, arrival);
        }finally {
            arrival.readLock.unlock();
        }
    }


    /**
     *   private boolean isUsing;         // 状态
     *     private BigDecimal mileage;  //里程
     *     private BigDecimal payRatio;
     *     private BigDecimal  trunkRatio; //TRUNK_RATIO

     * @throws RouteException
     */


    public void savePath(Serializable startId,
                         Serializable arrivalId,
                         Serializable nextId,
                         int onDay,
                         boolean isUsing,
                         BigDecimal mileage,
                         BigDecimal payRatio,
                         BigDecimal trunkRatio) throws RouteException {
        Station start = this.getStation(startId);
        MyStation arrival = this.getStation(arrivalId);
        loadRoute(arrival);  //在取路径之前，确保所有数据已加载

        arrival.writeLock.lock();
        try {
            Station next = stationStore.getStation(nextId);

            SqNextItem item = new SqNextItem(next);
            item.setOnDay(onDay);
            item.setUsing(isUsing);
            item.setMileage(mileage);
            item.setPayRatio(payRatio);
            item.setTrunkRatio(trunkRatio);
            routeService.savePath(start, arrival, item);
        }finally {
            arrival.writeLock.unlock();
        }
    }

    public void deletePath(Serializable startId,Serializable arrivalId,Serializable nextId){


        MyStation arrival = getStation(arrivalId);
        loadRoute(arrival);
        Lock lock = arrival.writeLock;
        lock.lock();
        try {
            routeService.deletePath(getStation(startId), arrival, getStation(nextId));
        }finally {
            lock.unlock();
        }
    }


    private void loadRoute(MyStation arrival) {
        logger.debug(" begin load route for arrival: {}",arrival.getId());
        if(arrival.isLoaded) return; //如果已加载完，则直接返回

        Lock lock = arrival.writeLock;
        if(!lock.tryLock()){            //如果锁不成功，则说明其它进程正在加载，
            logger.debug("load route  waiting for another thread ");
            long b = System.currentTimeMillis();
            logger.debug("load route begin spin");
            while(!arrival.isLoaded ){
                if((System.currentTimeMillis() - b) > 1000){
                    RmsRouteErrorCode.WAITING_ROUTE_LOADING.alertError();
                   // throw new RuntimeException("waiting too long for load :{}"+arrival.getId());
                }
                Thread.yield();
            }

            logger.debug("load route end spin");
            return;  //自旋完成
        }
        try{
            logger.info("load route for {} ",arrival.getId());
            loader.loader4Arrival(arrival);
            arrival.isLoaded = true;
        }finally {
            lock.unlock();
        }
    }

    public MyStation getStation(Serializable id) {
        MyStation station = (MyStation)stationStore.getStation(id);
        return  station;
    }

    public static class MyStation extends SqStation {

       final ReadWriteLock lock = new ReentrantReadWriteLock();
       final Lock readLock = lock.readLock();
       final  Lock writeLock = lock.writeLock();

       volatile boolean isLoaded;

        public MyStation(Serializable id) {

            super(id);
        }

        public MyStation(Serializable id, String name) {
            super(id, name);
        }

        @Override
        public String toString() {
            StringBuilder pathSb = new StringBuilder();
            for (Path path : pathMap.values()) {
                pathSb.append(path.getArrival().getId());
                pathSb.append("(");
                Collection<Path.NextItem> allNext = path.getAllNext();
                for (Path.NextItem nextItem : allNext) {
                    pathSb.append(nextItem.getStation().getId());

                    pathSb.append("|" + Integer.toBinaryString(((SqNextItem)nextItem).getOnDay()));
                    pathSb.append(",");
                }

                pathSb.append(")");
                pathSb.append("\n");
            }


            return "MyStation{" +
                    "\nname:" +getName()+
                    "\nid:" +getId()+
                    "\npath:[\n" + pathSb.toString()+
                    "]}";


        }
    }
}
