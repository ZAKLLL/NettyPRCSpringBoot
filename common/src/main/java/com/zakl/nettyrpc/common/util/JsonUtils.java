package com.zakl.nettyrpc.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public static Object[] jsonsToObjects(String[] jsons, String[] types) throws Exception {
        if (jsons == null || types == null || jsons.length != types.length) {
            return null;
        }
        Object[] ret = new Object[jsons.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = deserializeJsonDate(jsons[i], types[i]);
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

    public static Object deserializeJsonDate(String jsonData, String typeName) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        if (!typeName.contains("<") || !typeName.contains(">")) {
            //非泛型调用,直接进行反序列话
            return JSONObject.parseObject(jsonData, Thread.currentThread().getContextClassLoader().loadClass(typeName));
        } else {
            //泛型调用
            try {
                return JSONObject.parseObject(jsonData, constructTypeReference(typeName));
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                throw e;
            }
        }
    }


    public static TypeReference constructTypeReference(String typeName) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {

        TypeReference<List<?>> typeReference = new TypeReference<List<?>>() {
        };
        //反射修改final字段
        Field type = TypeReference.class.getDeclaredField("type");
        Field modifierField = Field.class.getDeclaredField("modifiers");
        modifierField.setAccessible(true);
        //剔除final标识,剔除protected标识
        modifierField.setInt(type, type.getModifiers() & ~Modifier.FINAL & ~Modifier.PROTECTED);
        type.setAccessible(true);
        type.set(typeReference, constructType(typeName));
        return typeReference;
    }

    //java.util.List<java.util.Map<java.lang.Integer, java.lang.String>>
    public static Type constructType(String type) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (!type.contains(">") && !type.contains("<") && !type.contains(",")) {
            return classLoader.loadClass(type.trim());
        }
        int i = type.indexOf("<");
        String outSideClassName = type.substring(0, i);
        Class<?> outSideClass = classLoader.loadClass(outSideClassName.trim());
        String tail = type.substring(i + 1, type.length() - 1);
        tail += ",";
        List<Type> types = new ArrayList<>();
        int stack = 0;
        int index = 0;
        int preIndex = 0;
        while (index < tail.length()) {
            if (tail.charAt(index) == '<') stack++;
            else if (tail.charAt(index) == '>') stack--;
            if (stack == 0 && tail.charAt(index) == ',') {
                types.add(constructType(tail.substring(preIndex, index)));
                preIndex = index + 1;
            }
            index++;
        }
        return ParameterizedTypeImpl.make(outSideClass, types.toArray(new Type[0]), null);
    }


}
