package com.zakl.nettyrpcclient;

import com.zakl.nettyrpcclient.async.AsyncCallObject;
import com.zakl.nettyrpcclient.async.AsyncCallback;
import com.zakl.nettyrpcclient.async.AsyncInvoker;
import com.zakl.nettyrpcclient.pojo.CostTime;
import com.zakl.nettyrpcclient.services.AddCalculate;
import com.zakl.nettyrpcclient.services.CostTimeCalculate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = {"com.zakl"})
class AppClientTests {

    @Autowired
    private CostTimeCalculate calculate;

    @Test
    void contextLoads() {
        long start = 0, end = 0;
        start = System.currentTimeMillis();

        AsyncInvoker invoker = new AsyncInvoker();

        //todo 是否能使用lambda表达式
        CostTime elapse0 = invoker.submit(new AsyncCallback<CostTime>() {
            @Override
            public CostTime call() {
                return calculate.calculate();
            }
        });

//        CostTime elapse1 = invoker.submit(calculate::calculate);
//
//        CostTime elapse2 = invoker.submit(calculate::calculate);

        System.out.println("1 async nettyrpc call:[" + "result:" + elapse0 + ", status:[" + ((AsyncCallObject) elapse0)._getStatus() + "]");
//        System.out.println("2 async nettyrpc call:[" + "result:" + elapse1 + ", status:[" + ((AsyncCallObject) elapse1)._getStatus() + "]");
//        System.out.println("3 async nettyrpc call:[" + "result:" + elapse2 + ", status:[" + ((AsyncCallObject) elapse2)._getStatus() + "]");

        end = System.currentTimeMillis();

        System.out.println("nettyrpc async calculate time:" + (end - start));
    }

}
