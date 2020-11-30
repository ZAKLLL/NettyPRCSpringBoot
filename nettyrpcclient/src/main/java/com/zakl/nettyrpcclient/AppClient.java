package com.zakl.nettyrpcclient;

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
