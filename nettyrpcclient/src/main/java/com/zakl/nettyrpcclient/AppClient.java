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
        ApplicationContext applicationContext = BeanUtils.getApplicationContext();
        System.out.println();
//        AddCalculate addc = (AddCalculate) applicationContext.getBean("com.newlandframework.rpc.com.zakl.nettyrpcserver.services.AddCalculate");
//
//        int add = addc.add(1, 2);
//        System.out.println(add);
//        MultiCalculate mc = applicationContext.getBean(MultiCalculate.class);
//        System.out.println(mc.multi(5, 6));
//
//        CostTimeCalculate cc = applicationContext.getBean(CostTimeCalculate.class);
//        CostTime calculate = cc.calculate();
//        System.out.println(calculate.toString());

    }

}
