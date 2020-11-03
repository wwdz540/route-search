package com.shangqiao56.tms.rms;


import com.shangqiao56.tms.autoconfigration.TmsSimpleApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@TmsSimpleApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableAsync
public class RouteApplication {
    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class,args);
    }
}