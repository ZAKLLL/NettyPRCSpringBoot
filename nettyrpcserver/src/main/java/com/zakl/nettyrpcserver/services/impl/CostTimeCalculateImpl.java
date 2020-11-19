package com.zakl.nettyrpcserver.services.impl;


import com.zakl.nettyrpcserver.pojo.CostTime;
import com.zakl.nettyrpcserver.services.CostTimeCalculate;
import org.springframework.stereotype.Service;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:CostTimeCalculateImpl.java
 * @description:CostTimeCalculateImpl功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
@Service(CostTimeCalculate.SERVIVE_BEAN_NAME)
public class CostTimeCalculateImpl implements CostTimeCalculate {
    @Override
    public CostTime calculate() {
        CostTime elapse = new CostTime();
        try {
            long start = 0, end = 0;
            start = System.currentTimeMillis();
            //模拟耗时操作
            Thread.sleep(3000L);
            end = System.currentTimeMillis();

            long interval = end - start;
            elapse.setElapse(interval);
            elapse.setDetail("I'm XiaoHaoBaby,cost time operate succ!");
            System.out.println("calculate time:" + interval);
            return elapse;
        } catch (InterruptedException e) {
            e.printStackTrace();
            elapse.setDetail("I'm XiaoHaoBaby,cost time operate fail!");
            return elapse;
        }
    }

    @Override
    public CostTime busy() {
        CostTime elapse = new CostTime();
        try {
            long start = 0, end = 0;
            start = System.currentTimeMillis();
            //模拟耗时操作,超过nettyrpc.default.msg.timeout定义的上限
            Thread.sleep(35 * 1000L);
            end = System.currentTimeMillis();

            long interval = end - start;
            elapse.setElapse(interval);
            elapse.setDetail("I'm XiaoHao,I'm busy now!");
            System.out.println("calculate time:" + interval);
            return elapse;
        } catch (InterruptedException e) {
            e.printStackTrace();
            elapse.setDetail("I'm XiaoHao,I'm handle error now!");
            return elapse;
        }
    }
}

