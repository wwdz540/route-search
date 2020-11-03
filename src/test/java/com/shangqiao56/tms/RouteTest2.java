package com.shangqiao56.tms;

import com.shangqiao56.tms.rms.RouteApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RouteApplication.class)
public class RouteTest2 {
/*
    private static final String QUERY_ROUTE_SQL = "select * from T_ROUTE where  ISDEL = 0";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    private PathFinder finder;

    @Test
    public void test1(){
        System.out.println(finder.getAllStations());
    }

    @Test
    public void testQuery(){
        //final LongAdder count = new LongAdder();

        AtomicInteger count = new AtomicInteger(0);

        jdbcTemplate.query(QUERY_ROUTE_SQL,rs->{
           // count.increment();
            System.out.println(rs.getLong("id"));
        });
    }*/
}
