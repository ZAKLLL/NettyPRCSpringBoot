package com.zakl.nettyrpcserver.services;

import java.util.List;
import java.util.Map;

/**
 * @author ZhangJiaKui
 * @classname Test
 * @description TODO
 * @date 11/26/2020 2:49 PM
 */
public interface Test {
    String SERVICE_BEAN_NAME="test";

    Map<String, List<Integer>> mapTest();
}
