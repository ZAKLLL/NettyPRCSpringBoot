package com.zakl.nettyrpc.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangJiaKui
 * @classname JsonUtils
 * @description 部分json工具类
 * @date 11/24/2020 6:04 PM
 */
public class JsonUtils {
    private final static Map<String, String> unBoxTypeMap;

    static {
        unBoxTypeMap = new HashMap<>();
        unBoxTypeMap.put("int", "java.lang.Integer");
        unBoxTypeMap.put("byte", "java.lang.Byte");
        unBoxTypeMap.put("short", "java.lang.Short");
        unBoxTypeMap.put("long", "java.lang.Long");
        unBoxTypeMap.put("double", "java.lang.Double");
        unBoxTypeMap.put("float", "java.lang.Float");
        unBoxTypeMap.put("boolean", "java.lang.Boolean");
    }

    public static Object[] jsonsToObjects(String[] jsons, String[] types) throws ClassNotFoundException {
        if (jsons == null || types == null || jsons.length != types.length) {
            return null;
        }
        Object[] ret = new Object[jsons.length];
        for (int i = 0; i < ret.length; i++) {


            //todo 泛型加载
            Class c = Thread.currentThread().getContextClassLoader().loadClass(unBoxTypeMap.getOrDefault(types[i], types[i]));

//            JSON.parseObject(jsons[i], new TypeReference<List<String>>(){});

            ret[i] = JSON.parseObject(jsons[i], c);

        }
        return ret;
    }

    public Object jsonToObject(String jsonData, String type) throws ClassNotFoundException {
        return JSON.parseObject(jsonData, Thread.currentThread().getClass().getClassLoader().loadClass(unBoxTypeMap.getOrDefault(type, type)));
    }

    public static String objectToJson(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T jsonToObject(String jsonData, Class<T> clazz) {
        return JSONObject.parseObject(jsonData, clazz);
    }

    public static String getBoxType(String type) {
        return unBoxTypeMap.getOrDefault(type, type);
    }

}
