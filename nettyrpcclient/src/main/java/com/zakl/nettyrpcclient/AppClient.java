package com.zakl.nettyrpcclient;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zakl"})
public class AppClient {



    public static void main(String[] args) {
        SpringApplication.run(AppClient.class, args);
    }



}
