package com.zakl.nettyrpcclient;

import com.newlandframework.rpc.services.AddCalculate;
import com.zakl.nettyrpcclient.util.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        ApplicationContext applicationContext = BeanUtils.getApplicationContext();
        AddCalculate addc = (AddCalculate) applicationContext.getBean("addc");
        int add = addc.add(1, 2);
        System.out.println(add);
    }

}
