/**
 * Copyright (C) 2017 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zakl.nettyrpcserver.netty;

import com.alibaba.fastjson.JSON;
import com.zakl.nettyrpc.common.model.MessageRequest;
import netscape.javascript.JSUtil;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MethodInvoker.java
 * @description:MethodInvoker功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/7/27
 */
public class MethodInvoker {
    private Object serviceBean;
    private StopWatch sw = new StopWatch();

    public Object getServiceBean() {
        return serviceBean;
    }

    public void setServiceBean(Object serviceBean) {
        this.serviceBean = serviceBean;
    }

    private final static Map<String, String> unBoxTypeMap;

    static {
        unBoxTypeMap = new HashMap<>();
        unBoxTypeMap.put("int", "java.lang.Integer");
        unBoxTypeMap.put("byte", "java.lang.Byte");
        unBoxTypeMap.put("short", "java.lang.Short");
        unBoxTypeMap.put("long", "java.lang.Long");
        unBoxTypeMap.put("double", "java.lang.Double");
        unBoxTypeMap.put("float", "java.lang.Float");
        unBoxTypeMap.put("bool", "java.lang.Boolean");
    }

    public Object invoke(MessageRequest request) throws Throwable {
        String methodName = request.getMethodName();
        String[] parameters = request.getParametersVal();
        String[] typeParameters = request.getTypeParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = JSON.parseObject(parameters[i], this.getClass().getClassLoader().loadClass(unBoxTypeMap.getOrDefault(typeParameters[i], typeParameters[i])));
        }
        sw.reset();
        sw.start();
        Object result = MethodUtils.invokeMethod(serviceBean, methodName, args);
        sw.stop();
        return result;
    }

    public long getInvokeTimespan() {
        return sw.getTime();
    }
}

