package com.shangqiao56.tms;

import com.shangqiao56.tms.rms.route.core.Station;
import org.junit.Test;

import java.util.List;


//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = RouteApplication.class)
public class RouteTest {

/*//    @Autowired
    PathFinder finder = new PathFinder();

    @Test
    public void test1(){

        *//***
         *   finder.addNode(new Node("上海",""));
         *         finder.addNode(new Node("昆山",""));
         *         finder.addNode(new Node("苏州",""));
         *         finder.addNode(new Node("常州",""));
         *         finder.addNode(new Node("镇江",""));
         *         finder.addNode(new Node("南京",""));
         *         finder.addNode(new Node("扬州",""));
         *
         *
         *         finder.addRelationShip(finder.getNode("上海"),finder.getNode("南京"),finder.getNode("昆山"));
         *         finder.addRelationShip(finder.getNode("昆山"),finder.getNode("南京"),finder.getNode("苏州"));
         *         finder.addRelationShip(finder.getNode("苏州"),finder.getNode("南京"),finder.getNode("常州"));
         *         finder.addRelationShip(finder.getNode("常州"),finder.getNode("南京"),finder.getNode("镇江"));
         *         finder.addRelationShip(finder.getNode("镇江"),finder.getNode("南京"),finder.getNode("南京"));
         *//*
        finder.saveStation(new Station("上海"));
        finder.saveStation(new Station("昆山"));
        finder.saveStation(new Station("苏州"));
        finder.saveStation(new Station("常州"));
        finder.saveStation(new Station("镇江"));
        finder.saveStation(new Station("南京"));
        finder.saveStation(new Station("扬州"));
        finder.saveStation(new Station("西安"));

        finder.link("上海","南京","昆山",null);
        finder.link("昆山","南京","苏州",null);
        finder.link("苏州","南京","常州",null);
        finder.link("常州","南京","镇江",null);
        finder.link("镇江","南京","南京",null);
        finder.link("常州","西安","扬州",null);
        finder.link("扬州","西安","南京",null);
        finder.link("扬州","南京","南京",null);


         List<Path> list = finder.findRoute("上海", "南京");

        list.forEach(x-> System.out.println(x.getCur().getId()));

        System.out.println("======");
        list = finder.findRoute("扬州","西安");
        list.forEach(x-> System.out.println(x.getCur().getId()));

        System.out.println(finder.getStation("南京"));

    }*/
}
