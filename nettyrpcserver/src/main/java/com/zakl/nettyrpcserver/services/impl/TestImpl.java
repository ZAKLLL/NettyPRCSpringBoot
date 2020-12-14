package com.zakl.nettyrpcserver.services.impl;

import com.zakl.nettyrpcserver.pojo.Person;
import com.zakl.nettyrpcserver.services.Test;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangJiaKui
 * @classname TestImpl
 * @description TODO
 * @date 11/26/2020 2:50 PM
 */
@Service(value = Test.SERVICE_BEAN_NAME)
public class TestImpl implements Test {
    @Override
    public Map<String, List<Integer>> mapTest() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        Map<String, List<Integer>> map = new HashMap<>();
        map.put("Hello", list);
        return map;
    }

    @Override
    public void save() {
        System.out.println("save.....");
    }

    @Override
    public <T> T get(T t) {
        return t;
    }

    @Override
    public String sendList(List<String> objects) {
        for (String object : objects) {
            System.out.println(object);
        }
        return null;
    }

    @Override
    public String sendList2(List<Person> strings) {
        for (Person string : strings) {
            System.out.println(string);
        }
        return null;
    }
}
