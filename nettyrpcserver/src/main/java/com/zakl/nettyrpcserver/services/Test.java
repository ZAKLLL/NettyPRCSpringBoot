package com.zakl.nettyrpcserver.services;

import com.zakl.nettyrpcserver.pojo.Person;

import java.util.List;
import java.util.Map;

/**
 * @author ZhangJiaKui
 * @classname Test
 * @description TODO
 * @date 11/26/2020 2:49 PM
 */
public interface Test {
    String SERVICE_BEAN_NAME = "test";

    Map<String, List<Integer>> mapTest();

    void save();

    <T> T get(T t);

    String sendList(List<String> objects);

    List<Person> sendList2(List<Person> strings);


}
