package com.zakl.nettyrpcserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author ZhangJiaKui
 * @classname Test
 * @description TODO
 * @date 1/6/2021 11:32 AM
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        String s = JSONObject.toJSONString(new Date());
        System.out.println(s);
        s = JSONObject.toJSON(new Date()).toString();
        System.out.println(s);
    }
}
