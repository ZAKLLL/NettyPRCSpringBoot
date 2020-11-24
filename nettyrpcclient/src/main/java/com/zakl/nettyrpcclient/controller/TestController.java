package com.zakl.nettyrpcclient.controller;

import com.zakl.nettyrpc.log.annotation.OperationLog;
import com.zakl.nettyrpc.log.enums.OperationType;
import com.zakl.nettyrpcclient.pojo.Person;
import com.zakl.nettyrpcclient.services.AddCalculate;
import com.zakl.nettyrpcclient.services.CostTimeCalculate;
import com.zakl.nettyrpcclient.services.PersonManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


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

    @Autowired
    private PersonManage personManage;

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

    @GetMapping("/p")
    public String p() {
        Person person = new Person();
        person.setAge(16);
        person.setId(11);
        person.setName("张三");
        return personManage.queryP(person, 5).toString();
    }
}
