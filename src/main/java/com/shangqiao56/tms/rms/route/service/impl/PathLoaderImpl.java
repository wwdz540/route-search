package com.shangqiao56.tms.rms.route.service.impl;

import com.shangqiao56.tms.StationService;
import com.shangqiao56.tms.rms.route.core.RouteException;
import com.shangqiao56.tms.rms.route.core.Station;
import com.shangqiao56.tms.rms.route.core.StationStore;
import com.shangqiao56.tms.rms.route.model.SqNextItem;
import com.shangqiao56.tms.rms.route.service.PathLoader;
import com.shangqiao56.tms.rms.route.service.SqRouteService;
import com.shangqiao56.tms.rms.route.util.DayOfWeekTranslate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class PathLoaderImpl implements PathLoader , ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(PathLoaderImpl.class);

    private StationStore stationStore;
    private SqRouteService routeService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private boolean isLoaded ;

    public void setStationStore(StationStore stationStore) {
        this.stationStore = stationStore;
    }

    public void setRouteService(SqRouteService routeService) {
        this.routeService = routeService;
    }

    DayOfWeekTranslate  dayOfWeekTranslate = DayOfWeekTranslate.getInstance();

    private static final String QUERY_ROUTE_ARRIVE_SQL = "select ID,START_STAT_ID,NEXT_STAT_ID,ARRIVE_STAT_ID,MILEAGE,PAY_RATIO,TRUNK_RATIO,TIME_STR,ISUSING  from T_ROUTE_NEW where  ISDEL = 0 and  ARRIVE_STAT_ID=? and type=1  and  ISUSING = 1 ";

    private static final String QUERY_ARRIVE_STAT_ID  = "select * from ( select count(ARRIVE_STAT_ID)  as count,ARRIVE_STAT_ID   from T_ROUTE_NEW group by  ARRIVE_STAT_ID order by count desc) where  ROWNUM <" ;

    @Override
    @Transactional
    public void loader4Arrival(final Station arrival) {

        logger.info("start load for arrival {}",arrival.getId());

        RowCallbackHandler handler = rs-> {
            Long id = rs.getLong("ID");
            Station start = stationStore.getStation(rs.getLong("START_STAT_ID"));
            Station next = stationStore.getStation(rs.getLong("NEXT_STAT_ID"));

            if(start == null || next == null){
                logger.warn("start or next is null, route id in db is {}",id);
                return;
            }

            String timeStr = rs.getString("TIME_STR");
            boolean isUsing = rs.getBoolean("ISUSING");
            BigDecimal mileage =  rs.getBigDecimal("MILEAGE");
            BigDecimal payRatio = rs.getBigDecimal("PAY_RATIO");
            BigDecimal trunkRatio = rs.getBigDecimal("TRUNK_RATIO");
            SqNextItem nextItem = new SqNextItem(next);


            int onDay = 0;
            if(timeStr != null){
                onDay = dayOfWeekTranslate.translateDaysString(timeStr);            //@todo 实际上有很多data为null的类型

            }else{
                logger.warn("load data error , time_str is null, table : T_ROUTE_NEW ,id: {}",id);
            }


            nextItem.setOnDay(onDay);
            nextItem.setUsing(isUsing);
            nextItem.setMileage(mileage);
            nextItem.setPayRatio(payRatio);
            nextItem.setTrunkRatio(trunkRatio);

            try {
                logger.debug("load path:{}->{}->{},{}",start.getId(),arrival.getId(),nextItem.getStation().getId(),nextItem.getOnDay());
                routeService.savePath(start,arrival,nextItem);

            } catch (RouteException e) {
                logger.warn("loader path ["+id+"] error :" ,e);
            }

        };

        jdbcTemplate.query(QUERY_ROUTE_ARRIVE_SQL, handler, arrival.getId());
        logger.info("end load for arrival {}",arrival.getId());
    }


    public void loadTopN(int top){
        List<Long> desList = jdbcTemplate.query(QUERY_ARRIVE_STAT_ID + top, (rs, i) -> {
            long id = rs.getLong("ARRIVE_STAT_ID");
            logger.info("will load data for destination: {},count:{}", id, rs.getInt("count"));
            return id;
        });


        AtomicInteger counter = new AtomicInteger(desList.size());

        for (int i = 0; i < 2; i++) {

            final int finalI = i;

            Thread thread = new Thread(){
                @Override
                public void run() {
                    logger.info("start loading thread"+this.getName());
                    int idx;
                    while( (idx = counter.decrementAndGet())>=0){
                        SqLoadedRouteServiceImpl.MyStation mArrive = (SqLoadedRouteServiceImpl.MyStation)stationStore.getStation(desList.get(idx));
                        if(mArrive == null) continue;

                        if(!mArrive.isLoaded) {
                            Lock lock = mArrive.writeLock;  ///
                            if(!lock.tryLock())continue;       //此处不用等待
                            try {
                                loader4Arrival(mArrive);
                                mArrive.isLoaded = true;
                            }finally {
                                lock.unlock();
                            }
                        }
                    }
                    logger.info("end loading thread"+this.getName());
                }
            };
            thread.setName("load_path_thread_"+finalI);
            thread.setPriority(3);
            thread.setUncaughtExceptionHandler((t,e)->{
                logger.error("load_data_exception on "+t.getName(),e);
            });
            thread.setDaemon(true);
            thread.start();
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("start application .....");
        if(!isLoaded){
            loadTopN(2000);
            isLoaded = true;
        }
    }
}

