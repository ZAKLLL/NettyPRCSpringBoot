package com.zakl.nettyrpcclient.controller;

import com.newlandframework.rpc.services.AddCalculate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;


/**
 * @author ZhangJiaKui
 * @classname TestController
 * @description TODO
 * @date 11/17/2020 4:13 PM
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private AddCalculate addCalculate;

    @GetMapping("/add")
    public int add(int a, int b) {
//        log.info("info");
        return addCalculate.add(a, b);
    }
}
