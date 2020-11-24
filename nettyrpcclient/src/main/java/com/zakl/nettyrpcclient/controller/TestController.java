package com.zakl.nettyrpcclient.controller;

import com.zakl.nettyrpc.log.annotation.OperationLog;
import com.zakl.nettyrpc.log.enums.OperationType;
import com.zakl.nettyrpcclient.services.AddCalculate;
import com.zakl.nettyrpcclient.services.CostTimeCalculate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;


/**
 * @author ZhangJiaKui
 * @classname TestController
 * @description Web测试
 * @date 11/17/2020 4:13 PM
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private AddCalculate addCalculate;

    @Autowired
    private CostTimeCalculate costTimeCalculate;

    @GetMapping("/add")
    @OperationLog(detail = "costTimeApi", operationType = OperationType.SELECT)
    public int add(int a, int b) {
        log.info("调用add方法");
//        TestController testController = (TestController) AopContext.currentProxy();
//        testController.test("123", 111);
//        test("123", 111);
//        return a + b;
        return addCalculate.add(a, b);
    }

    @GetMapping("/costTime")
    public String costTime() {
        log.info("调用costTime");
        return costTimeCalculate.calculate().toString();
    }
}
