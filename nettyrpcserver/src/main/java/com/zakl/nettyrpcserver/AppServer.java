package com.zakl.nettyrpcserver;

import com.zakl.nettyrpc.common.util.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.zakl"})
public class AppServer {

    public static void main(String[] args) {
        SpringApplication.run(AppServer.class, args);
        ApplicationContext applicationContext = BeanUtils.getApplicationContext();
        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
    }

}
