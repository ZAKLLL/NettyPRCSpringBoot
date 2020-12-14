package com.zakl.nettyrpcclient.controller;

import com.zakl.nettyrpc.log.annotation.OperationLog;
import com.zakl.nettyrpc.log.enums.OperationType;
import com.zakl.nettyrpcclient.pojo.Person;
import com.zakl.nettyrpcclient.services.AddCalculate;
import com.zakl.nettyrpcclient.services.CostTimeCalculate;
import com.zakl.nettyrpcclient.services.PersonManage;
import com.zakl.nettyrpcclient.services.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;


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

    @Autowired
    private Test test;

    @GetMapping("/add")
    @OperationLog(detail = "add", operationType = OperationType.SELECT)
    public int add(int a, int b) {

        return addCalculate.add(a, b);
    }

    @GetMapping("/add2")
    @OperationLog(detail = "add2", operationType = OperationType.SELECT)
    public int add2(int a, int b) {
        return addCalculate.add2(a, b);
    }

    @GetMapping("/costTime")
    public String costTime() {
        log.info("调用costTime");
        return costTimeCalculate.calculate().toString();
    }

    @GetMapping("/getmap")
    public Map<String, List<Integer>> getMap() {
        return test.mapTest();
    }

    @GetMapping("/sendList")
    public String sendList() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("Hello");
        strings.add("Hello");
        return test.sendList(strings);
    }

    @GetMapping("/sendList2")
    public String sendList2() {
        LinkedList<String> strings = new LinkedList<>();
        strings.add("Hello");
        strings.add("Hello");
        strings.add("Hello");
        return test.sendList(strings);
    }

    @GetMapping("/sendList3")
    public String sendList3() {
        LinkedList<Person> strings = new LinkedList<Person>(){};
        Person person = new Person();
        person.setName("Asd");
        strings.add(person);
        return test.sendList2(strings);
    }

    @GetMapping("/p2")
    public String p2() {
        Person person = new Person();
        person.setName("123");
        return personManage.queryP(person, 1).toString();
    }


    @GetMapping("/p")
    public List<String> p() throws InterruptedException, ExecutionException {
        List<String> ret = new ArrayList<>();
        List<FutureTask> futureTasks = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        IntStream.range(0, 10).forEach(i -> {
            Callable<Person> callable = () -> {
                System.out.println("线程" + i + "开始工作");
                countDownLatch.countDown();
                Person person = new Person();
                person.setAge(i);
                person.setId(i);
                person.setName("张三" + i);
                return personManage.queryP(person, i);
            };
            FutureTask<Person> personFutureTask = new FutureTask<>(callable);
            futureTasks.add(personFutureTask);
            Thread thread = new Thread(personFutureTask);
            thread.start();
        });
        countDownLatch.await();
        for (FutureTask futureTask : futureTasks) {
            String s = futureTask.get().toString();
            ret.add(s);
            System.out.println(s);
        }
        System.out.println(ret.size());
        return ret;
    }


    @GetMapping("/save")
    public String save() {
        test.save();
        return "success";
    }

    @GetMapping("/get")
    public Object get() {
        return test.get(new Date());
    }

}
