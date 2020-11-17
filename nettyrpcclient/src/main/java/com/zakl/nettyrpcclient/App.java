package com.zakl.nettyrpcclient;

import com.newlandframework.rpc.services.AddCalculate;
import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.MultiCalculate;
import com.newlandframework.rpc.pojo.CostTime;
import com.zakl.nettyrpcclient.util.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
//        ApplicationContext applicationContext = BeanUtils.getApplicationContext();
//        AddCalculate addc = (AddCalculate) applicationContext.getBean("com.newlandframework.rpc.services.AddCalculate");
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
