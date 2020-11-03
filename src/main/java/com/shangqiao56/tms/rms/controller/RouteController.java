package com.shangqiao56.tms.rms.controller;

import com.shangqiao56.tms.rms.common.RmsRouteErrorCode;
import com.shangqiao56.tms.rms.route.core.Path;
import com.shangqiao56.tms.rms.route.core.Route;
import com.shangqiao56.tms.rms.route.core.RouteEndlessException;
import com.shangqiao56.tms.rms.route.core.RouteException;
import com.shangqiao56.tms.rms.route.model.SqNextItem;
import com.shangqiao56.tms.rms.route.model.SqStation;
import com.shangqiao56.tms.rms.route.service.impl.SqLoadedRouteServiceImpl;
import com.shangqiao56.tms.rms.route.store.SqStationStore;
import com.shangqiao56.tms.rms.route.util.DayOfWeekTranslate;
import io.swagger.annotations.Api;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/route")
@Api(value = "路由接口")
public class RouteController  {

    private static  final Logger logger = LoggerFactory.getLogger(RouteController.class);

    DayOfWeekTranslate dwt = DayOfWeekTranslate.getInstance();

    @Resource
    private SqLoadedRouteServiceImpl sqLoadedRouteService;

    @Resource
    private SqStationStore stationStore;

    @PutMapping
    public void savePath(@RequestBody PathEntity pathEntity){

        logger.info("save path {}",pathEntity);

        try {
            sqLoadedRouteService.savePath(pathEntity.getStart(), pathEntity.getArrival(), pathEntity.getNext(),
                    dwt.translateDaysString(pathEntity.getTimeStr()), pathEntity.isUsing, pathEntity.getMileage(), pathEntity.getPayRatio(), pathEntity.getTrunkRatio());
        } catch (RouteException e) {
            logger.error("save path error ",e);
            RmsRouteErrorCode.UNDEFINED.alertError(e.getMessage());
        }
    }

    //批量操作 ，还未成功
    @PutMapping("/batch")
    public void savePathBatch(@RequestBody  List<PathEntity> list){
      /**
        
        //验证是否通过
        Map<String,Integer> map = new HashMap<>();
        list.forEach( p ->{
                    String key = p.getStart() +"_"+ p.getArrival()+"_" + p.getNext();
                    Integer fdata = map.get(key);
                }
        );
        
        
        **/
        int i=0;
        try {
            for ( ; i < list.size(); i++) {
                PathEntity pathEntity = list.get(i);
                sqLoadedRouteService.savePath(pathEntity.getStart(), pathEntity.getArrival(), pathEntity.getNext(),
                        dwt.translateDaysString(pathEntity.getTimeStr()), pathEntity.isUsing, pathEntity.getMileage(), pathEntity.getPayRatio(), pathEntity.getTrunkRatio());
            }
        }catch (Exception e){
            //回滚
                logger.error("出现冲空的数据,",e);
                 for(int j =i-1 ; j>=0 ; j--){
                     PathEntity pathEntity = list.get(i);
                     logger.warn("回滚：",pathEntity);
                     sqLoadedRouteService.deletePath(pathEntity.getStart(),pathEntity.getArrival(),pathEntity.getNext());
                 }

            RmsRouteErrorCode.UNDEFINED.alertError(e.getMessage());
        }
    }

    @DeleteMapping
    public void deletePath(@RequestParam  Long start,
                           @RequestParam Long arrival,
                           @RequestParam Long next){
        logger.info("delete Path  {},{},{}",start,arrival,next);
        sqLoadedRouteService.deletePath(start,arrival,next);
    }

   // @GetMapping
 /**   private List<RouteItemEntity> getRouteItemList(
            @RequestParam Long start,
            @RequestParam Long arrival) throws RouteException {

        logger.debug("get route for {},{}",start,arrival);
        Route route = sqLoadedRouteService.getRoute(start, arrival);
        return route.getPaths().stream().map(s->{
            RouteItemEntity entity = new RouteItemEntity();
            SqNextItem item = (SqNextItem) s;
            SqStation next = (SqStation) item.getStation();
            SqStation startStation = (SqStation)item.getPath().getStart();

            entity.setCurt((Long) startStation.getId());
            entity.setCurtName(startStation.getName());
            entity.setNext((Long) next.getId());
            entity.setNextName( next.getName() );


            entity.setIsusing(item.isUsing() ? 1:0);

            entity.setMileage(item.getMileage());

            entity.setPayRatio(item.getPayRatio());
            entity.setTrunkRatio(item.getTrunkRatio());
            return entity;
        }).collect(Collectors.toList());
    }**/

    @GetMapping
    public RouteEntity getRoute(@RequestParam Long start,
                                @RequestParam Long arrival) throws RouteException{

        RouteEntity result = new RouteEntity();
        List<Path.NextItem> nextItems;
        try {
            Route route = sqLoadedRouteService.getRoute(start, arrival);
            nextItems = route.getPaths();
        } catch (RouteException ex) {
            result.setStatus(-1);
            if (!(ex instanceof RouteEndlessException)) {
                throw ex;
            }
            logger.warn("获取路由出现循环:"+ex.getMessage());
            nextItems = ((RouteEndlessException)ex).getNextItems();
        }
        List<RouteItemEntity> routeItemEntities = mapNextItem2RouteEntity(nextItems);
        result.setItems(routeItemEntities);
        return result;
    }

    private List<RouteItemEntity> mapNextItem2RouteEntity(List<Path.NextItem> nextItems) {
        return nextItems.stream().map(s -> {
            RouteItemEntity entity = new RouteItemEntity();
            SqNextItem item = (SqNextItem) s;
            SqStation next = (SqStation) item.getStation();
            SqStation startStation = (SqStation) item.getPath().getStart();

            entity.setCurt((Long) startStation.getId());
            entity.setCurtName(startStation.getName());
            entity.setNext((Long) next.getId());
            entity.setNextName(next.getName());


            entity.setIsusing(item.isUsing() ? 1 : 0);

            entity.setMileage(item.getMileage());

            entity.setPayRatio(item.getPayRatio());
            entity.setTrunkRatio(item.getTrunkRatio());
            return entity;
        }).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @Accessors(chain=true)
    public static class RouteItemEntity {
        private Long curt;
        private String curtName;
        private Long next;
        private String nextName;
        private BigDecimal mileage;
        private BigDecimal payRatio;
        private BigDecimal trunkRatio;
        private Integer isusing;
    }

    @Data
    @NoArgsConstructor
    @Accessors(chain=true)
    public static class RouteEntity{
        private int status =1; // 0,-1死循环。
        private List<RouteItemEntity> items;
    }


    @Data
    @NoArgsConstructor
    @Accessors(chain=true)
    public static class PathEntity implements Serializable {
        private Long start;
        private Long arrival;           // 终点
        private Long next;          // 下一站点
        private boolean isUsing;         // 状态
        private BigDecimal mileage;  //里程
        private BigDecimal payRatio;
        private BigDecimal  trunkRatio; //TRUNK_RATIO
        private String timeStr;

        @Override
        public String toString() {
            return "PathEntity{" +
                    "start=" + start +
                    ", arrival=" + arrival +
                    ", next=" + next +
                    ", isUsing=" + isUsing +
                    ", mileage=" + mileage +
                    ", payRatio=" + payRatio +
                    ", trunkRatio=" + trunkRatio +
                    ", timeStr='" + timeStr + '\'' +
                    '}';
        }
    }

    @ExceptionHandler
    public ResponseEntity<Map<String,Object>> handleException(RouteException exception){

        Map<String, Object> resultMap = new HashMap(2);
        resultMap.put("status", 50001);
        resultMap.put("msg", exception.getMessage());
        return ResponseEntity.status(460).body(resultMap);
    }
}
