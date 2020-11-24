package com.zakl.nettyrpcclient;

import com.zakl.nettyrpc.common.util.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zakl"})
public class AppClient {

    public static void main(String[] args) {
        SpringApplication.run(AppClient.class, args);
//        ApplicationContext applicationContext = BeanUtils.getApplicationContext();
//        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
//            System.out.println(beanDefinitionName);
//        }
//        System.out.println();

    }

}
